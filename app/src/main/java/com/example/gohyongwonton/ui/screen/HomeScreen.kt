package com.example.gohyongwonton.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gohyongwonton.data.model.MenuItem
import com.example.gohyongwonton.viewmodel.HomeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onNavigateToCart: () -> Unit,
    onNavigateToDetail: (String) -> Unit,
    onNavigateToOrders: () -> Unit
) {
    val cartItems        by viewModel.cartItems.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val searchQuery      by viewModel.searchQuery.collectAsState()
    val isLoading        by viewModel.isLoading.collectAsState()
    val bestSellers      by viewModel.bestSellers.collectAsState()
    val filteredMenu     = viewModel.getFilteredMenu()
    val cartCount        = cartItems.sumOf { it.quantity }

    if (isLoading) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                CircularProgressIndicator()
                Spacer(Modifier.height(12.dp))
                Text("Memuat menu...", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Gohyong & Wonton 🥟", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Text("Madiun, Jawa Timur", fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                },
                actions = {
                    IconButton(onClick = onNavigateToOrders) {
                        Icon(Icons.Default.List, contentDescription = "Pesanan")
                    }
                    BadgedBox(badge = {
                        if (cartCount > 0) Badge { Text("$cartCount") }
                    }) {
                        IconButton(onClick = onNavigateToCart) {
                            Icon(Icons.Default.ShoppingCart, contentDescription = "Keranjang")
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            // Search Bar
            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { viewModel.setSearchQuery(it) },
                    placeholder = { Text("Cari menu...") },
                    leadingIcon  = { Icon(Icons.Default.Search, null) },
                    trailingIcon = {
                        if (searchQuery.isNotBlank()) {
                            IconButton(onClick = { viewModel.setSearchQuery("") }) {
                                Icon(Icons.Default.Clear, "Hapus")
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp)
                )
            }

            // Banner Promo
            item {
                Card(
                    modifier = Modifier.fillMaxWidth().height(120.dp).padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.tertiaryContainer
                    )
                ) {
                    Box(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                        Column {
                            Text("🎉 Promo Hari Ini!", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Text("Beli 10 pcs gohyong gratis minuman", fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onTertiaryContainer)
                            Spacer(Modifier.height(8.dp))
                            Text("Berlaku sampai jam 21.00 WIB", fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.7f))
                        }
                        Text("🥟🍜", fontSize = 40.sp, modifier = Modifier.align(Alignment.CenterEnd))
                    }
                }
                Spacer(Modifier.height(16.dp))
            }

            // Category Filter
            item {
                Text("Kategori", fontWeight = FontWeight.SemiBold, fontSize = 14.sp,
                    modifier = Modifier.padding(horizontal = 16.dp))
                Spacer(Modifier.height(8.dp))
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        FilterChip(
                            selected = selectedCategory == null,
                            onClick = { viewModel.setCategory(null) },
                            label = { Text("Semua") }
                        )
                    }
                    items(viewModel.allCategories) { cat ->
                        FilterChip(
                            selected = selectedCategory == cat,
                            onClick  = { viewModel.setCategory(cat) },
                            label    = { Text(cat.label) }
                        )
                    }
                }
                Spacer(Modifier.height(16.dp))
            }

            // Best Sellers
            if (selectedCategory == null && searchQuery.isBlank() && bestSellers.isNotEmpty()) {
                item {
                    Text("🌟 Terlaris", fontWeight = FontWeight.Bold, fontSize = 16.sp,
                        modifier = Modifier.padding(horizontal = 16.dp))
                    Spacer(Modifier.height(8.dp))
                }
                items(bestSellers.take(3)) { item ->
                    MenuItemCard(
                        item        = item,
                        onAddToCart = { viewModel.addToCart(item) },
                        onItemClick = { onNavigateToDetail(item.id) },
                        isBestSeller = true
                    )
                }
                item {
                    Divider(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp))
                    Text("📋 Semua Menu", fontWeight = FontWeight.Bold, fontSize = 16.sp,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp))
                }
            }

            // Menu List
            if (filteredMenu.isEmpty()) {
                item {
                    Box(modifier = Modifier.fillMaxWidth().padding(40.dp),
                        contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("🔍", fontSize = 40.sp)
                            Spacer(Modifier.height(8.dp))
                            Text("Menu tidak ditemukan",
                                color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            } else {
                items(filteredMenu, key = { it.id }) { item ->
                    MenuItemCard(
                        item        = item,
                        onAddToCart = { viewModel.addToCart(item) },
                        onItemClick = { onNavigateToDetail(item.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun MenuItemCard(
    item: MenuItem,
    onAddToCart: () -> Unit,
    onItemClick: () -> Unit,
    isBestSeller: Boolean = false
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .clickable(onClick = onItemClick),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            // ← Emoji sebagai gambar, tidak perlu AsyncImage / Storage
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Text(item.emoji, fontSize = 36.sp)
                if (isBestSeller) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(2.dp)
                            .background(Color(0xFFFFD700), CircleShape)
                            .size(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("★", fontSize = 9.sp, color = Color.White)
                    }
                }
            }

            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        item.name, fontWeight = FontWeight.SemiBold, fontSize = 14.sp,
                        maxLines = 1, overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text("⭐ ${item.rating}", fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Spacer(Modifier.height(2.dp))
                Text(item.description, fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2, overflow = TextOverflow.Ellipsis)
                Spacer(Modifier.height(4.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column {
                        Text(
                            "Rp ${"%,d".format(item.price).replace(",", ".")}",
                            fontWeight = FontWeight.Bold, fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(item.portionInfo, fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    if (item.isAvailable) {
                        FilledTonalButton(
                            onClick = onAddToCart,
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                            modifier = Modifier.height(32.dp)
                        ) {
                            Icon(Icons.Default.Add, null, modifier = Modifier.size(14.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("Tambah", fontSize = 12.sp)
                        }
                    } else {
                        Text("Habis", fontSize = 11.sp, color = MaterialTheme.colorScheme.error)
                    }
                }
            }
        }
    }
}