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
import com.example.carryon.ui.screens.profile.SettingsScreen
import com.example.carryon.ui.screens.calculate.CalculateScreen
import com.example.carryon.ui.screens.history.HistoryScreen
import com.example.carryon.ui.screens.tracking.TrackShipmentScreen
import com.example.carryon.ui.screens.tracking.TrackingLiveScreen
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
import com.example.carryon.ui.screens.home.SelectAddressScreen
import com.example.carryon.ui.screens.booking.DetailsScreen
import com.example.carryon.ui.screens.booking.RequestForRideScreen
import com.example.carryon.ui.screens.wallet.WalletScreen
import com.example.carryon.ui.screens.chat.ChatScreen
import com.example.carryon.ui.screens.support.SupportScreen
import com.example.carryon.ui.screens.support.TicketDetailScreen
import com.example.carryon.ui.screens.promo.PromoScreen
import com.example.carryon.ui.screens.invoice.InvoiceScreen
import com.example.carryon.ui.screens.profile.EditProfileScreen
import com.example.carryon.ui.screens.profile.SavedAddressesScreen
import com.example.carryon.ui.screens.help.HelpScreen
import com.example.carryon.data.network.getToken
import com.example.carryon.data.network.clearToken
import com.example.carryon.data.network.getLanguage

// Simple screen state for iOS compatibility
sealed class AppScreen {
    data object Splash : AppScreen()
    data object Welcome : AppScreen()
    data object Login : AppScreen()
    data class Register(val email: String = "") : AppScreen()
    data class Otp(val email: String, val mode: String = "login", val name: String = "") : AppScreen()
    data object Home : AppScreen()
    data object Profile : AppScreen()
    data object Calculate : AppScreen()
    data object History : AppScreen()
    data object TrackShipment : AppScreen()
    data object TrackingLive : AppScreen()
    data object Orders : AppScreen()
    data class PackageDetails(val orderId: String) : AppScreen()
    data class Booking(val pickup: String, val delivery: String, val packageType: String) : AppScreen()
    data class DriverRating(val driverName: String, val bookingId: String = "") : AppScreen()
    data object ReadyToBook : AppScreen()
    data object ActiveShipment : AppScreen()
    data class DeliveryDetails(val orderId: String) : AppScreen()
    data object EditProfile : AppScreen()
    data object SavedAddresses : AppScreen()
    data object Help : AppScreen()
    data class SenderReceiver(val bookingId: String) : AppScreen()
    data class BookingPayment(val bookingId: String, val totalAmount: Int = 220) : AppScreen()
    data class PaymentSuccess(val bookingId: String, val amount: Int = 220) : AppScreen()
    data class SelectAddress(val pickup: String = "", val delivery: String = "", val vehicleType: String = "") : AppScreen()
    data class Details(val vehicleType: String = "") : AppScreen()
    data class RequestForRide(val vehicleType: String = "") : AppScreen()
    data object Settings : AppScreen()
    // New screens
    data object Wallet : AppScreen()
    data class Chat(val bookingId: String, val driverName: String = "Driver") : AppScreen()
    data object Support : AppScreen()
    data class TicketDetail(val ticketId: String) : AppScreen()
    data object Promo : AppScreen()
    data class Invoice(val bookingId: String) : AppScreen()
}

