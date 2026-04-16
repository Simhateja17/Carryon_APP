package com.company.carryon

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import carryon.composeapp.generated.resources.Res
import carryon.composeapp.generated.resources.icon_home
import carryon.composeapp.generated.resources.icon_timer
import carryon.composeapp.generated.resources.payment_icon
import carryon.composeapp.generated.resources.icon_people
import org.jetbrains.compose.resources.painterResource
import com.company.carryon.ui.theme.CarryOnTheme
import com.company.carryon.ui.theme.PrimaryBlue
import com.company.carryon.ui.theme.PrimaryBlueSurface
import com.company.carryon.ui.screens.splash.SplashScreen
import com.company.carryon.ui.screens.auth.WelcomeScreen
import com.company.carryon.ui.screens.auth.LoginScreen
import com.company.carryon.ui.screens.auth.RegisterScreen
import com.company.carryon.ui.screens.auth.OtpScreen
import com.company.carryon.ui.screens.home.HomeScreen
import com.company.carryon.ui.screens.profile.ProfileScreen
import com.company.carryon.ui.screens.profile.SettingsScreen
import com.company.carryon.ui.screens.calculate.CalculateScreen
import com.company.carryon.ui.screens.history.HistoryScreen
import com.company.carryon.ui.screens.tracking.TrackShipmentScreen
import com.company.carryon.ui.screens.tracking.TrackingLiveScreen
import com.company.carryon.ui.screens.tracking.TrackingScreen
import com.company.carryon.ui.screens.tracking.PackageDetailsScreen
import com.company.carryon.ui.screens.orders.OrdersScreen
import com.company.carryon.ui.screens.booking.BookingScreen
import com.company.carryon.ui.screens.rating.DriverRatingScreen
import com.company.carryon.ui.screens.home.ReadyToBookScreen
import com.company.carryon.ui.screens.tracking.ActiveShipmentScreen
import com.company.carryon.ui.screens.tracking.DeliveryDetailsScreen
import com.company.carryon.ui.screens.booking.SenderReceiverScreen
import com.company.carryon.ui.screens.booking.PaymentScreen
import com.company.carryon.ui.screens.booking.PaymentSuccessScreen
import com.company.carryon.ui.screens.booking.SearchingDriverScreen
import com.company.carryon.ui.screens.booking.DeliveryScheduledScreen
import com.company.carryon.ui.screens.booking.ScheduledOrderDetailsScreen
import com.company.carryon.ui.screens.booking.ModifyScheduleScreen
import com.company.carryon.ui.screens.booking.CancelDeliveryScreen
import com.company.carryon.ui.screens.booking.CancellationUnavailableScreen
import com.company.carryon.ui.screens.tracking.DriverApproachingScreen
import com.company.carryon.ui.screens.home.SelectAddressScreen
import com.company.carryon.ui.screens.booking.DetailsScreen
import com.company.carryon.ui.screens.booking.RequestForRideScreen
import com.company.carryon.ui.screens.wallet.AddMoneyScreen
import com.company.carryon.ui.screens.wallet.AddPaymentMethodScreen
import com.company.carryon.ui.screens.wallet.SendMoneyScreen
import com.company.carryon.ui.screens.wallet.WalletScreen
import com.company.carryon.ui.screens.chat.ChatScreen
import com.company.carryon.ui.screens.support.ReportIssueScreen
import com.company.carryon.ui.screens.support.SupportCallScreen
import com.company.carryon.ui.screens.support.SupportChatScreen
import com.company.carryon.ui.screens.support.SupportScreen
import com.company.carryon.ui.screens.support.TicketDetailScreen
import com.company.carryon.ui.screens.promo.PromoScreen
import com.company.carryon.ui.screens.invoice.DeliveryReceiptsScreen
import com.company.carryon.ui.screens.invoice.InvoiceScreen
import com.company.carryon.ui.screens.invoice.InvoiceHubScreen
import com.company.carryon.ui.screens.profile.ChangePasswordScreen
import com.company.carryon.ui.screens.profile.ClearCacheScreen
import com.company.carryon.ui.screens.profile.DefaultVehicleScreen
import com.company.carryon.ui.screens.profile.EditProfileScreen
import com.company.carryon.ui.screens.profile.AddAddressScreen
import com.company.carryon.ui.screens.profile.LanguageSettingsScreen
import com.company.carryon.ui.screens.profile.LoggedInDevicesScreen
import com.company.carryon.ui.screens.profile.PrivacySecurityScreen
import com.company.carryon.ui.screens.profile.SavedAddressesScreen
import com.company.carryon.ui.screens.help.HelpScreen
import com.company.carryon.data.network.AuthStateManager
import com.company.carryon.data.network.clearToken
import com.company.carryon.data.network.getLanguage
import com.company.carryon.data.network.SupabaseConfig
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.launch

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
    data class TrackOrder(val bookingId: String) : AppScreen()
    data class TrackingLive(val bookingId: String) : AppScreen()
    data object Orders : AppScreen()
    data class PackageDetails(val orderId: String) : AppScreen()
    data class Booking(val pickup: String, val delivery: String, val packageType: String, val pickupLat: Double = 0.0, val pickupLng: Double = 0.0, val deliveryLat: Double = 0.0, val deliveryLng: Double = 0.0) : AppScreen()
    data class DriverRating(val driverName: String, val bookingId: String = "") : AppScreen()
    data object ReadyToBook : AppScreen()
    data object ActiveShipment : AppScreen()
    data class DeliveryDetails(val orderId: String) : AppScreen()
    data object EditProfile : AppScreen()
    data class SavedAddresses(val fromSettings: Boolean = false) : AppScreen()
    data class AddAddress(val fromSettings: Boolean = false) : AppScreen()
    data object Help : AppScreen()
    data class SenderReceiver(
        val pickupAddress: String = "",
        val deliveryAddress: String = "",
        val vehicleType: String = "",
        val price: Double = 0.0,
        val paymentMethod: String = "CASH",
        val pickupLat: Double = 0.0,
        val pickupLng: Double = 0.0,
        val deliveryLat: Double = 0.0,
        val deliveryLng: Double = 0.0
    ) : AppScreen()
    data class BookingPayment(
        val pickupAddress: String = "",
        val deliveryAddress: String = "",
        val vehicleType: String = "",
        val totalAmount: Double = 0.0,
        val paymentMethod: String = "CASH",
        val senderName: String = "",
        val senderPhone: String = "",
        val receiverName: String = "",
        val receiverPhone: String = "",
        val receiverEmail: String = "",
        val pickupLat: Double = 0.0,
        val pickupLng: Double = 0.0,
        val deliveryLat: Double = 0.0,
        val deliveryLng: Double = 0.0
    ) : AppScreen()
    data class PaymentSuccess(val bookingId: String, val amount: Double = 0.0) : AppScreen()
    data class SearchingDriver(val bookingId: String, val amount: Double = 0.0) : AppScreen()
    data class DeliveryScheduled(val bookingId: String = "") : AppScreen()
    data class ScheduledOrderDetails(val bookingId: String = "") : AppScreen()
    data class ModifySchedule(val bookingId: String = "") : AppScreen()
    data class CancelDelivery(val bookingId: String = "") : AppScreen()
    data class CancellationUnavailable(val bookingId: String = "") : AppScreen()
    data class DriverApproaching(val bookingId: String) : AppScreen()
    data class SelectAddress(val pickup: String = "", val delivery: String = "", val vehicleType: String = "") : AppScreen()
    data class Details(
        val vehicleType: String = "",
        val pickup: String = "",
        val delivery: String = "",
        val fromHome: Boolean = false
    ) : AppScreen()
    data class RequestForRide(
        val vehicleType: String = "",
        val pickup: String = "",
        val delivery: String = "",
        val senderName: String = "",
        val senderPhone: String = "",
        val receiverName: String = "",
        val receiverPhone: String = "",
        val receiverEmail: String = "",
        val deliveryMode: String = "Regular",
        val offloading: Boolean = false,
        val fromHome: Boolean = false
    ) : AppScreen()
    data object Settings : AppScreen()
    data object LanguageSettings : AppScreen()
    data object DefaultVehicle : AppScreen()
    data object ClearCache : AppScreen()
    // New screens
    data object Wallet : AppScreen()
    data object AddMoney : AppScreen()
    data object SendMoney : AppScreen()
    data object AddPaymentMethod : AppScreen()
    data object DeliveryReceipts : AppScreen()
    data class Chat(val bookingId: String, val driverName: String = "Driver") : AppScreen()
    data object Support : AppScreen()
    data object SupportChat : AppScreen()
    data object SupportCall : AppScreen()
    data object ReportIssue : AppScreen()
    data class TicketDetail(val ticketId: String) : AppScreen()
    data object PrivacySecurity : AppScreen()
    data object ChangePassword : AppScreen()
    data object LoggedInDevices : AppScreen()
    data object Promo : AppScreen()
    data object InvoiceHub : AppScreen()
    data class Invoice(val bookingId: String) : AppScreen()
}

