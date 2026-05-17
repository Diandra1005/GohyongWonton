package com.example.gohyongwonton.data.remote.dto

import com.example.gohyongwonton.data.model.MenuCategory
import com.example.gohyongwonton.data.model.MenuItem

// Firebase Realtime Database butuh data class dengan no-arg constructor
data class RealtimeMenuItem(
    val name: String = "",
    val description: String = "",
    val price: Int = 0,
    val emoji: String = "🥟",
    val category: String = "",
    val isAvailable: Boolean = true,
    val rating: Float = 4.5f,
    val portionInfo: String = "1 porsi",
    val isBestSeller: Boolean = false
) {
    fun toMenuItem(id: String): MenuItem = MenuItem(
        id          = id,
        name        = name,
        description = description,
        price       = price,
        emoji       = emoji,
        category    = try { MenuCategory.valueOf(category) } catch (e: Exception) { MenuCategory.GOHYONG },
        isAvailable = isAvailable,
        rating      = rating,
        portionInfo = portionInfo,
        isBestSeller = isBestSeller
    )
}