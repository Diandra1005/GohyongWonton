package com.example.gohyongwonton.data.remote.dto

import com.example.gohyongwonton.data.model.*

data class RealtimeOrderItem(
    val menuItemId: String = "",
    val menuItemName: String = "",
    val menuItemPrice: Int = 0,
    val menuItemEmoji: String = "🥟",
    val quantity: Int = 1,
    val notes: String = ""
) {
    fun toOrderItem(): OrderItem = OrderItem(
        menuItemId    = menuItemId,
        menuItemName  = menuItemName,
        menuItemPrice = menuItemPrice,
        menuItemEmoji = menuItemEmoji,
        quantity      = quantity,
        notes         = notes
    )
}

data class RealtimeOrder(
    val userId: String = "",
    val customerName: String = "",
    val address: String = "",
    val phoneNumber: String = "",
    val paymentMethod: String = "",
    val status: String = "",
    val createdAt: Long = 0L,
    val note: String = "",
    val deliveryFee: Int = 5000,
    val items: Map<String, RealtimeOrderItem> = emptyMap()  // ← Map karena RTDB tidak support List langsung
) {
    fun toOrder(id: String): Order = Order(
        id            = id,
        userId        = userId,
        customerName  = customerName,
        address       = address,
        phoneNumber   = phoneNumber,
        paymentMethod = try { PaymentMethod.valueOf(paymentMethod) } catch (e: Exception) { PaymentMethod.COD },
        status        = try { OrderStatus.valueOf(status) } catch (e: Exception) { OrderStatus.MENUNGGU },
        createdAt     = createdAt,
        note          = note,
        deliveryFee   = deliveryFee,
        items         = items.values.map { it.toOrderItem() }
    )
}