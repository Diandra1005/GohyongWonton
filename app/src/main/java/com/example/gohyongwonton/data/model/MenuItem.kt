package com.example.gohyongwonton.data.model

data class MenuItem(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val price: Int = 0,
    val emoji: String = "🥟",            // ← pakai emoji, tidak perlu URL/Storage
    val category: MenuCategory = MenuCategory.GOHYONG,
    val isAvailable: Boolean = true,
    val rating: Float = 4.5f,
    val portionInfo: String = "1 porsi",
    val isBestSeller: Boolean = false
)

enum class MenuCategory(val label: String) {
    GOHYONG("Gohyong"),
    WONTON("Wonton"),
    MINUMAN("Minuman"),
    TAMBAHAN("Tambahan")
}