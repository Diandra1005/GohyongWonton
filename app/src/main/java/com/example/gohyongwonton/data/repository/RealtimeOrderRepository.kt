package com.example.gohyongwonton.data.repository

import com.example.gohyongwonton.data.model.*
import com.example.gohyongwonton.data.remote.dto.RealtimeOrder
import com.example.gohyongwonton.data.remote.dto.RealtimeOrderItem
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class RealtimeOrderRepository {

    private val db        = FirebaseDatabase.getInstance()
    private val ordersRef = db.getReference("orders")

    fun getOrdersByUserFlow(userId: String): Flow<List<Order>> = callbackFlow {
        val userOrdersRef = ordersRef.child(userId)
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val orders = snapshot.children.mapNotNull { child ->
                    val id = child.key ?: return@mapNotNull null
                    child.getValue(RealtimeOrder::class.java)?.toOrder(id)
                }.sortedByDescending { it.createdAt }
                trySend(orders)
            }
            override fun onCancelled(error: DatabaseError) { close(error.toException()) }
        }
        userOrdersRef.addValueEventListener(listener)
        awaitClose { userOrdersRef.removeEventListener(listener) }
    }

    suspend fun placeOrder(
        userId: String,
        cartItems: List<CartItem>,
        customerName: String,
        address: String,
        phone: String,
        paymentMethod: PaymentMethod,
        note: String = ""
    ): Order {
        val newOrderRef = ordersRef.child(userId).push()
        val orderId     = newOrderRef.key ?: throw Exception("Gagal generate order ID")

        val itemsMap = cartItems.mapIndexed { index, cartItem ->
            "item_$index" to RealtimeOrderItem(
                menuItemId    = cartItem.menuItem.id,
                menuItemName  = cartItem.menuItem.name,
                menuItemPrice = cartItem.menuItem.price,
                menuItemEmoji = cartItem.menuItem.emoji,
                quantity      = cartItem.quantity,
                notes         = cartItem.notes
            )
        }.toMap()

        val realtimeOrder = RealtimeOrder(
            userId        = userId,
            customerName  = customerName,
            address       = address,
            phoneNumber   = phone,
            paymentMethod = paymentMethod.name,
            status        = OrderStatus.MENUNGGU.name,
            createdAt     = System.currentTimeMillis(),
            note          = note,
            deliveryFee   = 5_000,
            items         = itemsMap
        )

        newOrderRef.setValue(realtimeOrder).await()
        return realtimeOrder.toOrder(orderId)
    }

    suspend fun updateStatus(userId: String, orderId: String, status: OrderStatus) {
        ordersRef.child(userId).child(orderId).child("status").setValue(status.name).await()
    }
}