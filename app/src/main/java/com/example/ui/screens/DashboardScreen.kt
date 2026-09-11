package com.example.ui.screens

import android.app.Activity
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.R
import com.example.data.model.LiabilityEntity
import com.example.data.model.TransactionEntity
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.FinanceUiState
import com.example.ui.viewmodel.FinanceViewModel
import com.example.util.FileExportUtil
import com.example.util.indianNumber
import com.google.android.gms.auth.api.signin.GoogleSignIn
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun DashboardScreen(
    viewModel: FinanceViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val transactions by viewModel.transactions.collectAsStateWithLifecycle()
    val liabilities by viewModel.liabilities.collectAsStateWithLifecycle()
    val isAdminMode by viewModel.isAdminMode.collectAsStateWithLifecycle()
    val showSyncChoice by viewModel.showSyncChoiceDialog.collectAsStateWithLifecycle()
    val showSetNewPasswordDialog by viewModel.showSetNewPasswordDialog.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    var showAdminUnlockDialog by remember { mutableStateOf(false) }
    var showEditInitialBalanceDialog by remember { mutableStateOf(false) }
    var showSettingsDialog by remember { mutableStateOf(false) }
    var editingTransaction by remember { mutableStateOf<TransactionEntity?>(null) }
    var editingLiability by remember { mutableStateOf<LiabilityEntity?>(null) }

    // Dialog states for Exports, Passwords & Account Switch Confirmation
    var showDateRangeExportDialog by remember { mutableStateOf(false) }
    var showKeywordExportDialog by remember { mutableStateOf(false) }
    var showChangePinDialog by remember { mutableStateOf(false) }
    var showSwitchAccountWarningDialog by remember { mutableStateOf(false) }

    val googleSignInLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                task.result?.let { viewModel.onGoogleSignInSuccess(it) }
            } catch (e: Exception) {
                scope.launch { snackbarHostState.showSnackbar("Sign in error: ${e.localizedMessage}") }
            }
        }
    }

    val passwordRecoveryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                task.result?.let { viewModel.onPasswordRecoveryReauthSuccess(it) }
            } catch (e: Exception) {
                scope.launch { snackbarHostState.showSnackbar("Recovery error: ${e.localizedMessage}") }
            }
        }
    }

    LaunchedEffect(Unit) {
        viewModel.userFeedback.collectLatest { msg ->
            snackbarHostState.showSnackbar(msg)
        }
    }

    val baseDensity = LocalDensity.current
    val scaledDensity = remember(uiState.themeMode, baseDensity) {
        Density(
            density = baseDensity.density,
            fontScale = if (uiState.themeMode == "BASIC") baseDensity.fontScale * 1.35f else baseDensity.fontScale
        )
    }

    CompositionLocalProvider(
        LocalAppTheme provides uiState.themeMode,
        LocalDensity provides scaledDensity
    ) {
        val isHinata = uiState.themeMode == "HINATA"

        Box(
            modifier = modifier
                .fillMaxSize()
                .background(CanvasBackground)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                contentPadding = WindowInsets.systemBars.asPaddingValues(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header with "MADE BY RUH, WITH LOVE ❤"
                item {
                    AppHeader(
                        isAdminMode = isAdminMode,
                        googleEmail = uiState.googleAccountEmail,
                        onAdminToggleClick = {
                            if (isAdminMode) viewModel.lockAdminMode() else showAdminUnlockDialog = true
                        },
                        onSettingsClick = { showSettingsDialog = true }
                    )
                }

                // Main Live Balance Card
                item {
                    val formulaText = "Formula: Initial (${uiState.currencySymbol}${uiState.initialBalance.toInt()}) + Income (${uiState.currencySymbol}${uiState.totalIncome.toInt()}) - Expenses (${uiState.currencySymbol}${uiState.totalExpense.toInt()})"

                    when (uiState.themeMode) {
                        "RUH" -> {
                            RuhMainBalanceCard(
                                liveBalance = uiState.liveBalance,
                                totalIncome = uiState.totalIncome,
                                totalExpenses = uiState.totalExpense,
                                formulaText = formulaText
                            )
                        }
                        "BUMBLEBEE" -> {
                            BumblebeeMainBalanceCard(
                                liveBalance = uiState.liveBalance,
                                totalIncome = uiState.totalIncome,
                                totalExpenses = uiState.totalExpense,
                                formulaText = formulaText
                            )
                        }
                        "KAKASHI" -> {
                            KakashiMainBalanceCard(
                                liveBalance = uiState.liveBalance,
                                totalIncome = uiState.totalIncome,
                                totalExpenses = uiState.totalExpense,
                                formulaText = formulaText
                            )
                        }
                        "HINATA" -> {
                            HinataMainBalanceCard(
                                liveBalance = uiState.liveBalance,
                                totalIncome = uiState.totalIncome,
                                totalExpenses = uiState.totalExpense,
                                formulaText = formulaText
                            )
                        }
                        else -> {
                            // "DEFAULT" (Original Neon Cyberpunk) or "BASIC"
                            HinataMainBalanceCard(
                                liveBalance = uiState.liveBalance,
                                totalIncome = uiState.totalIncome,
                                totalExpenses = uiState.totalExpense,
                                formulaText = formulaText
                            )
                        }
                    }
                }

                // Split Balance Row (Fixed Initial & Liabilities)
                item {
                    SplitBalanceRow(
                        uiState = uiState,
                        isAdminMode = isAdminMode,
                        showLiabilities = uiState.showLiabilities,
                        onEditInitialBalance = {
                            if (isAdminMode) showEditInitialBalanceDialog = true else showAdminUnlockDialog = true
                        }
                    )
                }

                // Liability Payoff Progress Bar
                if (uiState.showLiabilities) {
                    item {
                        GlowingPayoffProgressBar(
                            currentLiability = uiState.currentLiability,
                            peakLiability = uiState.peakLiability,
                            currencySymbol = uiState.currencySymbol
                        )
                    }
                }

                // Transaction Entry Module
                item {
                    NormalTransactionModule(
                        currencySymbol = uiState.currencySymbol,
                        onRecordTransaction = { amount: Double, type: String, desc: String, pin: String, onDone: () -> Unit ->
                            viewModel.addTransaction(amount, type, desc, pin, onDone)
                        }
                    )
                }

                // Liability Module
                if (uiState.showLiabilities) {
                    item {
                        LiabilityManagementModule(
                            currencySymbol = uiState.currencySymbol,
                            onRecordLiability = { amount: Double, actionType: String, desc: String, pin: String, onDone: () -> Unit ->
                                viewModel.addLiability(amount, actionType, desc, pin, onDone)
                            }
                        )
                    }
                }

                // Permanent Audit Ledgers
                item {
                    AuditLedgersSection(
                        transactions = transactions,
                        liabilities = liabilities,
                        currencySymbol = uiState.currencySymbol,
                        isAdminMode = isAdminMode,
                        showLiabilities = uiState.showLiabilities,
                        onEditTx = { tx: TransactionEntity -> editingTransaction = tx },
                        onDeleteTx = { tx: TransactionEntity -> viewModel.deleteTransactionWithAdmin(tx) },
                        onEditLiability = { l: LiabilityEntity -> editingLiability = l },
                        onDeleteLiability = { l: LiabilityEntity -> viewModel.deleteLiabilityWithAdmin(l) }
                    )
                }

                item { Spacer(modifier = Modifier.height(32.dp)) }
            }

            if (isHinata) {
                FloatingPetalsOverlay(petalColor = NeonCyan, petalCount = 14)
            }

            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 24.dp)
            )
        }

        // 1. Admin Unlock Dialog (with Password Recovery)
        if (showAdminUnlockDialog) {
            AdminUnlockDialog(
                onDismiss = { showAdminUnlockDialog = false },
                onUnlock = { pin: String ->
                    if (viewModel.verifyAndUnlockAdminMode(pin)) showAdminUnlockDialog = false
                },
                googleAccountLinked = uiState.googleAccountEmail != null,
                onForgotPassword = {
                    showAdminUnlockDialog = false
                    passwordRecoveryLauncher.launch(viewModel.driveSyncManager.getSignInIntent())
                }
            )
        }

        // 2. Recovery Reset Password Dialog
        if (showSetNewPasswordDialog) {
            SetNewPasswordDialog(
                onDismiss = { viewModel.dismissSetNewPasswordDialog() },
                onConfirm = { newPwd -> viewModel.setNewTier2PasswordViaRecovery(newPwd) }
            )
        }

        // 3. Edit Fixed Initial Balance Dialog
        if (showEditInitialBalanceDialog) {
            EditInitialBalanceDialog(
                currentBalance = uiState.initialBalance,
                currencySymbol = uiState.currencySymbol,
                onDismiss = { showEditInitialBalanceDialog = false },
                onConfirm = { newBal: Double ->
                    viewModel.updateInitialBalanceWithTier2(newBal) { showEditInitialBalanceDialog = false }
                }
            )
        }

        // 4. Settings Dialog
        if (showSettingsDialog) {
            SettingsDialog(
                uiState = uiState,
                onDismiss = { showSettingsDialog = false },
                onSelectTheme = { theme: String -> viewModel.setThemeMode(theme) },
                onToggleLiabilities = { viewModel.setShowLiabilities(it) },
                onSignInGoogle = { googleSignInLauncher.launch(viewModel.driveSyncManager.getSignInIntent()) },
                onSwitchAccount = {
                    showSettingsDialog = false
                    showSwitchAccountWarningDialog = true
                },
                onBackupNow = { viewModel.performCloudBackup() },
                onRestoreNow = { viewModel.performCloudRestore() },
                onOpenDateRangeExport = {
                    showSettingsDialog = false
                    showDateRangeExportDialog = true
                },
                onOpenKeywordExport = {
                    showSettingsDialog = false
                    showKeywordExportDialog = true
                },
                onOpenChangePin = {
                    showSettingsDialog = false
                    showChangePinDialog = true
                }
            )
        }

        // 5. Account Switch Warning Dialog
        if (showSwitchAccountWarningDialog) {
            SwitchAccountWarningDialog(
                currentEmail = uiState.googleAccountEmail ?: "Google Account",
                onDismiss = { showSwitchAccountWarningDialog = false },
                onConfirmSwitch = {
                    showSwitchAccountWarningDialog = false
                    viewModel.performSwitchAccount {
                        googleSignInLauncher.launch(viewModel.driveSyncManager.getSignInIntent())
                    }
                }
            )
        }

        // 6. 4-Way Account Sync / Recovery Dialog
        if (showSyncChoice) {
            SyncConflictChoiceDialog(
                onMerge = { viewModel.resolveSyncChoiceMerge() },
                onKeepCloud = { viewModel.resolveSyncChoiceReplaceLocal() },
                onKeepLocal = { viewModel.resolveSyncChoiceDiscardCloud() },
                onDecideLater = { viewModel.dismissSyncChoiceDialog() }
            )
        }

        // 7. Export by Date Range Dialog
        if (showDateRangeExportDialog) {
            DateRangeExportDialog(
                onDismiss = { showDateRangeExportDialog = false },
                onExport = { startMs, endMs, includeLiab ->
                    val text = viewModel.buildExportText(startMs, endMs, includeLiab)
                    val uri = FileExportUtil.saveTextToDownloads(context, "RushuFin_Export_${System.currentTimeMillis()}.txt", text)
                    if (uri != null) {
                        Toast.makeText(context, "Export saved to Downloads folder!", Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(context, "Failed to save file.", Toast.LENGTH_SHORT).show()
                    }
                    showDateRangeExportDialog = false
                }
            )
        }

        // 8. Export by Keyword Dialog
        if (showKeywordExportDialog) {
            KeywordExportDialog(
                onDismiss = { showKeywordExportDialog = false },
                onExport = { keywords, startMs, endMs ->
                    val text = viewModel.buildKeywordExportText(keywords, startMs, endMs)
                    val uri = FileExportUtil.saveTextToDownloads(context, "RushuFin_Keyword_Export_${System.currentTimeMillis()}.txt", text)
                    if (uri != null) {
                        Toast.makeText(context, "Export saved to Downloads folder!", Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(context, "Failed to save file.", Toast.LENGTH_SHORT).show()
                    }
                    showKeywordExportDialog = false
                }
            )
        }

        // 9. Change Passwords Dialog (Tier 1 & Tier 2)
        if (showChangePinDialog) {
            ChangePasswordsDialog(
                currentTier1 = uiState.tier1Password,
                currentTier2 = uiState.tier2Password,
                onDismiss = { showChangePinDialog = false },
                onConfirm = { newT1, newT2 ->
                    viewModel.updatePasswords(newT1, newT2) {
                        showChangePinDialog = false
                    }
                }
            )
        }

        editingTransaction?.let { tx: TransactionEntity ->
            EditTransactionDialog(
                transaction = tx,
                currencySymbol = uiState.currencySymbol,
                onDismiss = { editingTransaction = null },
                onConfirm = { updated: TransactionEntity ->
                    viewModel.updateTransactionWithAdmin(updated) { editingTransaction = null }
                }
            )
        }

        editingLiability?.let { liability: LiabilityEntity ->
            EditLiabilityDialog(
                liability = liability,
                currencySymbol = uiState.currencySymbol,
                onDismiss = { editingLiability = null },
                onConfirm = { updated: LiabilityEntity ->
                    viewModel.updateLiabilityWithAdmin(updated) { editingLiability = null }
                }
            )
        }
    }
}

