@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.papifi.activity.ui.receipt

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.outlined.ReportProblem
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.papifi.designsystem.PapifiButton
import com.papifi.designsystem.PapifiPrimary
import com.papifi.model.TransactionStatus
import com.papifi.ui.MoneyFormatter

@Composable
fun TransactionReceiptScreen(
    transactionId: String,
    viewModel: TransactionReceiptViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToSupport: (String) -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(transactionId) {
        viewModel.sendIntent(TransactionReceiptIntent.LoadTransaction(transactionId))
    }

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is TransactionReceiptEffect.ShareText -> {
                    val sendIntent = Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(Intent.EXTRA_TEXT, effect.text)
                        type = "text/plain"
                    }
                    val shareIntent = Intent.createChooser(sendIntent, "Share Receipt")
                    context.startActivity(shareIntent)
                }
                is TransactionReceiptEffect.NavigateToSupport -> {
                    onNavigateToSupport(effect.reference)
                }
                is TransactionReceiptEffect.ShowMessage -> {
                    // Toast or snackbar if needed
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Transaction Receipt", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = PapifiPrimary
                )
            } else if (state.transaction == null) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = "Transaction receipt not found", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            } else {
                val tx = state.transaction!!
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Header Status Icon & Title
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(PapifiPrimary.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Success",
                            tint = PapifiPrimary,
                            modifier = Modifier.size(36.dp)
                        )
                    }

                    Text(
                        text = tx.title,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Text(
                        text = MoneyFormatter.format(tx.amount) + " ${tx.amount.currency.code}",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = PapifiPrimary
                    )

                    ReceiptStatusBadge(status = tx.status)

                    Spacer(modifier = Modifier.height(8.dp))

                    // Card Breakdown
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            ReceiptRow(label = "Transaction Reference", value = tx.reference)
                            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                            ReceiptRow(label = "Date & Time", value = tx.date)
                            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                            ReceiptRow(label = "Transaction Type", value = tx.type)
                            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                            ReceiptRow(label = "Details", value = tx.subtitle)

                            if (tx.fee != null) {
                                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                                ReceiptRow(label = "Transaction Fee", value = MoneyFormatter.format(tx.fee!!) + " ${tx.fee!!.currency.code}")
                            }

                            if (tx.exchangeRate != null) {
                                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                                ReceiptRow(label = "Exchange Rate", value = "1 USD = ${tx.exchangeRate} NGN")
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Actions
                    PapifiButton(
                        text = "Share Receipt",
                        onClick = { viewModel.sendIntent(TransactionReceiptIntent.ShareReceipt(tx)) }
                    )

                    OutlinedButton(
                        onClick = { viewModel.sendIntent(TransactionReceiptIntent.ReportProblem(tx.reference)) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(imageVector = Icons.Outlined.ReportProblem, contentDescription = "Report", tint = MaterialTheme.colorScheme.error)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "Report a Problem", color = MaterialTheme.colorScheme.error)
                    }
                }
            }
        }
    }
}

@Composable
private fun ReceiptStatusBadge(status: TransactionStatus) {
    val (bgColor, textColor) = when (status) {
        TransactionStatus.SUCCESSFUL -> Color(0xFFE8F5E9) to Color(0xFF2E7D32)
        TransactionStatus.PENDING, TransactionStatus.PROCESSING -> Color(0xFFFFF3E0) to Color(0xFFEF6C00)
        TransactionStatus.FAILED -> Color(0xFFFFEBEE) to Color(0xFFC62828)
        TransactionStatus.UNKNOWN -> Color(0xFFF5F5F5) to Color(0xFF616161)
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(bgColor)
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(
            text = status.name,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = textColor
        )
    }
}

@Composable
private fun ReceiptRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
