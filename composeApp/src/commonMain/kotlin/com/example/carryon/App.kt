package com.example.carryon

import androidx.compose.runtime.*
import androidx.compose.ui.tooling.preview.Preview
import com.example.carryon.ui.theme.CarryOnTheme
import com.example.carryon.ui.screens.splash.SplashScreen
import com.example.carryon.ui.screens.auth.WelcomeScreen
import com.example.carryon.ui.screens.auth.LoginScreen
import com.example.carryon.ui.screens.auth.RegisterScreen
import com.example.carryon.ui.screens.auth.OtpScreen
import com.example.carryon.ui.screens.home.HomeScreen
import com.example.carryon.ui.screens.profile.ProfileScreen
import com.example.carryon.ui.screens.calculate.CalculateScreen
import com.example.carryon.ui.screens.history.HistoryScreen
import com.example.carryon.ui.screens.tracking.TrackShipmentScreen
import com.example.carryon.ui.screens.tracking.PackageDetailsScreen
import com.example.carryon.ui.screens.orders.OrdersScreen
import com.example.carryon.ui.screens.booking.BookingScreen
import com.example.carryon.ui.screens.rating.DriverRatingScreen
import com.example.carryon.ui.screens.home.ReadyToBookScreen
import com.example.carryon.ui.screens.tracking.ActiveShipmentScreen
import com.example.carryon.ui.screens.tracking.DeliveryDetailsScreen
import com.example.carryon.ui.screens.booking.SenderReceiverScreen
import com.example.carryon.ui.screens.booking.PaymentScreen
import com.example.carryon.ui.screens.booking.PaymentSuccessScreen
import com.example.carryon.ui.screens.booking.BookingConfirmedScreen
import com.example.carryon.ui.screens.home.SelectAddressScreen
import com.example.carryon.ui.screens.booking.DetailsScreen
import com.example.carryon.ui.screens.booking.RequestForRideScreen
import com.example.carryon.ui.screens.booking.ThankYouScreen

// Simple screen state for iOS compatibility
sealed class AppScreen {
    data object Splash : AppScreen()
    data object Welcome : AppScreen()
    data object Login : AppScreen()
    data class Register(val phone: String = "") : AppScreen()
    data class Otp(val phone: String, val isNewUser: Boolean = false) : AppScreen()
    data object Home : AppScreen()
    data object Profile : AppScreen()
    data object Calculate : AppScreen()
    data object History : AppScreen()
    data object TrackShipment : AppScreen()
    data object Orders : AppScreen()
    data class PackageDetails(val orderId: String) : AppScreen()
    data class Booking(val pickup: String, val delivery: String, val packageType: String) : AppScreen()
    data class DriverRating(val driverName: String) : AppScreen()
    data object ReadyToBook : AppScreen()
    data object ActiveShipment : AppScreen()
    data class DeliveryDetails(val orderId: String) : AppScreen()
    data object CalculateBooking : AppScreen()
    data class SenderReceiver(val bookingId: String) : AppScreen()
    data class BookingPayment(val bookingId: String, val totalAmount: Int = 220) : AppScreen()
    data class PaymentSuccess(val bookingId: String, val amount: Int = 220) : AppScreen()
    data class BookingConfirmed(val bookingId: String) : AppScreen()
    data object SelectAddress : AppScreen()
    data object Details : AppScreen()
    data object RequestForRide : AppScreen()
    data object ThankYou : AppScreen()
}

