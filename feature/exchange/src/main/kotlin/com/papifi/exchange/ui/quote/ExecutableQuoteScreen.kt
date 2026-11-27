@file:OptIn(ExperimentalMaterial3Api::class)

package com.papifi.exchange.ui.quote

import android.widget.Toast
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.papifi.designsystem.PapifiButton
import com.papifi.designsystem.PapifiCard
import com.papifi.designsystem.PapifiPrimary
import com.papifi.model.Currency
import com.papifi.ui.MoneyFormatter
import org.koin.androidx.compose.koinViewModel
import java.util.Locale

@Composable
fun ExecutableQuoteScreen(
    fromCurrency: Currency,
    toCurrency: Currency,
    fromAmountMinor: Long,
    viewModel: ExecutableQuoteViewModel = koinViewModel(),
    onNavigateBack: () -> Unit = {},
    onNavigateToHome: () -> Unit = {}
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(fromCurrency, toCurrency, fromAmountMinor) {
        viewModel.sendIntent(
            ExecutableQuoteIntent.InitQuote(
                fromCurrency = fromCurrency,
                toCurrency = toCurrency,
                fromAmountMinor = fromAmountMinor
            )
        )
    }

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                ExecutableQuoteEffect.NavigateToHome -> onNavigateToHome()
                is ExecutableQuoteEffect.ShowToast -> {
                    Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Guaranteed Quote",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (state.isLoading && state.quote == null) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = PapifiPrimary
                )
            } else {
                state.quote?.let { quote ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // 1. Timer Ticker Banner
                        TimerTickerCard(
                            remainingSeconds = state.remainingSeconds,
                            isExpired = state.isExpired
                        )

                        // 2. Guaranteed Quote Breakdown Card
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(20.dp),
                                verticalArrangement = Arrangement.spacedBy(14.dp)
                            ) {
                                Text(
                                    text = "Quote Breakdown",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )

                                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

                                // You Send
                                QuoteRow(
                                    label = "You Send",
                                    value = MoneyFormatter.format(quote.fromAmount),
                                    isBoldValue = true
                                )

                                // You Get
                                QuoteRow(
                                    label = "You Get",
                                    value = MoneyFormatter.format(quote.toAmount),
                                    valueColor = PapifiPrimary,
                                    isBoldValue = true
                                )

                                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

                                // Guaranteed Exchange Rate
                                val rateFormatted = if (quote.guaranteedRate >= 1.0) {
                                    String.format(Locale.US, "%,.2f", quote.guaranteedRate)
                                } else {
                                    String.format(Locale.US, "%.6f", quote.guaranteedRate)
                                }
                                QuoteRow(
                                    label = "Guaranteed Exchange Rate",
                                    value = "1 ${quote.fromAmount.currency.code} = $rateFormatted ${quote.toAmount.currency.code}"
                                )

                                // Transaction Fee
                                QuoteRow(
                                    label = "Transaction Fee",
                                    value = MoneyFormatter.format(quote.fee)
                                )
                            }
                        }

                        // 3. Quote Expired Badge / Banner
                        if (state.isExpired) {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Warning,
                                        contentDescription = "Expired",
                                        tint = MaterialTheme.colorScheme.error,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = "Quote Expired",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp,
                                            color = MaterialTheme.colorScheme.onErrorContainer
                                        )
                                        Text(
                                            text = "The guaranteed exchange rate has expired. Please refresh to fetch a new quote.",
                                            fontSize = 12.sp,
                                            color = MaterialTheme.colorScheme.onErrorContainer
                                        )
                                    }
                                }
                            }
                        }

                        if (state.errorMessage != null) {
                            Text(
                                text = state.errorMessage!!,
                                color = MaterialTheme.colorScheme.error,
                                fontSize = 13.sp,
                                modifier = Modifier.padding(horizontal = 4.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // 4. Buttons (Refresh Quote & Confirm Exchange)
                        if (state.isExpired) {
                            Button(
                                onClick = { viewModel.sendIntent(ExecutableQuoteIntent.RefreshQuoteClicked) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = PapifiPrimary),
                                shape = MaterialTheme.shapes.medium
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = "Refresh",
                                    tint = Color.White
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Refresh Quote",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        } else {
                            PapifiButton(
                                text = if (state.isExecuting) "Processing Swap..." else "Confirm & Swap Money",
                                onClick = { viewModel.sendIntent(ExecutableQuoteIntent.ConfirmExchangeClicked) },
                                enabled = !state.isExecuting && !state.isExpired
                            )
                        }
                    }
                }
            }

            // Success Dialog
            if (state.isSuccess) {
                AlertDialog(
                    onDismissRequest = { viewModel.sendIntent(ExecutableQuoteIntent.DismissSuccessDialog) },
                    icon = {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(PapifiPrimary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Success",
                                tint = Color.White,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    },
                    title = {
                        Text(
                            text = "Exchange Successful!",
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    },
                    text = {
                        state.quote?.let { quote ->
                            Text(
                                text = "You have successfully converted ${MoneyFormatter.format(quote.fromAmount)} to ${MoneyFormatter.format(quote.toAmount)} at guaranteed rate.",
                                textAlign = TextAlign.Center,
                                fontSize = 14.sp
                            )
                        }
                    },
                    confirmButton = {
                        PapifiButton(
                            text = "Back to Home",
                            onClick = { viewModel.sendIntent(ExecutableQuoteIntent.DismissSuccessDialog) }
                        )
                    }
                )
            }
        }
    }
}

@Composable
private fun TimerTickerCard(
    remainingSeconds: Int,
    isExpired: Boolean
) {
    val minutes = remainingSeconds / 60
    val seconds = remainingSeconds % 60
    val timeFormatted = String.format(Locale.US, "%02d:%02d", minutes, seconds)

    val bgColor = if (isExpired) Color(0xFFFFEBEE) else PapifiPrimary.copy(alpha = 0.12f)
    val textColor = if (isExpired) Color(0xFFC62828) else PapifiPrimary

    Surface(
        shape = RoundedCornerShape(12.dp),
        color = bgColor,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.Schedule,
                contentDescription = "Timer",
                tint = textColor,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = if (isExpired) "Quote Expired" else "Quote expires in $timeFormatted",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = textColor
            )
        }
    }
}

@Composable
private fun QuoteRow(
    label: String,
    value: String,
    valueColor: Color = MaterialTheme.colorScheme.onSurface,
    isBoldValue: Boolean = false
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            fontSize = 15.sp,
            fontWeight = if (isBoldValue) FontWeight.Bold else FontWeight.Medium,
            color = valueColor
        )
    }
}
