package com.example.gohyongwonton.data.repository

import com.example.gohyongwonton.data.model.MenuCategory
import com.example.gohyongwonton.data.model.MenuItem
import com.example.gohyongwonton.data.remote.dto.RealtimeMenuItem
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class RealtimeMenuRepository {

    private val db      = FirebaseDatabase.getInstance()
    private val menuRef = db.getReference("menu")

    fun getAllMenuFlow(): Flow<List<MenuItem>> = callbackFlow {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val items = snapshot.children.mapNotNull { child ->
                    val id = child.key ?: return@mapNotNull null
                    child.getValue(RealtimeMenuItem::class.java)?.toMenuItem(id)
                }
                trySend(items)
            }
            override fun onCancelled(error: DatabaseError) { close(error.toException()) }
        }
        menuRef.addValueEventListener(listener)
        awaitClose { menuRef.removeEventListener(listener) }
    }

    fun getMenuByCategoryFlow(category: MenuCategory): Flow<List<MenuItem>> = callbackFlow {
        val query    = menuRef.orderByChild("category").equalTo(category.name)
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val items = snapshot.children.mapNotNull { child ->
                    val id = child.key ?: return@mapNotNull null
                    child.getValue(RealtimeMenuItem::class.java)?.toMenuItem(id)
                }
                trySend(items)
            }
            override fun onCancelled(error: DatabaseError) { close(error.toException()) }
        }
        query.addValueEventListener(listener)
        awaitClose { query.removeEventListener(listener) }
    }

    fun getBestSellersFlow(): Flow<List<MenuItem>> = callbackFlow {
        val query    = menuRef.orderByChild("isBestSeller").equalTo(true)
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val items = snapshot.children.mapNotNull { child ->
                    val id = child.key ?: return@mapNotNull null
                    child.getValue(RealtimeMenuItem::class.java)?.toMenuItem(id)
                }
                trySend(items)
            }
            override fun onCancelled(error: DatabaseError) { close(error.toException()) }
        }
        query.addValueEventListener(listener)
        awaitClose { query.removeEventListener(listener) }
    }
}