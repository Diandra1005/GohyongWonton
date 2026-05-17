package com.example.gohyongwonton.data.model

data class OrderItem(
    val menuItemId: String = "",
    val menuItemName: String = "",
    val menuItemPrice: Int = 0,
    val menuItemEmoji: String = "🥟",
    val quantity: Int = 1,
    val notes: String = ""
) {
    val subtotal: Int get() = menuItemPrice * quantity
}