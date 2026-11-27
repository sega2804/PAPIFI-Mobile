@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.papifi.activity.ui.list

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.CallMade
import androidx.compose.material.icons.automirrored.filled.CallReceived
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.papifi.designsystem.PapifiPrimary
import com.papifi.model.Currency
import com.papifi.model.TransactionStatus
import com.papifi.ui.MoneyFormatter

@Composable
fun ActivityListScreen(
    viewModel: ActivityViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToReceipt: (String) -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is ActivityEffect.NavigateToReceipt -> onNavigateToReceipt(effect.txId)
            }
        }
    }

    val filteredTransactions = state.transactions.filter { tx ->
        if (state.searchQuery.isBlank()) true
        else tx.title.contains(state.searchQuery, ignoreCase = true) ||
                tx.reference.contains(state.searchQuery, ignoreCase = true) ||
                tx.subtitle.contains(state.searchQuery, ignoreCase = true)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Transaction History", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Search Bar
            OutlinedTextField(
                value = state.searchQuery,
                onValueChange = { viewModel.sendIntent(ActivityIntent.SetSearchQuery(it)) },
                placeholder = { Text("Search transactions or ref...") },
                leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = "Search") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PapifiPrimary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                )
            )

            // Filter Type Chips
            val filterTypes = listOf(
                "ALL" to "All",
                "SEND" to "Sent",
                "RECEIVE" to "Received",
                "CONVERT" to "Converted",
                "ADD" to "Add"
            )

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(filterTypes) { (typeKey, label) ->
                    val isSelected = (state.selectedFilterType == typeKey) || (state.selectedFilterType == null && typeKey == "ALL")
                    FilterChip(
                        selected = isSelected,
                        onClick = {
                            viewModel.sendIntent(ActivityIntent.SetFilterType(if (typeKey == "ALL") null else typeKey))
                        },
                        label = { Text(label) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = PapifiPrimary,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }

            // Currency Filter Chips
            val currencies = listOf(null to "All Currencies") + Currency.entries.map { it to it.code }
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(currencies) { (curr, label) ->
                    val isSelected = state.selectedCurrency == curr
                    FilterChip(
                        selected = isSelected,
                        onClick = {
                            viewModel.sendIntent(ActivityIntent.SetCurrency(curr))
                        },
                        label = { Text(label) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.secondary,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // List or Loading / Empty state
            if (state.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = PapifiPrimary)
                }
            } else if (filteredTransactions.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = "No transactions found",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(filteredTransactions) { tx ->
                        TransactionCard(
                            item = tx,
                            onClick = { viewModel.sendIntent(ActivityIntent.TransactionClicked(tx.id)) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TransactionCard(
    item: com.papifi.activity.domain.TransactionActivityItem,
    onClick: () -> Unit
) {
    val isCredit = item.type == "RECEIVE" || item.type == "ADD"
    val icon = if (isCredit) Icons.AutoMirrored.Filled.CallReceived else Icons.AutoMirrored.Filled.CallMade
    val iconBg = if (isCredit) Color(0xFFE8F5E9) else Color(0xFFFFEBEE)
    val iconTint = if (isCredit) Color(0xFF2E7D32) else Color(0xFFC62828)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(iconBg),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = item.type,
                        tint = iconTint,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = item.title,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "${item.subtitle} • ${item.date}",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Column(
                horizontalAlignment = Alignment.End
            ) {
                val prefix = if (isCredit) "+" else "-"
                Text(
                    text = "$prefix${MoneyFormatter.format(item.amount)}",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isCredit) Color(0xFF2E7D32) else MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                ActivityStatusBadge(status = item.status)
            }
        }
    }
}

@Composable
private fun ActivityStatusBadge(status: TransactionStatus) {
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