@Composable
fun AppHeader(
    isAdminMode: Boolean,
    googleEmail: String?,
    onAdminToggleClick: () -> Unit,
    onSettingsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val theme = LocalAppTheme.current
    val isHinata = theme == "HINATA"
    val isKakashi = theme == "KAKASHI"
    val isBumblebee = theme == "BUMBLEBEE"
    val isRuh = theme == "RUH"

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .border(
                        1.5.dp,
                        when {
                            isRuh -> RuhCardBorder
                            isBumblebee -> BumblebeeCardBorder
                            isKakashi -> KakashiCardBorder
                            isHinata -> HinataPurpleLight
                            else -> NeonCyan
                        },
                        CircleShape
                    )
                    .background(
                        when {
                            isRuh -> RuhSurface
                            isBumblebee -> BumblebeeSurface
                            isKakashi -> KakashiSurface
                            isHinata -> HinataSurface
                            else -> Color(0xFF101014)
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.rushu_fin_logo),
                    contentDescription = "RUSHU FIN Logo",
                    modifier = Modifier.fillMaxSize()
                )
            }

            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "RUSHU FIN",
                        style = neonTextStyle(
                            when {
                                isRuh -> RuhBloodRed
                                isBumblebee -> BumblebeeGold
                                isKakashi -> KakashiBalanceBlue
                                isHinata -> HinataPurpleLight
                                else -> NeonCyan
                            },
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold,
                            glowRadius = 16f
                        )
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = when {
                            isRuh -> "🩸"
                            isBumblebee -> "🏎️"
                            isKakashi -> "⚡"
                            isHinata -> "🪷"
                            else -> ""
                        },
                        fontSize = 16.sp
                    )
                }

                // Subtitle: MADE BY RUH, WITH LOVE ❤
                Text(
                    text = "MADE BY RUH, WITH LOVE ❤",
                    color = when {
                        isRuh -> Color(0xFFFF8A80)
                        isBumblebee -> Color(0xFFFBBF24)
                        isKakashi -> Color(0xFF93C5FD)
                        isHinata -> Color(0xFFF472B6)
                        else -> NeonCyan
                    },
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )
            }
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Surface(
                onClick = onAdminToggleClick,
                shape = RoundedCornerShape(20.dp),
                color = if (isAdminMode) NeonRed.copy(alpha = 0.2f) else CardGlass,
                border = androidx.compose.foundation.BorderStroke(1.dp, if (isAdminMode) NeonRed else CardGlassBorder)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = if (isAdminMode) Icons.Default.LockOpen else Icons.Default.Lock,
                        contentDescription = null,
                        tint = if (isAdminMode) NeonRed else TextMuted,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = if (isAdminMode) "ADMIN ON" else "TIER 2",
                        color = if (isAdminMode) NeonRed else TextSecondary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            IconButton(
                onClick = onSettingsClick,
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(CardGlass)
                    .border(1.dp, CardGlassBorder, CircleShape)
            ) {
                Icon(
                    imageVector = if (googleEmail != null) Icons.Default.CloudDone else Icons.Default.Settings,
                    contentDescription = "Settings",
                    tint = if (googleEmail != null) NeonGreen else TextSecondary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

/**
 * Settings Dialog with All Themes, Exports, Password Management & Cloud Sync
 */
@Composable
fun SettingsDialog(
    uiState: FinanceUiState,
    onDismiss: () -> Unit,
    onSelectTheme: (String) -> Unit,
    onToggleLiabilities: (Boolean) -> Unit,
    onSignInGoogle: () -> Unit,
    onSwitchAccount: () -> Unit,
    onBackupNow: () -> Unit,
    onRestoreNow: () -> Unit,
    onOpenDateRangeExport: () -> Unit,
    onOpenKeywordExport: () -> Unit,
    onOpenChangePin: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = SurfaceDark,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Icon(Icons.Default.Settings, contentDescription = null, tint = NeonCyan)
                Text(text = "Settings & Preferences", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // 1. SCROLLABLE THEMES SELECTION (All 6 Themes including Neon Cyberpunk)
                GlassBox(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp)) {
                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(text = "SELECT APP THEME", color = NeonCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold)

                        // 🌐 NEON CYBERPUNK (DEFAULT)
                        ThemeOptionRow(
                            title = "🌐 Neon Cyberpunk",
                            description = "Electric cyan, neon green, and deep midnight dark",
                            selected = uiState.themeMode == "DEFAULT",
                            onClick = { onSelectTheme("DEFAULT") }
                        )

                        // 🩸 RUH
                        ThemeOptionRow(
                            title = "🩸 Ruh",
                            description = "Pitch black, blood red border, Sharingan & Ꮢᴜʜ᭓Ꮢɪᴅɛʀ",
                            selected = uiState.themeMode == "RUH",
                            onClick = { onSelectTheme("RUH") }
                        )

                        // 🏎️ BUMBLEBEE
                        ThemeOptionRow(
                            title = "🏎️ Bumblebee",
                            description = "Metallic amber car body, flames & pitch black",
                            selected = uiState.themeMode == "BUMBLEBEE",
                            onClick = { onSelectTheme("BUMBLEBEE") }
                        )

                        // ⚡ KAKASHI
                        ThemeOptionRow(
                            title = "⚡ Kakashi",
                            description = "Chidori electric blue, Sharingan red & deep obsidian",
                            selected = uiState.themeMode == "KAKASHI",
                            onClick = { onSelectTheme("KAKASHI") }
                        )

                        // 🪷 HINATA
                        ThemeOptionRow(
                            title = "🪷 Hinata",
                            description = "Falling petals, lavender glow & Byakugan mint green",
                            selected = uiState.themeMode == "HINATA",
                            onClick = { onSelectTheme("HINATA") }
                        )

                        // 📄 BASIC (LARGE TEXT)
                        ThemeOptionRow(
                            title = "📄 Basic (Large Text)",
                            description = "Clean large text high legibility theme",
                            selected = uiState.themeMode == "BASIC",
                            onClick = { onSelectTheme("BASIC") }
                        )
                    }
                }

                // 2. LIABILITIES TOGGLE
                GlassBox(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp)) {
                    Row(
                        modifier = Modifier.padding(12.dp).fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Show Liabilities", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                            Text("Track debts independently", color = TextSecondary, fontSize = 10.sp)
                        }
                        Switch(
                            checked = uiState.showLiabilities,
                            onCheckedChange = onToggleLiabilities,
                            colors = SwitchDefaults.colors(checkedTrackColor = NeonCyan)
                        )
                    }
                }

                // 3. GOOGLE DRIVE BACKUP & ACCOUNT SWITCHING
                GlassBox(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp)) {
                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(text = "GOOGLE DRIVE BACKUP", color = NeonCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Text(
                            text = if (uiState.googleAccountEmail != null) "Connected:\n${uiState.googleAccountEmail}" else "Link your Google account to auto-backup.",
                            color = TextSecondary,
                            fontSize = 11.sp
                        )

                        if (uiState.googleAccountEmail == null) {
                            Button(
                                onClick = onSignInGoogle,
                                colors = ButtonDefaults.buttonColors(containerColor = NeonCyan),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("LINK ACCOUNT", color = CanvasBackground, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        } else {
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Button(
                                    onClick = onBackupNow,
                                    colors = ButtonDefaults.buttonColors(containerColor = NeonCyan),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("BACKUP", color = CanvasBackground, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                }
                                Button(
                                    onClick = onRestoreNow,
                                    colors = ButtonDefaults.buttonColors(containerColor = CardGlass),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, NeonCyan),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("RESTORE", color = NeonCyan, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                }
                            }

                            OutlinedButton(
                                onClick = onSwitchAccount,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(Icons.Default.SwitchAccount, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Switch / Change Google Account", fontSize = 11.sp)
                            }
                        }
                    }
                }

                // 4. DATA EXPORTS (.TXT FILES)
                GlassBox(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp)) {
                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(text = "DATA EXPORTS (.TXT FILES)", color = NeonCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold)

                        OutlinedButton(
                            onClick = onOpenDateRangeExport,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.DateRange, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Export by Date Range", fontSize = 11.sp)
                        }

                        OutlinedButton(
                            onClick = onOpenKeywordExport,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Export Categorized by Keyword", fontSize = 11.sp)
                        }
                    }
                }

                // 5. SECURITY & PASSWORDS
                GlassBox(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp)) {
                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(text = "SECURITY PASSWORDS", color = NeonCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold)

                        OutlinedButton(
                            onClick = onOpenChangePin,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Change Tier 1 / Tier 2 Passwords", fontSize = 11.sp)
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = onDismiss, colors = ButtonDefaults.buttonColors(containerColor = NeonCyan)) {
                Text("DONE", color = CanvasBackground, fontWeight = FontWeight.Bold)
            }
        }
    )
}

