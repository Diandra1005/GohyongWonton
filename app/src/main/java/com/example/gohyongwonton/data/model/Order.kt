package com.example.gohyongwonton.data.model

data class Order(
    val id: String = "",
    val userId: String = "",                          // ← Firebase Auth UID
    val items: List<OrderItem> = emptyList(),
    val customerName: String = "",
    val address: String = "",
    val phoneNumber: String = "",
    val paymentMethod: PaymentMethod = PaymentMethod.COD,
    val status: OrderStatus = OrderStatus.MENUNGGU,
    val createdAt: Long = System.currentTimeMillis(),
    val note: String = "",
    val deliveryFee: Int = 5_000
) {
    val totalAmount: Int get() = items.sumOf { it.subtotal } + deliveryFee
    val totalItems: Int get()  = items.sumOf { it.quantity }
}

enum class PaymentMethod(val label: String, val icon: String) {
    COD("Bayar di Tempat (COD)", "💵"),
    TRANSFER("Transfer Bank", "🏦"),
    EWALLET("E-Wallet (OVO/GoPay/Dana)", "📱")
}

enum class OrderStatus(val label: String, val color: String) {
    MENUNGGU("Menunggu Konfirmasi", "Orange"),
    DIPROSES("Sedang Diproses", "Blue"),
    DIANTAR("Sedang Diantar", "Purple"),
    SELESAI("Pesanan Selesai", "Green"),
    DIBATALKAN("Dibatalkan", "Red")
}