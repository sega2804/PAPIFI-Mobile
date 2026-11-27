@file:OptIn(ExperimentalMaterial3Api::class)

package com.papifi.wallet.ui

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.CallMade
import androidx.compose.material.icons.automirrored.filled.CallReceived
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.papifi.designsystem.PapifiPrimary
import com.papifi.model.Currency
import com.papifi.model.TransactionStatus
import com.papifi.ui.MoneyFormatter
import com.papifi.wallet.domain.ExchangeRate
import com.papifi.wallet.domain.TransactionSummary
import com.papifi.wallet.domain.WalletBalance
import org.koin.androidx.compose.koinViewModel

fun Currency.flag(): String = when (this) {
    Currency.NGN -> "🇳🇬"
    Currency.USD -> "🇺🇸"
    Currency.GBP -> "🇬🇧"
    Currency.EUR -> "🇪🇺"
}

fun Currency.displayName(): String = when (this) {
    Currency.NGN -> "Nigerian Naira NGN"
    Currency.USD -> "US Dollar USD"
    Currency.GBP -> "British Pound GBP"
    Currency.EUR -> "Euro EUR"
}

@Composable
fun WalletScreen(
    viewModel: WalletViewModel = koinViewModel(),
    onNavigateToAddMoney: () -> Unit = {},
    onNavigateToSendMoney: () -> Unit = {},
    onNavigateToActivity: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {},
    onNavigateToKyc: () -> Unit = {},
    onNavigateToRecipients: () -> Unit = {},
    onNavigateToExchange: () -> Unit = {}
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                WalletEffect.NavigateToAddMoney -> onNavigateToAddMoney()
                WalletEffect.NavigateToSendMoney -> onNavigateToSendMoney()
                WalletEffect.NavigateToActivity -> onNavigateToActivity()
                WalletEffect.NavigateToRecipients -> onNavigateToRecipients()
                WalletEffect.NavigateToGetUsAccount -> onNavigateToKyc()
                WalletEffect.NavigateToProfile -> onNavigateToProfile()
                WalletEffect.NavigateToExchange -> onNavigateToExchange()
                is WalletEffect.ShowMessage -> {
                    Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    val activeBalance = state.balances.find { it.currency == state.selectedCurrency }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. Header
            HeaderSection(
                userInitials = state.userInitials,
                userName = state.userName,
                unreadCount = state.unreadNotificationsCount,
                onProfileClick = { viewModel.sendIntent(WalletIntent.ProfileClicked) },
                onNotificationsClick = { viewModel.sendIntent(WalletIntent.NotificationsClicked) }
            )

            // 2. Currency Selector Pill
            CurrencySelectorPill(
                currency = state.selectedCurrency,
                onClick = { viewModel.sendIntent(WalletIntent.SetBottomSheetVisible(true)) }
            )

            // 3. Balance Card with Quick Action Tiles
            BalanceCard(
                activeBalance = activeBalance,
                isBalanceVisible = state.isBalanceVisible,
                onToggleVisibility = { viewModel.sendIntent(WalletIntent.ToggleBalanceVisibility) },
                onAddMoney = { viewModel.sendIntent(WalletIntent.AddMoneyClicked) },
                onSendMoney = { viewModel.sendIntent(WalletIntent.SendMoneyClicked) },
                onTransactions = { viewModel.sendIntent(WalletIntent.TransactionsClicked) },
                onRecipients = { viewModel.sendIntent(WalletIntent.RecipientsClicked) }
            )

            // 4. US Bank Account Banner
            UsBankAccountBanner(
                onGetAccountClick = { viewModel.sendIntent(WalletIntent.GetUsAccountClicked) }
            )

            // 5. Exchange Rate Card
            state.exchangeRate?.let { exchangeRate ->
                ExchangeRateCard(
                    exchangeRate = exchangeRate,
                    onExchangeClick = { viewModel.sendIntent(WalletIntent.ExchangeClicked) }
                )
            }

            // 6. Recent Transactions Section
            RecentTransactionsSection(
                transactions = state.recentTransactions,
                onViewAllClick = { viewModel.sendIntent(WalletIntent.TransactionsClicked) }
            )

            Spacer(modifier = Modifier.height(24.dp))
        }

        if (state.isLoading && state.balances.isEmpty()) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = PapifiPrimary
            )
        }

        // 7. Wallet Selector Bottom Sheet Modal
        if (state.isBottomSheetVisible) {
            WalletSelectorBottomSheet(
                balances = state.balances,
                selectedCurrency = state.selectedCurrency,
                onCurrencySelected = { currency ->
                    viewModel.sendIntent(WalletIntent.SelectCurrency(currency))
                },
                onDismiss = {
                    viewModel.sendIntent(WalletIntent.SetBottomSheetVisible(false))
                }
            )
        }
    }
}

