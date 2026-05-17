package com.example.gohyongwonton.data.model

data class CartItem(
    val menuItem: MenuItem,
    val quantity: Int = 1,
    val notes: String = ""
) {
    val subtotal: Int get() = menuItem.price * quantity
}