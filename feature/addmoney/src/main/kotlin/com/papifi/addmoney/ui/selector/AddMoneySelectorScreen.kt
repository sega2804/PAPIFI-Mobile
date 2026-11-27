package com.papifi.addmoney.ui.selector

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.papifi.addmoney.domain.FundingMethod
import com.papifi.designsystem.PapifiButton
import com.papifi.model.Currency

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddMoneySelectorScreen(
    viewModel: AddMoneySelectorViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToNgnBankDetails: () -> Unit,
    onNavigateToForeignDepositDetails: (Currency) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(viewModel.effect) {
        viewModel.effect.collect { effect ->
            when (effect) {
                AddMoneySelectorEffect.NavigateToNgnBankDetails -> onNavigateToNgnBankDetails()
                is AddMoneySelectorEffect.NavigateToForeignDepositDetails -> onNavigateToForeignDepositDetails(effect.currency)
                is AddMoneySelectorEffect.ShowError -> {
                    Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add Money", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "Select Currency & Funding Method",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                // Currency Selector Chips
                Text(
                    text = "Currency",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(Currency.entries.toTypedArray()) { currency ->
                        val isSelected = currency == uiState.selectedCurrency
                        FilterChip(
                            selected = isSelected,
                            onClick = {
                                viewModel.sendIntent(AddMoneySelectorIntent.SelectCurrency(currency))
                            },
                            label = {
                                Text("${currency.symbol} ${currency.code}")
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Color(0xFF14452F),
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Funding Method",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                if (uiState.isLoading) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = Color(0xFF14452F))
                    }
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(uiState.availableMethods) { method ->
                            val isSelected = method == uiState.selectedMethod
                            FundingMethodCard(
                                method = method,
                                isSelected = isSelected,
                                onClick = {
                                    viewModel.sendIntent(AddMoneySelectorIntent.SelectMethod(method))
                                }
                            )
                        }
                    }
                }
            }

            Column(modifier = Modifier.padding(bottom = 16.dp)) {
                PapifiButton(
                    text = "Proceed to Funding",
                    onClick = {
                        viewModel.sendIntent(AddMoneySelectorIntent.ProceedClicked)
                    },
                    enabled = !uiState.isLoading && uiState.selectedMethod != null
                )
            }
        }
    }
}

@Composable
private fun FundingMethodCard(
    method: FundingMethod,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val (title, subtitle, icon) = when (method) {
        FundingMethod.VIRTUAL_ACCOUNT -> Triple(
            "Instant Bank Transfer",
            "Transfer via dedicated virtual bank account number",
            Icons.Default.AccountBalance
        )
        FundingMethod.BANK_TRANSFER -> Triple(
            "Direct Bank Deposit",
            "Standard bank transfer with rapid settlement",
            Icons.Default.SwapHoriz
        )
        FundingMethod.FOREIGN_WIRE -> Triple(
            "Foreign Wire / Deposit",
            "International SWIFT, SEPA, or domestic wire transfer",
            Icons.Default.Public
        )
        FundingMethod.CARD -> Triple(
            "Card Deposit",
            "Instant deposit using your Debit or Credit Card",
            Icons.Default.CreditCard
        )
    }

    val borderColor = if (isSelected) Color(0xFF14452F) else MaterialTheme.colorScheme.outlineVariant
    val backgroundColor = if (isSelected) Color(0xFF14452F).copy(alpha = 0.05f) else MaterialTheme.colorScheme.surface

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF14452F).copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color(0xFF14452F)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            RadioButton(
                selected = isSelected,
                onClick = onClick,
                colors = RadioButtonDefaults.colors(selectedColor = Color(0xFF14452F))
            )
        }
    }
}
