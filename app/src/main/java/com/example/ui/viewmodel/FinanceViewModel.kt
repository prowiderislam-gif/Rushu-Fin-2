package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.db.AppDatabase
import com.example.data.model.AppStateEntity
import com.example.data.model.LiabilityEntity
import com.example.data.model.TransactionEntity
import com.example.data.repository.FinanceRepository
import com.example.sync.DriveSyncManager
import com.example.sync.SyncResult
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class FinanceUiState(
    val initialBalance: Double = 100.0,
    val totalIncome: Double = 0.0,
    val totalExpense: Double = 0.0,
    val liveBalance: Double = 100.0, // Calculated strictly: initialBalance + totalIncome - totalExpense
    val currentLiability: Double = 0.0, // Sum(ADD_LIABILITY) - Sum(PAY_LIABILITY), strictly separate
    val peakLiability: Double = 0.0,
    val currencySymbol: String = "₹",
    val isAdminModeUnlocked: Boolean = false,
    val tier1Password: String = "1234",
    val tier2Password: String = "9999",
    val lastSyncTime: Long = 0L,
    val googleAccountEmail: String? = null,
    val isSyncing: Boolean = false,
    val syncMessage: String? = null
)

class FinanceViewModel(application: Application) : AndroidViewModel(application) {

    val database = AppDatabase.getDatabase(application)
    val repository = FinanceRepository(database.financeDao(), application)
    val driveSyncManager = DriveSyncManager(application, repository)

    val transactions: StateFlow<List<TransactionEntity>> = repository.transactions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val liabilities: StateFlow<List<LiabilityEntity>> = repository.liabilities
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val appState: StateFlow<AppStateEntity?> = repository.appState
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    private val _isAdminMode = MutableStateFlow(false)
    val isAdminMode: StateFlow<Boolean> = _isAdminMode.asStateFlow()

    private val _isSyncing = MutableStateFlow(false)
    val isSyncing: StateFlow<Boolean> = _isSyncing.asStateFlow()

    private val _syncMessage = MutableStateFlow<String?>(null)
    val syncMessage: StateFlow<String?> = _syncMessage.asStateFlow()

    private val _userFeedback = MutableSharedFlow<String>()
    val userFeedback: SharedFlow<String> = _userFeedback.asSharedFlow()

    private val dbDataFlow = combine(appState, transactions, liabilities) { state, txList, liabilityList ->
        Triple(state, txList, liabilityList)
    }

    private val uiFlagsFlow = combine(_isAdminMode, _isSyncing, _syncMessage) { isAdmin, syncing, message ->
        Triple(isAdmin, syncing, message)
    }

    val uiState: StateFlow<FinanceUiState> = combine(
        dbDataFlow,
        uiFlagsFlow
    ) { (state, txList, liabilityList), (isAdmin, syncing, message) ->
        val initial = state?.initialBalance ?: 100.0
        val tier1 = state?.tier1Password ?: "1234"
        val tier2 = state?.tier2Password ?: "9999"
        val currency = state?.currencySymbol ?: "₹"
        val syncTime = state?.lastSyncTime ?: 0L
        val email = state?.googleAccountEmail ?: driveSyncManager.getCurrentAccount()?.email

        var incomeSum = 0.0
        var expenseSum = 0.0
        txList.forEach { tx ->
            if (tx.type.equals("INCOME", ignoreCase = true)) {
                incomeSum += tx.amount
            } else if (tx.type.equals("EXPENSE", ignoreCase = true)) {
                expenseSum += tx.amount
            }
        }

        // Live balance is STRICTLY: initial + income - expense
        val calculatedLiveBalance = initial + incomeSum - expenseSum

        // Liabilities are STRICTLY independent
        var debtSum = 0.0
        liabilityList.forEach { l ->
            if (l.actionType.equals("ADD_LIABILITY", ignoreCase = true)) {
                debtSum += l.amount
            } else if (l.actionType.equals("PAY_LIABILITY", ignoreCase = true)) {
                debtSum -= l.amount
            }
        }
        val calculatedLiability = if (debtSum < 0) 0.0 else debtSum

        val storedPeak = state?.peakLiability ?: 0.0
        val effectivePeak = storedPeak.coerceAtLeast(calculatedLiability)

        FinanceUiState(
            initialBalance = initial,
            totalIncome = incomeSum,
            totalExpense = expenseSum,
            liveBalance = calculatedLiveBalance,
            currentLiability = calculatedLiability,
            peakLiability = effectivePeak,
            currencySymbol = currency,
            isAdminModeUnlocked = isAdmin,
            tier1Password = tier1,
            tier2Password = tier2,
            lastSyncTime = syncTime,
            googleAccountEmail = email,
            isSyncing = syncing,
            syncMessage = message
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), FinanceUiState())