@Composable
@Preview
fun App() {
    var currentScreen by remember { mutableStateOf<AppScreen>(AppScreen.Splash) }
    var currentLanguage by remember { mutableStateOf(getLanguage() ?: "en") }
    val scope = rememberCoroutineScope()
    val isLoggedIn by AuthStateManager.isLoggedIn.collectAsState()

    LaunchedEffect(isLoggedIn, currentScreen) {
        val isPublicScreen = currentScreen is AppScreen.Splash ||
            currentScreen is AppScreen.Welcome ||
            currentScreen is AppScreen.Login ||
            currentScreen is AppScreen.Register ||
            currentScreen is AppScreen.Otp

        if (!isLoggedIn && !isPublicScreen) {
            currentScreen = AppScreen.Welcome
        }
    }

    val showBottomBar = currentScreen !is AppScreen.Splash &&
        currentScreen !is AppScreen.Welcome &&
        currentScreen !is AppScreen.Login &&
        currentScreen !is AppScreen.Register &&
        currentScreen !is AppScreen.Otp &&
        currentScreen !is AppScreen.PaymentSuccess &&
        currentScreen !is AppScreen.SearchingDriver &&
        currentScreen !is AppScreen.DeliveryScheduled &&
        currentScreen !is AppScreen.ModifySchedule &&
        currentScreen !is AppScreen.CancelDelivery &&
        currentScreen !is AppScreen.CancellationUnavailable &&
        currentScreen !is AppScreen.DriverApproaching &&
        currentScreen !is AppScreen.Support &&
        currentScreen !is AppScreen.SupportChat &&
        currentScreen !is AppScreen.SupportCall &&
        currentScreen !is AppScreen.PrivacySecurity &&
        currentScreen !is AppScreen.ChangePassword &&
        currentScreen !is AppScreen.LoggedInDevices &&
        currentScreen !is AppScreen.SavedAddresses &&
        currentScreen !is AppScreen.AddAddress &&
        currentScreen !is AppScreen.LanguageSettings &&
        currentScreen !is AppScreen.DefaultVehicle &&
        currentScreen !is AppScreen.ClearCache &&
        currentScreen !is AppScreen.ReportIssue &&
        currentScreen !is AppScreen.AddMoney &&
        currentScreen !is AppScreen.SendMoney &&
        currentScreen !is AppScreen.AddPaymentMethod &&
        currentScreen !is AppScreen.DeliveryReceipts

    val selectedTab = when (currentScreen) {
        is AppScreen.Home, is AppScreen.SelectAddress, is AppScreen.ReadyToBook,
        is AppScreen.Calculate, is AppScreen.Details, is AppScreen.RequestForRide -> 0
        is AppScreen.Orders, is AppScreen.History, is AppScreen.TrackShipment, is AppScreen.TrackOrder,
        is AppScreen.TrackingLive, is AppScreen.PackageDetails, is AppScreen.ActiveShipment,
        is AppScreen.DeliveryDetails, is AppScreen.Booking, is AppScreen.SenderReceiver,
        is AppScreen.BookingPayment, is AppScreen.SearchingDriver, is AppScreen.DeliveryScheduled,
        is AppScreen.ScheduledOrderDetails, is AppScreen.ModifySchedule,
        is AppScreen.DriverApproaching -> 1
        is AppScreen.Wallet, is AppScreen.Invoice, is AppScreen.InvoiceHub -> 2
        is AppScreen.AddMoney -> 2
        is AppScreen.SendMoney -> 2
        is AppScreen.AddPaymentMethod -> 2
        is AppScreen.DeliveryReceipts -> 2
        is AppScreen.Profile, is AppScreen.EditProfile, is AppScreen.SavedAddresses, is AppScreen.AddAddress,
        is AppScreen.Settings, is AppScreen.Help, is AppScreen.Support, is AppScreen.ReportIssue,
        is AppScreen.SupportChat, is AppScreen.SupportCall, is AppScreen.TicketDetail, is AppScreen.PrivacySecurity, is AppScreen.ChangePassword, is AppScreen.LoggedInDevices, is AppScreen.LanguageSettings, is AppScreen.DefaultVehicle, is AppScreen.ClearCache, is AppScreen.Promo, is AppScreen.DriverRating,
        is AppScreen.Chat -> 3
        else -> 0
    }

    CarryOnTheme(language = currentLanguage) {
        Scaffold(
            bottomBar = {
                if (showBottomBar) {
                    AppBottomBar(
                        selectedTab = selectedTab,
                        onHomeClick = { currentScreen = AppScreen.Home },
                        onOrdersClick = { currentScreen = AppScreen.Orders },
                        onPaymentsClick = { currentScreen = AppScreen.Wallet },
                        onAccountClick = { currentScreen = AppScreen.Profile }
                    )
                }
            }
        ) { scaffoldPadding ->
        Box(modifier = Modifier.padding(scaffoldPadding)) {
        when (val screen = currentScreen) {
            is AppScreen.Splash -> {
                SplashScreen(
                    onLoggedIn = {
                        AuthStateManager.setLoggedIn(true)
                        currentScreen = AppScreen.Home
                    },
                    onNotLoggedIn = { currentScreen = AppScreen.Welcome }
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
                    onNavigateToRegister = { currentScreen = AppScreen.Register() },
                    onGoogleSignInSuccess = {
                        AuthStateManager.setLoggedIn(true)
                        currentScreen = AppScreen.Home
                    }
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
                    onVerifySuccess = {
                        AuthStateManager.setLoggedIn(true)
                        currentScreen = AppScreen.Home
                    },
                    onBack = { currentScreen = AppScreen.Login }
                )
            }
            is AppScreen.Home -> {
                HomeScreen(
                    onNavigateToBooking = { pickup, delivery, packageType ->
                        currentScreen = AppScreen.Details(
                            vehicleType = packageType,
                            pickup = pickup,
                            delivery = delivery,
                            fromHome = true
                        )
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
                    onNavigateToSavedAddresses = { currentScreen = AppScreen.SavedAddresses(fromSettings = false) },
                    onNavigateToHelp = { currentScreen = AppScreen.Support },
                    onNavigateToOrders = { currentScreen = AppScreen.Orders },
                    onNavigateToCalculate = { currentScreen = AppScreen.Calculate },
                    onNavigateToHistory = { currentScreen = AppScreen.History },
                    onNavigateToTrackShipment = { currentScreen = AppScreen.TrackShipment },
                    onNavigateToDriverRating = { currentScreen = AppScreen.Orders }, // Navigate to orders to rate from specific booking
                    onNavigateToSettings = { currentScreen = AppScreen.Settings },
                    onNavigateToWallet = { currentScreen = AppScreen.Wallet },
                    onNavigateToPromo = { currentScreen = AppScreen.PrivacySecurity },
                    onLogout = {
                        AuthStateManager.setLoggedIn(false)
                        clearToken()
                        scope.launch {
                            try { SupabaseConfig.client.auth.signOut() } catch (_: Exception) { }
                        }
                        currentScreen = AppScreen.Welcome
                    },
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
                    onAddNewAddress = { currentScreen = AppScreen.AddAddress(fromSettings = screen.fromSettings) },
                    onBack = {
                        currentScreen = if (screen.fromSettings) {
                            AppScreen.Settings
                        } else {
                            AppScreen.Profile
                        }
                    }
                )
            }
            is AppScreen.AddAddress -> {
                AddAddressScreen(
                    onBack = { currentScreen = AppScreen.SavedAddresses(fromSettings = screen.fromSettings) },
                    onSave = { currentScreen = AppScreen.SavedAddresses(fromSettings = screen.fromSettings) }
                )
            }
            is AppScreen.Help -> {
                HelpScreen(
                    onBack = { currentScreen = AppScreen.Profile },
                    onNavigateToOrders = { currentScreen = AppScreen.Orders },
                    onNavigateToTracking = { currentScreen = AppScreen.TrackShipment },
                    onNavigateToSupport = { currentScreen = AppScreen.Support }
                )
            }
            is AppScreen.PrivacySecurity -> {
                PrivacySecurityScreen(
                    onBack = { currentScreen = AppScreen.Profile },
                    onChangePassword = { currentScreen = AppScreen.ChangePassword },
                    onLoggedInDevices = { currentScreen = AppScreen.LoggedInDevices }
                )
            }
            is AppScreen.ChangePassword -> {
                ChangePasswordScreen(
                    onBack = { currentScreen = AppScreen.PrivacySecurity }
                )
            }
            is AppScreen.LoggedInDevices -> {
                LoggedInDevicesScreen(
                    onBack = { currentScreen = AppScreen.PrivacySecurity }
                )
            }
            is AppScreen.History -> {
                HistoryScreen(
                    onInstantDelivery = { currentScreen = AppScreen.SelectAddress() },
                    onScheduleDelivery = { currentScreen = AppScreen.SelectAddress() },
                    onOrderClick = { orderId -> currentScreen = AppScreen.DeliveryDetails(orderId) },
                    onViewAll = { currentScreen = AppScreen.Orders }
                )
            }
            is AppScreen.TrackShipment -> {
                TrackShipmentScreen(
                    onSearch = { },
                    onViewDetails = { bookingId ->
                        currentScreen = AppScreen.TrackingLive(bookingId)
                    }
                )
            }
            is AppScreen.TrackingLive -> {
                TrackingLiveScreen(
                    bookingId = screen.bookingId,
                    onBack = { currentScreen = AppScreen.TrackShipment }
                )
            }
            is AppScreen.TrackOrder -> {
                TrackingScreen(
                    bookingId = screen.bookingId,
                    onBack = { currentScreen = AppScreen.Orders },
                    onNavigateToHome = { currentScreen = AppScreen.Home }
                )
            }
            is AppScreen.Orders -> {
                OrdersScreen(
                    onBack = { currentScreen = AppScreen.Home },
                    onOrderClick = { orderId ->
                        currentScreen = AppScreen.DeliveryDetails(orderId)
                    },
                    onTrackOrder = { orderId ->
                        currentScreen = AppScreen.TrackOrder(orderId)
                    },
                )
            }
            is AppScreen.PackageDetails -> {
                PackageDetailsScreen(
                    orderId = screen.orderId,
                    onBack = { currentScreen = AppScreen.Orders },
                    onRateDriver = { driverName -> currentScreen = AppScreen.DriverRating(driverName, screen.orderId) }
                )
            }
            is AppScreen.Booking -> {
                BookingScreen(
                    pickupAddress = screen.pickup,
                    deliveryAddress = screen.delivery,
                    packageType = screen.packageType,
                    pickupLat = screen.pickupLat,
                    pickupLng = screen.pickupLng,
                    deliveryLat = screen.deliveryLat,
                    deliveryLng = screen.deliveryLng,
                    onConfirmBooking = { vehicleType, price, paymentMethod ->
                        currentScreen = AppScreen.SenderReceiver(
                            pickupAddress = screen.pickup,
                            deliveryAddress = screen.delivery,
                            vehicleType = vehicleType,
                            price = price,
                            paymentMethod = paymentMethod,
                            pickupLat = screen.pickupLat,
                            pickupLng = screen.pickupLng,
                            deliveryLat = screen.deliveryLat,
                            deliveryLng = screen.deliveryLng
                        )
                    },
                    onBack = { currentScreen = AppScreen.Calculate }
                )
            }
            is AppScreen.SenderReceiver -> {
                SenderReceiverScreen(
                    onBack = { currentScreen = AppScreen.Calculate },
                    onNext = { senderName, senderPhone, receiverName, receiverPhone, receiverEmail, _ ->
                        currentScreen = AppScreen.BookingPayment(
                            pickupAddress = screen.pickupAddress,
                            deliveryAddress = screen.deliveryAddress,
                            vehicleType = screen.vehicleType,
                            totalAmount = screen.price,
                            paymentMethod = screen.paymentMethod,
                            senderName = senderName,
                            senderPhone = senderPhone,
                            receiverName = receiverName,
                            receiverPhone = receiverPhone,
                            receiverEmail = receiverEmail,
                            pickupLat = screen.pickupLat,
                            pickupLng = screen.pickupLng,
                            deliveryLat = screen.deliveryLat,
                            deliveryLng = screen.deliveryLng
                        )
                    }
                )
            }
            is AppScreen.BookingPayment -> {
                PaymentScreen(
                    totalAmount = screen.totalAmount.toInt(),
                    onBack = { 
                        currentScreen = AppScreen.SenderReceiver(
                            pickupAddress = screen.pickupAddress,
                            deliveryAddress = screen.deliveryAddress,
                            vehicleType = screen.vehicleType,
                            price = screen.totalAmount,
                            paymentMethod = screen.paymentMethod,
                            pickupLat = screen.pickupLat,
                            pickupLng = screen.pickupLng,
                            deliveryLat = screen.deliveryLat,
                            deliveryLng = screen.deliveryLng
                        )
                    },
                    onConfirmPayment = { _ ->
                        // Navigate to payment success - actual booking creation happens there or via a coroutine
                        currentScreen = AppScreen.PaymentSuccess(
                            bookingId = "", // Will be populated after API call
                            amount = screen.totalAmount
                        )
                    }
                )
            }
            is AppScreen.PaymentSuccess -> {
                PaymentSuccessScreen(
                    amount = screen.amount,
                    onContinue = {
                        if (screen.bookingId.isNotBlank()) {
                            currentScreen = AppScreen.SearchingDriver(screen.bookingId, screen.amount)
                        } else {
                            currentScreen = AppScreen.Orders
                        }
                    }
                )
            }
            is AppScreen.SearchingDriver -> {
                SearchingDriverScreen(
                    bookingId = screen.bookingId,
                    amount = screen.amount,
                    onDriverFound = { currentScreen = AppScreen.DriverApproaching(screen.bookingId) },
                    onScheduled = { currentScreen = AppScreen.DeliveryScheduled(screen.bookingId) },
                    onCancel = { currentScreen = AppScreen.Orders }
                )
            }
            is AppScreen.DeliveryScheduled -> {
                DeliveryScheduledScreen(
                    onBack = { currentScreen = AppScreen.Orders },
                    onViewOrder = {
                        currentScreen = AppScreen.ScheduledOrderDetails(screen.bookingId)
                    },
                    onModifySchedule = { currentScreen = AppScreen.ModifySchedule(screen.bookingId) },
                    onCancelDelivery = { currentScreen = AppScreen.Orders }
                )
            }
            is AppScreen.ScheduledOrderDetails -> {
                ScheduledOrderDetailsScreen(
                    bookingId = screen.bookingId,
                    onBack = { currentScreen = AppScreen.DeliveryScheduled(screen.bookingId) },
                    onModifySchedule = { currentScreen = AppScreen.ModifySchedule(screen.bookingId) },
                    onCancelDelivery = { currentScreen = AppScreen.Orders }
                )
            }
            is AppScreen.ModifySchedule -> {
                ModifyScheduleScreen(
                    onBack = { currentScreen = AppScreen.ScheduledOrderDetails(screen.bookingId) },
                    onUpdateSchedule = { currentScreen = AppScreen.ScheduledOrderDetails(screen.bookingId) },
                    onCancelDelivery = { currentScreen = AppScreen.CancelDelivery(screen.bookingId) }
                )
            }
            is AppScreen.CancelDelivery -> {
                CancelDeliveryScreen(
                    onBack = { currentScreen = AppScreen.ModifySchedule(screen.bookingId) },
                    onConfirmCancel = {
                        currentScreen = if (isCancellationAllowed(screen.bookingId)) {
                            AppScreen.Orders
                        } else {
                            AppScreen.CancellationUnavailable(screen.bookingId)
                        }
                    },
                    onKeepBooking = { currentScreen = AppScreen.ModifySchedule(screen.bookingId) }
                )
            }
            is AppScreen.CancellationUnavailable -> {
                CancellationUnavailableScreen(
                    bookingId = screen.bookingId,
                    onBack = { currentScreen = AppScreen.ModifySchedule(screen.bookingId) },
                    onChatSupport = { currentScreen = AppScreen.Support },
                    onGoBack = { currentScreen = AppScreen.ModifySchedule(screen.bookingId) }
                )
            }
            is AppScreen.DriverApproaching -> {
                DriverApproachingScreen(
                    bookingId = screen.bookingId,
                    onPickupDone = { currentScreen = AppScreen.TrackingLive(screen.bookingId) },
                    onBack = { currentScreen = AppScreen.Orders }
                )
            }
            is AppScreen.DriverRating -> {
                DriverRatingScreen(
                    driverName = screen.driverName,
                    bookingId = screen.bookingId,
                    onSubmit = {
                        if (screen.bookingId.isNotBlank()) {
                            currentScreen = AppScreen.DeliveryDetails(screen.bookingId)
                        } else {
                            currentScreen = AppScreen.Orders
                        }
                    },
                    onBack = {
                        if (screen.bookingId.isNotBlank()) {
                            currentScreen = AppScreen.DeliveryDetails(screen.bookingId)
                        } else {
                            currentScreen = AppScreen.Orders
                        }
                    }
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
                    onViewInvoice = { currentScreen = AppScreen.Invoice(screen.orderId) }
                )
            }
            is AppScreen.SelectAddress -> {
                SelectAddressScreen(
                    initialFrom = screen.pickup,
                    initialTo = screen.delivery,
                    vehicleType = screen.vehicleType,
                    onNext = { vt, pickup, delivery ->
                        currentScreen = AppScreen.Details(
                            vehicleType = vt,
                            pickup = pickup,
                            delivery = delivery,
                            fromHome = false
                        )
                    },
                    onBack = { currentScreen = AppScreen.Home }
                )
            }
            is AppScreen.Details -> {
                DetailsScreen(
                    vehicleType = screen.vehicleType,
                    pickup = screen.pickup,
                    delivery = screen.delivery,
                    onContinue = { vt, pickup, delivery, senderName, senderPhone, receiverName, receiverPhone, deliveryMode, offloading ->
                        currentScreen = AppScreen.RequestForRide(vt, pickup, delivery, senderName, senderPhone, receiverName, receiverPhone, "", deliveryMode, offloading)
                    },
                    onBack = {
                        currentScreen = if (screen.fromHome) {
                            AppScreen.Home
                        } else {
                            AppScreen.SelectAddress(
                                pickup = screen.pickup,
                                delivery = screen.delivery,
                                vehicleType = screen.vehicleType
                            )
                        }
                    }
                )
            }
            is AppScreen.RequestForRide -> {
                RequestForRideScreen(
                    vehicleType = screen.vehicleType,
                    pickupAddress = screen.pickup,
                    deliveryAddress = screen.delivery,
                    senderName = screen.senderName,
                    senderPhone = screen.senderPhone,
                    receiverName = screen.receiverName,
                    receiverPhone = screen.receiverPhone,
                    receiverEmail = screen.receiverEmail,
                    deliveryMode = screen.deliveryMode,
                    offloading = screen.offloading,
                    onContinue = { bookingId, amount ->
                        currentScreen = AppScreen.PaymentSuccess(bookingId, amount)
                    },
                    onBack = {
                        currentScreen = AppScreen.Details(
                            vehicleType = screen.vehicleType,
                            pickup = screen.pickup,
                            delivery = screen.delivery,
                            fromHome = screen.fromHome
                        )
                    }
                )
            }
            is AppScreen.Settings -> {
                SettingsScreen(
                    onBack = { currentScreen = AppScreen.Profile },
                    onNavigateToLanguage = { currentScreen = AppScreen.LanguageSettings },
                    onNavigateToSavedAddresses = { currentScreen = AppScreen.SavedAddresses(fromSettings = true) },
                    onNavigateToDefaultVehicle = { currentScreen = AppScreen.DefaultVehicle },
                    onNavigateToClearCache = { currentScreen = AppScreen.ClearCache },
                    onLanguageChanged = { currentLanguage = it }
                )
            }
            is AppScreen.LanguageSettings -> {
                LanguageSettingsScreen(
                    onBack = { currentScreen = AppScreen.Settings },
                    onLanguageChanged = { currentLanguage = it }
                )
            }
            is AppScreen.DefaultVehicle -> {
                DefaultVehicleScreen(
                    onBack = { currentScreen = AppScreen.Settings },
                    onSave = { currentScreen = AppScreen.Settings }
                )
            }
            is AppScreen.ClearCache -> {
                ClearCacheScreen(
                    onBack = { currentScreen = AppScreen.Settings }
                )
            }
            // ── New Screens ──────────────────────────────────
            is AppScreen.Wallet -> {
                WalletScreen(
                    onBack = { currentScreen = AppScreen.Profile },
                    onAddMoney = { currentScreen = AppScreen.AddMoney },
                    onSendMoney = { currentScreen = AppScreen.SendMoney },
                    onAddNewMethod = { currentScreen = AppScreen.AddPaymentMethod },
                    onDownloadInvoices = { currentScreen = AppScreen.InvoiceHub },
                    onViewReceipts = { currentScreen = AppScreen.DeliveryReceipts }
                )
            }
            is AppScreen.AddMoney -> {
                AddMoneyScreen(
                    onBack = { currentScreen = AppScreen.Wallet }
                )
            }
            is AppScreen.SendMoney -> {
                SendMoneyScreen(
                    onBack = { currentScreen = AppScreen.Wallet }
                )
            }
            is AppScreen.AddPaymentMethod -> {
                AddPaymentMethodScreen(
                    onBack = { currentScreen = AppScreen.Wallet }
                )
            }
            is AppScreen.InvoiceHub -> {
                InvoiceHubScreen(
                    onBack = { currentScreen = AppScreen.Wallet }
                )
            }
            is AppScreen.DeliveryReceipts -> {
                DeliveryReceiptsScreen(
                    onBack = { currentScreen = AppScreen.Wallet }
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
                    onTicketClick = { ticketId ->
                        currentScreen = when (ticketId) {
                            "chat" -> AppScreen.SupportChat
                            "call" -> AppScreen.SupportCall
                            "report_issue" -> AppScreen.ReportIssue
                            else -> AppScreen.TicketDetail(ticketId)
                        }
                    }
                )
            }
            is AppScreen.SupportChat -> {
                SupportChatScreen(
                    onBack = { currentScreen = AppScreen.Support }
                )
            }
            is AppScreen.SupportCall -> {
                SupportCallScreen(
                    onBack = { currentScreen = AppScreen.Support },
                    onEndCall = { currentScreen = AppScreen.Support }
                )
            }
            is AppScreen.ReportIssue -> {
                ReportIssueScreen(
                    onBack = { currentScreen = AppScreen.Support },
                    onSubmit = { currentScreen = AppScreen.Support }
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
    }
}

@Composable
private fun AppBottomBar(
    selectedTab: Int,
    onHomeClick: () -> Unit,
    onOrdersClick: () -> Unit,
    onPaymentsClick: () -> Unit,
    onAccountClick: () -> Unit
) {
    Surface(
        color = Color.White,
        tonalElevation = 8.dp
    ) {
        val items = listOf(
            Res.drawable.icon_home to "HOME",
            Res.drawable.icon_timer to "ORDERS",
            Res.drawable.payment_icon to "WALLET",
            Res.drawable.icon_people to "PROFILE"
        )
        val actions = listOf(onHomeClick, onOrdersClick, onPaymentsClick, onAccountClick)

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            items.forEachIndexed { index, item ->
                val isSelected = selectedTab == index
                val (iconRes, label) = item

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { actions[index]() },
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (isSelected) {
                        Surface(
                            shape = CircleShape,
                            color = PrimaryBlue,
                            shadowElevation = 6.dp,
                            modifier = Modifier.size(64.dp)
                        ) {
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Image(
                                    painter = painterResource(iconRes),
                                    contentDescription = label,
                                    modifier = Modifier.size(18.dp),
                                    contentScale = ContentScale.Fit
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(label, color = Color.White, style = MaterialTheme.typography.labelSmall)
                            }
                        }
                    } else {
                        Image(
                            painter = painterResource(iconRes),
                            contentDescription = label,
                            modifier = Modifier.size(21.dp),
                            contentScale = ContentScale.Fit
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            label,
                            color = Color(0xFF97A3B6),
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }
            }
        }
    }
}

private fun isCancellationAllowed(bookingId: String): Boolean {
    if (bookingId.isBlank()) return true
    val lowered = bookingId.lowercase()
    return !(
        lowered.contains("arrived") ||
            lowered.contains("reached") ||
            lowered.contains("picked") ||
            lowered.contains("no_cancel")
        )
}
