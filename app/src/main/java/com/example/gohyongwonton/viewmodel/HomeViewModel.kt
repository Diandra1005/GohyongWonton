package com.example.gohyongwonton.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gohyongwonton.data.model.MenuCategory
import com.example.gohyongwonton.data.model.MenuItem
import com.example.gohyongwonton.data.repository.CartRepository
import com.example.gohyongwonton.data.repository.RealtimeMenuRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel(
    private val menuRepo: RealtimeMenuRepository = RealtimeMenuRepository(),
    val cartRepo: CartRepository = CartRepository()
) : ViewModel() {

    private val _selectedCategory = MutableStateFlow<MenuCategory?>(null)
    val selectedCategory: StateFlow<MenuCategory?> = _selectedCategory.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _allMenu = MutableStateFlow<List<MenuItem>>(emptyList())
    val allMenu: StateFlow<List<MenuItem>> = _allMenu.asStateFlow()

    private val _bestSellers = MutableStateFlow<List<MenuItem>>(emptyList())
    val bestSellers: StateFlow<List<MenuItem>> = _bestSellers.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    val cartItems     = cartRepo.cartItems
    val allCategories = MenuCategory.values().toList()

    init {
        observeMenu()
        observeBestSellers()
    }

    private fun observeMenu() {
        viewModelScope.launch {
            menuRepo.getAllMenuFlow().collect { items ->
                _allMenu.value = items
                _isLoading.value = false
            }
        }
    }

    private fun observeBestSellers() {
        viewModelScope.launch {
            menuRepo.getBestSellersFlow().collect { items ->
                _bestSellers.value = items
            }
        }
    }

    fun getFilteredMenu(): List<MenuItem> {
        val query    = _searchQuery.value.trim()
        val category = _selectedCategory.value
        val all      = _allMenu.value
        return when {
            query.isNotBlank() -> all.filter {
                it.name.contains(query, ignoreCase = true) ||
                        it.description.contains(query, ignoreCase = true)
            }
            category != null   -> all.filter { it.category == category }
            else               -> all
        }
    }

    fun setCategory(category: MenuCategory?) { _selectedCategory.value = category }
    fun setSearchQuery(q: String)            { _searchQuery.value = q }
    fun addToCart(item: MenuItem)            { cartRepo.addItem(item) }
    fun cartItemCount(): Int = cartRepo.getTotalItems()
    fun cartTotal(): Int     = cartRepo.getTotalPrice()
}