@Composable
private fun HeaderSection(
    userInitials: String,
    userName: String,
    unreadCount: Int,
    onProfileClick: () -> Unit,
    onNotificationsClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .clickable(onClick = onProfileClick)
                .padding(4.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(PapifiPrimary),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = userInitials,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = "Welcome back,",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = userName,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        IconButton(onClick = onNotificationsClick) {
            BadgedBox(
                badge = {
                    if (unreadCount > 0) {
                        Badge(
                            containerColor = Color(0xFFBA1A1A),
                            contentColor = Color.White
                        ) {
                            Text(text = unreadCount.toString())
                        }
                    }
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = "Notifications",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@Composable
private fun CurrencySelectorPill(
    currency: Currency,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(50),
        color = MaterialTheme.colorScheme.surfaceVariant,
        shadowElevation = 1.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = currency.flag(), fontSize = 20.sp)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = currency.displayName(),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Icon(
                imageVector = Icons.Default.KeyboardArrowDown,
                contentDescription = "Select Currency",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun BalanceCard(
    activeBalance: WalletBalance?,
    isBalanceVisible: Boolean,
    onToggleVisibility: () -> Unit,
    onAddMoney: () -> Unit,
    onSendMoney: () -> Unit,
    onTransactions: () -> Unit,
    onRecipients: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = PapifiPrimary),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            // Header Row: Label + Eye Icon
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Available Balance",
                    fontSize = 13.sp,
                    color = Color.White.copy(alpha = 0.8f),
                    fontWeight = FontWeight.Medium
                )
                IconButton(onClick = onToggleVisibility) {
                    Icon(
                        imageVector = if (isBalanceVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = "Toggle Balance Visibility",
                        tint = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Main Balance Text Display
            if (activeBalance != null) {
                if (isBalanceVisible) {
                    Row(
                        verticalAlignment = Alignment.Bottom,
                        modifier = Modifier.padding(vertical = 4.dp)
                    ) {
                        Text(
                            text = MoneyFormatter.format(activeBalance.available),
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = activeBalance.currency.code,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White.copy(alpha = 0.8f),
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                    }
                } else {
                    Text(
                        text = "••••••••",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Available vs Reserved Details
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Total: " + if (isBalanceVisible) MoneyFormatter.format(activeBalance.total) else "••••",
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                    Text(
                        text = "Reserved: " + if (isBalanceVisible) MoneyFormatter.format(activeBalance.reserved) else "••••",
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                }

                // Banner for Unprovisioned Currency
                if (!activeBalance.isProvisioned) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0x33FFCC00))
                            .padding(10.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Outlined.Warning,
                                contentDescription = "Unprovisioned",
                                tint = Color(0xFFFFD54F),
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Unprovisioned / Not Enabled - Request activation to start using ${activeBalance.currency.code}.",
                                fontSize = 12.sp,
                                color = Color.White,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            } else {
                Text(
                    text = "No balance data",
                    fontSize = 16.sp,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Quick Action Tiles Row (4 items)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                QuickActionTile(
                    title = "Add Money",
                    icon = Icons.Default.Add,
                    onClick = onAddMoney,
                    modifier = Modifier.weight(1f)
                )
                QuickActionTile(
                    title = "Send Money",
                    icon = Icons.Default.Send,
                    onClick = onSendMoney,
                    modifier = Modifier.weight(1f)
                )
                QuickActionTile(
                    title = "Transactions",
                    icon = Icons.Default.ReceiptLong,
                    onClick = onTransactions,
                    modifier = Modifier.weight(1f)
                )
                QuickActionTile(
                    title = "Recipients",
                    icon = Icons.Default.People,
                    onClick = onRecipients,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun QuickActionTile(
    title: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        modifier = modifier
            .shadow(elevation = 2.dp, shape = RoundedCornerShape(5.dp))
            .border(width = 0.5.dp, color = Color.White.copy(alpha = 0.4f), shape = RoundedCornerShape(5.dp)),
        shape = RoundedCornerShape(5.dp),
        color = Color.White.copy(alpha = 0.10f)
    ) {
        Column(
            modifier = Modifier.padding(vertical = 12.dp, horizontal = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = Color.White,
                modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = title,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White,
                maxLines = 1,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun UsBankAccountBanner(
    onGetAccountClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(PapifiPrimary.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AccountBalance,
                        contentDescription = "US Account",
                        tint = PapifiPrimary,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "Get your US Bank Account",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = "Receive USD directly with ACH & Wire routing.",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                    )
                }
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                onClick = onGetAccountClick,
                colors = ButtonDefaults.buttonColors(containerColor = PapifiPrimary),
                shape = RoundedCornerShape(8.dp),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "Apply Now",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
private fun ExchangeRateCard(
    exchangeRate: ExchangeRate,
    onExchangeClick: () -> Unit = {}
) {
    val gradientBrush = Brush.horizontalGradient(
        colors = listOf(
            Color.White,
            Color(0xFF9AAFA6)
        )
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(gradientBrush)
            .clickable(onClick = onExchangeClick)
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Live Exchange Rate",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF14452F)
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "1 ${exchangeRate.fromCurrency.code} = ${String.format("%,.2f", exchangeRate.rate)} ${exchangeRate.toCurrency.code}",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0A2619)
                )
            }

            Text(
                text = exchangeRate.updatedAt,
                fontSize = 11.sp,
                color = Color(0xFF14452F).copy(alpha = 0.8f),
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun RecentTransactionsSection(
    transactions: List<TransactionSummary>,
    onViewAllClick: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Recent Transactions",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "View All",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = PapifiPrimary,
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .clickable(onClick = onViewAllClick)
                    .padding(4.dp)
            )
        }

        if (transactions.isEmpty()) {
            Text(
                text = "No recent transactions",
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(vertical = 12.dp)
            )
        } else {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    transactions.forEachIndexed { index, tx ->
                        TransactionItem(transaction = tx)
                        if (index < transactions.lastIndex) {
                            HorizontalDivider(
                                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TransactionItem(
    transaction: TransactionSummary
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(
                        if (transaction.isCredit) Color(0xFFE8F5E9) else Color(0xFFFFEBEE)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (transaction.isCredit) Icons.AutoMirrored.Filled.CallReceived else Icons.AutoMirrored.Filled.CallMade,
                    contentDescription = if (transaction.isCredit) "Credit" else "Debit",
                    tint = if (transaction.isCredit) Color(0xFF2E7D32) else Color(0xFFC62828),
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(
                    text = transaction.title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "${transaction.subtitle} • ${transaction.date}",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Column(
            horizontalAlignment = Alignment.End
        ) {
            val amountPrefix = if (transaction.isCredit) "+" else "-"
            Text(
                text = "$amountPrefix${MoneyFormatter.format(transaction.amount)}",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = if (transaction.isCredit) Color(0xFF2E7D32) else MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(2.dp))
            StatusBadge(status = transaction.status)
        }
    }
}

@Composable
private fun StatusBadge(status: TransactionStatus) {
    val (bgColor, textColor) = when (status) {
        TransactionStatus.SUCCESSFUL -> Color(0xFFE8F5E9) to Color(0xFF2E7D32)
        TransactionStatus.PENDING, TransactionStatus.PROCESSING -> Color(0xFFFFF3E0) to Color(0xFFEF6C00)
        TransactionStatus.FAILED -> Color(0xFFFFEBEE) to Color(0xFFC62828)
        TransactionStatus.UNKNOWN -> Color(0xFFF5F5F5) to Color(0xFF616161)
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(bgColor)
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        Text(
            text = status.name,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            color = textColor
        )
    }
}

@Composable
private fun WalletSelectorBottomSheet(
    balances: List<WalletBalance>,
    selectedCurrency: Currency,
    onCurrencySelected: (Currency) -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp, start = 16.dp, end = 16.dp, top = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Select Wallet",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Currency.entries.forEach { currency ->
                val balance = balances.find { it.currency == currency }
                val isSelected = currency == selectedCurrency

                Surface(
                    onClick = { onCurrencySelected(currency) },
                    shape = RoundedCornerShape(12.dp),
                    color = if (isSelected) PapifiPrimary.copy(alpha = 0.1f) else MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = currency.flag(), fontSize = 24.sp)
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = currency.displayName(),
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = if (balance != null) {
                                        if (balance.isProvisioned) {
                                            "Available: " + MoneyFormatter.format(balance.available)
                                        } else {
                                            "Not Enabled"
                                        }
                                    } else "Loading...",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        if (isSelected) {
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(CircleShape)
                                    .background(PapifiPrimary),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Selected",
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