@Composable
fun ThemeOptionRow(
    title: String,
    description: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(if (selected) CardGlass else Color.Transparent)
            .border(
                width = 1.dp,
                color = if (selected) NeonCyan.copy(alpha = 0.6f) else CardGlassBorder,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = if (selected) Icons.Default.CheckCircle else Icons.Default.Circle,
            contentDescription = null,
            tint = if (selected) NeonCyan else TextMuted,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Column {
            Text(title, color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
            Text(description, color = TextSecondary, fontSize = 10.sp)
        }
    }
}

/**
 * Explicit Account Switch Warning Dialog
 * Alerts user that local storage will be cleared and reset to 0, backed up first,
 * and allows switching to another Google Account to restore its backup.
 */
@Composable
fun SwitchAccountWarningDialog(
    currentEmail: String,
    onDismiss: () -> Unit,
    onConfirmSwitch: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = SurfaceDark,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Icon(Icons.Default.Warning, contentDescription = null, tint = NeonYellow)
                Text("Switch Google Account", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 15.sp)
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "Current account: $currentEmail",
                    color = NeonCyan,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "1. Your current data will be safely backed up to Google Drive first.\n\n" +
                            "2. Local storage will be reset to ₹0 (cleared) so your device starts on a clean slate.\n\n" +
                            "3. You will choose your new Google Account, and then you can restore that account's cloud backup.",
                    color = TextSecondary,
                    fontSize = 12.sp,
                    lineHeight = 18.sp
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirmSwitch,
                colors = ButtonDefaults.buttonColors(containerColor = NeonRed)
            ) {
                Text("YES, BACKUP & SWITCH", color = CanvasBackground, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("CANCEL", color = TextSecondary)
            }
        }
    )
}

