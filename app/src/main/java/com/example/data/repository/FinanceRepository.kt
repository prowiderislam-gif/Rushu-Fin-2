package com.example.data.repository

import android.content.Context
import com.example.data.dao.FinanceDao
import com.example.data.db.AppDatabase
import com.example.data.model.AppStateEntity
import com.example.data.model.LiabilityEntity
import com.example.data.model.TransactionEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class FinanceRepository(
    private val financeDao: FinanceDao,
    private val context: Context
) {
    val transactions: Flow<List<TransactionEntity>> = financeDao.getAllTransactions()
    val liabilities: Flow<List<LiabilityEntity>> = financeDao.getAllLiabilities()
    val appState: Flow<AppStateEntity?> = financeDao.getAppState()

    private val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    private val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())

    suspend fun getOrCreateAppState(): AppStateEntity = withContext(Dispatchers.IO) {
        val current = financeDao.getAppStateSync()
        if (current != null) {
            current
        } else {
            val initial = AppStateEntity()
            financeDao.insertOrUpdateAppState(initial)
            initial
        }
    }

    suspend fun addTransaction(amount: Double, type: String, description: String, category: String = "Uncategorized"): Long = withContext(Dispatchers.IO) {
        val now = Date()
        val tx = TransactionEntity(
            amount = amount,
            type = type.uppercase(Locale.getDefault()),
            description = description.trim(),
            category = category.trim().ifBlank { "Uncategorized" },
            dateString = dateFormat.format(now),
            timeString = timeFormat.format(now),
            timestamp = now.time
        )
        financeDao.insertTransaction(tx)
    }

    suspend fun getDistinctCategories(): List<String> = withContext(Dispatchers.IO) {
        financeDao.getDistinctCategoriesSync()
    }

    suspend fun updateTransaction(transaction: TransactionEntity) = withContext(Dispatchers.IO) {
        financeDao.updateTransaction(transaction)
    }

    suspend fun deleteTransaction(transaction: TransactionEntity) = withContext(Dispatchers.IO) {
        financeDao.deleteTransaction(transaction)
    }

    suspend fun addLiability(amount: Double, actionType: String, description: String): Long = withContext(Dispatchers.IO) {
        val now = Date()
        val liability = LiabilityEntity(
            amount = amount,
            actionType = actionType.uppercase(Locale.getDefault()),
            description = description.trim(),
            dateString = dateFormat.format(now),
            timeString = timeFormat.format(now),
            timestamp = now.time
        )
        val id = financeDao.insertLiability(liability)

        // Check if peak liability needs updating
        if (actionType.uppercase(Locale.getDefault()) == "ADD_LIABILITY") {
            val all = financeDao.getAllLiabilitiesSync()
            var currentDebt = 0.0
            all.forEach {
                if (it.actionType == "ADD_LIABILITY") currentDebt += it.amount
                else if (it.actionType == "PAY_LIABILITY") currentDebt -= it.amount
            }
            if (currentDebt < 0) currentDebt = 0.0
            val state = getOrCreateAppState()
            if (currentDebt > state.peakLiability) {
                financeDao.insertOrUpdateAppState(state.copy(peakLiability = currentDebt))
            }
        }
        id
    }

    suspend fun updateLiability(liability: LiabilityEntity) = withContext(Dispatchers.IO) {
        financeDao.updateLiability(liability)
    }

    suspend fun deleteLiability(liability: LiabilityEntity) = withContext(Dispatchers.IO) {
        financeDao.deleteLiability(liability)
    }

    suspend fun updateInitialBalance(newBalance: Double) = withContext(Dispatchers.IO) {
        val current = getOrCreateAppState()
        financeDao.insertOrUpdateAppState(current.copy(initialBalance = newBalance))
    }

    suspend fun updatePasswords(tier1: String, tier2: String) = withContext(Dispatchers.IO) {
        val current = getOrCreateAppState()
        financeDao.insertOrUpdateAppState(current.copy(tier1Password = tier1, tier2Password = tier2))
    }

    suspend fun updateShowLiabilities(show: Boolean) = withContext(Dispatchers.IO) {
        val current = getOrCreateAppState()
        financeDao.insertOrUpdateAppState(current.copy(showLiabilities = show))
    }

    suspend fun updateThemeMode(mode: String) = withContext(Dispatchers.IO) {
        val current = getOrCreateAppState()
        financeDao.insertOrUpdateAppState(current.copy(themeMode = mode))
    }

    suspend fun updateSyncInfo(email: String?, syncTime: Long) = withContext(Dispatchers.IO) {
        val current = getOrCreateAppState()
        financeDao.insertOrUpdateAppState(current.copy(googleAccountEmail = email, lastSyncTime = syncTime))
    }

    suspend fun resetPeakLiability(newPeak: Double) = withContext(Dispatchers.IO) {
        val current = getOrCreateAppState()
        financeDao.insertOrUpdateAppState(current.copy(peakLiability = newPeak))
    }

    suspend fun exportBackupJson(): String = withContext(Dispatchers.IO) {
        val state = getOrCreateAppState()
        val txs = financeDao.getAllTransactionsSync()
        val liabilitiesList = financeDao.getAllLiabilitiesSync()

        val root = JSONObject()
        root.put("version", 1)
        root.put("exportTime", System.currentTimeMillis())

        val stateObj = JSONObject()
        stateObj.put("initialBalance", state.initialBalance)
        stateObj.put("tier1Password", state.tier1Password)
        stateObj.put("tier2Password", state.tier2Password)
        stateObj.put("currencySymbol", state.currencySymbol)
        stateObj.put("peakLiability", state.peakLiability)
        stateObj.put("showLiabilities", state.showLiabilities)
        stateObj.put("themeMode", state.themeMode)
        stateObj.put("googleAccountEmail", state.googleAccountEmail ?: "")
        stateObj.put("lastSyncTime", state.lastSyncTime)
        root.put("appState", stateObj)

        val txArray = JSONArray()
        txs.forEach { tx ->
            val obj = JSONObject()
            obj.put("id", tx.id)
            obj.put("amount", tx.amount)
            obj.put("type", tx.type)
            obj.put("description", tx.description)
            obj.put("category", tx.category)
            obj.put("dateString", tx.dateString)
            obj.put("timeString", tx.timeString)
            obj.put("timestamp", tx.timestamp)
            txArray.put(obj)
        }
        root.put("transactions", txArray)

        val lArray = JSONArray()
        liabilitiesList.forEach { l ->
            val obj = JSONObject()
            obj.put("id", l.id)
            obj.put("amount", l.amount)
            obj.put("actionType", l.actionType)
            obj.put("description", l.description)
            obj.put("dateString", l.dateString)
            obj.put("timeString", l.timeString)
            obj.put("timestamp", l.timestamp)
            lArray.put(obj)
        }
        root.put("liabilities", lArray)

        root.toString(2)
    }

    suspend fun restoreBackupJson(jsonString: String): Boolean = withContext(Dispatchers.IO) {
        try {
            val root = JSONObject(jsonString)
            if (root.has("appState")) {
                val stateObj = root.getJSONObject("appState")
                val restoredState = AppStateEntity(
                    id = 1,
                    initialBalance = stateObj.optDouble("initialBalance", 0.0),
                    tier1Password = stateObj.optString("tier1Password", "1234"),
                    tier2Password = stateObj.optString("tier2Password", "9999"),
                    currencySymbol = stateObj.optString("currencySymbol", "₹"),
                    lastSyncTime = stateObj.optLong("lastSyncTime", System.currentTimeMillis()),
                    googleAccountEmail = stateObj.optString("googleAccountEmail", "").takeIf { it.isNotEmpty() },
                    peakLiability = stateObj.optDouble("peakLiability", 0.0),
                    showLiabilities = stateObj.optBoolean("showLiabilities", true),
                    themeMode = stateObj.optString("themeMode", "DEFAULT")
                )
                financeDao.insertOrUpdateAppState(restoredState)
            }

            if (root.has("transactions")) {
                val txArray = root.getJSONArray("transactions")
                val list = mutableListOf<TransactionEntity>()
                for (i in 0 until txArray.length()) {
                    val obj = txArray.getJSONObject(i)
                    list.add(
                        TransactionEntity(
                            id = obj.optLong("id", 0L),
                            amount = obj.getDouble("amount"),
                            type = obj.getString("type"),
                            description = obj.getString("description"),
                            category = obj.optString("category", "Uncategorized"),
                            dateString = obj.getString("dateString"),
                            timeString = obj.getString("timeString"),
                            timestamp = obj.optLong("timestamp", System.currentTimeMillis())
                        )
                    )
                }
                financeDao.clearAllTransactions()
                financeDao.insertAllTransactions(list)
            }

            if (root.has("liabilities")) {
                val lArray = root.getJSONArray("liabilities")
                val list = mutableListOf<LiabilityEntity>()
                for (i in 0 until lArray.length()) {
                    val obj = lArray.getJSONObject(i)
                    list.add(
                        LiabilityEntity(
                            id = obj.optLong("id", 0L),
                            amount = obj.getDouble("amount"),
                            actionType = obj.getString("actionType"),
                            description = obj.getString("description"),
                            dateString = obj.getString("dateString"),
                            timeString = obj.getString("timeString"),
                            timestamp = obj.optLong("timestamp", System.currentTimeMillis())
                        )
                    )
                }
                financeDao.clearAllLiabilities()
                financeDao.insertAllLiabilities(list)
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun clearAllLocalDataForSwitch() = withContext(Dispatchers.IO) {
        // Keep Tier 1/Tier 2 passwords (device-level security, not tied to any
        // one Google account), but wipe transactions, liabilities, and reset
        // the balance/liability/account fields so the new account starts clean.
        val current = financeDao.getAppStateSync()
        financeDao.clearAllTransactions()
        financeDao.clearAllLiabilities()
        val resetState = AppStateEntity(
            id = 1,
            initialBalance = 0.0,
            tier1Password = current?.tier1Password ?: "1234",
            tier2Password = current?.tier2Password ?: "9999",
            currencySymbol = current?.currencySymbol ?: "₹",
            lastSyncTime = 0L,
            googleAccountEmail = null,
            peakLiability = 0.0
        )
        financeDao.insertOrUpdateAppState(resetState)
    }

    suspend fun mergeBackupJson(cloudJsonString: String): String = withContext(Dispatchers.IO) {
        val root = JSONObject(cloudJsonString)

        // --- Merge transactions: match by amount+type+description+date+time to skip duplicates ---
        val localTx = financeDao.getAllTransactionsSync()
        val cloudTxList = mutableListOf<TransactionEntity>()
        if (root.has("transactions")) {
            val txArray = root.getJSONArray("transactions")
            for (i in 0 until txArray.length()) {
                val obj = txArray.getJSONObject(i)
                cloudTxList.add(
                    TransactionEntity(
                        id = 0L,
                        amount = obj.getDouble("amount"),
                        type = obj.getString("type"),
                        description = obj.getString("description"),
                        category = obj.optString("category", "Uncategorized"),
                        dateString = obj.getString("dateString"),
                        timeString = obj.getString("timeString"),
                        timestamp = obj.optLong("timestamp", 0L)
                    )
                )
            }
        }
        fun txKey(t: TransactionEntity) =
            "${t.amount}|${t.type}|${t.description.trim().lowercase(Locale.getDefault())}|${t.dateString}|${t.timeString}"
        val mergedTxMap = LinkedHashMap<String, TransactionEntity>()
        (cloudTxList + localTx).forEach { tx -> mergedTxMap[txKey(tx)] = tx }
        val mergedTxList = mergedTxMap.values.sortedBy { it.timestamp }

        // --- Merge liabilities: same matching approach ---
        val localLiab = financeDao.getAllLiabilitiesSync()
        val cloudLiabList = mutableListOf<LiabilityEntity>()
        if (root.has("liabilities")) {
            val lArray = root.getJSONArray("liabilities")
            for (i in 0 until lArray.length()) {
                val obj = lArray.getJSONObject(i)
                cloudLiabList.add(
                    LiabilityEntity(
                        id = 0L,
                        amount = obj.getDouble("amount"),
                        actionType = obj.getString("actionType"),
                        description = obj.getString("description"),
                        dateString = obj.getString("dateString"),
                        timeString = obj.getString("timeString"),
                        timestamp = obj.optLong("timestamp", 0L)
                    )
                )
            }
        }
        fun liabKey(l: LiabilityEntity) =
            "${l.amount}|${l.actionType}|${l.description.trim().lowercase(Locale.getDefault())}|${l.dateString}|${l.timeString}"
        val mergedLiabMap = LinkedHashMap<String, LiabilityEntity>()
        (cloudLiabList + localLiab).forEach { l -> mergedLiabMap[liabKey(l)] = l }
        val mergedLiabList = mergedLiabMap.values.sortedBy { it.timestamp }

        // --- Merge app state: cloud's financial settings win for initialBalance/currency
        // (that's the account's configured starting point), but device passwords and the
        // higher of the two peak-liability values are preserved. ---
        val localState = getOrCreateAppState()
        val cloudStateObj = if (root.has("appState")) root.getJSONObject("appState") else null
        val cloudInitial = cloudStateObj?.optDouble("initialBalance", localState.initialBalance) ?: localState.initialBalance
        val cloudCurrency = cloudStateObj?.optString("currencySymbol", localState.currencySymbol) ?: localState.currencySymbol
        val cloudPeak = cloudStateObj?.optDouble("peakLiability", 0.0) ?: 0.0

        var computedDebt = 0.0
        mergedLiabList.forEach {
            if (it.actionType.equals("ADD_LIABILITY", ignoreCase = true)) computedDebt += it.amount
            else if (it.actionType.equals("PAY_LIABILITY", ignoreCase = true)) computedDebt -= it.amount
        }
        if (computedDebt < 0) computedDebt = 0.0
        val mergedPeak = maxOf(cloudPeak, localState.peakLiability, computedDebt)

        val mergedState = localState.copy(
            initialBalance = cloudInitial,
            currencySymbol = cloudCurrency,
            peakLiability = mergedPeak
        )

        // Write the merged result locally
        financeDao.clearAllTransactions()
        financeDao.insertAllTransactions(mergedTxList)
        financeDao.clearAllLiabilities()
        financeDao.insertAllLiabilities(mergedLiabList)
        financeDao.insertOrUpdateAppState(mergedState)

        // Return a fresh export of the now-merged local data, ready to re-upload
        exportBackupJson()
    }

    fun getDatabaseFile(): File {
        return context.getDatabasePath(AppDatabase.DATABASE_NAME)
    }
}
