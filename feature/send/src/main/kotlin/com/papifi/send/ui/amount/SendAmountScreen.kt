@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.papifi.send.ui.amount

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.papifi.designsystem.PapifiButton
import com.papifi.designsystem.PapifiPrimary
import com.papifi.model.Money
import com.papifi.send.domain.TransferRoute
import com.papifi.ui.MoneyFormatter
import org.koin.androidx.compose.koinViewModel

@Composable
fun SendAmountScreen(
    route: TransferRoute,
    viewModel: SendAmountViewModel = koinViewModel(),
    onNavigateBack: () -> Unit = {},
    onNavigateToReview: (TransferRoute, Long, String) -> Unit = { _, _, _ -> }
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = androidx.compose.ui.platform.LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is SendAmountEffect.NavigateToReview -> {
                    onNavigateToReview(effect.route, effect.amountMinor, effect.note)
                }
                is SendAmountEffect.ShowError -> {
                    Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    val formattedBalance = MoneyFormatter.format(Money(state.availableBalanceMinor, state.currency))

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Enter Amount") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            Text(
                text = "Available Balance: $formattedBalance",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = state.amountString,
                onValueChange = { viewModel.sendIntent(SendAmountIntent.AmountChanged(it)) },
                label = { Text("Amount (${state.currency.symbol})") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                isError = state.errorMessage != null,
                shape = MaterialTheme.shapes.medium
            )

            if (state.errorMessage != null) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = state.errorMessage ?: "",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = state.note,
                onValueChange = { viewModel.sendIntent(SendAmountIntent.NoteChanged(it)) },
                label = { Text("Transfer Note (Optional)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = MaterialTheme.shapes.medium
            )

            Spacer(modifier = Modifier.height(32.dp))

            PapifiButton(
                text = "Continue to Review",
                onClick = { viewModel.sendIntent(SendAmountIntent.ContinueClicked) }
            )
        }
    }
}
