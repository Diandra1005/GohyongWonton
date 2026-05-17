package com.example.gohyongwonton.ui.screen

import android.graphics.Bitmap
import android.graphics.Color
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gohyongwonton.data.model.Order
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter

// ── Helper: generate QR code Bitmap dari string ───────────────────────
fun generateQrBitmap(content: String, size: Int = 512): Bitmap {
    val hints   = mapOf(EncodeHintType.MARGIN to 1)
    val writer  = QRCodeWriter()
    val matrix  = writer.encode(content, BarcodeFormat.QR_CODE, size, size, hints)
    val bmp     = Bitmap.createBitmap(size, size, Bitmap.Config.RGB_565)
    for (x in 0 until size) {
        for (y in 0 until size) {
            bmp.setPixel(x, y, if (matrix[x, y]) Color.BLACK else Color.WHITE)
        }
    }
    return bmp
}

// ── OrderSuccessScreen ────────────────────────────────────────────────
@Composable
fun OrderSuccessScreen(
    order: Order?,
    onBackToHome: () -> Unit,
    onViewOrders: () -> Unit
) {
    // Konten QR: orderId + nama + total, dipisah newline
    val qrContent = order?.let {
        "Order ID: ${it.id}\nNama: ${it.customerName}\nTotal: Rp ${formatRupiah(it.totalAmount)}\nStatus: ${it.status.label}"
    } ?: "Order tidak ditemukan"

    val qrBitmap = remember(qrContent) {
        generateQrBitmap(qrContent)
    }

    LazyColumn(
        modifier            = Modifier.fillMaxSize(),
        contentPadding      = PaddingValues(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // ── Header sukses ──────────────────────────────────────────
        item {
            Spacer(Modifier.height(24.dp))
            Text("🎉", fontSize = 72.sp)
            Spacer(Modifier.height(8.dp))
            Text(
                "Pesanan Berhasil!",
                fontSize     = 24.sp,
                fontWeight   = FontWeight.Bold,
                color        = MaterialTheme.colorScheme.primary,
                textAlign    = TextAlign.Center
            )
            Spacer(Modifier.height(4.dp))
            Text(
                "Terima kasih sudah memesan.\nPesananmu sedang kami proses 🍜",
                fontSize  = 14.sp,
                textAlign = TextAlign.Center,
                color     = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // ── QR Code ────────────────────────────────────────────────
        item {
            Card(
                shape     = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                modifier  = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier            = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "QR Pesanan",
                        fontWeight = FontWeight.Bold,
                        fontSize   = 14.sp
                    )
                    Text(
                        "Tunjukkan kode ini saat ambil pesanan",
                        fontSize = 11.sp,
                        color    = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.height(16.dp))
                    Image(
                        bitmap              = qrBitmap.asImageBitmap(),
                        contentDescription  = "QR Code Pesanan",
                        modifier            = Modifier
                            .size(200.dp)
                            .background(
                                androidx.compose.ui.graphics.Color.White,
                                RoundedCornerShape(8.dp)
                            )
                            .padding(8.dp)
                    )
                    Spacer(Modifier.height(12.dp))
                    Text(
                        "#${order?.id?.take(8)?.uppercase() ?: "-"}",
                        fontWeight = FontWeight.Bold,
                        fontSize   = 16.sp,
                        color      = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        // ── Detail Pesanan ─────────────────────────────────────────
        if (order != null) {
            item {
                Card(
                    shape     = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier  = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("📋 Detail Pesanan", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Spacer(Modifier.height(12.dp))

                        // Item-item yang dipesan
                        order.items.forEach { item ->
                            Row(
                                modifier             = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 3.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    "${item.menuItemEmoji} ${item.menuItemName} x${item.quantity}",
                                    fontSize = 13.sp,
                                    modifier = Modifier.weight(1f)
                                )
                                Text(
                                    "Rp ${formatRupiah(item.subtotal)}",
                                    fontSize   = 13.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }

                        Divider(modifier = Modifier.padding(vertical = 8.dp))

                        // Ongkir
                        Row(
                            Modifier.fillMaxWidth(),
                            Arrangement.SpaceBetween
                        ) {
                            Text(
                                "Ongkos kirim",
                                fontSize = 13.sp,
                                color    = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text("Rp ${formatRupiah(order.deliveryFee)}", fontSize = 13.sp)
                        }

                        Spacer(Modifier.height(4.dp))

                        // Total
                        Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
                            Text("Total", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text(
                                "Rp ${formatRupiah(order.totalAmount)}",
                                fontWeight = FontWeight.Bold,
                                fontSize   = 14.sp,
                                color      = MaterialTheme.colorScheme.primary
                            )
                        }

                        Divider(modifier = Modifier.padding(vertical = 8.dp))

                        // Info pengiriman
                        InfoRow("👤 Nama",     order.customerName)
                        InfoRow("📱 HP",        order.phoneNumber)
                        InfoRow("📍 Alamat",   order.address)
                        InfoRow("💳 Pembayaran", order.paymentMethod.label)
                        if (order.note.isNotBlank()) {
                            InfoRow("📝 Catatan", order.note)
                        }
                    }
                }
            }
        }

        // ── Tombol aksi ────────────────────────────────────────────
        item {
            Button(
                onClick  = onViewOrders,
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape    = RoundedCornerShape(12.dp)
            ) {
                Text("Lihat Riwayat Pesanan 📦", fontSize = 14.sp)
            }
            Spacer(Modifier.height(8.dp))
            OutlinedButton(
                onClick  = onBackToHome,
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape    = RoundedCornerShape(12.dp)
            ) {
                Text("Kembali ke Menu 🏠", fontSize = 14.sp)
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}

// ── Helper composable baris info ──────────────────────────────────────
@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            label,
            fontSize = 12.sp,
            color    = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(0.4f)
        )
        Text(
            value,
            fontSize = 12.sp,
            modifier = Modifier.weight(0.6f),
            textAlign = TextAlign.End
        )
    }
}