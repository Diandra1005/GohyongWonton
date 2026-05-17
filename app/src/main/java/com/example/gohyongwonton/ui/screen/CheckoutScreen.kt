package com.example.gohyongwonton.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gohyongwonton.data.model.PaymentMethod
import com.example.gohyongwonton.viewmodel.CartViewModel
import com.example.gohyongwonton.viewmodel.CheckoutViewModel

// HAPUS fun formatRupiah di sini — sudah dipindah ke Utils.kt

@Composable
fun SectionCard(title: String, content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Spacer(Modifier.height(12.dp))
            content()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(
    viewModel: CheckoutViewModel,
    cartViewModel: CartViewModel,
    userId: String,
    onBack: () -> Unit,
    onOrderPlaced: (String) -> Unit
) {
    val name      by viewModel.customerName.collectAsState()
    val address   by viewModel.address.collectAsState()
    val phone     by viewModel.phone.collectAsState()
    val note      by viewModel.note.collectAsState()
    val payment   by viewModel.paymentMethod.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error     by viewModel.error.collectAsState()
    val cartItems by cartViewModel.cartItems.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Checkout 📋") },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, "Kembali") }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.padding(padding).fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (error != null) {
                item {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            "⚠️ $error",
                            modifier = Modifier.padding(12.dp),
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            fontSize = 13.sp
                        )
                    }
                }
            }

            // Ringkasan Pesanan
            item {
                SectionCard(title = "🛒 Ringkasan Pesanan") {
                    cartItems.forEach { item ->
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                "${item.menuItem.name} x${item.quantity}",
                                fontSize = 13.sp,
                                modifier = Modifier.weight(1f)
                            )
                            Text(
                                "Rp ${formatRupiah(item.subtotal)}",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                    Divider(modifier = Modifier.padding(vertical = 8.dp))
                    Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
                        Text(
                            "Ongkos kirim",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 13.sp
                        )
                        Text("Rp ${formatRupiah(cartViewModel.deliveryFee())}", fontSize = 13.sp)
                    }
                    Row(Modifier.fillMaxWidth().padding(top = 4.dp), Arrangement.SpaceBetween) {
                        Text("Total Pembayaran", fontWeight = FontWeight.Bold)
                        Text(
                            "Rp ${formatRupiah(cartViewModel.grandTotal())}",
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            // Data Penerima
            item {
                SectionCard(title = "👤 Data Penerima") {
                    OutlinedTextField(
                        value = name, onValueChange = viewModel::setName,
                        label = { Text("Nama Lengkap *") }, singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = phone, onValueChange = viewModel::setPhone,
                        label = { Text("Nomor HP *") }, singleLine = true,
                        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                            keyboardType = androidx.compose.ui.text.input.KeyboardType.Phone
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = address, onValueChange = viewModel::setAddress,
                        label = { Text("Alamat Lengkap *") }, maxLines = 3,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = note, onValueChange = viewModel::setNote,
                        label = { Text("Catatan (opsional)") },
                        placeholder = { Text("Contoh: pedas sedikit, tanpa bawang...") },
                        maxLines = 2,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            // Metode Pembayaran
            item {
                SectionCard(title = "💳 Metode Pembayaran") {
                    PaymentMethod.values().forEach { method ->
                        Row(
                            Modifier.fillMaxWidth().padding(vertical = 2.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = payment == method,
                                onClick  = { viewModel.setPayment(method) }
                            )
                            Spacer(Modifier.width(4.dp))
                            Text("${method.icon} ${method.label}", fontSize = 14.sp)
                        }
                    }
                }
            }

            // Tombol Konfirmasi
            item {
                Button(
                    onClick = {
                        viewModel.placeOrder(
                            userId    = userId,
                            onSuccess = { order -> onOrderPlaced(order.id) },
                            onError   = { }
                        )
                    },
                    enabled  = viewModel.isFormValid() && !isLoading,
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape    = RoundedCornerShape(12.dp)
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color    = MaterialTheme.colorScheme.onPrimary
                        )
                    } else {
                        Text("Konfirmasi Pesanan 🎉", fontSize = 15.sp)
                    }
                }
                Spacer(Modifier.height(8.dp))
                Text(
                    "* Wajib diisi",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}