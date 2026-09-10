package com.example.ui.screens

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.R
import com.example.data.model.LiabilityEntity
import com.example.data.model.TransactionEntity
import com.example.ui.components.GlassBox
import com.example.ui.components.GlowingPayoffProgressBar
import com.example.ui.components.neonTextStyle
import com.example.ui.theme.CanvasBackground
import com.example.ui.theme.CardGlass
import com.example.ui.theme.CardGlassBorder
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonCyanGlow
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.NeonGreenGlow
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.NeonRed
import com.example.ui.theme.NeonRedGlow
import com.example.ui.theme.NeonYellow
import com.example.ui.theme.NeonYellowGlow
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.FinanceUiState
import com.example.ui.viewmodel.FinanceViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import com.example.util.indianNumber
import com.example.util.FileExportUtil
import com.example.ui.theme.AppTheme
import com.example.ui.theme.LocalAppTheme

@Composable
fun DashboardScreen(
    viewModel: FinanceViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val transactions by viewModel.transactions.collectAsStateWithLifecycle()
    val liabilities by viewModel.liabilities.collectAsStateWithLifecycle()
    val isAdminMode by viewModel.isAdminMode.collectAsStateWithLifecycle()
    val showSyncChoiceDialog by viewModel.showSyncChoiceDialog.collectAsStateWithLifecycle()
    val showSetNewPasswordDialog by viewModel.showSetNewPasswordDialog.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    var showExportDialog by remember { mutableStateOf(false) }
    var showCategoryExportDialog by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current

    // Dialog States
    var showAdminUnlockDialog by remember { mutableStateOf(false) }
    var showEditInitialBalanceDialog by remember { mutableStateOf(false) }
    var showSettingsDialog by remember { mutableStateOf(false) }
    var editingTransaction by remember { mutableStateOf<TransactionEntity?>(null) }
    var editingLiability by remember { mutableStateOf<LiabilityEntity?>(null) }

    // Google Sign-In Launcher
    val googleSignInLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.result
                if (account != null) {
                    viewModel.onGoogleSignInSuccess(account)
                }
            } catch (e: Exception) {
                scope.launch { snackbarHostState.showSnackbar("Sign in error: ${e.localizedMessage}") }
            }
        }
    }

    LaunchedEffect(Unit) {
        viewModel.userFeedback.collectLatest { msg ->
            snackbarHostState.showSnackbar(msg)
        }
    }

    // Separate launcher for Tier 2 password recovery re-authentication —
    // kept distinct from the normal link/switch-account launcher so its
    // result is checked against the already-linked account instead of
    // triggering the merge/replace/discard choice dialog.
    val passwordRecoveryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.result
                if (account != null) {
                    viewModel.onPasswordRecoveryReauthSuccess(account)
                }
            } catch (e: Exception) {
                scope.launch { snackbarHostState.showSnackbar("Verification error: ${e.localizedMessage}") }
            }
        }
    }

    val baseDensity = LocalDensity.current
    val scaledDensity = remember(uiState.themeMode, baseDensity) {
        Density(
            density = baseDensity.density,
            fontScale = if (uiState.themeMode == AppTheme.BASIC) baseDensity.fontScale * 1.35f else baseDensity.fontScale
        )
    }

    CompositionLocalProvider(
        LocalAppTheme provides uiState.themeMode,
        LocalDensity provides scaledDensity
    ) {

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
            // App Header Bar with Branding & Quick Actions
            item {
                AppHeader(
                    isAdminMode = isAdminMode,
                    googleEmail = uiState.googleAccountEmail,
                    lastSyncTime = uiState.lastSyncTime,
                    onAdminToggleClick = {
                        if (isAdminMode) {
                            viewModel.lockAdminMode()
                        } else {
                            showAdminUnlockDialog = true
                        }
                    },
                    onSettingsClick = { showSettingsDialog = true }
                )
            }

            // Top Section — Main Live Balance Box
            item {
                MainLiveBalanceCard(
                    uiState = uiState
                )
            }

            // Middle Section — Split Balance Row (Yellow Glass Fixed vs Dynamic Liability)
            item {
                SplitBalanceRow(
                    uiState = uiState,
                    isAdminMode = isAdminMode,
                    showLiabilities = uiState.showLiabilities,
                    onEditInitialBalance = {
                        if (isAdminMode) {
                            showEditInitialBalanceDialog = true
                        } else {
                            showAdminUnlockDialog = true
                        }
                    }
                )
            }

            // Dynamic Liability Payoff Progress Indicator
            if (uiState.showLiabilities) {
                item {
                    GlowingPayoffProgressBar(
                        currentLiability = uiState.currentLiability,
                        peakLiability = uiState.peakLiability,
                        currencySymbol = uiState.currencySymbol
                    )
                }
            }

            // Lower-Middle Section — Normal Income & Expense Transactions Input
            item {
                NormalTransactionModule(
                    currencySymbol = uiState.currencySymbol,
                    onRecordTransaction = { amount, type, desc, pin, onDone ->
                        viewModel.addTransaction(amount, type, desc, pin, onDone)
                    }
                )
            }

            // Bottom Section — Independent Liability Management Module
            if (uiState.showLiabilities) {
                item {
                    LiabilityManagementModule(
                        currencySymbol = uiState.currencySymbol,
                        onRecordLiability = { amount, actionType, desc, pin, onDone ->
                            viewModel.addLiability(amount, actionType, desc, pin, onDone)
                        }
                    )
                }
            }

            // Dual Permanent Ledgers (Main Financial Ledger & Independent Liability Ledger)
            item {
                AuditLedgersSection(
                    transactions = transactions,
                    liabilities = liabilities,
                    currencySymbol = uiState.currencySymbol,
                    isAdminMode = isAdminMode,
                    showLiabilities = uiState.showLiabilities,
                    onEditTx = { tx -> editingTransaction = tx },
                    onDeleteTx = { tx -> viewModel.deleteTransactionWithAdmin(tx) },
                    onEditLiability = { l -> editingLiability = l },
                    onDeleteLiability = { l -> viewModel.deleteLiabilityWithAdmin(l) }
                )
            }

            item {
                Spacer(modifier = Modifier.height(32.dp))
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 24.dp)
        )
    }

    // Dialogs
    if (showAdminUnlockDialog) {
        AdminUnlockDialog(
            onDismiss = { showAdminUnlockDialog = false },
            onUnlock = { pin ->
                if (viewModel.verifyAndUnlockAdminMode(pin)) {
                    showAdminUnlockDialog = false
                }
            },
            googleAccountLinked = uiState.googleAccountEmail != null,
            onForgotPassword = {
                if (uiState.googleAccountEmail != null) {
                    passwordRecoveryLauncher.launch(viewModel.driveSyncManager.getSignInIntent())
                } else {
                    scope.launch {
                        snackbarHostState.showSnackbar("Link a Google account first (Settings) to enable free password recovery.")
                    }
                }
            }
        )
    }

    if (showSetNewPasswordDialog) {
        SetNewTier2PasswordDialog(
            onDismiss = { viewModel.dismissSetNewPasswordDialog() },
            onConfirm = { newPassword -> viewModel.setNewTier2PasswordViaRecovery(newPassword) }
        )
    }

    if (showEditInitialBalanceDialog) {
        EditInitialBalanceDialog(
            currentBalance = uiState.initialBalance,
            currencySymbol = uiState.currencySymbol,
            onDismiss = { showEditInitialBalanceDialog = false },
            onConfirm = { newBal ->
                viewModel.updateInitialBalanceWithTier2(newBal) {
                    showEditInitialBalanceDialog = false
                }
            }
        )
    }

    if (showSettingsDialog) {
        SettingsAndCloudSyncDialog(
            uiState = uiState,
            onDismiss = { showSettingsDialog = false },
            onSignInGoogle = {
                googleSignInLauncher.launch(viewModel.driveSyncManager.getSignInIntent())
            },
            onBackupNow = { viewModel.performCloudBackup() },
            onRestoreNow = { viewModel.performCloudRestore() },
            onClearBackup = { viewModel.performClearCloudBackup() },
            onSwitchAccount = {
                viewModel.performSwitchAccount {
                    googleSignInLauncher.launch(viewModel.driveSyncManager.getSignInIntent())
                }
            },
            onChangePasswords = { t1, t2, onDone ->
                viewModel.updatePasswords(t1, t2, onDone)
            },
            onExportTransactions = {
                showSettingsDialog = false
                showExportDialog = true
            },
            onExportByCategory = {
                showSettingsDialog = false
                showCategoryExportDialog = true
            },
            onToggleShowLiabilities = { show -> viewModel.setShowLiabilities(show) },
            onSelectTheme = { mode -> viewModel.setThemeMode(mode) }
        )
    }

    if (showSyncChoiceDialog) {
        SyncChoiceDialog(
            onMerge = { viewModel.resolveSyncChoiceMerge() },
            onReplace = { viewModel.resolveSyncChoiceReplaceLocal() },
            onDiscardCloud = { viewModel.resolveSyncChoiceDiscardCloud() },
            onDismiss = { viewModel.dismissSyncChoiceDialog() }
        )
    }

    if (showExportDialog) {
        ExportTransactionsDialog(
            onDismiss = { showExportDialog = false },
            onExport = { startTimestamp, endTimestamp, includeLiabilities ->
                val exportText = viewModel.buildExportText(startTimestamp, endTimestamp, includeLiabilities)
                val fileName = "RushuFin_Export_${System.currentTimeMillis()}.txt"
                val uri = FileExportUtil.saveTextToDownloads(context, fileName, exportText)
                showExportDialog = false
                scope.launch {
                    if (uri != null) {
                        val result = snackbarHostState.showSnackbar(
                            message = "Saved to Downloads/$fileName",
                            actionLabel = "SHARE"
                        )
                        if (result == SnackbarResult.ActionPerformed) {
                            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_STREAM, uri)
                                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                            }
                            context.startActivity(Intent.createChooser(shareIntent, "Share transaction export"))
                        }
                    } else {
                        snackbarHostState.showSnackbar("Export failed. Please try again.")
                    }
                }
            }
        )
    }

    if (showCategoryExportDialog) {
        KeywordExportDialog(
            onDismiss = { showCategoryExportDialog = false },
            onExport = { keywords, startTimestamp, endTimestamp ->
                val exportText = viewModel.buildKeywordExportText(keywords, startTimestamp, endTimestamp)
                val fileName = "RushuFin_KeywordExport_${System.currentTimeMillis()}.txt"
                val uri = FileExportUtil.saveTextToDownloads(context, fileName, exportText)
                showCategoryExportDialog = false
                scope.launch {
                    if (uri != null) {
                        val result = snackbarHostState.showSnackbar(
                            message = "Saved to Downloads/$fileName",
                            actionLabel = "SHARE"
                        )
                        if (result == SnackbarResult.ActionPerformed) {
                            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_STREAM, uri)
                                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                            }
                            context.startActivity(Intent.createChooser(shareIntent, "Share keyword export"))
                        }
                    } else {
                        snackbarHostState.showSnackbar("Export failed. Please try again.")
                    }
                }
            }
        )
    }

    editingTransaction?.let { tx ->
        EditTransactionDialog(
            transaction = tx,
            currencySymbol = uiState.currencySymbol,
            onDismiss = { editingTransaction = null },
            onConfirm = { updated ->
                viewModel.updateTransactionWithAdmin(updated) {
                    editingTransaction = null
                }
            }
        )
    }

    editingLiability?.let { liability ->
        EditLiabilityDialog(
            liability = liability,
            currencySymbol = uiState.currencySymbol,
            onDismiss = { editingLiability = null },
            onConfirm = { updated ->
                viewModel.updateLiabilityWithAdmin(updated) {
                    editingLiability = null
                }
            }
        )
    }
    } // end CompositionLocalProvider(LocalAppTheme, LocalDensity)
}

