package com.example.carryon.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.carryon.ui.screens.splash.SplashScreen
import com.example.carryon.ui.screens.auth.LoginScreen
import com.example.carryon.ui.screens.auth.OtpScreen
import com.example.carryon.ui.screens.auth.RegisterScreen
import com.example.carryon.ui.screens.home.HomeScreen
import com.example.carryon.ui.screens.booking.BookingScreen
import com.example.carryon.ui.screens.tracking.TrackingScreen
import com.example.carryon.ui.screens.tracking.TrackShipmentScreen
import com.example.carryon.ui.screens.tracking.TrackingLiveScreen
import com.example.carryon.ui.screens.tracking.PackageDetailsScreen
import com.example.carryon.ui.screens.orders.OrdersScreen
import com.example.carryon.ui.screens.orders.OrderDetailScreen
import com.example.carryon.ui.screens.profile.ProfileScreen
import com.example.carryon.ui.screens.profile.EditProfileScreen
import com.example.carryon.ui.screens.profile.SavedAddressesScreen
import com.example.carryon.ui.screens.help.HelpScreen
import com.example.carryon.ui.screens.calculate.CalculateScreen
import com.example.carryon.ui.screens.history.HistoryScreen
import com.example.carryon.ui.screens.rating.DriverRatingScreen
import com.example.carryon.ui.screens.home.ReadyToBookScreen
import com.example.carryon.ui.screens.tracking.ActiveShipmentScreen
import com.example.carryon.ui.screens.tracking.DeliveryDetailsScreen
import com.example.carryon.ui.screens.booking.SenderReceiverScreen
import com.example.carryon.ui.screens.booking.PaymentScreen
import com.example.carryon.ui.screens.booking.PaymentSuccessScreen

sealed class Screen(val route: String) {
    // Splash
    data object Splash : Screen("splash")
    
    // Auth
    data object Login : Screen("login")
    data object Otp : Screen("otp/{email}?mode={mode}&name={name}") {
        private fun percentEncode(value: String): String = buildString {
            for (c in value) {
                if (c.isLetterOrDigit() || c == '-' || c == '_' || c == '.' || c == '~') {
                    append(c)
                } else {
                    for (b in c.toString().encodeToByteArray()) {
                        append('%')
                        append(b.toUByte().toString(16).uppercase().padStart(2, '0'))
                    }
                }
            }
        }

        fun createRoute(email: String, mode: String = "login", name: String = ""): String {
            return "otp/${percentEncode(email)}?mode=$mode&name=${percentEncode(name)}"
        }
    }
    data object Register : Screen("register/{email}") {
        fun createRoute(email: String) = "register/$email"
    }
    
    // Main
    data object Home : Screen("home")
    data object Booking : Screen("booking/{pickup}/{delivery}/{packageType}") {
        fun createRoute(pickup: String, delivery: String, packageType: String) = 
            "booking/${pickup.replace("/", "_")}/${delivery.replace("/", "_")}/$packageType"
    }
    data object Tracking : Screen("tracking/{bookingId}") {
        fun createRoute(bookingId: String) = "tracking/$bookingId"
    }
    
    // New Screens
    data object Calculate : Screen("calculate")
    data object History : Screen("history")
    data object TrackShipment : Screen("track-shipment")
    data object TrackingLive : Screen("tracking-live")
    data object PackageDetails : Screen("package-details/{trackingNumber}") {
        fun createRoute(trackingNumber: String) = "package-details/$trackingNumber"
    }
    data object DriverRating : Screen("driver-rating/{driverName}") {
        fun createRoute(driverName: String) = "driver-rating/$driverName"
    }

    // New UI Screens
    data object ReadyToBook : Screen("ready-to-book")
    data object ActiveShipment : Screen("active-shipment")
    data object DeliveryDetails : Screen("delivery-details/{orderId}") {
        fun createRoute(orderId: String) = "delivery-details/$orderId"
    }

    // Booking Flow
    data object SenderReceiver : Screen("sender-receiver/{bookingId}") {
        fun createRoute(bookingId: String) = "sender-receiver/$bookingId"
    }
    data object BookingPayment : Screen("booking-payment/{bookingId}/{totalAmount}") {
        fun createRoute(bookingId: String, totalAmount: Int = 220) = "booking-payment/$bookingId/$totalAmount"
    }
    data object PaymentSuccessNav : Screen("payment-success/{bookingId}/{amount}") {
        fun createRoute(bookingId: String, amount: Int = 220) = "payment-success/$bookingId/$amount"
    }

    // Orders
    data object Orders : Screen("orders")
    data object OrderDetail : Screen("order/{orderId}") {
        fun createRoute(orderId: String) = "order/$orderId"
    }
    
    // Profile
    data object Profile : Screen("profile")
    data object EditProfile : Screen("edit-profile")
    data object SavedAddresses : Screen("saved-addresses")
    
