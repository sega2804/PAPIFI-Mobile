package com.papifi.addmoney.ui.foreign

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.papifi.designsystem.PapifiButton
import com.papifi.model.Currency
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForeignDepositDetailsScreen(
    currency: Currency,
    viewModel: ForeignDepositDetailsViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToPending: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(currency) {
        viewModel.loadDetails(currency)
    }

    LaunchedEffect(viewModel.effect) {
        viewModel.effect.collect { effect ->
            when (effect) {
                ForeignDepositDetailsEffect.NavigateToPending -> onNavigateToPending()
                is ForeignDepositDetailsEffect.ShowToast -> {
                    Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
                }
                is ForeignDepositDetailsEffect.CopyAllToClipboard -> {
                    clipboardManager.setText(AnnotatedString(effect.text))
                    scope.launch {
                        snackbarHostState.showSnackbar("All deposit details copied!")
                    }
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("${currency.code} Foreign Deposit", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            if (uiState.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color(0xFF14452F))
                }
            } else {
                val details = uiState.fundingDetails
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                ) {
                    if (details == null || !details.isAvailable) {
                        // "Not Available" banner if currency receiving details are unprovisioned
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 16.dp),
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
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.size(32.dp)
                                )
                                Spacer(modifier = Modifier.width(16.dp))
                                Column {
                                    Text(
                                        text = "Not Available",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp,
                                        color = MaterialTheme.colorScheme.onErrorContainer
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "Receiving deposit details for ${currency.code} are currently unprovisioned. Please select another currency.",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onErrorContainer
                                    )
                                }
                            }
                        }
                    } else {
                        Text(
                            text = "International Wire / Bank Details",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )

                        // Foreign Account Details Card
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                            ) {
                                DetailRow(
                                    label = "Account Name",
                                    value = details.accountName,
                                    onCopy = {
                                        clipboardManager.setText(AnnotatedString(details.accountName))
                                        scope.launch { snackbarHostState.showSnackbar("Copied Account Name") }
                                    }
                                )
                                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                                DetailRow(
                                    label = "Account Number",
                                    value = details.accountNumber,
                                    onCopy = {
                                        clipboardManager.setText(AnnotatedString(details.accountNumber))
                                        scope.launch { snackbarHostState.showSnackbar("Copied Account Number") }
                                    }
                                )

                                details.iban?.let { iban ->
                                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                                    DetailRow(
                                        label = "IBAN",
                                        value = iban,
                                        onCopy = {
                                            clipboardManager.setText(AnnotatedString(iban))
                                            scope.launch { snackbarHostState.showSnackbar("Copied IBAN") }
                                        }
                                    )
                                }

                                details.swiftCode?.let { swift ->
                                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                                    DetailRow(
                                        label = "SWIFT / BIC",
                                        value = swift,
                                        onCopy = {
                                            clipboardManager.setText(AnnotatedString(swift))
                                            scope.launch { snackbarHostState.showSnackbar("Copied SWIFT/BIC") }
                                        }
                                    )
                                }

                                details.routingNumber?.let { routing ->
                                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                                    DetailRow(
                                        label = "Routing Number",
                                        value = routing,
                                        onCopy = {
                                            clipboardManager.setText(AnnotatedString(routing))
                                            scope.launch { snackbarHostState.showSnackbar("Copied Routing Number") }
                                        }
                                    )
                                }

                                details.sortCode?.let { sortCode ->
                                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                                    DetailRow(
                                        label = "Sort Code",
                                        value = sortCode,
                                        onCopy = {
                                            clipboardManager.setText(AnnotatedString(sortCode))
                                            scope.launch { snackbarHostState.showSnackbar("Copied Sort Code") }
                                        }
                                    )
                                }

                                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                                DetailRow(
                                    label = "Bank Name",
                                    value = details.bankName,
                                    onCopy = {
                                        clipboardManager.setText(AnnotatedString(details.bankName))
                                        scope.launch { snackbarHostState.showSnackbar("Copied Bank Name") }
                                    }
                                )

                                details.bankAddress?.let { address ->
                                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                                    DetailRow(
                                        label = "Bank Address",
                                        value = address,
                                        onCopy = {
                                            clipboardManager.setText(AnnotatedString(address))
                                            scope.launch { snackbarHostState.showSnackbar("Copied Bank Address") }
                                        }
                                    )
                                }

                                details.referenceCode?.let { reference ->
                                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                                    DetailRow(
                                        label = "Reference Code / Note",
                                        value = reference,
                                        isHighlight = true,
                                        onCopy = {
                                            clipboardManager.setText(AnnotatedString(reference))
                                            scope.launch { snackbarHostState.showSnackbar("Copied Reference Code") }
                                        }
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Instructions Card
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF14452F).copy(alpha = 0.08f))
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Info,
                                    contentDescription = null,
                                    tint = Color(0xFF14452F),
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = details.instructions,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Column(
                    modifier = Modifier.padding(bottom = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (details != null && details.isAvailable) {
                        OutlinedButton(
                            onClick = {
                                viewModel.sendIntent(ForeignDepositDetailsIntent.CopyAllDetailsClicked)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp),
                            shape = MaterialTheme.shapes.medium
                        ) {
                            Icon(
                                imageVector = Icons.Default.ContentCopy,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Copy All Details")
                        }
                    }

                    PapifiButton(
                        text = "Done",
                        onClick = {
                            viewModel.sendIntent(ForeignDepositDetailsIntent.DoneClicked)
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun DetailRow(
    label: String,
    value: String,
    isHighlight: Boolean = false,
    onCopy: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = value,
                fontSize = 15.sp,
                fontWeight = if (isHighlight) FontWeight.Bold else FontWeight.SemiBold,
                color = if (isHighlight) Color(0xFF14452F) else MaterialTheme.colorScheme.onSurface
            )
        }
        IconButton(onClick = onCopy) {
            Icon(
                imageVector = Icons.Default.ContentCopy,
                contentDescription = "Copy $label",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