/**
 * App Header with Branding, Admin Mode status, and Cloud Sync toggle.
 */
@Composable
fun AppHeader(
    isAdminMode: Boolean,
    googleEmail: String?,
    lastSyncTime: Long,
    onAdminToggleClick: () -> Unit,
    onSettingsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // App Logo & Title
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .border(1.5.dp, NeonCyan, CircleShape)
                    .background(Color(0xFF101014)),
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
                        style = neonTextStyle(NeonCyan, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, glowRadius = 16f)
                    )
                    val themeAccentEmoji = when (LocalAppTheme.current) {
                        AppTheme.GOLDEN_HIVE -> "\uD83D\uDC1D"
                        AppTheme.SAKURA_BLOOM, AppTheme.MOONLIT_PURPLE -> "\uD83C\uDF38"
                        else -> null
                    }
                    if (themeAccentEmoji != null) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = themeAccentEmoji, fontSize = 16.sp)
                    }
                }
                Text(
                    text = "Personal Finance & Liability Tracking",
                    color = TextSecondary,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        // Action Buttons: Admin Mode Chip & Settings
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Admin Mode Chip
            Surface(
                onClick = onAdminToggleClick,
                shape = RoundedCornerShape(20.dp),
                color = if (isAdminMode) NeonRed.copy(alpha = 0.2f) else CardGlass,
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (isAdminMode) NeonRed else CardGlassBorder
                ),
                modifier = Modifier.testTag("admin_mode_toggle")
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

            // Settings & Google Drive Sync Button
            IconButton(
                onClick = onSettingsClick,
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(CardGlass)
                    .border(1.dp, CardGlassBorder, CircleShape)
                    .testTag("settings_button")
            ) {
                Icon(
                    imageVector = if (googleEmail != null) Icons.Default.CloudDone else Icons.Default.Settings,
                    contentDescription = "Settings & Cloud Sync",
                    tint = if (googleEmail != null) NeonGreen else TextSecondary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

/**
 * Top Section — Main Live Balance Box:
 * A large, prominent glassmorphic box spanning full width at the very top.
 * Features dynamic Green inner glow if balance >= 0, and Red inner glow if balance < 0.
 * Displays calculated live balance: Initial Fixed Amount + Income - Expenses.
 * Strictly non-editable directly.
 */
@Composable
fun MainLiveBalanceCard(
    uiState: FinanceUiState,
    modifier: Modifier = Modifier
) {
    val isPositive = uiState.liveBalance >= 0
    val glowColor = if (isPositive) NeonGreen else NeonRed
    val textColor = if (isPositive) NeonGreen else NeonRed

    GlassBox(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        glowColor = glowColor,
        glowIntensity = 0.55f
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "MAIN LIVE BALANCE",
                    style = neonTextStyle(glowColor, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, glowRadius = 10f)
                )
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = glowColor.copy(alpha = 0.15f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, glowColor.copy(alpha = 0.5f))
                ) {
                    Text(
                        text = if (isPositive) "SURPLUS" else "DEFICIT",
                        color = glowColor,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Calculated Live Net Balance
            Text(
                text = "${uiState.currencySymbol} ${indianNumber(uiState.liveBalance)}",
                style = neonTextStyle(textColor, fontSize = 36.sp, fontWeight = FontWeight.Black, glowRadius = 24f),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Non-editable calculation formula badge
            Text(
                text = "Formula: Initial (${uiState.currencySymbol}${uiState.initialBalance.toInt()}) + Income (${uiState.currencySymbol}${uiState.totalIncome.toInt()}) - Expenses (${uiState.currencySymbol}${uiState.totalExpense.toInt()})",
                color = TextSecondary,
                fontSize = 11.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            HorizontalDivider(color = CardGlassBorder.copy(alpha = 0.4f), thickness = 1.dp)

            Spacer(modifier = Modifier.height(12.dp))

            // Income and Expense breakdown row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(26.dp)
                            .clip(CircleShape)
                            .background(NeonGreen.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowUpward,
                            contentDescription = null,
                            tint = NeonGreen,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Column {
                        Text(text = "Total Income", color = TextMuted, fontSize = 10.sp)
                        Text(
                            text = "+${uiState.currencySymbol}${indianNumber(uiState.totalIncome)}",
                            color = NeonGreen,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(26.dp)
                            .clip(CircleShape)
                            .background(NeonRed.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowDownward,
                            contentDescription = null,
                            tint = NeonRed,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Column {
                        Text(text = "Total Expenses", color = TextMuted, fontSize = 10.sp)
                        Text(
                            text = "-${uiState.currencySymbol}${indianNumber(uiState.totalExpense)}",
                            color = NeonRed,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

/**
 * Middle Section — Split Balance Row:
 * Two equal-half glass boxes side-by-side:
 * Left Box (Yellow Glass - Fixed Initial Balance): Inner glowing yellow light, stores baseline starting funds.
 * Right Box (Dynamic Red/Green Glass - Liabilities): Red inner glow when liability > 0, turns Green once liability reaches 0.
 */
@Composable
fun SplitBalanceRow(
    uiState: FinanceUiState,
    isAdminMode: Boolean,
    showLiabilities: Boolean,
    onEditInitialBalance: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isDebtFree = uiState.currentLiability <= 0.001
    val liabilityGlow = if (isDebtFree) NeonGreen else NeonRed
    val liabilityText = if (isDebtFree) NeonGreen else NeonRed

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Left Box: Yellow Glass (Fixed Initial Balance)
        GlassBox(
            modifier = (if (showLiabilities) Modifier.weight(1f) else Modifier.fillMaxWidth())
                .clickable { onEditInitialBalance() }
                .testTag("fixed_initial_balance_card"),
            shape = RoundedCornerShape(20.dp),
            glowColor = NeonYellow,
            glowIntensity = 0.4f
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "FIXED INITIAL",
                        style = neonTextStyle(NeonYellow, fontSize = 11.sp, fontWeight = FontWeight.Bold, glowRadius = 10f)
                    )
                    Icon(
                        imageVector = if (isAdminMode) Icons.Default.Edit else Icons.Default.Lock,
                        contentDescription = "Edit with Tier 2",
                        tint = if (isAdminMode) NeonYellow else TextMuted,
                        modifier = Modifier.size(15.dp)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "${uiState.currencySymbol} ${indianNumber(uiState.initialBalance)}",
                    style = neonTextStyle(NeonYellow, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, glowRadius = 14f)
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = if (isAdminMode) "Tap to edit (Admin)" else "Tier 2 protected",
                    color = TextMuted,
                    fontSize = 10.sp
                )
            }
        }

        // Right Box: Dynamic Red/Green Glass (Interest & Liabilities) — hidden when liabilities are toggled off
        if (showLiabilities) {
        GlassBox(
            modifier = Modifier
                .weight(1f)
                .testTag("liabilities_card"),
            shape = RoundedCornerShape(20.dp),
            glowColor = liabilityGlow,
            glowIntensity = 0.45f
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "LIABILITIES",
                        style = neonTextStyle(liabilityText, fontSize = 11.sp, fontWeight = FontWeight.Bold, glowRadius = 10f)
                    )
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = liabilityGlow.copy(alpha = 0.2f),
                        border = androidx.compose.foundation.BorderStroke(0.5.dp, liabilityGlow.copy(alpha = 0.5f))
                    ) {
                        Text(
                            text = if (isDebtFree) "CLEAR" else "DEBT",
                            color = liabilityGlow,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "${uiState.currencySymbol} ${indianNumber(uiState.currentLiability)}",
                    style = neonTextStyle(liabilityText, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, glowRadius = 14f)
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Tracked independently",
                    color = TextMuted,
                    fontSize = 10.sp
                )
            }
        }
        }
    }
}

/**
 * Lower-Middle Section — Normal Income & Expense Transactions Input Card:
 * Partitioned input cards for +ive (Income) and -ive (Expense).
 * Requires both amount and mandatory short text description.
 * Requires Tier 1 Password to record.
 */
@Composable
fun NormalTransactionModule(
    currencySymbol: String,
    onRecordTransaction: (amount: Double, type: String, description: String, pin: String, onDone: () -> Unit) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedType by remember { mutableStateOf("INCOME") } // "INCOME" or "EXPENSE"
    var amountText by remember { mutableStateOf("") }
    var descriptionText by remember { mutableStateOf("") }
    var tier1PinText by remember { mutableStateOf("") }

    val isIncome = selectedType == "INCOME"
    val accentColor = if (isIncome) NeonGreen else NeonRed
    val accentGlow = if (isIncome) NeonGreenGlow else NeonRedGlow

    GlassBox(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        glowColor = accentColor.copy(alpha = 0.3f),
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
                    style = neonTextStyle(NeonCyan, fontSize = 13.sp, fontWeight = FontWeight.Bold, glowRadius = 10f)
                )
                Text(
                    text = "Main Financial Ledger",
                    color = TextMuted,
                    fontSize = 11.sp
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Switcher: +ive Income vs -ive Expense
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF0E0E12))
                    .border(1.dp, CardGlassBorder, RoundedCornerShape(12.dp))
                    .padding(3.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isIncome) NeonGreen.copy(alpha = 0.25f) else Color.Transparent)
                        .clickable { selectedType = "INCOME" }
                        .padding(vertical = 10.dp)
                        .testTag("tab_income"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "+ive INCOME",
                        style = neonTextStyle(if (isIncome) NeonGreen else TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.Bold, glowRadius = 8f)
                    )
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (!isIncome) NeonRed.copy(alpha = 0.25f) else Color.Transparent)
                        .clickable { selectedType = "EXPENSE" }
                        .padding(vertical = 10.dp)
                        .testTag("tab_expense"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "-ive EXPENSE",
                        style = neonTextStyle(if (!isIncome) NeonRed else TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.Bold, glowRadius = 8f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Amount Input
            OutlinedTextField(
                value = amountText,
                onValueChange = { amountText = it },
                label = { Text("Amount ($currencySymbol)") },
                placeholder = { Text("e.g. 500") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal, imeAction = ImeAction.Next),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = accentColor,
                    unfocusedBorderColor = CardGlassBorder,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    cursorColor = accentColor
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("transaction_amount_input")
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Description Input (Mandatory)
            OutlinedTextField(
                value = descriptionText,
                onValueChange = { descriptionText = it },
                label = { Text("Description (Mandatory)") },
                placeholder = { Text(if (isIncome) "e.g. Salary, Payment From Moni" else "e.g. bought Pen, Groceries") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text, imeAction = ImeAction.Next),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = accentColor,
                    unfocusedBorderColor = CardGlassBorder,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    cursorColor = accentColor
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("transaction_desc_input")
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Tier 1 PIN Input
            OutlinedTextField(
                value = tier1PinText,
                onValueChange = { tier1PinText = it },
                label = { Text("Tier 1 Password (Default: 1234)") },
                placeholder = { Text("Enter 4-digit PIN") },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword, imeAction = ImeAction.Done),
                singleLine = true,
                leadingIcon = {
                    Icon(Icons.Default.Security, contentDescription = null, tint = accentColor)
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = accentColor,
                    unfocusedBorderColor = CardGlassBorder,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    cursorColor = accentColor
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("transaction_tier1_pin_input")
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Submit Button
            Button(
                onClick = {
                    val amt = amountText.toDoubleOrNull() ?: 0.0
                    onRecordTransaction(amt, selectedType, descriptionText, tier1PinText) {
                        amountText = ""
                        descriptionText = ""
                        tier1PinText = ""
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = accentColor),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("submit_transaction_button")
            ) {
                Text(
                    text = if (isIncome) "RECORD INCOME (+)" else "RECORD EXPENSE (-)",
                    color = CanvasBackground,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
        }
    }
}

/**
 * Bottom Section — Independent Liability Management Module:
 * Sleekly separated from main transactions so it does NOT clutter the screen.
 * Allows adding new liabilities (increasing debt balance) or making liability payments/deductions (decreasing debt balance).
 * Requires a mandatory short text description and Tier 1 Password for every liability entry/payment.
 * CRITICAL LOGIC RULE: Liability balances and liability payments MUST be stored, tracked, and calculated entirely SEPARATELY.
 */
@Composable
fun LiabilityManagementModule(
    currencySymbol: String,
    onRecordLiability: (amount: Double, actionType: String, description: String, pin: String, onDone: () -> Unit) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedAction by remember { mutableStateOf("ADD_LIABILITY") } // "ADD_LIABILITY" or "PAY_LIABILITY"
    var amountText by remember { mutableStateOf("") }
    var descriptionText by remember { mutableStateOf("") }
    var tier1PinText by remember { mutableStateOf("") }

    val isAdding = selectedAction == "ADD_LIABILITY"
    val accentColor = if (isAdding) NeonRed else NeonCyan

    GlassBox(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        glowColor = accentColor.copy(alpha = 0.3f),
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
                Column {
                    Text(
                        text = "INDEPENDENT LIABILITY MODULE",
                        style = neonTextStyle(accentColor, fontSize = 13.sp, fontWeight = FontWeight.Bold, glowRadius = 10f)
                    )
                    Text(
                        text = "Does NOT deduct or affect main live balance",
                        color = TextMuted,
                        fontSize = 10.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Switcher: Add Liability (+Debt) vs Pay Liability (-Debt)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF0E0E12))
                    .border(1.dp, CardGlassBorder, RoundedCornerShape(12.dp))
                    .padding(3.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isAdding) NeonRed.copy(alpha = 0.25f) else Color.Transparent)
                        .clickable { selectedAction = "ADD_LIABILITY" }
                        .padding(vertical = 10.dp)
                        .testTag("tab_add_liability"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "+ ADD LIABILITY",
                        style = neonTextStyle(if (isAdding) NeonRed else TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.Bold, glowRadius = 8f)
                    )
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (!isAdding) NeonCyan.copy(alpha = 0.25f) else Color.Transparent)
                        .clickable { selectedAction = "PAY_LIABILITY" }
                        .padding(vertical = 10.dp)
                        .testTag("tab_pay_liability"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "- PAY/REDUCE DEBT",
                        style = neonTextStyle(if (!isAdding) NeonCyan else TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.Bold, glowRadius = 8f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Amount Input
            OutlinedTextField(
                value = amountText,
                onValueChange = { amountText = it },
                label = { Text("Liability Amount ($currencySymbol)") },
                placeholder = { Text("e.g. 2000") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal, imeAction = ImeAction.Next),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = accentColor,
                    unfocusedBorderColor = CardGlassBorder,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    cursorColor = accentColor
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("liability_amount_input")
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Description Input
            OutlinedTextField(
                value = descriptionText,
                onValueChange = { descriptionText = it },
                label = { Text("Description / Creditor Note (Mandatory)") },
                placeholder = { Text(if (isAdding) "e.g. Borrowed from Raj, Loan EMI" else "e.g. Paid installment to Raj") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text, imeAction = ImeAction.Next),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = accentColor,
                    unfocusedBorderColor = CardGlassBorder,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    cursorColor = accentColor
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("liability_desc_input")
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Tier 1 PIN
            OutlinedTextField(
                value = tier1PinText,
                onValueChange = { tier1PinText = it },
                label = { Text("Tier 1 Password (Default: 1234)") },
                placeholder = { Text("Enter 4-digit PIN") },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword, imeAction = ImeAction.Done),
                singleLine = true,
                leadingIcon = {
                    Icon(Icons.Default.Security, contentDescription = null, tint = accentColor)
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = accentColor,
                    unfocusedBorderColor = CardGlassBorder,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    cursorColor = accentColor
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("liability_tier1_pin_input")
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
                colors = ButtonDefaults.buttonColors(containerColor = accentColor),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("submit_liability_button")
            ) {
                Text(
                    text = if (isAdding) "RECORD NEW DEBT (+)" else "RECORD DEBT PAYMENT (-)",
                    color = CanvasBackground,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
        }
    }
}

/**
 * Dual Permanent Ledgers:
 * Ledger 1: Main Financial Ledger (Date, Time, Amount, Type, Description). Color-coded:
 *   +ive Income in vibrant Glowing Neon Green
 *   -ive Expense in vibrant Glowing Neon Red
 * Ledger 2: Independent Liability Ledger (Date, Time, Amount, Action Type, Description).
 * Immutability by default; Admin Mode (Tier 2) allows Edit & Delete!
 */
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
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Main Ledger, 1: Liability Ledger

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "PERMANENT AUDIT LEDGERS",
                style = neonTextStyle(NeonCyan, fontSize = 13.sp, fontWeight = FontWeight.Bold, glowRadius = 10f)
            )
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = if (isAdminMode) NeonRed.copy(alpha = 0.2f) else CardGlass,
                border = androidx.compose.foundation.BorderStroke(1.dp, if (isAdminMode) NeonRed else CardGlassBorder)
            ) {
                Text(
                    text = if (isAdminMode) "ADMIN OVERRIDE ACTIVE" else "IMMUTABLE AUDIT",
                    color = if (isAdminMode) NeonRed else TextSecondary,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Ledger Switcher Tabs — only shown when there's a liability ledger to switch to
        if (showLiabilities) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFF0E0E12))
                .border(1.dp, CardGlassBorder, RoundedCornerShape(12.dp))
                .padding(4.dp)
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (selectedTab == 0) CardGlass else Color.Transparent)
                    .clickable { selectedTab = 0 }
                    .padding(vertical = 8.dp)
                    .testTag("tab_main_ledger"),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Main Ledger (${transactions.size})",
                    color = if (selectedTab == 0) NeonGreen else TextSecondary,
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
                    .padding(vertical = 8.dp)
                    .testTag("tab_liability_ledger"),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Liability Ledger (${liabilities.size})",
                    color = if (selectedTab == 1) NeonCyan else TextSecondary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))
        }

        val effectiveTab = if (showLiabilities) selectedTab else 0

        if (effectiveTab == 0) {
            // Main Ledger Entries
            if (transactions.isEmpty()) {
                EmptyLedgerCard(message = "No transactions logged yet. Add your first income or expense above!")
            } else {
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
            }
        } else {
            // Liability Ledger Entries
            if (liabilities.isEmpty()) {
                EmptyLedgerCard(message = "No liability records found. Record borrowings or debt payments above!")
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
    val isIncome = transaction.type.equals("INCOME", ignoreCase = true)
    val itemColor = if (isIncome) NeonGreen else NeonRed
    val itemGlow = if (isIncome) NeonGreenGlow else NeonRedGlow

    GlassBox(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        glowColor = itemColor.copy(alpha = 0.25f),
        glowIntensity = 0.25f
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.weight(1f)
            ) {
                // Sleek glowing indicator line
                Box(
                    modifier = Modifier
                        .width(3.dp)
                        .height(36.dp)
                        .clip(CircleShape)
                        .background(itemColor)
                )

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = transaction.description,
                        style = neonTextStyle(itemColor, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, glowRadius = 8f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "${transaction.dateString} • ${transaction.timeString}",
                        color = TextMuted,
                        fontSize = 11.sp
                    )
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "${if (isIncome) "+" else "-"}$currencySymbol ${indianNumber(transaction.amount)}",
                    style = neonTextStyle(itemColor, fontSize = 16.sp, fontWeight = FontWeight.Bold, glowRadius = 10f)
                )

                if (isAdminMode) {
                    IconButton(onClick = onEdit, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit Transaction", tint = NeonCyan, modifier = Modifier.size(16.dp))
                    }
                    IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete Transaction", tint = NeonRed, modifier = Modifier.size(16.dp))
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
    val isAdd = liability.actionType.equals("ADD_LIABILITY", ignoreCase = true)
    val itemColor = if (isAdd) NeonRed else NeonCyan

    GlassBox(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        glowColor = itemColor.copy(alpha = 0.25f),
        glowIntensity = 0.25f
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .width(3.dp)
                        .height(36.dp)
                        .clip(CircleShape)
                        .background(itemColor)
                )

                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = liability.description,
                            style = neonTextStyle(itemColor, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, glowRadius = 8f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "${if (isAdd) "Added Debt" else "Payment Made"} • ${liability.dateString} • ${liability.timeString}",
                        color = TextMuted,
                        fontSize = 11.sp
                    )
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "${if (isAdd) "+" else "-"}$currencySymbol ${indianNumber(liability.amount)}",
                    style = neonTextStyle(itemColor, fontSize = 16.sp, fontWeight = FontWeight.Bold, glowRadius = 10f)
                )

                if (isAdminMode) {
                    IconButton(onClick = onEdit, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit Liability", tint = NeonCyan, modifier = Modifier.size(16.dp))
                    }
                    IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete Liability", tint = NeonRed, modifier = Modifier.size(16.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun EmptyLedgerCard(message: String) {
    GlassBox(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(Icons.Default.Security, contentDescription = null, tint = TextMuted, modifier = Modifier.size(28.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = message,
                color = TextSecondary,
                fontSize = 12.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}

// ----------------------------------------------------
// Dialogs: Security Tiers, Settings, Cloud Sync, Edits
// ----------------------------------------------------

@Composable
fun SyncChoiceDialog(
    onMerge: () -> Unit,
    onReplace: () -> Unit,
    onDiscardCloud: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = SurfaceDark,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Icon(Icons.Default.CloudSync, contentDescription = null, tint = NeonCyan)
                Text(text = "Local vs. Cloud Data", color = TextPrimary, fontWeight = FontWeight.Bold)
            }
        },
        text = {
            Column {
                Text(
                    text = "This account may already have cloud data, and this device may have data of its own. How should they be resolved?",
                    color = TextSecondary,
                    fontSize = 13.sp
                )
                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = onMerge,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = NeonCyan)
                ) {
                    Text("MERGE BOTH", color = CanvasBackground, fontWeight = FontWeight.Bold)
                }
                Text(
                    text = "Combine device data with cloud data, skipping exact duplicates.",
                    color = TextSecondary,
                    fontSize = 10.sp,
                    modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
                )

                OutlinedButton(
                    onClick = onReplace,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("REPLACE WITH CLOUD DATA", fontSize = 13.sp)
                }
                Text(
                    text = "Discard this device's data and load the cloud backup instead.",
                    color = TextSecondary,
                    fontSize = 10.sp,
                    modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
                )

                OutlinedButton(
                    onClick = onDiscardCloud,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = NeonRed)
                ) {
                    Text("DISCARD CLOUD, KEEP THIS DEVICE", fontSize = 13.sp)
                }
                Text(
                    text = "Overwrite the cloud backup with this device's current data.",
                    color = TextSecondary,
                    fontSize = 10.sp,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("DECIDE LATER", color = TextSecondary)
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExportTransactionsDialog(
    onDismiss: () -> Unit,
    onExport: (startTimestamp: Long?, endTimestamp: Long?, includeLiabilities: Boolean) -> Unit
) {
    var includeLiabilities by remember { mutableStateOf(true) }
    var exportFromBeginning by remember { mutableStateOf(true) }
    var startDateMillis by remember { mutableStateOf<Long?>(null) }
    var endDateMillis by remember { mutableStateOf<Long?>(null) }
    var showStartPicker by remember { mutableStateOf(false) }
    var showEndPicker by remember { mutableStateOf(false) }

    val dateLabelFormat = remember { SimpleDateFormat("dd MMM yyyy", Locale.getDefault()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = SurfaceDark,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Icon(Icons.Default.Download, contentDescription = null, tint = NeonCyan)
                Text(text = "Export Transactions", color = TextPrimary, fontWeight = FontWeight.Bold)
            }
        },
        text = {
            Column {
                // Include liabilities toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Include Liability Section", color = TextPrimary, fontSize = 13.sp)
                    Switch(
                        checked = includeLiabilities,
                        onCheckedChange = { includeLiabilities = it },
                        colors = SwitchDefaults.colors(checkedTrackColor = NeonCyan)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))
                HorizontalDivider(color = CardGlassBorder)
                Spacer(modifier = Modifier.height(10.dp))

                // From-beginning toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Export From The Very Beginning", color = TextPrimary, fontSize = 13.sp)
                    Switch(
                        checked = exportFromBeginning,
                        onCheckedChange = { exportFromBeginning = it },
                        colors = SwitchDefaults.colors(checkedTrackColor = NeonCyan)
                    )
                }

                if (!exportFromBeginning) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Choose a custom date range:",
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = { showStartPicker = true },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = startDateMillis?.let { dateLabelFormat.format(Date(it)) } ?: "Start Date",
                                fontSize = 11.sp
                            )
                        }
                        OutlinedButton(
                            onClick = { showEndPicker = true },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = endDateMillis?.let { dateLabelFormat.format(Date(it)) } ?: "End Date",
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val start = if (exportFromBeginning) null else startDateMillis
                    val end = if (exportFromBeginning) null else endDateMillis
                    onExport(start, end, includeLiabilities)
                },
                colors = ButtonDefaults.buttonColors(containerColor = NeonCyan)
            ) {
                Text("EXPORT", color = CanvasBackground, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("CANCEL", color = TextSecondary)
            }
        }
    )

    if (showStartPicker) {
        val state = rememberDatePickerState(initialSelectedDateMillis = startDateMillis)
        DatePickerDialog(
            onDismissRequest = { showStartPicker = false },
            confirmButton = {
                TextButton(onClick = {
                    startDateMillis = state.selectedDateMillis
                    showStartPicker = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showStartPicker = false }) { Text("CANCEL") }
            }
        ) {
            DatePicker(state = state)
        }
    }

    if (showEndPicker) {
        val state = rememberDatePickerState(initialSelectedDateMillis = endDateMillis)
        DatePickerDialog(
            onDismissRequest = { showEndPicker = false },
            confirmButton = {
                TextButton(onClick = {
                    // Push to end-of-day so the selected day is fully included.
                    val millis = state.selectedDateMillis
                    endDateMillis = millis?.plus(23 * 60 * 60 * 1000L + 59 * 60 * 1000L + 59 * 1000L)
                    showEndPicker = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showEndPicker = false }) { Text("CANCEL") }
            }
        ) {
            DatePicker(state = state)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KeywordExportDialog(
    onDismiss: () -> Unit,
    onExport: (keywords: List<String>, startTimestamp: Long?, endTimestamp: Long?) -> Unit
) {
    val keywords = remember { mutableStateListOf<String>() }
    var keywordInput by remember { mutableStateOf("") }
    var exportFromBeginning by remember { mutableStateOf(true) }
    var startDateMillis by remember { mutableStateOf<Long?>(null) }
    var endDateMillis by remember { mutableStateOf<Long?>(null) }
    var showStartPicker by remember { mutableStateOf(false) }
    var showEndPicker by remember { mutableStateOf(false) }

    val dateLabelFormat = remember { SimpleDateFormat("dd MMM yyyy", Locale.getDefault()) }

    fun addKeyword() {
        val trimmed = keywordInput.trim()
        if (trimmed.isNotEmpty() && !keywords.any { it.equals(trimmed, ignoreCase = true) }) {
            keywords.add(trimmed)
        }
        keywordInput = ""
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = SurfaceDark,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Icon(Icons.Default.Download, contentDescription = null, tint = NeonCyan)
                Text(text = "Export by Keyword", color = TextPrimary, fontWeight = FontWeight.Bold)
            }
        },
        text = {
            Column {
                Text(
                    text = "Type a name or word (e.g. \"X\", \"Salary\") — any transaction whose description contains it will be grouped and subtotaled. Add as many as you like, or leave this empty to export everything.",
                    color = TextSecondary,
                    fontSize = 12.sp
                )
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = keywordInput,
                        onValueChange = { keywordInput = it },
                        label = { Text("Keyword / Name") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text, imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(onDone = { addKeyword() }),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NeonCyan,
                            unfocusedBorderColor = CardGlassBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            cursorColor = NeonCyan
                        ),
                        modifier = Modifier.weight(1f)
                    )
                    Button(
                        onClick = { addKeyword() },
                        colors = ButtonDefaults.buttonColors(containerColor = NeonCyan)
                    ) {
                        Text("ADD", color = CanvasBackground, fontWeight = FontWeight.Bold)
                    }
                }

                if (keywords.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 140.dp)
                            .verticalScroll(rememberScrollState())
                    ) {
                        keywords.forEach { kw ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(kw, color = TextPrimary, fontSize = 13.sp)
                                IconButton(onClick = { keywords.remove(kw) }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Remove", tint = NeonRed, modifier = Modifier.size(18.dp))
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
                HorizontalDivider(color = CardGlassBorder)
                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Export From The Very Beginning", color = TextPrimary, fontSize = 13.sp)
                    Switch(
                        checked = exportFromBeginning,
                        onCheckedChange = { exportFromBeginning = it },
                        colors = SwitchDefaults.colors(checkedTrackColor = NeonCyan)
                    )
                }

                if (!exportFromBeginning) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Choose a custom date range:",
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = { showStartPicker = true },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = startDateMillis?.let { dateLabelFormat.format(Date(it)) } ?: "Start Date",
                                fontSize = 11.sp
                            )
                        }
                        OutlinedButton(
                            onClick = { showEndPicker = true },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = endDateMillis?.let { dateLabelFormat.format(Date(it)) } ?: "End Date",
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (keywordInput.isNotBlank()) addKeyword()
                    val start = if (exportFromBeginning) null else startDateMillis
                    val end = if (exportFromBeginning) null else endDateMillis
                    onExport(keywords.toList(), start, end)
                },
                colors = ButtonDefaults.buttonColors(containerColor = NeonCyan)
            ) {
                Text("EXPORT", color = CanvasBackground, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("CANCEL", color = TextSecondary)
            }
        }
    )

    if (showStartPicker) {
        val state = rememberDatePickerState(initialSelectedDateMillis = startDateMillis)
        DatePickerDialog(
            onDismissRequest = { showStartPicker = false },
            confirmButton = {
                TextButton(onClick = {
                    startDateMillis = state.selectedDateMillis
                    showStartPicker = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showStartPicker = false }) { Text("CANCEL") }
            }
        ) {
            DatePicker(state = state)
        }
    }

    if (showEndPicker) {
        val state = rememberDatePickerState(initialSelectedDateMillis = endDateMillis)
        DatePickerDialog(
            onDismissRequest = { showEndPicker = false },
            confirmButton = {
                TextButton(onClick = {
                    val millis = state.selectedDateMillis
                    endDateMillis = millis?.plus(23 * 60 * 60 * 1000L + 59 * 60 * 1000L + 59 * 1000L)
                    showEndPicker = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showEndPicker = false }) { Text("CANCEL") }
            }
        ) {
            DatePicker(state = state)
        }
    }
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
        title = {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Icon(Icons.Default.Key, contentDescription = null, tint = NeonRed)
                Text(text = "Tier 2 Emergency Protocol", color = TextPrimary, fontWeight = FontWeight.Bold)
            }
        },
        text = {
            Column {
                Text(
                    text = "Enter Master Password to unlock Admin Mode. This grants permission to modify baseline balances and past immutable audit logs.",
                    color = TextSecondary,
                    fontSize = 13.sp
                )
                Spacer(modifier = Modifier.height(14.dp))
                OutlinedTextField(
                    value = pinText,
                    onValueChange = { pinText = it },
                    label = { Text("Master Password (Default: 9999)") },
                    placeholder = { Text("Enter Tier 2 password") },
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeonRed,
                        unfocusedBorderColor = CardGlassBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        cursorColor = NeonRed
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("admin_pin_input")
                )
                Spacer(modifier = Modifier.height(8.dp))
                TextButton(onClick = onForgotPassword) {
                    Text(
                        text = if (googleAccountLinked) "Forgot Tier 2 Password? Verify with Google" else "Forgot Tier 2 Password? (Link Google account first)",
                        color = NeonCyan,
                        fontSize = 12.sp
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onUnlock(pinText) },
                colors = ButtonDefaults.buttonColors(containerColor = NeonRed),
                modifier = Modifier.testTag("unlock_admin_confirm_button")
            ) {
                Text("UNLOCK ADMIN MODE", color = CanvasBackground, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("CANCEL", color = TextSecondary)
            }
        }
    )
}

@Composable
fun SetNewTier2PasswordDialog(
    onDismiss: () -> Unit,
    onConfirm: (newPassword: String) -> Unit
) {
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    val passwordsMatch = newPassword.isNotBlank() && newPassword == confirmPassword

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = SurfaceDark,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Icon(Icons.Default.Key, contentDescription = null, tint = NeonCyan)
                Text(text = "Set New Tier 2 Password", color = TextPrimary, fontWeight = FontWeight.Bold)
            }
        },
        text = {
            Column {
                Text(
                    text = "Your identity was verified via Google. Choose a new Tier 2 (Admin) master password.",
                    color = TextSecondary,
                    fontSize = 13.sp
                )
                Spacer(modifier = Modifier.height(14.dp))
                OutlinedTextField(
                    value = newPassword,
                    onValueChange = { newPassword = it },
                    label = { Text("New Master Password") },
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeonCyan,
                        unfocusedBorderColor = CardGlassBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        cursorColor = NeonCyan
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    label = { Text("Confirm New Password") },
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeonCyan,
                        unfocusedBorderColor = CardGlassBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        cursorColor = NeonCyan
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                if (confirmPassword.isNotBlank() && !passwordsMatch) {
                    Text(
                        text = "Passwords do not match.",
                        color = NeonRed,
                        fontSize = 11.sp,
                        modifier = Modifier.padding(top = 6.dp)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(newPassword) },
                enabled = passwordsMatch,
                colors = ButtonDefaults.buttonColors(containerColor = NeonCyan)
            ) {
                Text("SET NEW PASSWORD", color = CanvasBackground, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("CANCEL", color = TextSecondary)
            }
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
        title = {
            Text(text = "Edit Fixed Baseline Balance", color = NeonYellow, fontWeight = FontWeight.Bold)
        },
        text = {
            Column {
                Text(
                    text = "Set the starting baseline funds for RUSHU FIN. Changes take effect on the Main Live Balance immediately.",
                    color = TextSecondary,
                    fontSize = 13.sp
                )
                Spacer(modifier = Modifier.height(14.dp))
                OutlinedTextField(
                    value = balanceText,
                    onValueChange = { balanceText = it },
                    label = { Text("Starting Funds ($currencySymbol)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeonYellow,
                        unfocusedBorderColor = CardGlassBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
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
                Text("UPDATE BASELINE", color = CanvasBackground, fontWeight = FontWeight.Bold)
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
 * A single selectable row in the App Theme picker — tap anywhere on the
 * row to select that theme.
 */
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

@Composable
fun SettingsAndCloudSyncDialog(
    uiState: FinanceUiState,
    onDismiss: () -> Unit,
    onSignInGoogle: () -> Unit,
    onBackupNow: () -> Unit,
    onRestoreNow: () -> Unit,
    onClearBackup: () -> Unit,
    onSwitchAccount: () -> Unit,
    onExportTransactions: () -> Unit,
    onExportByCategory: () -> Unit,
    onToggleShowLiabilities: (Boolean) -> Unit,
    onSelectTheme: (String) -> Unit,
    onChangePasswords: (tier1: String, tier2: String, onDone: () -> Unit) -> Unit
) {
    var newTier1 by remember { mutableStateOf(uiState.tier1Password) }
    var newTier2 by remember { mutableStateOf(uiState.tier2Password) }
    var confirmClearBackup by remember { mutableStateOf(false) }
    var confirmSwitchAccount by remember { mutableStateOf(false) }

    val lastSyncFormatted = if (uiState.lastSyncTime > 0) {
        SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(Date(uiState.lastSyncTime))
    } else {
        "Never"
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = SurfaceDark,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Icon(Icons.Default.CloudSync, contentDescription = null, tint = NeonCyan)
                Text(text = "Free Google Drive Sync & Security", color = TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Google Account Section
                GlassBox(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(text = "GOOGLE DRIVE BACKUP (FREE)", color = NeonCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = if (uiState.googleAccountEmail != null) {
                                "Connected: ${uiState.googleAccountEmail}\nLast Backup: $lastSyncFormatted"
                            } else {
                                "Link your free personal Google account to auto-backup and restore your financial database across devices."
                            },
                            color = TextSecondary,
                            fontSize = 12.sp
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        if (uiState.googleAccountEmail == null) {
                            Button(
                                onClick = onSignInGoogle,
                                colors = ButtonDefaults.buttonColors(containerColor = NeonCyan),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("LINK GOOGLE ACCOUNT", color = CanvasBackground, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        } else {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedButton(
                                    onClick = onBackupNow,
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Text("BACKUP NOW", fontSize = 11.sp, color = NeonGreen)
                                }
                                OutlinedButton(
                                    onClick = onRestoreNow,
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Text("RESTORE DATA", fontSize = 11.sp, color = NeonCyan)
                                }
                            }
                        }

                        if (uiState.isSyncing) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                CircularProgressIndicator(modifier = Modifier.size(16.dp), color = NeonCyan, strokeWidth = 2.dp)
                                Text("Sync in progress...", color = NeonCyan, fontSize = 11.sp)
                            }
                        }

                        if (uiState.googleAccountEmail != null && uiState.isAdminModeUnlocked) {
                            Spacer(modifier = Modifier.height(10.dp))
                            if (!confirmClearBackup) {
                                OutlinedButton(
                                    onClick = { confirmClearBackup = true },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = NeonRed)
                                ) {
                                    Text("CLEAR CLOUD BACKUP", fontSize = 11.sp)
                                }
                            } else {
                                Text(
                                    text = "This permanently deletes your Drive backup. Are you sure?",
                                    color = NeonRed,
                                    fontSize = 11.sp
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Button(
                                        onClick = {
                                            confirmClearBackup = false
                                            onClearBackup()
                                        },
                                        modifier = Modifier.weight(1f),
                                        shape = RoundedCornerShape(10.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = NeonRed)
                                    ) {
                                        Text("YES, CLEAR", fontSize = 11.sp, color = CanvasBackground, fontWeight = FontWeight.Bold)
                                    }
                                    OutlinedButton(
                                        onClick = { confirmClearBackup = false },
                                        modifier = Modifier.weight(1f),
                                        shape = RoundedCornerShape(10.dp)
                                    ) {
                                        Text("CANCEL", fontSize = 11.sp)
                                    }
                                }
                            }
                        }

                        if (uiState.googleAccountEmail != null) {
                            Spacer(modifier = Modifier.height(10.dp))
                            if (!confirmSwitchAccount) {
                                OutlinedButton(
                                    onClick = { confirmSwitchAccount = true },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = NeonCyan)
                                ) {
                                    Text("SWITCH GOOGLE ACCOUNT", fontSize = 11.sp)
                                }
                            } else {
                                Text(
                                    text = "This backs up the current account, clears local data, then lets you sign in to a different account. Continue?",
                                    color = NeonCyan,
                                    fontSize = 11.sp
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Button(
                                        onClick = {
                                            confirmSwitchAccount = false
                                            onSwitchAccount()
                                        },
                                        modifier = Modifier.weight(1f),
                                        shape = RoundedCornerShape(10.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = NeonCyan)
                                    ) {
                                        Text("YES, SWITCH", fontSize = 11.sp, color = CanvasBackground, fontWeight = FontWeight.Bold)
                                    }
                                    OutlinedButton(
                                        onClick = { confirmSwitchAccount = false },
                                        modifier = Modifier.weight(1f),
                                        shape = RoundedCornerShape(10.dp)
                                    ) {
                                        Text("CANCEL", fontSize = 11.sp)
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Display Settings Section
                GlassBox(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(text = "DISPLAY SETTINGS", color = NeonCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Show Liability Section", color = TextPrimary, fontSize = 13.sp)
                                Text(
                                    text = "Turn off to hide liabilities everywhere and show only income, expenses, and balance.",
                                    color = TextSecondary,
                                    fontSize = 10.sp
                                )
                            }
                            Switch(
                                checked = uiState.showLiabilities,
                                onCheckedChange = { onToggleShowLiabilities(it) },
                                colors = SwitchDefaults.colors(checkedTrackColor = NeonCyan)
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))
                        HorizontalDivider(color = CardGlassBorder)
                        Spacer(modifier = Modifier.height(14.dp))

                        Text("App Theme", color = TextPrimary, fontSize = 13.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Pick the look that suits you best.",
                            color = TextSecondary,
                            fontSize = 10.sp
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        ThemeOptionRow(
                            title = "Default (Neon)",
                            description = "The original glowing dark theme.",
                            selected = uiState.themeMode == AppTheme.DEFAULT,
                            onClick = { onSelectTheme(AppTheme.DEFAULT) }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        ThemeOptionRow(
                            title = "Basic / Large Text",
                            description = "Bigger text, no glow — easier to read for older eyes.",
                            selected = uiState.themeMode == AppTheme.BASIC,
                            onClick = { onSelectTheme(AppTheme.BASIC) }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        ThemeOptionRow(
                            title = "🐝 Golden Hive",
                            description = "Black and gold, honeycomb accents.",
                            selected = uiState.themeMode == AppTheme.GOLDEN_HIVE,
                            onClick = { onSelectTheme(AppTheme.GOLDEN_HIVE) }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        ThemeOptionRow(
                            title = "🌸 Sakura Bloom",
                            description = "Black and rose-pink, cherry blossom accents.",
                            selected = uiState.themeMode == AppTheme.SAKURA_BLOOM,
                            onClick = { onSelectTheme(AppTheme.SAKURA_BLOOM) }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        ThemeOptionRow(
                            title = "🌙 Moonlit Purple",
                            description = "Violet chrome with green balance, gold initial, pink liability.",
                            selected = uiState.themeMode == AppTheme.MOONLIT_PURPLE,
                            onClick = { onSelectTheme(AppTheme.MOONLIT_PURPLE) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Export Transactions Section
                GlassBox(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(text = "EXPORT TRANSACTIONS", color = NeonCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Save your ledger as a text file you can open or share.",
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Button(
                            onClick = onExportTransactions,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = NeonCyan)
                        ) {
                            Icon(Icons.Default.Download, contentDescription = null, tint = CanvasBackground, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("EXPORT AS TEXT FILE", fontSize = 12.sp, color = CanvasBackground, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedButton(
                            onClick = onExportByCategory,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = NeonCyan)
                        ) {
                            Icon(Icons.Default.Download, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("EXPORT BY KEYWORD", fontSize = 12.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Security Passwords Section
                GlassBox(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(text = "SECURITY TIER PASSWORDS", color = NeonYellow, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(6.dp))

                        if (uiState.isAdminModeUnlocked) {
                            OutlinedTextField(
                                value = newTier1,
                                onValueChange = { newTier1 = it },
                                label = { Text("Tier 1 PIN (Transactions)") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = newTier2,
                                onValueChange = { newTier2 = it },
                                label = { Text("Tier 2 Master (Admin Mode)") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(
                                onClick = {
                                    onChangePasswords(newTier1, newTier2) {
                                        onDismiss()
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = NeonYellow),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("SAVE PASSWORDS", color = CanvasBackground, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        } else {
                            Text(
                                text = "Unlock Admin Mode (Tier 2) to change Tier 1 PIN or Master Password.",
                                color = TextMuted,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("CLOSE", color = NeonCyan, fontWeight = FontWeight.Bold)
            }
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
    var descText by remember { mutableStateOf(transaction.description) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = SurfaceDark,
        title = { Text("Admin Override: Edit Transaction", color = NeonCyan, fontWeight = FontWeight.Bold) },
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
                    value = descText,
                    onValueChange = { descText = it },
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
                    onConfirm(transaction.copy(amount = amt, description = descText))
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
    var descText by remember { mutableStateOf(liability.description) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = SurfaceDark,
        title = { Text("Admin Override: Edit Liability", color = NeonCyan, fontWeight = FontWeight.Bold) },
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
                    value = descText,
                    onValueChange = { descText = it },
                    label = { Text("Description") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val amt = amountText.toDoubleOrNull() ?: liability.amount
                    onConfirm(liability.copy(amount = amt, description = descText))
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