    init {
        viewModelScope.launch {
            repository.getOrCreateAppState()
            // Auto check current linked Google account if any
            driveSyncManager.getCurrentAccount()?.email?.let { accountEmail ->
                repository.updateSyncInfo(accountEmail, System.currentTimeMillis())
            }
        }
    }

    // Security Verification
    fun verifyTier1Password(inputPin: String): Boolean {
        val currentPin = appState.value?.tier1Password ?: "1234"
        return inputPin.trim() == currentPin.trim()
    }

    fun verifyAndUnlockAdminMode(inputPin: String): Boolean {
        val masterPin = appState.value?.tier2Password ?: "9999"
        if (inputPin.trim() == masterPin.trim()) {
            _isAdminMode.value = true
            emitFeedback("Admin Mode Unlocked (Tier 2 Override Active)")
            return true
        } else {
            emitFeedback("Invalid Tier 2 Master Password")
            return false
        }
    }

    fun lockAdminMode() {
        _isAdminMode.value = false
        emitFeedback("Admin Mode Locked")
    }

    // Record Normal Income / Expense
    fun addTransaction(
        amount: Double,
        type: String,
        description: String,
        tier1Pin: String,
        onSuccess: () -> Unit
    ) {
        if (!verifyTier1Password(tier1Pin)) {
            emitFeedback("Invalid Tier 1 Password! Entry Rejected.")
            return
        }
        if (amount <= 0.0) {
            emitFeedback("Amount must be greater than zero.")
            return
        }
        if (description.isBlank()) {
            emitFeedback("Description is mandatory.")
            return
        }

        viewModelScope.launch {
            repository.addTransaction(amount, type, description)
            emitFeedback("${if (type == "INCOME") "+ive Income" else "-ive Expense"} recorded successfully.")
            onSuccess()
            triggerAutoSync()
        }
    }

    // Record Liability (+Debt or Pay Debt)
    fun addLiability(
        amount: Double,
        actionType: String,
        description: String,
        tier1Pin: String,
        onSuccess: () -> Unit
    ) {
        if (!verifyTier1Password(tier1Pin)) {
            emitFeedback("Invalid Tier 1 Password! Liability Entry Rejected.")
            return
        }
        if (amount <= 0.0) {
            emitFeedback("Liability amount must be greater than zero.")
            return
        }
        if (description.isBlank()) {
            emitFeedback("Description is mandatory.")
            return
        }

        viewModelScope.launch {
            repository.addLiability(amount, actionType, description)
            emitFeedback("${if (actionType == "ADD_LIABILITY") "Liability added" else "Liability payment deducted"} successfully.")
            onSuccess()
            triggerAutoSync()
        }
    }

    // Admin Mode Operations (Tier 2)
    fun updateInitialBalanceWithTier2(newBalance: Double, onSuccess: () -> Unit) {
        if (!_isAdminMode.value) {
            emitFeedback("Admin Mode (Tier 2) required to modify Fixed Initial Balance.")
            return
        }
        viewModelScope.launch {
            repository.updateInitialBalance(newBalance)
            emitFeedback("Fixed Initial Balance updated to ${uiState.value.currencySymbol}$newBalance")
            onSuccess()
            triggerAutoSync()
        }
    }

    fun updateTransactionWithAdmin(transaction: TransactionEntity, onSuccess: () -> Unit) {
        if (!_isAdminMode.value) {
            emitFeedback("Admin Mode (Tier 2) required to edit past transactions.")
            return
        }
        viewModelScope.launch {
            repository.updateTransaction(transaction)
            emitFeedback("Transaction updated.")
            onSuccess()
            triggerAutoSync()
        }
    }

