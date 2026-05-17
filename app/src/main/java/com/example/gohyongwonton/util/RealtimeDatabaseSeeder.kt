package com.example.gohyongwonton.util

import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await

object RealtimeDatabaseSeeder {

    private val db = FirebaseDatabase.getInstance()

    suspend fun seedMenuIfEmpty() {
        val menuRef = db.getReference("menu")

        // Cek apakah sudah ada data
        val snapshot = menuRef.limitToFirst(1).get().await()
        if (snapshot.exists()) return

        val menuItems = mapOf(
            // ── Gohyong ─────────────────────────────────────────────
            "gohyong_original" to mapOf(
                "name" to "Gohyong Original",
                "description" to "Kulit lumpia isi daging ayam & udang, digoreng crispy",
                "price" to 15000, "emoji" to "🥟", "category" to "GOHYONG",
                "isAvailable" to true, "rating" to 4.8, "portionInfo" to "5 pcs", "isBestSeller" to true
            ),
            "gohyong_spesial" to mapOf(
                "name" to "Gohyong Spesial",
                "description" to "Isian double daging + keju leleh meleleh",
                "price" to 20000, "emoji" to "🥟", "category" to "GOHYONG",
                "isAvailable" to true, "rating" to 4.9, "portionInfo" to "5 pcs", "isBestSeller" to true
            ),
            "gohyong_pedas" to mapOf(
                "name" to "Gohyong Pedas",
                "description" to "Gohyong dengan saus sambal mercon extra hot",
                "price" to 17000, "emoji" to "🌶️", "category" to "GOHYONG",
                "isAvailable" to true, "rating" to 4.7, "portionInfo" to "5 pcs", "isBestSeller" to false
            ),

            // ── Wonton ──────────────────────────────────────────────
            "wonton_soup" to mapOf(
                "name" to "Wonton Soup",
                "description" to "Bakso wonton kuah kaldu ayam hangat & gurih",
                "price" to 18000, "emoji" to "🍜", "category" to "WONTON",
                "isAvailable" to true, "rating" to 4.7, "portionInfo" to "1 mangkok (8 pcs)", "isBestSeller" to true
            ),
            "wonton_goreng" to mapOf(
                "name" to "Wonton Goreng",
                "description" to "Wonton crispy dengan saus asam manis",
                "price" to 16000, "emoji" to "🍘", "category" to "WONTON",
                "isAvailable" to true, "rating" to 4.6, "portionInfo" to "8 pcs", "isBestSeller" to false
            ),
            "wonton_kuah_pedas" to mapOf(
                "name" to "Wonton Kuah Pedas",
                "description" to "Wonton soup dengan kuah kaldu pedas segar",
                "price" to 20000, "emoji" to "🍜", "category" to "WONTON",
                "isAvailable" to true, "rating" to 4.8, "portionInfo" to "1 mangkok (8 pcs)", "isBestSeller" to false
            ),

            // ── Minuman ─────────────────────────────────────────────
            "es_teh_manis" to mapOf(
                "name" to "Es Teh Manis",
                "description" to "Teh manis segar dengan es batu",
                "price" to 5000, "emoji" to "🧋", "category" to "MINUMAN",
                "isAvailable" to true, "rating" to 4.5, "portionInfo" to "1 gelas", "isBestSeller" to false
            ),
            "es_jeruk" to mapOf(
                "name" to "Es Jeruk",
                "description" to "Jeruk peras segar dingin",
                "price" to 7000, "emoji" to "🍊", "category" to "MINUMAN",
                "isAvailable" to true, "rating" to 4.6, "portionInfo" to "1 gelas", "isBestSeller" to false
            ),
            "air_mineral" to mapOf(
                "name" to "Air Mineral",
                "description" to "Aqua botol 600ml",
                "price" to 4000, "emoji" to "💧", "category" to "MINUMAN",
                "isAvailable" to true, "rating" to 4.5, "portionInfo" to "1 botol", "isBestSeller" to false
            ),

            // ── Tambahan ────────────────────────────────────────────
            "saus_ekstra" to mapOf(
                "name" to "Saus Ekstra",
                "description" to "Saus sambal / saus tiram ekstra",
                "price" to 2000, "emoji" to "🥫", "category" to "TAMBAHAN",
                "isAvailable" to true, "rating" to 4.5, "portionInfo" to "1 sachet", "isBestSeller" to false
            ),
            "nasi_putih" to mapOf(
                "name" to "Nasi Putih",
                "description" to "Nasi putih pulen sebagai pelengkap",
                "price" to 5000, "emoji" to "🍚", "category" to "TAMBAHAN",
                "isAvailable" to true, "rating" to 4.5, "portionInfo" to "1 porsi", "isBestSeller" to false
            )
        )

        menuRef.setValue(menuItems).await()
        println("✅ Realtime Database seeded: ${menuItems.size} menu items")
    }
}