@Composable
@Preview
fun App() {
    var currentScreen by remember { mutableStateOf<AppScreen>(AppScreen.Splash) }
    var currentLanguage by remember { mutableStateOf(getLanguage() ?: "en") }

    CarryOnTheme(language = currentLanguage) {
        when (val screen = currentScreen) {
            is AppScreen.Splash -> {
                SplashScreen(
                    onSplashComplete = {
                        currentScreen = if (getToken() != null) AppScreen.Home else AppScreen.Welcome
                    }
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
                    phone = screen.email,
                    onRegisterSuccess = { currentScreen = AppScreen.Otp(screen.email) },
                    onNavigateToOtp = { email, name ->
                        currentScreen = AppScreen.Otp(email, mode = "signup", name = name)
                    }
                )
            }
            is AppScreen.Otp -> {
                OtpScreen(
                    email = screen.email,
                    mode = screen.mode,
                    name = screen.name,
                    onVerifySuccess = { currentScreen = AppScreen.Home },
                    onBack = { currentScreen = AppScreen.Login }
                )
            }
            is AppScreen.Home -> {
                HomeScreen(
                    onNavigateToBooking = { pickup, delivery, packageType ->
                        currentScreen = AppScreen.SelectAddress(pickup, delivery, packageType)
                    },
                    onNavigateToOrders = { currentScreen = AppScreen.Orders },
                    onNavigateToProfile = { currentScreen = AppScreen.Profile },
                    onNavigateToTracking = { currentScreen = AppScreen.ActiveShipment },
                    onNavigateToHistory = { currentScreen = AppScreen.History },
                    onNavigateToCalculate = { currentScreen = AppScreen.Calculate },
                    onLanguageChanged = { currentLanguage = it }
                )
            }
            is AppScreen.Profile -> {
                ProfileScreen(
                    onNavigateToEditProfile = { currentScreen = AppScreen.EditProfile },
                    onNavigateToSavedAddresses = { currentScreen = AppScreen.SavedAddresses },
                    onNavigateToHelp = { currentScreen = AppScreen.Support },
                    onNavigateToOrders = { currentScreen = AppScreen.Orders },
                    onNavigateToCalculate = { currentScreen = AppScreen.Calculate },
                    onNavigateToHistory = { currentScreen = AppScreen.History },
                    onNavigateToTrackShipment = { currentScreen = AppScreen.TrackShipment },
                    onNavigateToDriverRating = { currentScreen = AppScreen.DriverRating("Josh Knight") },
                    onNavigateToSettings = { currentScreen = AppScreen.Settings },
                    onNavigateToWallet = { currentScreen = AppScreen.Wallet },
                    onNavigateToPromo = { currentScreen = AppScreen.Promo },
                    onLogout = { clearToken(); currentScreen = AppScreen.Welcome },
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
            is AppScreen.EditProfile -> {
                EditProfileScreen(
                    onBack = { currentScreen = AppScreen.Profile }
                )
            }
            is AppScreen.SavedAddresses -> {
                SavedAddressesScreen(
                    onBack = { currentScreen = AppScreen.Profile }
                )
            }
            is AppScreen.Help -> {
                HelpScreen(
                    onBack = { currentScreen = AppScreen.Profile }
                )
            }
            is AppScreen.History -> {
                HistoryScreen(
                    onInstantDelivery = { currentScreen = AppScreen.SelectAddress() },
                    onScheduleDelivery = { currentScreen = AppScreen.SelectAddress() },
                    onOrderClick = { orderId -> currentScreen = AppScreen.DeliveryDetails(orderId) },
                    onViewAll = { currentScreen = AppScreen.Orders },
                    onNavigateToHome = { currentScreen = AppScreen.Home },
                    onNavigateToProfile = { currentScreen = AppScreen.Profile },
                    onNavigateToOrders = { currentScreen = AppScreen.Orders },
                    onNavigateToWallet = { currentScreen = AppScreen.Wallet }
                )
            }
            is AppScreen.TrackShipment -> {
                TrackShipmentScreen(
                    onSearch = { },
                    onViewDetails = { _ ->
                        currentScreen = AppScreen.TrackingLive
                    },
                    onNavigateToHistory = { currentScreen = AppScreen.History }
                )
            }
            is AppScreen.TrackingLive -> {
                TrackingLiveScreen(
                    onBack = { currentScreen = AppScreen.TrackShipment }
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
                    onBack = { currentScreen = AppScreen.Calculate }
                )
            }
            is AppScreen.SenderReceiver -> {
                SenderReceiverScreen(
                    onBack = { currentScreen = AppScreen.Calculate },
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
                    onContinue = { currentScreen = AppScreen.ActiveShipment }
                )
            }
            is AppScreen.DriverRating -> {
                DriverRatingScreen(
                    driverName = screen.driverName,
                    bookingId = screen.bookingId,
                    onSubmit = { currentScreen = AppScreen.Home },
                    onBack = { currentScreen = AppScreen.Home },
                    onNavigateToHome = { currentScreen = AppScreen.Home },
                    onNavigateToOrders = { currentScreen = AppScreen.Orders },
                    onNavigateToWallet = { currentScreen = AppScreen.Wallet },
                    onNavigateToProfile = { currentScreen = AppScreen.Profile }
                )
            }
            is AppScreen.ReadyToBook -> {
                ReadyToBookScreen(
                    onLetsRide = { currentScreen = AppScreen.Home }
                )
            }
            is AppScreen.ActiveShipment -> {
                ActiveShipmentScreen(
                    onTrackShipments = { currentScreen = AppScreen.TrackShipment },
                    onNavigateToHome = { currentScreen = AppScreen.Home },
                    onNavigateToOrders = { currentScreen = AppScreen.Orders },
                    onNavigateToWallet = { currentScreen = AppScreen.Wallet },
                    onNavigateToProfile = { currentScreen = AppScreen.Profile },
                    onChatWithDriver = { bookingId, driverName ->
                        currentScreen = AppScreen.Chat(bookingId, driverName)
                    }
                )
            }
            is AppScreen.DeliveryDetails -> {
                DeliveryDetailsScreen(
                    orderId = screen.orderId,
                    onBack = { currentScreen = AppScreen.ActiveShipment },
                    onDelivered = { currentScreen = AppScreen.DriverRating("Driver", screen.orderId) },
                    onUnsuccessful = { currentScreen = AppScreen.ActiveShipment },
                    onChatWithDriver = { currentScreen = AppScreen.Chat(screen.orderId, "Driver") },
                    onViewInvoice = { currentScreen = AppScreen.Invoice(screen.orderId) },
                    onNavigateToHome = { currentScreen = AppScreen.Home },
                    onNavigateToOrders = { currentScreen = AppScreen.Orders },
                    onNavigateToWallet = { currentScreen = AppScreen.Wallet },
                    onNavigateToProfile = { currentScreen = AppScreen.Profile }
                )
            }
            is AppScreen.SelectAddress -> {
                SelectAddressScreen(
                    initialFrom = screen.pickup,
                    initialTo = screen.delivery,
                    vehicleType = screen.vehicleType,
                    onNext = { vt -> currentScreen = AppScreen.Details(vt) },
                    onBack = { currentScreen = AppScreen.Home }
                )
            }
            is AppScreen.Details -> {
                DetailsScreen(
                    vehicleType = screen.vehicleType,
                    onContinue = { vt -> currentScreen = AppScreen.RequestForRide(vt) },
                    onBack = { currentScreen = AppScreen.SelectAddress() }
                )
            }
            is AppScreen.RequestForRide -> {
                RequestForRideScreen(
                    vehicleType = screen.vehicleType,
                    onContinue = { currentScreen = AppScreen.PaymentSuccess("booking", 220) },
                    onBack = { currentScreen = AppScreen.Details() }
                )
            }
            is AppScreen.Settings -> {
                SettingsScreen(
                    onBack = { currentScreen = AppScreen.Profile },
                    onLanguageChanged = { currentLanguage = it }
                )
            }
            // ── New Screens ──────────────────────────────────
            is AppScreen.Wallet -> {
                WalletScreen(
                    onBack = { currentScreen = AppScreen.Profile }
                )
            }
            is AppScreen.Chat -> {
                ChatScreen(
                    bookingId = screen.bookingId,
                    driverName = screen.driverName,
                    onBack = { currentScreen = AppScreen.ActiveShipment }
                )
            }
            is AppScreen.Support -> {
                SupportScreen(
                    onBack = { currentScreen = AppScreen.Profile },
                    onTicketClick = { ticketId -> currentScreen = AppScreen.TicketDetail(ticketId) }
                )
            }
            is AppScreen.TicketDetail -> {
                TicketDetailScreen(
                    ticketId = screen.ticketId,
                    onBack = { currentScreen = AppScreen.Support }
                )
            }
            is AppScreen.Promo -> {
                PromoScreen(
                    onBack = { currentScreen = AppScreen.Profile },
                    onApplyCoupon = null
                )
            }
            is AppScreen.Invoice -> {
                InvoiceScreen(
                    bookingId = screen.bookingId,
                    onBack = { currentScreen = AppScreen.Orders }
                )
            }
        }
    }
}