    fun deleteTransactionWithAdmin(transaction: TransactionEntity) {
        if (!_isAdminMode.value) {
            emitFeedback("Admin Mode (Tier 2) required to delete ledger records.")
            return
        }
        viewModelScope.launch {
            repository.deleteTransaction(transaction)
            emitFeedback("Transaction removed from permanent ledger.")
            triggerAutoSync()
        }
    }

    fun updateLiabilityWithAdmin(liability: LiabilityEntity, onSuccess: () -> Unit) {
        if (!_isAdminMode.value) {
            emitFeedback("Admin Mode (Tier 2) required to edit liability records.")
            return
        }
        viewModelScope.launch {
            repository.updateLiability(liability)
            emitFeedback("Liability entry updated.")
            onSuccess()
            triggerAutoSync()
        }
    }

    fun deleteLiabilityWithAdmin(liability: LiabilityEntity) {
        if (!_isAdminMode.value) {
            emitFeedback("Admin Mode (Tier 2) required to delete liability records.")
            return
        }
        viewModelScope.launch {
            repository.deleteLiability(liability)
            emitFeedback("Liability removed from ledger.")
            triggerAutoSync()
        }
    }

    fun updatePasswords(newTier1: String, newTier2: String, onSuccess: () -> Unit) {
        if (!_isAdminMode.value) {
            emitFeedback("Admin Mode (Tier 2) required to update security passwords.")
            return
        }
        if (newTier1.isBlank() || newTier2.isBlank()) {
            emitFeedback("Passwords cannot be blank.")
            return
        }
        viewModelScope.launch {
            repository.updatePasswords(newTier1.trim(), newTier2.trim())
            emitFeedback("Security passwords updated successfully.")
            onSuccess()
            triggerAutoSync()
        }
    }

    // Google Drive Cloud Sync Routines
    fun onGoogleSignInSuccess(account: GoogleSignInAccount) {
        viewModelScope.launch {
            repository.updateSyncInfo(account.email, System.currentTimeMillis())
            emitFeedback("Google Account linked: ${account.email}")
            // Pull down any existing cloud backup instead of overwriting it.
            // If no backup exists yet (first-time link), this will simply report
            // "No existing backup file found" and the user can tap Backup Now.
            performCloudRestore()
        }
    }

    fun performCloudBackup() {
        viewModelScope.launch {
            _isSyncing.value = true
            _syncMessage.value = "Backing up snapshot to Google Drive..."
            val result = driveSyncManager.backupToGoogleDrive()
            _isSyncing.value = false
            when (result) {
                is SyncResult.Success -> {
                    _syncMessage.value = result.message
                    emitFeedback(result.message)
                }
                is SyncResult.Error -> {
                    _syncMessage.value = result.message
                    emitFeedback(result.message)
                }
            }
        }
    }

    fun performCloudRestore() {
        viewModelScope.launch {
            _isSyncing.value = true
            _syncMessage.value = "Restoring database from Google Drive..."
            val result = driveSyncManager.restoreFromGoogleDrive()
            _isSyncing.value = false
            when (result) {
                is SyncResult.Success -> {
                    _syncMessage.value = result.message
                    emitFeedback(result.message)
                }
                is SyncResult.Error -> {
                    _syncMessage.value = result.message
                    emitFeedback(result.message)
                }
            }
        }
    }

    fun performClearCloudBackup() {
        viewModelScope.launch {
            _isSyncing.value = true
            _syncMessage.value = "Clearing cloud backup..."
            val result = driveSyncManager.clearCloudBackup()
            _isSyncing.value = false
            when (result) {
                is SyncResult.Success -> {
                    _syncMessage.value = result.message
                    emitFeedback(result.message)
                }
                is SyncResult.Error -> {
                    _syncMessage.value = result.message
                    emitFeedback(result.message)
                }
            }
        }
    }

    private fun triggerAutoSync() {
        // Automatically sync snapshot if Google Account is linked
        if (driveSyncManager.getCurrentAccount() != null) {
            viewModelScope.launch {
                driveSyncManager.backupToGoogleDrive()
            }
        }
    }

    fun emitFeedback(msg: String) {
        viewModelScope.launch {
            _userFeedback.emit(msg)
        }
    }
}
