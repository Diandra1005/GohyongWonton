package com.example.gohyongwonton.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gohyongwonton.data.model.*
import com.example.gohyongwonton.data.repository.CartRepository
import com.example.gohyongwonton.data.repository.RealtimeOrderRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// ───── Cart ViewModel ─────────────────────────────────────────────────
class CartViewModel(val cartRepo: CartRepository) : ViewModel() {

    val cartItems = cartRepo.cartItems

    // FIX: semua pakai String, bukan Int
    fun increase(itemId: String) = cartRepo.increaseQty(itemId)
    fun decrease(itemId: String) = cartRepo.decreaseQty(itemId)
    fun remove(itemId: String)   = cartRepo.removeItem(itemId)

    fun totalPrice(): Int  = cartRepo.getTotalPrice()
    fun totalItems(): Int  = cartRepo.getTotalItems()
    fun deliveryFee(): Int = if (cartRepo.getTotalItems() > 0) 5_000 else 0
    fun grandTotal(): Int  = totalPrice() + deliveryFee()
}

// ───── Checkout ViewModel ─────────────────────────────────────────────
class CheckoutViewModel(
    private val cartRepo: CartRepository,
    private val orderRepo: RealtimeOrderRepository  // FIX: pakai Realtime
) : ViewModel() {

    private val _customerName  = MutableStateFlow("")
    private val _address       = MutableStateFlow("")
    private val _phone         = MutableStateFlow("")
    private val _note          = MutableStateFlow("")
    private val _paymentMethod = MutableStateFlow(PaymentMethod.COD)
    private val _lastOrder     = MutableStateFlow<Order?>(null)
    private val _isLoading     = MutableStateFlow(false)
    private val _error         = MutableStateFlow<String?>(null)  // FIX: tambah error

    val customerName:  StateFlow<String>        = _customerName.asStateFlow()
    val address:       StateFlow<String>        = _address.asStateFlow()
    val phone:         StateFlow<String>        = _phone.asStateFlow()
    val note:          StateFlow<String>        = _note.asStateFlow()
    val paymentMethod: StateFlow<PaymentMethod> = _paymentMethod.asStateFlow()
    val lastOrder:     StateFlow<Order?>        = _lastOrder.asStateFlow()
    val isLoading:     StateFlow<Boolean>       = _isLoading.asStateFlow()
    val error:         StateFlow<String?>       = _error.asStateFlow()

    fun setName(v: String)           { _customerName.value = v }
    fun setAddress(v: String)        { _address.value = v }
    fun setPhone(v: String)          { _phone.value = v }
    fun setNote(v: String)           { _note.value = v }
    fun setPayment(v: PaymentMethod) { _paymentMethod.value = v }

    fun isFormValid(): Boolean =
        _customerName.value.isNotBlank() &&
                _address.value.isNotBlank() &&
                _phone.value.isNotBlank()

    // FIX: tambah userId + callback + viewModelScope + suspend
    fun placeOrder(
        userId: String,
        onSuccess: (Order) -> Unit,
        onError: (String) -> Unit
    ) {
        if (!isFormValid()) return
        _isLoading.value = true
        _error.value     = null

        viewModelScope.launch {
            try {
                val order = orderRepo.placeOrder(
                    userId        = userId,
                    cartItems     = cartRepo.cartItems.value,
                    customerName  = _customerName.value,
                    address       = _address.value,
                    phone         = _phone.value,
                    paymentMethod = _paymentMethod.value,
                    note          = _note.value
                )
                cartRepo.clearCart()
                _lastOrder.value = order
                onSuccess(order)
            } catch (e: Exception) {
                val msg = e.message ?: "Terjadi kesalahan, coba lagi"
                _error.value = msg
                onError(msg)
            } finally {
                _isLoading.value = false
            }
        }
    }
}

// ───── Order ViewModel ────────────────────────────────────────────────
class OrderViewModel(
    private val orderRepo: RealtimeOrderRepository  // FIX: pakai Realtime
) : ViewModel() {

    private val _orders    = MutableStateFlow<List<Order>>(emptyList())
    private val _isLoading = MutableStateFlow(false)

    val orders:    StateFlow<List<Order>> = _orders.asStateFlow()
    val isLoading: StateFlow<Boolean>     = _isLoading.asStateFlow()

    // FIX: loadOrders dengan userId untuk collect Flow dari Firebase
    fun loadOrders(userId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                orderRepo.getOrdersByUserFlow(userId).collect { list ->
                    _orders.value    = list
                    _isLoading.value = false
                }
            } catch (e: Exception) {
                _isLoading.value = false
            }
        }
    }

    fun getOrder(id: String): Order? = _orders.value.find { it.id == id }
}