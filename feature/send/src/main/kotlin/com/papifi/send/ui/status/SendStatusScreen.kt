@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.papifi.send.ui.status

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.papifi.designsystem.PapifiButton
import com.papifi.designsystem.PapifiCard
import com.papifi.designsystem.PapifiPrimary
import com.papifi.send.ui.review.ReviewRow
import org.koin.androidx.compose.koinViewModel

@Composable
fun SendStatusScreen(
    transactionReference: String,
    viewModel: SendStatusViewModel = koinViewModel(),
    onNavigateToWallet: () -> Unit = {}
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(transactionReference) {
        viewModel.sendIntent(SendStatusIntent.Init(transactionReference))
    }

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is SendStatusEffect.ReturnToWallet -> {
                    onNavigateToWallet()
                }
                is SendStatusEffect.ShowReceipt -> {
                    Toast.makeText(context, "Receipt for ${effect.reference} saved", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(40.dp))

            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = PapifiPrimary,
                    modifier = Modifier.size(40.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Transfer Successful!",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Your transfer has been processed successfully.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            PapifiCard(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    ReviewRow("Transaction Ref", state.transactionReference)
                    Spacer(modifier = Modifier.height(8.dp))
                    ReviewRow("Status", "SUCCESSFUL")
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            PapifiButton(
                text = "View Receipt",
                onClick = { viewModel.sendIntent(SendStatusIntent.ViewReceiptClicked) }
            )

            Spacer(modifier = Modifier.height(12.dp))

            androidx.compose.material3.OutlinedButton(
                onClick = { viewModel.sendIntent(SendStatusIntent.ReturnToWalletClicked) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = MaterialTheme.shapes.medium
            ) {
                Text(text = "Return to Wallet", style = MaterialTheme.typography.bodyLarge)
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
