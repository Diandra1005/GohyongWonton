package com.example.gohyongwonton.navigation

sealed class Screen(val route: String) {
    object Login        : Screen("login")
    object Home         : Screen("home")
    object Cart         : Screen("cart")
    object Checkout     : Screen("checkout")
    object Profile      : Screen("profile")
    object OrderSuccess : Screen("order_success/{orderId}") {
        fun createRoute(orderId: String) = "order_success/$orderId"
    }
    object OrderList    : Screen("order_list")
    object Detail       : Screen("detail/{menuId}") {
        fun createRoute(menuId: String) = "detail/$menuId"
    }
}