package com.example.ui.screens

import android.app.Activity
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

@Composable
fun DashboardScreen(
    viewModel: FinanceViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val transactions by viewModel.transactions.collectAsStateWithLifecycle()
    val liabilities by viewModel.liabilities.collectAsStateWithLifecycle()
    val isAdminMode by viewModel.isAdminMode.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    var showAdminUnlockDialog by remember { mutableStateOf(false) }
    var showEditInitialBalanceDialog by remember { mutableStateOf(false) }
    var showSettingsDialog by remember { mutableStateOf(false) }
    var editingTransaction by remember { mutableStateOf<TransactionEntity?>(null) }
    var editingLiability by remember { mutableStateOf<LiabilityEntity?>(null) }

    // Dialog states for Exports and Security
    var showDateRangeExportDialog by remember { mutableStateOf(false) }
    var showKeywordExportDialog by remember { mutableStateOf(false) }
    var showChangePinDialog by remember { mutableStateOf(false) }

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

    LaunchedEffect(Unit) {
        viewModel.userFeedback.collectLatest { msg ->
            snackbarHostState.showSnackbar(msg)
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
        val isHinata = uiState.themeMode == AppTheme.HINATA

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
                // App Header with "MADE BY RUH, WITH LOVE ❤"
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

                // Main Live Balance Card with Ruh, Kakashi, Bumblebee, Hinata, or Classic
                item {
                    val formulaText = "Formula: Initial (${uiState.currencySymbol}${uiState.initialBalance.toInt()}) + Income (${uiState.currencySymbol}${uiState.totalIncome.toInt()}) - Expenses (${uiState.currencySymbol}${uiState.totalExpense.toInt()})"

                    when (uiState.themeMode) {
                        AppTheme.RUH -> {
                            RuhMainBalanceCard(
                                liveBalance = uiState.liveBalance,
                                totalIncome = uiState.totalIncome,
                                totalExpenses = uiState.totalExpense,
                                formulaText = formulaText
                            )
                        }
                        AppTheme.BUMBLEBEE -> {
                            BumblebeeMainBalanceCard(
                                liveBalance = uiState.liveBalance,
                                totalIncome = uiState.totalIncome,
                                totalExpenses = uiState.totalExpense,
                                formulaText = formulaText
                            )
                        }
                        AppTheme.KAKASHI -> {
                            KakashiMainBalanceCard(
                                liveBalance = uiState.liveBalance,
                                totalIncome = uiState.totalIncome,
                                totalExpenses = uiState.totalExpense,
                                formulaText = formulaText
                            )
                        }
                        AppTheme.HINATA -> {
                            HinataMainBalanceCard(
                                liveBalance = uiState.liveBalance,
                                totalIncome = uiState.totalIncome,
                                totalExpenses = uiState.totalExpense,
                                formulaText = formulaText
                            )
                        }
                        else -> {
                            // Classic Neon or Basic
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
                        onRecordTransaction = { amount, type, desc, pin, onDone ->
                            viewModel.addTransaction(amount, type, desc, pin, onDone)
                        }
                    )
                }

                // Liability Module
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

                // Permanent Audit Ledgers
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

                item { Spacer(modifier = Modifier.height(32.dp)) }
            }

            // Falling petals for Hinata's theme
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

        // Dialogs
        if (showAdminUnlockDialog) {
            AdminUnlockDialog(
                onDismiss = { showAdminUnlockDialog = false },
                onUnlock = { pin ->
                    if (viewModel.verifyAndUnlockAdminMode(pin)) showAdminUnlockDialog = false
                },
                googleAccountLinked = uiState.googleAccountEmail != null,
                onForgotPassword = {}
            )
        }

        if (showEditInitialBalanceDialog) {
            EditInitialBalanceDialog(
                currentBalance = uiState.initialBalance,
                currencySymbol = uiState.currencySymbol,
                onDismiss = { showEditInitialBalanceDialog = false },
                onConfirm = { newBal ->
                    viewModel.updateInitialBalanceWithTier2(newBal) { showEditInitialBalanceDialog = false }
                }
            )
        }

        if (showSettingsDialog) {
            SettingsDialog(
                uiState = uiState,
                onDismiss = { showSettingsDialog = false },
                onSelectTheme = { theme -> viewModel.setThemeMode(theme) },
                onToggleLiabilities = { viewModel.setShowLiabilities(it) },
                onSignInGoogle = { googleSignInLauncher.launch(viewModel.driveSyncManager.getSignInIntent()) },
                onSwitchGoogleAccount = { googleSignInLauncher.launch(viewModel.driveSyncManager.getSignInIntent()) },
                onDisconnectGoogle = { viewModel.driveSyncManager.signOut { viewModel.refreshGoogleAccount() } },
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

        if (showDateRangeExportDialog) {
            DateRangeExportDialog(
                onDismiss = { showDateRangeExportDialog = false },
                onExport = { start, end ->
                    FileExportUtil.exportTransactionsByDate(context, transactions, start, end)
                    showDateRangeExportDialog = false
                }
            )
        }

        if (showKeywordExportDialog) {
            KeywordExportDialog(
                onDismiss = { showKeywordExportDialog = false },
                onExport = { keyword ->
                    FileExportUtil.exportTransactionsByKeyword(context, transactions, keyword)
                    showKeywordExportDialog = false
                }
            )
        }

        if (showChangePinDialog) {
            ChangePinDialog(
                onDismiss = { showChangePinDialog = false },
                onChangeTier1 = { old, new -> viewModel.changeTier1Pin(old, new) },
                onChangeTier2 = { old, new -> viewModel.changeTier2Pin(old, new) }
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

/**
 * App Header with "MADE BY RUH, WITH LOVE ❤" Subtitle
 */
@Composable
fun AppHeader(
    isAdminMode: Boolean,
    googleEmail: String?,
    onAdminToggleClick: () -> Unit,
    onSettingsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val theme = LocalAppTheme.current
    val isHinata = theme == AppTheme.HINATA
    val isKakashi = theme == AppTheme.KAKASHI
    val isBumblebee = theme == AppTheme.BUMBLEBEE
    val isRuh = theme == AppTheme.RUH

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
 * Full Settings Dialog with All 6 Themes, Account Switching, Exports & Passwords
 */
@Composable
fun SettingsDialog(
    uiState: FinanceUiState,
    onDismiss: () -> Unit,
    onSelectTheme: (String) -> Unit,
    onToggleLiabilities: (Boolean) -> Unit,
    onSignInGoogle: () -> Unit,
    onSwitchGoogleAccount: () -> Unit,
    onDisconnectGoogle: () -> Unit,
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
                // 1. ALL 6 THEMES SELECTION SECTION
                GlassBox(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp)) {
                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(text = "SELECT APP THEME", color = NeonCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold)

                        // 🩸 RUH THEME
                        ThemeOptionRow(
                            title = "🩸 Ruh",
                            description = "Pitch black, blood red border, Sharingan & Ꮢᴜʜ᭓Ꮢɪᴅɛʀ",
                            selected = uiState.themeMode == AppTheme.RUH,
                            onClick = { onSelectTheme(AppTheme.RUH) }
                        )

                        // 🏎️ BUMBLEBEE THEME
                        ThemeOptionRow(
                            title = "🏎️ Bumblebee",
                            description = "Metallic amber car body, flames & pitch black",
                            selected = uiState.themeMode == AppTheme.BUMBLEBEE,
                            onClick = { onSelectTheme(AppTheme.BUMBLEBEE) }
                        )

                        // ⚡ KAKASHI THEME
                        ThemeOptionRow(
                            title = "⚡ Kakashi",
                            description = "Chidori electric blue, Sharingan red & deep obsidian",
                            selected = uiState.themeMode == AppTheme.KAKASHI,
                            onClick = { onSelectTheme(AppTheme.KAKASHI) }
                        )

                        // 🪷 HINATA THEME
                        ThemeOptionRow(
                            title = "🪷 Hinata",
                            description = "Falling petals, lavender glow & Byakugan mint green",
                            selected = uiState.themeMode == AppTheme.HINATA,
                            onClick = { onSelectTheme(AppTheme.HINATA) }
                        )

                        // 🌐 NEON CYBERPUNK (DEFAULT)
                        ThemeOptionRow(
                            title = "🌐 Neon Cyberpunk",
                            description = "Electric cyan, neon green, and deep midnight dark",
                            selected = uiState.themeMode == AppTheme.NEON,
                            onClick = { onSelectTheme(AppTheme.NEON) }
                        )

                        // 📄 BASIC (LARGE TEXT)
                        ThemeOptionRow(
                            title = "📄 Basic (Large Text)",
                            description = "High legibility, enlarged fonts, clean classic contrast",
                            selected = uiState.themeMode == AppTheme.BASIC,
                            onClick = { onSelectTheme(AppTheme.BASIC) }
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
                        Text(text = "GOOGLE DRIVE CLOUD SYNC", color = NeonCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Text(
                            text = if (uiState.googleAccountEmail != null) "Linked Account:\n${uiState.googleAccountEmail}" else "Connect your Google account to auto-backup data.",
                            color = TextSecondary,
                            fontSize = 11.sp
                        )

                        if (uiState.googleAccountEmail == null) {
                            Button(
                                onClick = onSignInGoogle,
                                colors = ButtonDefaults.buttonColors(containerColor = NeonCyan),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("LINK GOOGLE ACCOUNT", color = CanvasBackground, fontWeight = FontWeight.Bold, fontSize = 12.sp)
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

                            // Switch / Disconnect buttons
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedButton(onClick = onSwitchGoogleAccount, modifier = Modifier.weight(1f)) {
                                    Text("SWITCH ACCOUNT", fontSize = 10.sp)
                                }
                                OutlinedButton(onClick = onDisconnectGoogle, modifier = Modifier.weight(1f)) {
                                    Text("DISCONNECT", color = NeonRed, fontSize = 10.sp)
                                }
                            }
                        }
                    }
                }

                // 4. DATA EXPORTS (.TXT FILES)
                GlassBox(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp)) {
                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(text = "EXPORT DATA (.TXT FILE)", color = NeonCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold)

                        OutlinedButton(
                            onClick = onOpenDateRangeExport,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.DateRange, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Export by Date Range", fontSize = 12.sp)
                        }

                        OutlinedButton(
                            onClick = onOpenKeywordExport,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Export Categorized by Keyword", fontSize = 12.sp)
                        }
                    }
                }

                // 5. SECURITY & PASSWORDS
                GlassBox(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp)) {
                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(text = "SECURITY & PASSWORDS", color = NeonCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold)

                        OutlinedButton(
                            onClick = onOpenChangePin,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Change Tier 1 / Tier 2 PIN", fontSize = 12.sp)
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
 * Export by Date Range Dialog
 */
@Composable
fun DateRangeExportDialog(
    onDismiss: () -> Unit,
    onExport: (String, String) -> Unit
) {
    var startDate by remember { mutableStateOf("") }
    var endDate by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = SurfaceDark,
        title = { Text("Export by Date Range", color = NeonCyan, fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("Enter dates in DD/MM/YYYY format or leave blank for all.", color = TextSecondary, fontSize = 12.sp)
                OutlinedTextField(
                    value = startDate,
                    onValueChange = { startDate = it },
                    label = { Text("Start Date (e.g. 01/01/2026)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = endDate,
                    onValueChange = { endDate = it },
                    label = { Text("End Date (e.g. 31/12/2026)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onExport(startDate.trim(), endDate.trim()) },
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
    onExport: (String) -> Unit
) {
    var keyword by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = SurfaceDark,
        title = { Text("Export by Keyword / Category", color = NeonCyan, fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("Filters all transactions matching this keyword and calculates subtotals.", color = TextSecondary, fontSize = 12.sp)
                OutlinedTextField(
                    value = keyword,
                    onValueChange = { keyword = it },
                    label = { Text("Keyword (e.g. Salary, Rent, Food)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onExport(keyword.trim()) },
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
 * Change PIN Dialog
 */
@Composable
fun ChangePinDialog(
    onDismiss: () -> Unit,
    onChangeTier1: (String, String) -> Unit,
    onChangeTier2: (String, String) -> Unit
) {
    var tabIndex by remember { mutableIntStateOf(0) }
    var currentPin by remember { mutableStateOf("") }
    var newPin by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = SurfaceDark,
        title = { Text("Change Security Passwords", color = NeonCyan, fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                TabRow(selectedTabIndex = tabIndex, containerColor = SurfaceDark) {
                    Tab(selected = tabIndex == 0, onClick = { tabIndex = 0; currentPin = ""; newPin = "" }) {
                        Text("Tier 1 PIN", color = if (tabIndex == 0) NeonCyan else TextMuted, modifier = Modifier.padding(8.dp))
                    }
                    Tab(selected = tabIndex == 1, onClick = { tabIndex = 1; currentPin = ""; newPin = "" }) {
                        Text("Tier 2 Password", color = if (tabIndex == 1) NeonRed else TextMuted, modifier = Modifier.padding(8.dp))
                    }
                }

                OutlinedTextField(
                    value = currentPin,
                    onValueChange = { currentPin = it },
                    label = { Text("Current ${if (tabIndex == 0) "Tier 1 PIN" else "Tier 2 Password"}") },
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = newPin,
                    onValueChange = { newPin = it },
                    label = { Text("New ${if (tabIndex == 0) "Tier 1 PIN" else "Tier 2 Password"}") },
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (tabIndex == 0) onChangeTier1(currentPin, newPin) else onChangeTier2(currentPin, newPin)
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(containerColor = if (tabIndex == 0) NeonCyan else NeonRed)
            ) {
                Text("UPDATE", color = CanvasBackground, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("CANCEL", color = TextSecondary) }
        }
    )
}

/**
 * Edit Transaction Dialog for Admin Mode
 */
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
        title = {
            Text(text = "Edit Transaction (Admin)", color = NeonCyan, fontWeight = FontWeight.Bold)
        },
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

/**
 * Edit Liability Dialog for Admin Mode
 */
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
        title = {
            Text(text = "Edit Liability (Admin)", color = NeonRed, fontWeight = FontWeight.Bold)
        },
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
    val isHinata = theme == AppTheme.HINATA
    val isKakashi = theme == AppTheme.KAKASHI
    val isBumblebee = theme == AppTheme.BUMBLEBEE
    val isRuh = theme == AppTheme.RUH
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
    val isRuh = theme == AppTheme.RUH
    val isBumblebee = theme == AppTheme.BUMBLEBEE
    val isKakashi = theme == AppTheme.KAKASHI

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
    val isRuh = theme == AppTheme.RUH
    val isBumblebee = theme == AppTheme.BUMBLEBEE
    val isKakashi = theme == AppTheme.KAKASHI

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
    val isRuh = theme == AppTheme.RUH
    val isBumblebee = theme == AppTheme.BUMBLEBEE
    val isKakashi = theme == AppTheme.KAKASHI

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
    val isRuh = theme == AppTheme.RUH
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
    val isRuh = theme == AppTheme.RUH
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
            Text(text = "Tier 2 Master Access", color = TextPrimary, fontWeight = FontWeight.Bold)
        },
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
        title = {
            Text(text = "Edit Fixed Starting Balance", color = NeonYellow, fontWeight = FontWeight.Bold)
        },
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