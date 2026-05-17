package com.example.gohyongwonton

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.gohyongwonton.data.repository.*
import com.example.gohyongwonton.navigation.Screen
import com.example.gohyongwonton.ui.screen.*
import com.example.gohyongwonton.ui.theme.GohyongWontonTheme
import com.example.gohyongwonton.util.RealtimeDatabaseSeeder
import com.example.gohyongwonton.viewmodel.*
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GohyongWontonTheme {
                Surface(modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background) {
                    GohyongWontonApp()
                }
            }
        }
    }
}

@Composable
fun GohyongWontonApp() {
    val context       = LocalContext.current
    val navController = rememberNavController()
    val scope         = rememberCoroutineScope()

    // Repositories
    val authRepo  = remember { AuthRepository(context) }
    val cartRepo  = remember { CartRepository() }
    val orderRepo = remember { RealtimeOrderRepository() }
    val menuRepo  = remember { RealtimeMenuRepository() }

    // ViewModels
    val authVM     = remember { AuthViewModel(authRepo) }
    val homeVM     = remember { HomeViewModel(menuRepo = menuRepo, cartRepo = cartRepo) }
    val cartVM     = remember { CartViewModel(cartRepo = cartRepo) }
    val checkoutVM = remember { CheckoutViewModel(cartRepo = cartRepo, orderRepo = orderRepo) }
    val orderVM    = remember { OrderViewModel(orderRepo = orderRepo) }

    // Seed data awal ke Realtime Database (hanya sekali jika kosong)
    LaunchedEffect(Unit) {
        scope.launch { RealtimeDatabaseSeeder.seedMenuIfEmpty() }
    }

    val startDest = remember {
        if (authRepo.isLoggedIn) Screen.Home.route else Screen.Login.route
    }

    NavHost(navController = navController, startDestination = startDest) {

        // ── Login ──────────────────────────────────────────────────
        composable(Screen.Login.route) {
            LoginScreen(
                viewModel      = authVM,
                onLoginSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }

        // ── Home ───────────────────────────────────────────────────
        composable(Screen.Home.route) {
            LaunchedEffect(authVM.currentUserId) {
                authVM.currentUserId?.let { orderVM.loadOrders(it) }
            }
            HomeScreen(
                viewModel          = homeVM,
                onNavigateToCart   = { navController.navigate(Screen.Cart.route) },
                onNavigateToDetail = { id -> navController.navigate(Screen.Detail.createRoute(id)) },
                onNavigateToOrders = { navController.navigate(Screen.OrderList.route) }
            )
        }

        // ── Cart ───────────────────────────────────────────────────
        composable(Screen.Cart.route) {
            CartScreen(
                viewModel  = cartVM,
                onBack     = { navController.popBackStack() },
                onCheckout = { navController.navigate(Screen.Checkout.route) }
            )
        }

        // ── Checkout ───────────────────────────────────────────────
        composable(Screen.Checkout.route) {
            CheckoutScreen(
                viewModel     = checkoutVM,
                cartViewModel = cartVM,
                userId        = authVM.currentUserId ?: "",
                onBack        = { navController.popBackStack() },
                onOrderPlaced = { orderId ->
                    navController.navigate(Screen.OrderSuccess.createRoute(orderId)) {
                        popUpTo(Screen.Home.route)
                    }
                }
            )
        }

        // ── Order Success ──────────────────────────────────────────
        composable(Screen.OrderSuccess.route) { backStackEntry ->
            val orderId   = backStackEntry.arguments?.getString("orderId") ?: ""
            val lastOrder by checkoutVM.lastOrder.collectAsState()
            val order = lastOrder ?: orderVM.getOrder(orderId)

            OrderSuccessScreen(
                order        = order,
                onBackToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                },
                onViewOrders = {
                    navController.navigate(Screen.OrderList.route) {
                        popUpTo(Screen.Home.route)
                    }
                }
            )
        }

        // ── Order List ─────────────────────────────────────────────
        composable(Screen.OrderList.route) {
            LaunchedEffect(authVM.currentUserId) {
                authVM.currentUserId?.let { orderVM.loadOrders(it) }
            }
            OrderListScreen(
                viewModel = orderVM,
                onBack    = { navController.popBackStack() }
            )
        }

        // ── Detail (placeholder) ───────────────────────────────────
        composable(Screen.Detail.route) {
            LaunchedEffect(Unit) { navController.popBackStack() }
        }
    }
}