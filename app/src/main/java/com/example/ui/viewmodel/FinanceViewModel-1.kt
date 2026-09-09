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
import com.example.util.indianNumber
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class FinanceUiState(
    val initialBalance: Double = 0.0,
    val totalIncome: Double = 0.0,
    val totalExpense: Double = 0.0,
    val liveBalance: Double = 0.0, // Calculated strictly: initialBalance + totalIncome - totalExpense
    val currentLiability: Double = 0.0, // Sum(ADD_LIABILITY) - Sum(PAY_LIABILITY), strictly separate
    val peakLiability: Double = 0.0,
    val currencySymbol: String = "₹",
    val isAdminModeUnlocked: Boolean = false,
    val tier1Password: String = "1234",
    val tier2Password: String = "9999",
    val lastSyncTime: Long = 0L,
    val googleAccountEmail: String? = null,
    val isSyncing: Boolean = false,
    val syncMessage: String? = null,
    val showLiabilities: Boolean = true,
    val themeMode: String = "DEFAULT"
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
        val initial = state?.initialBalance ?: 0.0
        val tier1 = state?.tier1Password ?: "1234"
        val tier2 = state?.tier2Password ?: "9999"
        val currency = state?.currencySymbol ?: "₹"
        val syncTime = state?.lastSyncTime ?: 0L
        val email = state?.googleAccountEmail ?: driveSyncManager.getCurrentAccount()?.email
        val showLiab = state?.showLiabilities ?: true
        val theme = state?.themeMode ?: "DEFAULT"

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
            syncMessage = message,
            showLiabilities = showLiab,
            themeMode = theme
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
    private val _showSyncChoiceDialog = MutableStateFlow(false)
    val showSyncChoiceDialog: StateFlow<Boolean> = _showSyncChoiceDialog.asStateFlow()

    private val _showSetNewPasswordDialog = MutableStateFlow(false)
    val showSetNewPasswordDialog: StateFlow<Boolean> = _showSetNewPasswordDialog.asStateFlow()

    fun onPasswordRecoveryReauthSuccess(account: GoogleSignInAccount) {
        val linkedEmail = uiState.value.googleAccountEmail
        if (linkedEmail != null && linkedEmail.equals(account.email, ignoreCase = true)) {
            _showSetNewPasswordDialog.value = true
            emitFeedback("Identity verified via Google. Set a new Tier 2 password.")
        } else {
            emitFeedback("That Google account doesn't match the one linked to this app. Recovery denied.")
        }
    }

    fun dismissSetNewPasswordDialog() {
        _showSetNewPasswordDialog.value = false
    }

    fun setNewTier2PasswordViaRecovery(newTier2Password: String) {
        if (newTier2Password.isBlank()) {
            emitFeedback("New password cannot be blank.")
            return
        }
        viewModelScope.launch {
            val currentTier1 = appState.value?.tier1Password ?: "1234"
            repository.updatePasswords(currentTier1, newTier2Password.trim())
            emitFeedback("Tier 2 password reset successfully via Google verification.")
            _showSetNewPasswordDialog.value = false
            triggerAutoSync()
        }
    }

    fun onGoogleSignInSuccess(account: GoogleSignInAccount) {
        viewModelScope.launch {
            repository.updateSyncInfo(account.email, System.currentTimeMillis())
            emitFeedback("Google Account linked: ${account.email}")
            // Don't touch any data yet — ask the user how they want to resolve
            // local vs. cloud before doing anything.
            _showSyncChoiceDialog.value = true
        }
    }

    fun resolveSyncChoiceMerge() {
        _showSyncChoiceDialog.value = false
        performCloudMerge()
    }

    fun resolveSyncChoiceReplaceLocal() {
        _showSyncChoiceDialog.value = false
        performCloudRestore()
    }

    fun resolveSyncChoiceDiscardCloud() {
        _showSyncChoiceDialog.value = false
        performCloudBackup()
    }

    fun dismissSyncChoiceDialog() {
        _showSyncChoiceDialog.value = false
    }

    fun performCloudMerge() {
        viewModelScope.launch {
            _isSyncing.value = true
            _syncMessage.value = "Merging local and cloud data..."
            val result = driveSyncManager.mergeWithGoogleDrive()
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

    fun performSwitchAccount(onReadyForNewSignIn: () -> Unit) {
        viewModelScope.launch {
            _isSyncing.value = true

            // 1. Make sure the current account's data is safely backed up first.
            _syncMessage.value = "Backing up current account before switching..."
            val backupResult = driveSyncManager.backupToGoogleDrive()
            if (backupResult is SyncResult.Error) {
                _isSyncing.value = false
                _syncMessage.value = backupResult.message
                emitFeedback("Could not back up current account: ${backupResult.message}")
                return@launch
            }

            // 2. Wipe local data so the next account starts on a clean slate
            // (its own backup, if any, will be pulled down right after sign-in).
            _syncMessage.value = "Clearing local data for account switch..."
            repository.clearAllLocalDataForSwitch()

            // 3. Sign out so Google shows the account picker instead of
            // silently reusing the same account.
            driveSyncManager.signOut {
                _isSyncing.value = false
                _syncMessage.value = "Choose the next Google account to continue."
                emitFeedback("Signed out. Choose the next Google account.")
                onReadyForNewSignIn()
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

    /**
     * Builds a plain-text export of transactions (and optionally liabilities),
     * filtered to [startTimestamp]..[endTimestamp] (either can be null to mean
     * "no lower/upper bound", so both null = all time).
     */
    fun buildExportText(startTimestamp: Long?, endTimestamp: Long?, includeLiabilities: Boolean): String {
        val state = uiState.value
        val sym = state.currencySymbol

        val txList = transactions.value
            .filter { tx ->
                (startTimestamp == null || tx.timestamp >= startTimestamp) &&
                    (endTimestamp == null || tx.timestamp <= endTimestamp)
            }
            .sortedBy { it.timestamp }

        val liabList = if (includeLiabilities) {
            liabilities.value
                .filter { l ->
                    (startTimestamp == null || l.timestamp >= startTimestamp) &&
                        (endTimestamp == null || l.timestamp <= endTimestamp)
                }
                .sortedBy { it.timestamp }
        } else emptyList()

        val generatedAt = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(Date())
        val periodLabel = if (startTimestamp == null && endTimestamp == null) {
            "All Time"
        } else {
            val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
            val startLabel = startTimestamp?.let { sdf.format(Date(it)) } ?: "Beginning"
            val endLabel = endTimestamp?.let { sdf.format(Date(it)) } ?: "Now"
            "$startLabel  to  $endLabel"
        }

        val sb = StringBuilder()
        sb.appendLine("RUSHU FIN - TRANSACTION EXPORT")
        sb.appendLine("Generated: $generatedAt")
        sb.appendLine("Period: $periodLabel")
        sb.appendLine("=".repeat(50))
        sb.appendLine()
        sb.appendLine("SUMMARY")
        sb.appendLine("-".repeat(50))
        sb.appendLine("Initial Balance   : $sym ${indianNumber(state.initialBalance)}")
        sb.appendLine("Live Balance      : $sym ${indianNumber(state.liveBalance)}")
        sb.appendLine("Total Income      : +$sym${indianNumber(state.totalIncome)}")
        sb.appendLine("Total Expenses    : -$sym${indianNumber(state.totalExpense)}")
        if (includeLiabilities) {
            sb.appendLine("Current Liability : $sym ${indianNumber(state.currentLiability)}")
        }
        sb.appendLine()

        sb.appendLine("=".repeat(50))
        sb.appendLine("MAIN LEDGER (INCOME / EXPENSE)")
        sb.appendLine("=".repeat(50))
        if (txList.isEmpty()) {
            sb.appendLine("(No transactions in this period)")
        } else {
            txList.forEach { tx ->
                val sign = if (tx.type.equals("INCOME", ignoreCase = true)) "+" else "-"
                sb.appendLine("${tx.dateString} ${tx.timeString}  |  $sign$sym${indianNumber(tx.amount)}  |  ${tx.description}")
            }
        }

        if (includeLiabilities) {
            sb.appendLine()
            sb.appendLine("=".repeat(50))
            sb.appendLine("LIABILITY LEDGER")
            sb.appendLine("=".repeat(50))
            if (liabList.isEmpty()) {
                sb.appendLine("(No liability records in this period)")
            } else {
                liabList.forEach { l ->
                    val isAdd = l.actionType.equals("ADD_LIABILITY", ignoreCase = true)
                    val sign = if (isAdd) "+" else "-"
                    val label = if (isAdd) "New Liability" else "Payment"
                    sb.appendLine("${l.dateString} ${l.timeString}  |  $sign$sym${indianNumber(l.amount)}  |  $label: ${l.description}")
                }
            }
        }

        sb.appendLine()
        sb.appendLine("-".repeat(50))
        sb.appendLine("Exported from RUSHU FIN - Personal Finance & Liability Tracking")

        return sb.toString()
    }

    /**
     * Builds a plain-text export grouped by keyword, where each "category" is
     * really just a keyword (e.g. a name like "X", or a word like "Salary")
     * matched against each transaction's description text — no separate
     * category field needed. A transaction matching more than one keyword
     * appears under each keyword it matches.
     */
    fun buildKeywordExportText(keywords: List<String>, startTimestamp: Long?, endTimestamp: Long?): String {
        val sym = uiState.value.currencySymbol
        val allTx = transactions.value
            .filter { tx ->
                (startTimestamp == null || tx.timestamp >= startTimestamp) &&
                    (endTimestamp == null || tx.timestamp <= endTimestamp)
            }
            .sortedBy { it.timestamp }

        val generatedAt = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(Date())
        val periodLabel = if (startTimestamp == null && endTimestamp == null) {
            "All Time"
        } else {
            val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
            val startLabel = startTimestamp?.let { sdf.format(Date(it)) } ?: "Beginning"
            val endLabel = endTimestamp?.let { sdf.format(Date(it)) } ?: "Now"
            "$startLabel  to  $endLabel"
        }

        val sb = StringBuilder()
        sb.appendLine("RUSHU FIN - KEYWORD EXPORT")
        sb.appendLine("Generated: $generatedAt")
        sb.appendLine("Period: $periodLabel")
        sb.appendLine("Keywords: ${keywords.joinToString(", ")}")
        sb.appendLine("(A transaction is grouped under a keyword if that word appears anywhere in its description. A transaction matching more than one keyword is listed under each.)")
        sb.appendLine("=".repeat(50))

        var grandIncome = 0.0
        var grandExpense = 0.0
        var anyMatch = false

        keywords.forEach { keyword ->
            val matches = allTx.filter { it.description.contains(keyword, ignoreCase = true) }
            if (matches.isEmpty()) {
                sb.appendLine()
                sb.appendLine("KEYWORD: $keyword")
                sb.appendLine("-".repeat(50))
                sb.appendLine("(No matching transactions)")
                return@forEach
            }
            anyMatch = true

            var kwIncome = 0.0
            var kwExpense = 0.0
            matches.forEach { tx ->
                if (tx.type.equals("INCOME", ignoreCase = true)) kwIncome += tx.amount
                else kwExpense += tx.amount
            }
            grandIncome += kwIncome
            grandExpense += kwExpense

            sb.appendLine()
            sb.appendLine("KEYWORD: $keyword")
            sb.appendLine("-".repeat(50))
            matches.forEach { tx ->
                val sign = if (tx.type.equals("INCOME", ignoreCase = true)) "+" else "-"
                sb.appendLine("${tx.dateString} ${tx.timeString}  |  $sign$sym${indianNumber(tx.amount)}  |  ${tx.description}")
            }
            sb.appendLine("-".repeat(50))
            sb.appendLine("Subtotal Income   : +$sym${indianNumber(kwIncome)}")
            sb.appendLine("Subtotal Expenses : -$sym${indianNumber(kwExpense)}")
            sb.appendLine("Subtotal Net      : $sym${indianNumber(kwIncome - kwExpense)}")
        }

        sb.appendLine()
        sb.appendLine("=".repeat(50))
        sb.appendLine("COMBINED TOTAL (Sum of matched keyword groups above — overlapping matches, if any, are counted once per group they appear in)")
        sb.appendLine("=".repeat(50))
        if (!anyMatch) {
            sb.appendLine("(No transactions matched any of the given keywords)")
        } else {
            sb.appendLine("Total Income      : +$sym${indianNumber(grandIncome)}")
            sb.appendLine("Total Expenses    : -$sym${indianNumber(grandExpense)}")
            sb.appendLine("Net               : $sym${indianNumber(grandIncome - grandExpense)}")
        }
        sb.appendLine()
        sb.appendLine("-".repeat(50))
        sb.appendLine("Exported from RUSHU FIN - Personal Finance & Liability Tracking")

        return sb.toString()
    }

    fun setShowLiabilities(show: Boolean) {
        viewModelScope.launch {
            repository.updateShowLiabilities(show)
            emitFeedback(if (show) "Liability section shown." else "Liability section hidden.")
            triggerAutoSync()
        }
    }

    fun setThemeMode(mode: String) {
        viewModelScope.launch {
            repository.updateThemeMode(mode)
            val label = when (mode) {
                "BASIC" -> "Basic / Large Text theme selected."
                "KITTY" -> "Sweet Kitty theme selected."
                else -> "Default theme selected."
            }
            emitFeedback(label)
            triggerAutoSync()
        }
    }

    fun emitFeedback(msg: String) {
        viewModelScope.launch {
            _userFeedback.emit(msg)
        }
    }
}
