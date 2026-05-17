package com.example.gohyongwonton.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gohyongwonton.data.model.Order
import com.example.gohyongwonton.data.model.OrderStatus
import com.example.gohyongwonton.viewmodel.OrderViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderListScreen(
    viewModel: OrderViewModel,
    onBack: () -> Unit
) {
    val orders    by viewModel.orders.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Riwayat Pesanan 📦") },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, "Kembali") }
                }
            )
        }
    ) { padding ->
        when {
            isLoading -> {
                Box(Modifier.fillMaxSize().padding(padding), Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            orders.isEmpty() -> {
                Box(Modifier.fillMaxSize().padding(padding), Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("📦", fontSize = 64.sp)
                        Spacer(Modifier.height(16.dp))
                        Text("Belum ada pesanan", fontWeight = FontWeight.Medium)
                        Text("Yuk pesan sekarang!", fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
            else -> {
                LazyColumn(
                    modifier = Modifier.padding(padding),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(orders, key = { it.id }) { order ->
                        OrderCard(order = order)
                    }
                }
            }
        }
    }
}

@Composable
fun OrderCard(order: Order) {
    val statusColor = when (order.status) {
        OrderStatus.MENUNGGU   -> MaterialTheme.colorScheme.tertiary
        OrderStatus.DIPROSES   -> MaterialTheme.colorScheme.primary
        OrderStatus.DIANTAR    -> MaterialTheme.colorScheme.secondary
        OrderStatus.SELESAI    -> MaterialTheme.colorScheme.primaryContainer
        OrderStatus.DIBATALKAN -> MaterialTheme.colorScheme.errorContainer
    }
    val statusTextColor = when (order.status) {
        OrderStatus.SELESAI    -> MaterialTheme.colorScheme.onPrimaryContainer
        OrderStatus.DIBATALKAN -> MaterialTheme.colorScheme.onErrorContainer
        else                   -> MaterialTheme.colorScheme.onPrimary
    }

    Card(
        modifier  = Modifier.fillMaxWidth(),
        shape     = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
                Text("Pesanan #${order.id.take(8)}", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                Surface(color = statusColor, shape = RoundedCornerShape(20.dp)) {
                    Text(order.status.label,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        fontSize = 11.sp, fontWeight = FontWeight.Medium, color = statusTextColor)
                }
            }
            Spacer(Modifier.height(8.dp))
            // ↓ Gunakan menuItemName dan menuItemEmoji
            Text(
                order.items.joinToString(", ") { "${it.menuItemEmoji} ${it.menuItemName} x${it.quantity}" },
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2
            )
            Spacer(Modifier.height(8.dp))
            Divider()
            Spacer(Modifier.height(8.dp))
            Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
                Column {
                    Text(order.paymentMethod.label, fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("📍 ${order.address}", fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1)
                }
                Text("Rp ${formatRupiah(order.totalAmount)}",
                    fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            }
        }
    }
}