/**
 * 4-Way Account Switching Dialog (Merge, Cloud, Local, Decide Later)
 */
@Composable
fun SyncConflictChoiceDialog(
    onMerge: () -> Unit,
    onKeepCloud: () -> Unit,
    onKeepLocal: () -> Unit,
    onDecideLater: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDecideLater,
        containerColor = SurfaceDark,
        title = {
            Text("Sync Account Data", color = NeonCyan, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    "You just linked a Google account. How would you like to handle your data?",
                    color = TextPrimary,
                    fontSize = 12.sp
                )

                Button(
                    onClick = onMerge,
                    colors = ButtonDefaults.buttonColors(containerColor = NeonGreen),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("1. MERGE BOTH (Recommended)", color = CanvasBackground, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                }

                Button(
                    onClick = onKeepCloud,
                    colors = ButtonDefaults.buttonColors(containerColor = NeonCyan),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("2. KEEP CLOUD (Replace Local)", color = CanvasBackground, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                }

                Button(
                    onClick = onKeepLocal,
                    colors = ButtonDefaults.buttonColors(containerColor = NeonYellow),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("3. KEEP LOCAL (Overwrite Cloud)", color = CanvasBackground, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                }

                OutlinedButton(
                    onClick = onDecideLater,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("4. DECIDE LATER", color = TextSecondary, fontSize = 11.sp)
                }
            }
        },
        confirmButton = {}
    )
}

/**
 * Export by Date Range Dialog
 */
@Composable
fun DateRangeExportDialog(
    onDismiss: () -> Unit,
    onExport: (startTimestamp: Long?, endTimestamp: Long?, includeLiabilities: Boolean) -> Unit
) {
    var startDateText by remember { mutableStateOf("") }
    var endDateText by remember { mutableStateOf("") }
    var includeLiabilities by remember { mutableStateOf(true) }

    val sdf = remember { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = SurfaceDark,
        title = { Text("Export by Date Range", color = NeonCyan, fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("Enter dates in DD/MM/YYYY format or leave blank for all time.", color = TextSecondary, fontSize = 12.sp)
                OutlinedTextField(
                    value = startDateText,
                    onValueChange = { startDateText = it },
                    label = { Text("Start Date (DD/MM/YYYY)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = endDateText,
                    onValueChange = { endDateText = it },
                    label = { Text("End Date (DD/MM/YYYY)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = includeLiabilities, onCheckedChange = { includeLiabilities = it })
                    Text("Include Liabilities in report", color = TextPrimary, fontSize = 12.sp)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val startMs = try { if (startDateText.isNotBlank()) sdf.parse(startDateText.trim())?.time else null } catch (e: Exception) { null }
                    val endMs = try { if (endDateText.isNotBlank()) (sdf.parse(endDateText.trim())?.time?.plus(86400000L - 1L)) else null } catch (e: Exception) { null }
                    onExport(startMs, endMs, includeLiabilities)
                },
                colors = ButtonDefaults.buttonColors(containerColor = NeonCyan)
            ) {
                Text("DOWNLOAD TXT", color = CanvasBackground, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("CANCEL", color = TextSecondary) }
        }
    )
}

/**
 * Export by Keyword Dialog
 */
@Composable
fun KeywordExportDialog(
    onDismiss: () -> Unit,
    onExport: (keywords: List<String>, startTimestamp: Long?, endTimestamp: Long?) -> Unit
) {
    var keywordsText by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = SurfaceDark,
        title = { Text("Export Categorized by Keyword", color = NeonCyan, fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("Enter keywords separated by commas (e.g. Salary, Rent, Food, Travel).", color = TextSecondary, fontSize = 12.sp)
                OutlinedTextField(
                    value = keywordsText,
                    onValueChange = { keywordsText = it },
                    label = { Text("Keywords (comma separated)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val list = keywordsText.split(",").map { it.trim() }.filter { it.isNotEmpty() }
                    onExport(list, null, null)
                },
                colors = ButtonDefaults.buttonColors(containerColor = NeonCyan)
            ) {
                Text("DOWNLOAD TXT", color = CanvasBackground, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("CANCEL", color = TextSecondary) }
        }
    )
}

/**
 * Change Passwords Dialog (Tier 1 & Tier 2)
 */
@Composable
fun ChangePasswordsDialog(
    currentTier1: String,
    currentTier2: String,
    onDismiss: () -> Unit,
    onConfirm: (newTier1: String, newTier2: String) -> Unit
) {
    var tier1Text by remember { mutableStateOf(currentTier1) }
    var tier2Text by remember { mutableStateOf(currentTier2) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = SurfaceDark,
        title = { Text("Change Security Passwords", color = NeonCyan, fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = tier1Text,
                    onValueChange = { tier1Text = it },
                    label = { Text("Tier 1 Transaction PIN") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = tier2Text,
                    onValueChange = { tier2Text = it },
                    label = { Text("Tier 2 Master Password") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(tier1Text.trim(), tier2Text.trim()) },
                colors = ButtonDefaults.buttonColors(containerColor = NeonCyan)
            ) {
                Text("SAVE PASSWORDS", color = CanvasBackground, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("CANCEL", color = TextSecondary) }
        }
    )
}

/**
 * Reset Password Dialog via Google Recovery
 */
@Composable
fun SetNewPasswordDialog(
    onDismiss: () -> Unit,
    onConfirm: (newTier2Password: String) -> Unit
) {
    var newPwd by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = SurfaceDark,
        title = { Text("Reset Master Password", color = NeonCyan, fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("Your identity was verified with Google! Enter your new Tier 2 Master Password:", color = TextSecondary, fontSize = 12.sp)
                OutlinedTextField(
                    value = newPwd,
                    onValueChange = { newPwd = it },
                    label = { Text("New Master Password") },
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(newPwd.trim()) },
                colors = ButtonDefaults.buttonColors(containerColor = NeonCyan)
            ) {
                Text("SAVE PASSWORD", color = CanvasBackground, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("CANCEL", color = TextSecondary) }
        }
    )
}

@Composable
fun AdminUnlockDialog(
    onDismiss: () -> Unit,
    onUnlock: (pin: String) -> Unit,
    googleAccountLinked: Boolean,
    onForgotPassword: () -> Unit
) {
    var pinText by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = SurfaceDark,
        title = { Text(text = "Tier 2 Master Access", color = TextPrimary, fontWeight = FontWeight.Bold) },
        text = {
            Column {
                Text("Enter Master Password (Default: 9999) to unlock Admin Mode.", color = TextSecondary, fontSize = 13.sp)
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = pinText,
                    onValueChange = { pinText = it },
                    label = { Text("Master Password") },
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                if (googleAccountLinked) {
                    Spacer(modifier = Modifier.height(8.dp))
                    TextButton(onClick = onForgotPassword, modifier = Modifier.align(Alignment.End)) {
                        Text("Forgot Password? Reset via Google", color = NeonCyan, fontSize = 11.sp)
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onUnlock(pinText) },
                colors = ButtonDefaults.buttonColors(containerColor = NeonRed)
            ) {
                Text("UNLOCK", color = CanvasBackground, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("CANCEL", color = TextSecondary) }
        }
    )
}

@Composable
fun EditInitialBalanceDialog(
    currentBalance: Double,
    currencySymbol: String,
    onDismiss: () -> Unit,
    onConfirm: (Double) -> Unit
) {
    var balanceText by remember { mutableStateOf(currentBalance.toString()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = SurfaceDark,
        title = { Text(text = "Edit Fixed Starting Balance", color = NeonYellow, fontWeight = FontWeight.Bold) },
        text = {
            Column {
                Text("Changes apply to Main Live Balance immediately.", color = TextSecondary, fontSize = 13.sp)
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = balanceText,
                    onValueChange = { balanceText = it },
                    label = { Text("Starting Funds ($currencySymbol)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val bal = balanceText.toDoubleOrNull() ?: currentBalance
                    onConfirm(bal)
                },
                colors = ButtonDefaults.buttonColors(containerColor = NeonYellow)
            ) {
                Text("UPDATE", color = CanvasBackground, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("CANCEL", color = TextSecondary) }
        }
    )
}

@Composable
fun EditTransactionDialog(
    transaction: TransactionEntity,
    currencySymbol: String,
    onDismiss: () -> Unit,
    onConfirm: (TransactionEntity) -> Unit
) {
    var amountText by remember { mutableStateOf(transaction.amount.toString()) }
    var descriptionText by remember { mutableStateOf(transaction.description) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = SurfaceDark,
        title = { Text(text = "Edit Transaction (Admin)", color = NeonCyan, fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = amountText,
                    onValueChange = { amountText = it },
                    label = { Text("Amount ($currencySymbol)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = descriptionText,
                    onValueChange = { descriptionText = it },
                    label = { Text("Description") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val amt = amountText.toDoubleOrNull() ?: transaction.amount
                    onConfirm(transaction.copy(amount = amt, description = descriptionText))
                },
                colors = ButtonDefaults.buttonColors(containerColor = NeonCyan)
            ) {
                Text("SAVE", color = CanvasBackground, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("CANCEL", color = TextSecondary) }
        }
    )
}

@Composable
fun EditLiabilityDialog(
    liability: LiabilityEntity,
    currencySymbol: String,
    onDismiss: () -> Unit,
    onConfirm: (LiabilityEntity) -> Unit
) {
    var amountText by remember { mutableStateOf(liability.amount.toString()) }
    var descriptionText by remember { mutableStateOf(liability.description) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = SurfaceDark,
        title = { Text(text = "Edit Liability (Admin)", color = NeonRed, fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = amountText,
                    onValueChange = { amountText = it },
                    label = { Text("Amount ($currencySymbol)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = descriptionText,
                    onValueChange = { descriptionText = it },
                    label = { Text("Creditor Note / Description") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val amt = amountText.toDoubleOrNull() ?: liability.amount
                    onConfirm(liability.copy(amount = amt, description = descriptionText))
                },
                colors = ButtonDefaults.buttonColors(containerColor = NeonRed)
            ) {
                Text("SAVE", color = CanvasBackground, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("CANCEL", color = TextSecondary) }
        }
    )
}

@Composable
fun SplitBalanceRow(
    uiState: FinanceUiState,
    isAdminMode: Boolean,
    showLiabilities: Boolean,
    onEditInitialBalance: () -> Unit,
    modifier: Modifier = Modifier
) {
    val theme = LocalAppTheme.current
    val isHinata = theme == "HINATA"
    val isKakashi = theme == "KAKASHI"
    val isBumblebee = theme == "BUMBLEBEE"
    val isRuh = theme == "RUH"
    val isDebtFree = uiState.currentLiability <= 0.001

    val liabilityGlow = when {
        isRuh -> if (isDebtFree) RuhCardGlow else Color(0x66B91C1C)
        isBumblebee -> if (isDebtFree) BumblebeeCardGlow else Color(0x66F97316)
        isKakashi -> if (isDebtFree) KakashiCardGlow else Color(0x66FF334B)
        isHinata -> HinataCardGlow
        else -> if (isDebtFree) NeonGreenGlow else NeonRedGlow
    }

    val liabilityNumColor = when {
        isRuh -> if (isDebtFree) RuhBloodRed else RuhExpenseRed
        isBumblebee -> if (isDebtFree) BumblebeeGold else BumblebeeFlameOrange
        isKakashi -> if (isDebtFree) KakashiBalanceBlue else KakashiSharinganRed
        isHinata -> RoseDeficit
        else -> if (isDebtFree) NeonGreen else NeonRed
    }

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        GlassBox(
            modifier = (if (showLiabilities) Modifier.weight(1f) else Modifier.fillMaxWidth())
                .height(118.dp)
                .clickable { onEditInitialBalance() },
            shape = RoundedCornerShape(20.dp),
            glowColor = when {
                isRuh -> RuhCardGlow
                isBumblebee -> BumblebeeCardGlow
                isKakashi -> KakashiCardGlow
                isHinata -> HinataCardGlow
                else -> NeonYellow
            },
            glowIntensity = 0.4f
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(14.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "FIXED INITIAL",
                        style = neonTextStyle(
                            when {
                                isRuh -> Color(0xFFFFCDD2)
                                isBumblebee -> Color(0xFFFDE68A)
                                isKakashi -> Color(0xFFBAE6FD)
                                isHinata -> HinataGold
                                else -> NeonYellow
                            },
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            glowRadius = 10f
                        )
                    )
                    Icon(
                        imageVector = if (isAdminMode) Icons.Default.Edit else Icons.Default.Lock,
                        contentDescription = null,
                        tint = if (isAdminMode) NeonYellow else TextMuted,
                        modifier = Modifier.size(15.dp)
                    )
                }

                Text(
                    text = "${uiState.currencySymbol} ${indianNumber(uiState.initialBalance)}",
                    style = neonTextStyle(
                        when {
                            isRuh -> RuhFixedRed
                            isBumblebee -> BumblebeeFixedGold
                            isKakashi -> KakashiFixedBlue
                            isHinata -> HinataGold
                            else -> NeonYellow
                        },
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold,
                        glowRadius = 14f
                    )
                )

                Text(
                    text = if (isAdminMode) "Tap to edit (Admin)" else "Tier 2 protected",
                    color = when {
                        isRuh -> Color(0xFF991B1B)
                        isBumblebee -> Color(0xFF92400E)
                        isKakashi -> Color(0xFF64748B)
                        isHinata -> HinataPurpleMuted.copy(alpha = 0.7f)
                        else -> TextMuted
                    },
                    fontSize = 10.sp
                )
            }
        }

        if (showLiabilities) {
            GlassBox(
                modifier = Modifier
                    .weight(1f)
                    .height(118.dp),
                shape = RoundedCornerShape(20.dp),
                glowColor = liabilityGlow,
                glowIntensity = 0.45f
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(14.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "LIABILITIES",
                            style = neonTextStyle(
                                liabilityNumColor,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                glowRadius = 10f
                            )
                        )
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = when {
                                isRuh -> if (isDebtFree) RuhPillBg else Color(0xFF450A0A)
                                isBumblebee -> if (isDebtFree) BumblebeePillBg else Color(0xFF3B1203)
                                isKakashi -> if (isDebtFree) KakashiPillBg else Color(0xFF26050A)
                                else -> if (isDebtFree) NeonGreen.copy(alpha = 0.2f) else NeonRed.copy(alpha = 0.2f)
                            }
                        ) {
                            Text(
                                text = if (isDebtFree) "CLEAR" else "DEBT",
                                color = liabilityNumColor,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Text(
                        text = "${uiState.currencySymbol} ${indianNumber(uiState.currentLiability)}",
                        style = neonTextStyle(
                            liabilityNumColor,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold,
                            glowRadius = 14f
                        )
                    )

                    Text(
                        text = "Tracked independently",
                        color = when {
                            isRuh -> Color(0xFF991B1B)
                            isBumblebee -> Color(0xFF92400E)
                            isKakashi -> Color(0xFF64748B)
                            else -> TextMuted
                        },
                        fontSize = 10.sp
                    )
                }
            }
        }
    }
}

@Composable
fun NormalTransactionModule(
    currencySymbol: String,
    onRecordTransaction: (amount: Double, type: String, description: String, pin: String, onDone: () -> Unit) -> Unit,
    modifier: Modifier = Modifier
) {
    val theme = LocalAppTheme.current
    val isRuh = theme == "RUH"
    val isBumblebee = theme == "BUMBLEBEE"
    val isKakashi = theme == "KAKASHI"

    var selectedType by remember { mutableStateOf("INCOME") }
    var amountText by remember { mutableStateOf("") }
    var descriptionText by remember { mutableStateOf("") }
    var tier1PinText by remember { mutableStateOf("") }

    val isIncome = selectedType == "INCOME"

    GlassBox(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        glowColor = when {
            isRuh -> RuhCardGlow
            isBumblebee -> BumblebeeCardGlow
            isKakashi -> KakashiCardGlow
            else -> if (isIncome) NeonGreenGlow else NeonRedGlow
        },
        glowIntensity = 0.35f
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "RECORD TRANSACTION",
                    style = neonTextStyle(
                        when {
                            isRuh -> RuhBloodRed
                            isBumblebee -> BumblebeeGold
                            isKakashi -> Color.White
                            else -> NeonCyan
                        },
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        glowRadius = 10f
                    )
                )
                Text(
                    text = "Main Financial Ledger",
                    color = when {
                        isRuh -> Color(0xFFE57373)
                        isBumblebee -> Color(0xFFD97706)
                        isKakashi -> Color(0xFF8FA3BF)
                        else -> TextMuted
                    },
                    fontSize = 11.sp
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(SurfaceDark)
                    .border(1.dp, CardGlassBorder, RoundedCornerShape(12.dp))
                    .padding(3.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(
                            if (isIncome) {
                                when {
                                    isRuh -> RuhPillBg
                                    isBumblebee -> BumblebeePillBg
                                    isKakashi -> KakashiPillBg
                                    else -> NeonGreen.copy(alpha = 0.25f)
                                }
                            } else Color.Transparent
                        )
                        .clickable { selectedType = "INCOME" }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "+ive INCOME",
                        style = neonTextStyle(
                            if (isIncome) {
                                when {
                                    isRuh -> RuhIncomeRed
                                    isBumblebee -> BumblebeeIncomeGold
                                    isKakashi -> KakashiIncomeBlue
                                    else -> NeonGreen
                                }
                            } else TextSecondary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            glowRadius = 8f
                        )
                    )
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(
                            if (!isIncome) {
                                when {
                                    isRuh -> Color(0xFF3B070E)
                                    isBumblebee -> Color(0xFF3B1203)
                                    isKakashi -> Color(0xFF26050A)
                                    else -> NeonRed.copy(alpha = 0.25f)
                                }
                            } else Color.Transparent
                        )
                        .clickable { selectedType = "EXPENSE" }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "-ive EXPENSE",
                        style = neonTextStyle(
                            if (!isIncome) {
                                when {
                                    isRuh -> RuhExpenseRed
                                    isBumblebee -> BumblebeeFlameOrange
                                    isKakashi -> KakashiSharinganRed
                                    else -> NeonRed
                                }
                            } else TextSecondary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            glowRadius = 8f
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            OutlinedTextField(
                value = amountText,
                onValueChange = { amountText = it },
                label = { Text("Amount ($currencySymbol)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal, imeAction = ImeAction.Next),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = descriptionText,
                onValueChange = { descriptionText = it },
                label = { Text("Description") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = tier1PinText,
                onValueChange = { tier1PinText = it },
                label = { Text("Tier 1 PIN (Default: 1234)") },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword, imeAction = ImeAction.Done),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    val amt = amountText.toDoubleOrNull() ?: 0.0
                    onRecordTransaction(amt, selectedType, descriptionText, tier1PinText) {
                        amountText = ""
                        descriptionText = ""
                        tier1PinText = ""
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = when {
                        isRuh -> RuhButtonBg
                        isBumblebee -> BumblebeeButtonBg
                        isKakashi -> KakashiButtonBg
                        else -> if (isIncome) NeonGreen else NeonRed
                    }
                ),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Text(
                    text = if (isIncome) "RECORD INCOME (+)" else "RECORD EXPENSE (-)",
                    color = when {
                        isRuh -> RuhIncomeRed
                        isBumblebee -> BumblebeeGold
                        isKakashi -> KakashiIncomeBlue
                        else -> CanvasBackground
                    },
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Composable
fun LiabilityManagementModule(
    currencySymbol: String,
    onRecordLiability: (amount: Double, actionType: String, description: String, pin: String, onDone: () -> Unit) -> Unit,
    modifier: Modifier = Modifier
) {
    val theme = LocalAppTheme.current
    val isRuh = theme == "RUH"
    val isBumblebee = theme == "BUMBLEBEE"
    val isKakashi = theme == "KAKASHI"

    var selectedAction by remember { mutableStateOf("ADD_LIABILITY") }
    var amountText by remember { mutableStateOf("") }
    var descriptionText by remember { mutableStateOf("") }
    var tier1PinText by remember { mutableStateOf("") }

    val isAdding = selectedAction == "ADD_LIABILITY"

    GlassBox(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        glowColor = when {
            isRuh -> RuhCardGlow
            isBumblebee -> BumblebeeCardGlow
            isKakashi -> KakashiCardGlow
            else -> if (isAdding) NeonRedGlow else NeonCyanGlow
        },
        glowIntensity = 0.35f
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            Text(
                text = "INDEPENDENT LIABILITY MODULE",
                style = neonTextStyle(
                    when {
                        isRuh -> RuhBloodRed
                        isBumblebee -> BumblebeeGold
                        isKakashi -> Color.White
                        else -> if (isAdding) NeonRed else NeonCyan
                    },
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    glowRadius = 10f
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(SurfaceDark)
                    .border(1.dp, CardGlassBorder, RoundedCornerShape(12.dp))
                    .padding(3.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isAdding) (if (isRuh) Color(0xFF450A0A) else NeonRed.copy(alpha = 0.25f)) else Color.Transparent)
                        .clickable { selectedAction = "ADD_LIABILITY" }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "+ ADD DEBT",
                        style = neonTextStyle(
                            if (isAdding) (if (isRuh) RuhExpenseRed else NeonRed) else TextSecondary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            glowRadius = 8f
                        )
                    )
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (!isAdding) (if (isRuh) RuhPillBg else NeonCyan.copy(alpha = 0.25f)) else Color.Transparent)
                        .clickable { selectedAction = "PAY_LIABILITY" }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "- PAY DEBT",
                        style = neonTextStyle(
                            if (!isAdding) (if (isRuh) RuhIncomeRed else NeonCyan) else TextSecondary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            glowRadius = 8f
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            OutlinedTextField(
                value = amountText,
                onValueChange = { amountText = it },
                label = { Text("Liability Amount ($currencySymbol)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal, imeAction = ImeAction.Next),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = descriptionText,
                onValueChange = { descriptionText = it },
                label = { Text("Creditor Note / Description") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = tier1PinText,
                onValueChange = { tier1PinText = it },
                label = { Text("Tier 1 PIN (Default: 1234)") },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword, imeAction = ImeAction.Done),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    val amt = amountText.toDoubleOrNull() ?: 0.0
                    onRecordLiability(amt, selectedAction, descriptionText, tier1PinText) {
                        amountText = ""
                        descriptionText = ""
                        tier1PinText = ""
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = when {
                        isRuh -> RuhButtonBg
                        isBumblebee -> BumblebeeButtonBg
                        isKakashi -> KakashiButtonBg
                        else -> if (isAdding) NeonRed else NeonCyan
                    }
                ),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Text(
                    text = if (isAdding) "RECORD DEBT (+)" else "RECORD PAYMENT (-)",
                    color = when {
                        isRuh -> RuhBloodRed
                        isBumblebee -> BumblebeeGold
                        isKakashi -> KakashiBalanceBlue
                        else -> CanvasBackground
                    },
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Composable
fun AuditLedgersSection(
    transactions: List<TransactionEntity>,
    liabilities: List<LiabilityEntity>,
    currencySymbol: String,
    isAdminMode: Boolean,
    showLiabilities: Boolean,
    onEditTx: (TransactionEntity) -> Unit,
    onDeleteTx: (TransactionEntity) -> Unit,
    onEditLiability: (LiabilityEntity) -> Unit,
    onDeleteLiability: (LiabilityEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    val theme = LocalAppTheme.current
    val isRuh = theme == "RUH"
    val isBumblebee = theme == "BUMBLEBEE"
    val isKakashi = theme == "KAKASHI"

    var selectedTab by remember { mutableIntStateOf(0) }

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "PERMANENT AUDIT LEDGERS",
                style = neonTextStyle(
                    when {
                        isRuh -> RuhBloodRed
                        isBumblebee -> BumblebeeGold
                        isKakashi -> Color.White
                        else -> NeonCyan
                    },
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    glowRadius = 10f
                )
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        if (showLiabilities) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(SurfaceDark)
                    .border(1.dp, CardGlassBorder, RoundedCornerShape(12.dp))
                    .padding(4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (selectedTab == 0) CardGlass else Color.Transparent)
                        .clickable { selectedTab = 0 }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Main (${transactions.size})",
                        color = if (selectedTab == 0) (if (isRuh) RuhBloodRed else NeonGreen) else TextSecondary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (selectedTab == 1) CardGlass else Color.Transparent)
                        .clickable { selectedTab = 1 }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Liabilities (${liabilities.size})",
                        color = if (selectedTab == 1) (if (isRuh) RuhIncomeRed else NeonCyan) else TextSecondary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
        }

        val effectiveTab = if (showLiabilities) selectedTab else 0

        if (effectiveTab == 0) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                transactions.forEach { tx ->
                    TransactionItemCard(
                        transaction = tx,
                        currencySymbol = currencySymbol,
                        isAdminMode = isAdminMode,
                        onEdit = { onEditTx(tx) },
                        onDelete = { onDeleteTx(tx) }
                    )
                }
            }
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                liabilities.forEach { liability ->
                    LiabilityItemCard(
                        liability = liability,
                        currencySymbol = currencySymbol,
                        isAdminMode = isAdminMode,
                        onEdit = { onEditLiability(liability) },
                        onDelete = { onDeleteLiability(liability) }
                    )
                }
            }
        }
    }
}

@Composable
fun TransactionItemCard(
    transaction: TransactionEntity,
    currencySymbol: String,
    isAdminMode: Boolean,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val theme = LocalAppTheme.current
    val isRuh = theme == "RUH"
    val isIncome = transaction.type.equals("INCOME", ignoreCase = true)
    val amountColor = if (isIncome) {
        if (isRuh) RuhIncomeRed else NeonGreen
    } else {
        if (isRuh) RuhExpenseRed else NeonRed
    }

    GlassBox(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        glowColor = amountColor.copy(alpha = 0.25f),
        glowIntensity = 0.25f
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = transaction.description,
                    style = neonTextStyle(Color.White, fontSize = 14.sp, fontWeight = FontWeight.SemiBold),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "${transaction.dateString} • ${transaction.timeString}",
                    color = TextMuted,
                    fontSize = 11.sp
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "${if (isIncome) "+" else "-"}$currencySymbol ${indianNumber(transaction.amount)}",
                    style = neonTextStyle(amountColor, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                )

                if (isAdminMode) {
                    IconButton(onClick = onEdit, modifier = Modifier.size(30.dp)) {
                        Icon(Icons.Default.Edit, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(15.dp))
                    }
                    IconButton(onClick = onDelete, modifier = Modifier.size(30.dp)) {
                        Icon(Icons.Default.Delete, contentDescription = null, tint = NeonRed, modifier = Modifier.size(15.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun LiabilityItemCard(
    liability: LiabilityEntity,
    currencySymbol: String,
    isAdminMode: Boolean,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val theme = LocalAppTheme.current
    val isRuh = theme == "RUH"
    val isAdd = liability.actionType.equals("ADD_LIABILITY", ignoreCase = true)
    val amountColor = if (isAdd) {
        if (isRuh) RuhExpenseRed else NeonRed
    } else {
        if (isRuh) RuhIncomeRed else NeonCyan
    }

    GlassBox(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        glowColor = amountColor.copy(alpha = 0.25f),
        glowIntensity = 0.25f
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = liability.description,
                    style = neonTextStyle(Color.White, fontSize = 14.sp, fontWeight = FontWeight.SemiBold),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "${if (isAdd) "Added Debt" else "Payment"} • ${liability.dateString}",
                    color = TextMuted,
                    fontSize = 11.sp
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "${if (isAdd) "+" else "-"}$currencySymbol ${indianNumber(liability.amount)}",
                    style = neonTextStyle(amountColor, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                )

                if (isAdminMode) {
                    IconButton(onClick = onEdit, modifier = Modifier.size(30.dp)) {
                        Icon(Icons.Default.Edit, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(15.dp))
                    }
                    IconButton(onClick = onDelete, modifier = Modifier.size(30.dp)) {
                        Icon(Icons.Default.Delete, contentDescription = null, tint = NeonRed, modifier = Modifier.size(15.dp))
                    }
                }
            }
        }
    }
}