@Composable
@Preview
fun App() {
    var currentScreen by remember { mutableStateOf<AppScreen>(AppScreen.Splash) }
    
    CarryOnTheme {
        when (val screen = currentScreen) {
            is AppScreen.Splash -> {
                SplashScreen(
                    onSplashComplete = { currentScreen = AppScreen.Welcome }
                )
            }
            is AppScreen.Welcome -> {
                WelcomeScreen(
                    onCreateAccount = { currentScreen = AppScreen.Register() },
                    onLogin = { currentScreen = AppScreen.Login }
                )
            }
            is AppScreen.Login -> {
                LoginScreen(
                    onNavigateToOtp = { email -> currentScreen = AppScreen.Otp(email) },
                    onNavigateToRegister = { currentScreen = AppScreen.Register() }
                )
            }
            is AppScreen.Register -> {
                RegisterScreen(
                    phone = screen.phone,
                    onRegisterSuccess = { currentScreen = AppScreen.Otp(screen.phone, isNewUser = true) }
                )
            }
            is AppScreen.Otp -> {
                OtpScreen(
                    phone = screen.phone,
                    onVerifySuccess = { _ -> currentScreen = AppScreen.Home },
                    onBack = { currentScreen = if (screen.isNewUser) AppScreen.Register() else AppScreen.Login }
                )
            }
            is AppScreen.Home -> {
                HomeScreen(
                    onNavigateToBooking = { pickup, delivery, packageType -> 
                        currentScreen = AppScreen.SelectAddress
                    },
                    onNavigateToOrders = { currentScreen = AppScreen.Orders },
                    onNavigateToProfile = { currentScreen = AppScreen.Profile },
                    onNavigateToTracking = { currentScreen = AppScreen.ActiveShipment }
                )
            }
            is AppScreen.Profile -> {
                ProfileScreen(
                    onNavigateToEditProfile = {},
                    onNavigateToSavedAddresses = {},
                    onNavigateToHelp = {},
                    onNavigateToOrders = { currentScreen = AppScreen.Orders },
                    onNavigateToCalculate = { currentScreen = AppScreen.Calculate },
                    onNavigateToHistory = { currentScreen = AppScreen.History },
                    onNavigateToTrackShipment = { currentScreen = AppScreen.TrackShipment },
                    onNavigateToDriverRating = { currentScreen = AppScreen.DriverRating("Josh Knight") },
                    onLogout = { currentScreen = AppScreen.Login },
                    onBack = { currentScreen = AppScreen.Home }
                )
            }
            is AppScreen.Calculate -> {
                CalculateScreen(
                    onBack = { currentScreen = AppScreen.Home },
                    onFreeCheck = { from, to, option, weight ->
                        currentScreen = AppScreen.Booking(from, to, option)
                    }
                )
            }
            is AppScreen.CalculateBooking -> {
                CalculateScreen(
                    onBack = { currentScreen = AppScreen.Home },
                    onFreeCheck = { from, to, option, weight ->
                        currentScreen = AppScreen.Booking(from, to, option)
                    }
                )
            }
            is AppScreen.History -> {
                HistoryScreen(
                    onInstantDelivery = { currentScreen = AppScreen.Home },
                    onScheduleDelivery = { currentScreen = AppScreen.Home },
                    onOrderClick = { orderId -> currentScreen = AppScreen.DeliveryDetails(orderId) },
                    onViewAll = { currentScreen = AppScreen.Orders }
                )
            }
            is AppScreen.TrackShipment -> {
                TrackShipmentScreen(
                    onSearch = { },
                    onViewDetails = { orderId -> 
                        currentScreen = AppScreen.PackageDetails(orderId) 
                    }
                )
            }
            is AppScreen.Orders -> {
                OrdersScreen(
                    onBack = { currentScreen = AppScreen.Home },
                    onOrderClick = { orderId ->
                        currentScreen = AppScreen.DeliveryDetails(orderId)
                    }
                )
            }
            is AppScreen.PackageDetails -> {
                PackageDetailsScreen(
                    orderId = screen.orderId,
                    onBack = { currentScreen = AppScreen.Orders },
                    onRateDriver = { currentScreen = AppScreen.DriverRating("Josh Knight") }
                )
            }
            is AppScreen.Booking -> {
                BookingScreen(
                    pickupAddress = screen.pickup,
                    deliveryAddress = screen.delivery,
                    packageType = screen.packageType,
                    onConfirmBooking = { bookingId -> currentScreen = AppScreen.SenderReceiver(bookingId) },
                    onBack = { currentScreen = AppScreen.CalculateBooking }
                )
            }
            is AppScreen.SenderReceiver -> {
                SenderReceiverScreen(
                    onBack = { currentScreen = AppScreen.CalculateBooking },
                    onNext = { _, _, _, _, _ ->
                        currentScreen = AppScreen.BookingPayment(screen.bookingId)
                    }
                )
            }
            is AppScreen.BookingPayment -> {
                PaymentScreen(
                    totalAmount = screen.totalAmount,
                    onBack = { currentScreen = AppScreen.SenderReceiver(screen.bookingId) },
                    onConfirmPayment = { _ ->
                        currentScreen = AppScreen.PaymentSuccess(screen.bookingId, screen.totalAmount)
                    }
                )
            }
            is AppScreen.PaymentSuccess -> {
                PaymentSuccessScreen(
                    amount = screen.amount,
                    onContinue = { currentScreen = AppScreen.ThankYou }
                )
            }
            is AppScreen.BookingConfirmed -> {
                BookingConfirmedScreen(
                    bookingId = screen.bookingId,
                    onTrackOrder = { currentScreen = AppScreen.ActiveShipment },
                    onGoHome = { currentScreen = AppScreen.Home }
                )
            }
            is AppScreen.DriverRating -> {
                DriverRatingScreen(
                    driverName = screen.driverName,
                    onSubmit = { currentScreen = AppScreen.Home },
                    onBack = { currentScreen = AppScreen.Profile }
                )
            }
            is AppScreen.ReadyToBook -> {
                ReadyToBookScreen(
                    onLetsRide = { currentScreen = AppScreen.Home }
                )
            }
            is AppScreen.ActiveShipment -> {
                ActiveShipmentScreen(
                    onTrackShipments = { currentScreen = AppScreen.DeliveryDetails("560023") },
                    onNavigateToHome = { currentScreen = AppScreen.Home }
                )
            }
            is AppScreen.DeliveryDetails -> {
                DeliveryDetailsScreen(
                    orderId = screen.orderId,
                    onBack = { currentScreen = AppScreen.ActiveShipment },
                    onDelivered = { currentScreen = AppScreen.Home },
                    onUnsuccessful = { currentScreen = AppScreen.ActiveShipment }
                )
            }
            is AppScreen.SelectAddress -> {
                SelectAddressScreen(
                    onNext = { currentScreen = AppScreen.Details },
                    onBack = { currentScreen = AppScreen.Home }
                )
            }
            is AppScreen.Details -> {
                DetailsScreen(
                    onContinue = { currentScreen = AppScreen.RequestForRide },
                    onBack = { currentScreen = AppScreen.SelectAddress }
                )
            }
            is AppScreen.RequestForRide -> {
                RequestForRideScreen(
                    onContinue = { currentScreen = AppScreen.PaymentSuccess("booking", 220) },
                    onBack = { currentScreen = AppScreen.Details }
                )
            }
            is AppScreen.ThankYou -> {
                ThankYouScreen(
                    onViewOrder = { currentScreen = AppScreen.BookingConfirmed("booking") }
                )
            }
        }
    }
}