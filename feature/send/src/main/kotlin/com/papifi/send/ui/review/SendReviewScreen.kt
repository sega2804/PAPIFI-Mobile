@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.papifi.send.ui.review

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.papifi.designsystem.PapifiButton
import com.papifi.designsystem.PapifiCard
import com.papifi.designsystem.PapifiPrimary
import com.papifi.model.Money
import com.papifi.send.domain.TransferRoute
import com.papifi.send.ui.pin.PinVerificationDialog
import com.papifi.ui.MoneyFormatter
import org.koin.androidx.compose.koinViewModel

@Composable
fun SendReviewScreen(
    recipientId: String,
    route: TransferRoute,
    amountMinor: Long,
    note: String,
    viewModel: SendReviewViewModel = koinViewModel(),
    onNavigateBack: () -> Unit = {},
    onNavigateToStatus: (String) -> Unit = {}
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(recipientId, route, amountMinor) {
        viewModel.sendIntent(SendReviewIntent.InitReview(recipientId, route, amountMinor, note))
    }

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is SendReviewEffect.NavigateToStatus -> {
                    onNavigateToStatus(effect.transactionReference)
                }
                is SendReviewEffect.ShowError -> {
                    Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    val currency = state.beneficiary?.currency ?: com.papifi.model.Currency.NGN
    val formattedAmount = MoneyFormatter.format(Money(state.amountMinor, currency))
    val formattedFee = MoneyFormatter.format(Money(state.feeMinor, currency))
    val totalMinor = state.amountMinor + if (currency == com.papifi.model.Currency.NGN) state.feeMinor else 0L
    val formattedTotal = MoneyFormatter.format(Money(totalMinor, currency))

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Review Transfer") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                PapifiCard(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "Transfer Breakdown",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = PapifiPrimary
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        ReviewRow("Recipient", state.beneficiary?.name ?: "Loading...")
                        ReviewRow("Account / Bank", "${state.beneficiary?.accountNumber ?: ""} • ${state.beneficiary?.bankName ?: ""}")
                        ReviewRow("Transfer Route", route.name.replace("_", " "))
                        if (note.isNotBlank()) {
                            ReviewRow("Note", note)
                        }
                        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
                        ReviewRow("Amount", formattedAmount)
                        ReviewRow("Fee", formattedFee)
                        ReviewRow("Exchange Rate", state.exchangeRate)
                        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
                        ReviewRow("Total Debited", formattedTotal, isBold = true)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (state.idempotencyKey.isNotBlank()) {
                    Text(
                        text = "Idempotency Key: ${state.idempotencyKey.take(16)}...",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                PapifiButton(
                    text = "Confirm & Pay",
                    onClick = { viewModel.sendIntent(SendReviewIntent.ConfirmAndPayClicked) }
                )
            }

            if (state.isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(androidx.compose.ui.graphics.Color.Black.copy(alpha = 0.3f)),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = PapifiPrimary)
                }
            }
        }

        if (state.isPinDialogVisible) {
            PinVerificationDialog(
                onDismiss = { viewModel.sendIntent(SendReviewIntent.TogglePinDialog(false)) },
                onPinVerified = { pin -> viewModel.sendIntent(SendReviewIntent.PinEntered(pin)) }
            )
        }
    }
}

@Composable
fun ReviewRow(label: String, value: String, isBold: Boolean = false) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
