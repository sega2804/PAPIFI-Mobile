@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.papifi.send.ui.destination

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Wallet
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.papifi.designsystem.PapifiCard
import com.papifi.designsystem.PapifiPrimary
import com.papifi.send.domain.TransferRoute

@Composable
fun TransferDestinationScreen(
    onNavigateBack: () -> Unit = {},
    onNavigateToAmount: (TransferRoute) -> Unit = {}
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Transfer Route") },
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
                text = "Select transfer destination route",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(16.dp))

            RouteCard(
                title = "Local Bank Transfer",
                subtitle = "Send to any local bank account instantly",
                icon = Icons.Default.AccountBalance,
                onClick = { onNavigateToAmount(TransferRoute.LOCAL_BANK) }
            )

            Spacer(modifier = Modifier.height(12.dp))

            RouteCard(
                title = "International Wire",
                subtitle = "Send funds globally via SWIFT / wire",
                icon = Icons.Default.Public,
                onClick = { onNavigateToAmount(TransferRoute.INTERNATIONAL) }
            )

            Spacer(modifier = Modifier.height(12.dp))

            RouteCard(
                title = "PAPIFI Wallet Tag",
                subtitle = "Send instantly to another PAPIFI user",
                icon = Icons.Default.Wallet,
                onClick = { onNavigateToAmount(TransferRoute.PAPIFI_WALLET) }
            )
        }
    }
}

@Composable
fun RouteCard(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    PapifiCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = PapifiPrimary,
                modifier = Modifier.padding(end = 16.dp)
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
