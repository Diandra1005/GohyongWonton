package com.example.gohyongwonton.data.repository

import com.example.gohyongwonton.data.model.CartItem
import com.example.gohyongwonton.data.model.MenuItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class CartRepository {

    private val _cartItems = MutableStateFlow<List<CartItem>>(emptyList())
    val cartItems: StateFlow<List<CartItem>> = _cartItems.asStateFlow()

    fun addItem(menuItem: MenuItem, notes: String = "") {
        val current  = _cartItems.value.toMutableList()
        val existing = current.indexOfFirst { it.menuItem.id == menuItem.id }
        if (existing >= 0) {
            current[existing] = current[existing].copy(quantity = current[existing].quantity + 1)
        } else {
            current.add(CartItem(menuItem = menuItem, quantity = 1, notes = notes))
        }
        _cartItems.value = current
    }

    fun removeItem(menuItemId: String) {
        _cartItems.value = _cartItems.value.filter { it.menuItem.id != menuItemId }
    }

    fun increaseQty(menuItemId: String) { updateQty(menuItemId, 1) }

    fun decreaseQty(menuItemId: String) {
        val item = _cartItems.value.find { it.menuItem.id == menuItemId } ?: return
        if (item.quantity <= 1) removeItem(menuItemId) else updateQty(menuItemId, -1)
    }

    private fun updateQty(menuItemId: String, delta: Int) {
        val current = _cartItems.value.toMutableList()
        val index   = current.indexOfFirst { it.menuItem.id == menuItemId }
        if (index >= 0) {
            current[index] = current[index].copy(quantity = current[index].quantity + delta)
            _cartItems.value = current
        }
    }

    fun clearCart()        { _cartItems.value = emptyList() }
    fun getTotalPrice(): Int = _cartItems.value.sumOf { it.subtotal }
    fun getTotalItems(): Int = _cartItems.value.sumOf { it.quantity }

    fun updateNotes(menuItemId: String, notes: String) {
        val current = _cartItems.value.toMutableList()
        val index   = current.indexOfFirst { it.menuItem.id == menuItemId }
        if (index >= 0) {
            current[index] = current[index].copy(notes = notes)
            _cartItems.value = current
        }
    }
}