    // Help
    data object Help : Screen("help")
}

@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController(),
    isLoggedIn: Boolean = false,
    onLoginSuccess: () -> Unit = {},
    onLogout: () -> Unit = {}
) {
    val startDestination = if (isLoggedIn) Screen.Home.route else Screen.Splash.route
    
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // Splash Screen
        composable(Screen.Splash.route) {
            SplashScreen(
                onSplashComplete = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }
        
        // Auth Screens
        composable(Screen.Login.route) {
            LoginScreen(
                onNavigateToOtp = { email ->
                    navController.navigate(Screen.Otp.createRoute(email, mode = "login"))
                },
                onNavigateToRegister = {
                    navController.navigate(Screen.Register.createRoute(""))
                }
            )
        }
        
        composable(
            Screen.Otp.route,
            arguments = listOf(
                navArgument("mode") { type = NavType.StringType; defaultValue = "login" },
                navArgument("name") { type = NavType.StringType; defaultValue = "" }
            )
        ) { backStackEntry ->
            val email = backStackEntry.arguments?.getString("email") ?: ""
            val mode = backStackEntry.arguments?.getString("mode") ?: "login"
            val name = backStackEntry.arguments?.getString("name") ?: ""
            OtpScreen(
                email = email,
                mode = mode,
                name = name,
                onVerifySuccess = {
                    onLoginSuccess()
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }
        
        composable(Screen.Register.route) { backStackEntry ->
            val emailArg = backStackEntry.arguments?.getString("email") ?: ""
            RegisterScreen(
                phone = emailArg,
                onRegisterSuccess = {
                    onLoginSuccess()
                    navController.navigate(Screen.ReadyToBook.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onNavigateToOtp = { email, name ->
                    navController.navigate(Screen.Otp.createRoute(email, mode = "signup", name = name))
                }
            )
        }
        
        // Main Screens
        composable(Screen.Home.route) {
            HomeScreen(
                onNavigateToBooking = { pickup, delivery, packageType ->
                    navController.navigate(Screen.Calculate.route)
                },
                onNavigateToOrders = {
                    navController.navigate(Screen.Orders.route)
                },
                onNavigateToProfile = {
                    navController.navigate(Screen.Profile.route)
                },
                onNavigateToTracking = { bookingId ->
                    navController.navigate(Screen.Tracking.createRoute(bookingId))
                },
                onNavigateToHistory = {
                    navController.navigate(Screen.History.route)
                }
            )
        }
        
        composable(Screen.Booking.route) { backStackEntry ->
            val pickup = backStackEntry.arguments?.getString("pickup")?.replace("_", "/") ?: ""
            val delivery = backStackEntry.arguments?.getString("delivery")?.replace("_", "/") ?: ""
            val packageType = backStackEntry.arguments?.getString("packageType") ?: "Parcels"
            BookingScreen(
                pickupAddress = pickup,
                deliveryAddress = delivery,
                packageType = packageType,
                onConfirmBooking = { bookingId ->
                    navController.navigate(Screen.SenderReceiver.createRoute(bookingId))
                },
                onBack = { navController.popBackStack() }
            )
        }
        
        composable(Screen.Tracking.route) { backStackEntry ->
            val bookingId = backStackEntry.arguments?.getString("bookingId") ?: ""
            TrackingScreen(
                bookingId = bookingId,
                onBack = { navController.popBackStack() },
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                }
            )
        }
        
        // Orders Screens
        composable(Screen.Orders.route) {
            OrdersScreen(
                onBack = { navController.popBackStack() },
                onOrderClick = { orderId ->
                    navController.navigate(Screen.DeliveryDetails.createRoute(orderId))
                }
            )
        }
        
        composable(Screen.OrderDetail.route) { backStackEntry ->
            val orderId = backStackEntry.arguments?.getString("orderId") ?: ""
            OrderDetailScreen(
                orderId = orderId,
                onBack = { navController.popBackStack() }
            )
        }
        
        // Profile Screens
        composable(Screen.Profile.route) {
            ProfileScreen(
                onBack = { navController.popBackStack() },
                onNavigateToEditProfile = {
                    navController.navigate(Screen.EditProfile.route)
                },
                onNavigateToSavedAddresses = {
                    navController.navigate(Screen.SavedAddresses.route)
                },
                onNavigateToHelp = {
                    navController.navigate(Screen.Help.route)
                },
                onNavigateToOrders = {
                    navController.navigate(Screen.Orders.route)
                },
                onNavigateToCalculate = {
                    navController.navigate(Screen.Calculate.route)
                },
                onNavigateToHistory = {
                    navController.navigate(Screen.History.route)
                },
                onNavigateToTrackShipment = {
                    navController.navigate(Screen.TrackShipment.route)
                },
                onNavigateToDriverRating = {
                    navController.navigate(Screen.DriverRating.createRoute("Josh Knight"))
                },
                onNavigateToSettings = {
                    navController.navigate(Screen.Profile.route)
                },
                onLogout = {
                    onLogout()
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                }
            )
        }
        
        composable(Screen.EditProfile.route) {
            EditProfileScreen(
                onBack = { navController.popBackStack() }
            )
        }
        
        composable(Screen.SavedAddresses.route) {
            SavedAddressesScreen(
                onBack = { navController.popBackStack() }
            )
        }
        
        // Help Screen
        composable(Screen.Help.route) {
            HelpScreen(
                onBack = { navController.popBackStack() }
            )
        }
        
        // Calculate Screen
        composable(Screen.Calculate.route) {
            CalculateScreen(
                onBack = { navController.popBackStack() },
                onFreeCheck = { from, to, option, weight ->
                    navController.navigate(Screen.Booking.createRoute(from, to, option))
                }
            )
        }
        
        // History Screen
        composable(Screen.History.route) {
            HistoryScreen(
                onInstantDelivery = { },
                onScheduleDelivery = { },
                onOrderClick = { orderId ->
                    navController.navigate(Screen.DeliveryDetails.createRoute(orderId))
                },
                onViewAll = { }
            )
        }
        
        // Track Shipment Screen
        composable(Screen.TrackShipment.route) {
            TrackShipmentScreen(
                onSearch = { trackingNumber ->
                    navController.navigate(Screen.PackageDetails.createRoute(trackingNumber))
                },
                onViewDetails = { _ ->
                    navController.navigate(Screen.TrackingLive.route)
                },
                onNavigateToHistory = {
                    navController.navigate(Screen.History.route)
                }
            )
        }

        // Tracking Live Screen
        composable(Screen.TrackingLive.route) {
            TrackingLiveScreen(
                onBack = { navController.popBackStack() }
            )
        }
        
        // Package Details Screen
        composable(Screen.PackageDetails.route) { backStackEntry ->
            val trackingNumber = backStackEntry.arguments?.getString("trackingNumber") ?: ""
            PackageDetailsScreen(
                orderId = trackingNumber,
                onBack = { navController.popBackStack() },
                onRateDriver = {
                    navController.navigate(Screen.DriverRating.createRoute("Josh Knight"))
                }
            )
        }
        
        // Driver Rating Screen
        composable(Screen.DriverRating.route) { backStackEntry ->
            val driverName = backStackEntry.arguments?.getString("driverName") ?: "Driver"
            DriverRatingScreen(
                driverName = driverName,
                onSubmit = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }

        // Ready to Book Screen
        composable(Screen.ReadyToBook.route) {
            ReadyToBookScreen(
                onLetsRide = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.ReadyToBook.route) { inclusive = true }
                    }
                }
            )
        }

        // Active Shipment Screen
        composable(Screen.ActiveShipment.route) {
            ActiveShipmentScreen(
                onTrackShipments = {
                    navController.navigate(Screen.TrackShipment.route)
                },
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                },
                onNavigateToHistory = {
                    navController.navigate(Screen.History.route)
                }
            )
        }

        // Delivery Details Screen
        composable(Screen.DeliveryDetails.route) { backStackEntry ->
            val orderId = backStackEntry.arguments?.getString("orderId") ?: ""
            DeliveryDetailsScreen(
                orderId = orderId,
                onBack = { navController.popBackStack() },
                onDelivered = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                },
                onUnsuccessful = { navController.popBackStack() }
            )
        }

        // Sender & Receiver Screen
        composable(Screen.SenderReceiver.route) { backStackEntry ->
            val bookingId = backStackEntry.arguments?.getString("bookingId") ?: ""
            SenderReceiverScreen(
                onBack = { navController.popBackStack() },
                onNext = { _, _, _, _, _ ->
                    navController.navigate(Screen.BookingPayment.createRoute(bookingId))
                }
            )
        }

        // Payment Screen
        composable(Screen.BookingPayment.route) { backStackEntry ->
            val bookingId = backStackEntry.arguments?.getString("bookingId") ?: ""
            val totalAmount = backStackEntry.arguments?.getString("totalAmount")?.toIntOrNull() ?: 220
            PaymentScreen(
                totalAmount = totalAmount,
                onBack = { navController.popBackStack() },
                onConfirmPayment = { _ ->
                    navController.navigate(Screen.PaymentSuccessNav.createRoute(bookingId, totalAmount))
                }
            )
        }

        // Payment Success Screen
        composable(Screen.PaymentSuccessNav.route) { backStackEntry ->
            val bookingId = backStackEntry.arguments?.getString("bookingId") ?: ""
            val amount = backStackEntry.arguments?.getString("amount")?.toIntOrNull() ?: 220
            PaymentSuccessScreen(
                amount = amount,
                onContinue = {
                    navController.navigate(Screen.ActiveShipment.route) {
                        popUpTo(Screen.Home.route)
                    }
                }
            )
        }

    }
}
