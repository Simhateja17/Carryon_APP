package com.example.carryon.i18n

interface AppStrings {
    // ── Common / Shared ──
    val appName: String get() = "Carry On"
    val next: String get() = "Next"
    val continueText: String get() = "Continue"
    val cancel: String get() = "Cancel"
    val save: String get() = "Save"
    val delete: String get() = "Delete"
    val submit: String get() = "Submit"
    val search: String get() = "Search"
    val ok: String get() = "OK"
    val back: String get() = "Back"
    val loading: String get() = "Loading..."
    val or: String get() = "Or"
    val me: String get() = "Me"
    val recipient: String get() = "Recipient"
    val from: String get() = "From"
    val to: String get() = "To"
    val email: String get() = "Email"
    val notifications: String get() = "Notifications"

    // ── Navigation ──
    val navHome: String get() = "Home"
    val navOrders: String get() = "Orders"
    val navPayments: String get() = "Payments"
    val navAccount: String get() = "Account"

    // ── Welcome Screen ──
    val welcome: String get() = "Welcome"
    val welcomeSubtitle: String get() = "Have a Better Experience with "
    val createAnAccount: String get() = "Create an account"
    val logIn: String get() = "Log In"

    // ── Login Screen ──
    val welcomeTo: String get() = "Welcome to "
    val loginSubtitle: String get() = "Hello there, Sign in to Continue"
    val emailAddress: String get() = "Email Address"
    val enterYourEmail: String get() = "Enter your email"
    val password: String get() = "Password"
    val forgotPassword: String get() = "Forgot Password ?"
    val dontHaveAccount: String get() = "Don't have an Account ?"
    val signUp: String get() = "Sign up"
    val noAccountFound: String get() = "No account found with this email. Please sign up."
    val unexpectedError: String get() = "Unexpected error"

    // ── Register Screen ──
    val registerSubtitle: String get() = "Hello there, Sign up to get started"
    val name: String get() = "Name"
    val enterYourName: String get() = "Enter your name"
    val confirmPassword: String get() = "Confirm Password"
    val confirmPasswordPlaceholder: String get() = "Confirm password"
    val failedToSendCode: String get() = "Failed to send verification code"

    // ── OTP Screen ──
    val enterTheCode: String get() = "Enter the Code"
    val verificationCodeSentTo: String get() = "A verification code has been sent to"
    val dontReceiveCode: String get() = "Don't receive code?  "
    val resendAgain: String get() = "Resend again"
    val resendAgainTimer: (Int) -> String get() = { "Resend again (${it}s)" }
    val verificationFailed: String get() = "Verification failed"
    val pleaseEnter6DigitCode: String get() = "Please enter 6-digit code"
    val failedToResendCode: String get() = "Failed to resend code"

    // ── Home Screen ──
    val weAreReadyToServe: String get() = "We are Ready to\nServe"
    val pickupLocation: String get() = "Pickup Location"
    val useMyLocation: String get() = "Use my location"
    val detectingLocation: String get() = "Detecting your location…"
    val enterPickupAddress: String get() = "Enter pickup address"
    val deliveryLocation: String get() = "Delivery Location"
    val enterDeliveryAddress: String get() = "Enter delivery address"
    val vehicleType: String get() = "Vehicle Type"
    val fastAndReliable: String get() = "Fast & reliable delivery"
    val ourServices: String get() = "Our services"
    val sameDayDelivery: String get() = "Same day\ndelivery"
    val overnightDelivery: String get() = "Overnight\ndelivery"
    val expressDelivery: String get() = "Express\ndelivery"

    // ── Ready To Book Screen ──
    val youAreReadyToBook: String get() = "You are ready to Book"
    val readyToBookSubtitle: String get() = "Your account is now activated. Lets book your first load"
    val letsRide: String get() = "Lets Ride"

    // ── Select Address Screen ──
    val selectAddress: String get() = "Select address"
    val nearbyPlaces: String get() = "Nearby places"
    val noNearbyPlaces: String get() = "No nearby places found"

    // ── Booking Screen ──
    val distance: String get() = "Distance"
    val duration: String get() = "Duration"
    val charge: String get() = "Charge"
    val pickup: String get() = "Pickup"
    val delivery: String get() = "Delivery"
    val pickupLocationLabel: String get() = "Pickup Location"
    val deliveryLocationLabel: String get() = "Delivery Location"
    val selectWhoPays: String get() = "Select who pays"
    val paymentType: String get() = "Payment type"
    val selectVehicle: String get() = "Select Vehicle"
    val ride: String get() = "Ride"
    val upToKg: (Int) -> String get() = { "Up to $it kg" }
    val rmAmount: (Int) -> String get() = { "RM $it" }
    val minDuration: (Int) -> String get() = { "$it min" }

    // ── Details Screen ──
    val details: String get() = "Details"
    val whatAreYouSending: String get() = "What are you sending"
    val selectTypeOfItem: String get() = "Select type of item (e.g gadget, document)"
    val select: String get() = "Select"
    val quantity: String get() = "Quantity"
    val recipientNames: String get() = "Recipient Names"
    val recipientContactNumber: String get() = "Recipient contact number"
    val takePictureOfPackage: String get() = "Take a picture of the package"
    val prohibitedItems: String get() = "Our Prohibited Items include: firearms & weapons, explosives, flammable substances, illegal drugs, hazardous chemicals, perishable food, live animals, liquids over 100ml, sharp objects, counterfeit goods, and cash or valuables."

    // ── Sender Receiver Screen ──
    val requestForRide: String get() = "Request for Ride"
    val currentLocation: String get() = "Current Location"
    val office: String get() = "Office"
    val whatAreYouSendingQuestion: String get() = "What are you sending?"
    val sampleType: String get() = "Sample type"
    val request: String get() = "Request"
    val senderName: String get() = "Sender Name"
    val overallTrack: String get() = "Overall Track"
    val receiverName: String get() = "Receiver Name"
    val address: String get() = "Address"

    // ── Payment Screen ──
    val selectPaymentMethod: String get() = "Select payment method"
    val priceOfPackage: String get() = "This is price of the package"
    val paymentMethod: String get() = "Payment Method"
    val visaCard: String get() = "Visa Card"
    val mastercard: String get() = "Mastercard"
    val cashOnDelivery: String get() = "Cash on Delivery"
    val wallet: String get() = "Wallet"
    val cash: String get() = "Cash"

    // ── Payment Success Screen ──
    val paymentSuccess: String get() = "Payment Success"
    val paymentProcessedSuccessfully: String get() = "Your payment has been processed\nsuccessfully"

    // ── Request For Ride Screen ──
    val selectPickupLocation: String get() = "Select pickup location"
    val selectDeliveryLocation: String get() = "Select delivery location"
    val fairPrice: String get() = "Fair Price"
    val taxPercent: String get() = "Tax (5%)"
    val reviews: String get() = "reviews"

    // ── Track Shipment Screen ──
    val trackYourShipment: String get() = "Track your shipment"
    val enterTrackingNumber: String get() = "Enter your tracking number"
    val yourPackage: String get() = "Your Package"
    val documents: String get() = "Documents"
    val destination: String get() = "Destination"
    val itemWeight: String get() = "Item Weight"
    val viewDetails: String get() = "View details"
    val sentPackage: String get() = "Sent Package"
    val transit: String get() = "Transit"
    val onAJourney: String get() = "On a journey"
    val accepted: String get() = "Accepted"

    // ── Tracking Screen ──
    val orderIdLabel: (String) -> String get() = { "Order ID: $it" }
    val estimatedDelivery: String get() = "Estimated delivery: 11:30 AM"
    val processing: String get() = "Processing"
    val delivering: String get() = "Delivering"
    val parcels: String get() = "Parcels"
    val estExpress: String get() = "Est. Express"
    val rateDriver: String get() = "Rate Driver"
    val orderConfirmed: String get() = "Order Confirmed"
    val orderPlaced: String get() = "Your order has been placed"
    val driverAssigned: String get() = "Driver Assigned"
    val driverOnTheWay: String get() = "John Smith is on the way"
    val pickedUp: String get() = "Picked Up"
    val packageCollected: String get() = "Package collected from sender"
    val inTransit: String get() = "In Transit"
    val onTheWayToDestination: String get() = "On the way to destination"
    val outForDelivery: String get() = "Out for Delivery"
    val nearYourLocation: String get() = "Near your location"
    val delivered: String get() = "Delivered"
    val packageDeliveredSuccessfully: String get() = "Package delivered successfully"
    val giveRatingForDriver: String get() = "Give Rating for Driver"
    val howWasTheDriver: String get() = "How was the driver?"
    val whatImpressedYou: String get() = "Yay! What impressed you?"
    val goodCommunication: String get() = "Good communication"
    val excellentService: String get() = "Excellent Service"
    val cleanAndComfy: String get() = "Clean & Comfy"
    val tipsForDriver: String get() = "Tips to make your driver's happy"

    // ── Tracking Live Screen ──
    val mins: String get() = "mins"
    val deliveryPartnerDriving: String get() = "Delivery partner is driving\nsafely to deliver your order"
    val callDeliveryAgent: String get() = "Call Delivery Agent"

    // ── Active Shipment Screen ──
    val shareWithNeighbors: String get() = "Share delivery with neighbors"
    val forExtraDiscount: String get() = "For extra discount"
    val dispatched: String get() = "Dispatched"
    val deliverBy: String get() = "Deliver by"
    val trackShipments: String get() = "Track Shipments"

    // ── Delivery Details Screen ──
    val newLabel: String get() = "New"
    val sendersName: String get() = "Sender's Name"
    val sendersNumber: String get() = "Sender's Number"
    val receiversName: String get() = "Receiver's Name"
    val receiversNumber: String get() = "Receiver's Number"
    val deliveryMethod: String get() = "Delivery Method: "
    val deliveryFee: String get() = "Delivery Fee: "
    val unsuccessful: String get() = "Unsuccessful"

    // ── Package Details Screen ──
    val packageLabel: String get() = "Package"

    // ── Orders Screen ──
    val myOrders: String get() = "My Orders"
    val all: String get() = "All"
    val active: String get() = "Active"
    val completed: String get() = "Completed"
    val cancelled: String get() = "Cancelled"
    val noOrdersFound: String get() = "No orders found"
    val ordersWillAppearHere: String get() = "Your orders will appear here"
    val findingDriver: String get() = "Finding Driver"

    // ── Profile Screen ──
    val hiThere: String get() = "Hi There!"
    val savedAddresses: String get() = "Saved\nAddresses"
    val rewards: String get() = "Rewards"
    val helpAndSupport: String get() = "Help & Support"
    val termsAndConditions: String get() = "Terms and Conditions"
    val settings: String get() = "Settings"
    val referYourFriend: String get() = "Refer Your Friend"
    val logout: String get() = "Logout"

    // ── Settings Screen ──
    val account: String get() = "Account"
    val editProfile: String get() = "Edit Profile"
    val changePassword: String get() = "Change Password"
    val savedAddressesMenu: String get() = "Saved Addresses"
    val pushNotifications: String get() = "Push Notifications"
    val emailNotifications: String get() = "Email Notifications"
    val smsNotifications: String get() = "SMS Notifications"
    val preferences: String get() = "Preferences"
    val language: String get() = "Language"
    val currency: String get() = "Currency"
    val about: String get() = "About"
    val appVersion: String get() = "App Version"
    val privacyPolicy: String get() = "Privacy Policy"
    val termsOfService: String get() = "Terms of Service"

    // ── Edit Profile Screen ──
    val changePhoto: String get() = "Change Photo"
    val fullName: String get() = "Full Name"
    val phoneNumber: String get() = "Phone Number"
    val phoneCannotBeChanged: String get() = "Phone number cannot be changed"
    val saveChanges: String get() = "Save Changes"
    val success: String get() = "Success"
    val profileUpdatedSuccessfully: String get() = "Your profile has been updated successfully."

    // ── Saved Addresses Screen ──
    val noSavedAddresses: String get() = "No saved addresses"
    val addAddressesForQuickBooking: String get() = "Add addresses for quick booking"
    val addAddress: String get() = "+ Add Address"
    val deleteAddress: String get() = "Delete Address"
    val deleteAddressConfirmation: String get() = "Are you sure you want to delete this address?"
    val pickLocationOnMap: String get() = "Pick Location on Map"
    val tapOnMapToSelect: String get() = "Tap on the map to select a location"
    val confirmLocation: String get() = "Confirm Location"
    val addNewAddress: String get() = "Add New Address"
    val labelExample: String get() = "Label (e.g., Mom's House)"
    val pickOnMap: String get() = "Pick on Map"
    val fullAddress: String get() = "Full Address"
    val landmarkOptional: String get() = "Landmark (Optional)"
    val contactName: String get() = "Contact Name"
    val contactPhone: String get() = "Contact Phone"

    // ── Help Screen ──
    val contactUs: String get() = "Contact Us"
    val callUs: String get() = "Call Us"
    val available247: String get() = "Available 24/7"
    val chatSupport: String get() = "Chat Support"
    val typicallyReplies: String get() = "Typically replies in 5 minutes"
    val emailUs: String get() = "Email Us"
    val quickHelp: String get() = "Quick Help"
    val trackOrder: String get() = "Track Order"
    val paymentIssue: String get() = "Payment Issue"
    val cancelOrder: String get() = "Cancel Order"
    val refundStatus: String get() = "Refund Status"
    val faq: String get() = "Frequently Asked Questions"
    val faqBookDelivery: String get() = "How do I book a delivery?"
    val faqBookDeliveryAnswer: String get() = "Enter your pickup and delivery locations on the home screen, select a vehicle type, and tap 'Book Now'. You'll see the estimated price before confirming."
    val faqPriceCalculated: String get() = "How is the price calculated?"
    val faqPriceCalculatedAnswer: String get() = "The price includes a base fare plus a per-kilometer charge based on the vehicle type and distance. During peak hours, there may be additional surge charges."
    val faqScheduleDelivery: String get() = "Can I schedule a delivery for later?"
    val faqScheduleDeliveryAnswer: String get() = "Yes! On the home screen, tap on 'Schedule' to book a delivery for a future date and time."
    val faqTrackDelivery: String get() = "How do I track my delivery?"
    val faqTrackDeliveryAnswer: String get() = "Once your booking is confirmed and a driver is assigned, you can track the delivery in real-time on the tracking screen."
    val faqPaymentMethods: String get() = "What payment methods are accepted?"
    val faqPaymentMethodsAnswer: String get() = "We accept Cash, DuitNow, Credit/Debit Cards, and Wallet payments."
    val faqCancelBooking: String get() = "How do I cancel a booking?"
    val faqCancelBookingAnswer: String get() = "You can cancel a booking from the tracking screen before the driver picks up your package. Cancellation charges may apply."
    val faqDamagedPackage: String get() = "What if my package is damaged?"
    val faqDamagedPackageAnswer: String get() = "We have insurance coverage for all deliveries. Contact our support team within 24 hours of delivery to file a claim."
    val faqContactDriver: String get() = "How do I contact the driver?"
    val faqContactDriverAnswer: String get() = "You can call the driver directly from the tracking screen once they are assigned to your booking."

    // ── Calculate Screen ──
    val calculate: String get() = "Calculate"
    val domestic: String get() = "Domestic"
    val international: String get() = "International"
    val whatAreYouSendingCalc: String get() = "What are you sending?"
    val products: String get() = "Products"
    val boxes: String get() = "Boxes"
    val addAddressPlaceholder: String get() = "Add address"
    val deliveryOption: String get() = "Delivery Option"
    val kilogram: String get() = "Kilogram"
    val freeCheck: String get() = "Free Check"
    val required: String get() = "This field is required"
    val calculating: String get() = "Calculating..."

    // ── History Screen ──
    val deliveryInProgress: String get() = "You have 1 delivery in progress"
    val toDeliveryLocation: String get() = "to delivery location"
    val inProgress: String get() = "In progress"
    val whatWouldYouLikeToDo: String get() = "What would you like to do?"
    val instantDelivery: String get() = "Instant Delivery"
    val instantDeliveryDesc: String get() = "Courier takes only your package and delivers instantly"
    val scheduleDelivery: String get() = "Schedule Delivery"
    val scheduleDeliveryDesc: String get() = "Courier comes to pick up on your specified date and time"
    val history: String get() = "History"
    val viewAll: String get() = "View all"
    val dropOff: String get() = "Drop off"
    val recipientLabel: (String) -> String get() = { "Receipient: $it" }

    // ── Driver Rating Screen ──
    val sayNiceToDriver: String get() = "Say something nice to your driver"
    val enterYourTips: String get() = "Enter your tips here"

    // ── Language Selection Dialog ──
    val selectYourLanguage: String get() = "Select Your Language"

    // ── Wallet Screen ──
    val walletTitle: String get() = "My Wallet"
    val walletBalance: String get() = "Wallet Balance"
    val topUp: String get() = "Top Up"
    val topUpWallet: String get() = "Top Up Wallet"
    val enterAmount: String get() = "Enter amount (RM)"
    val transactionHistory: String get() = "Transaction History"
    val noTransactions: String get() = "No transactions yet"

    // ── Chat Screen ──
    val chatWithDriver: String get() = "Chat with driver"
    val typeMessage: String get() = "Type a message..."
    val send: String get() = "Send"
    val noMessagesYet: String get() = "No messages yet"
    val startConversation: String get() = "Send a message to start the conversation"

    // ── Support Screen ──
    val supportTitle: String get() = "Help & Support"
    val noTickets: String get() = "No support tickets"
    val noTicketsSubtitle: String get() = "Need help? Create a ticket"
    val createTicket: String get() = "Create Support Ticket"
    val categoryLabel: String get() = "Category"
    val subjectLabel: String get() = "Subject"
    val describeIssue: String get() = "Describe your issue..."
    val deliveryIssue: String get() = "Delivery"
    val paymentIssueCat: String get() = "Payment"
    val driverComplaint: String get() = "Driver"
    val refundRequest: String get() = "Refund"
    val appBug: String get() = "App Bug"
    val otherCategory: String get() = "Other"
    val closeTicket: String get() = "Close"
    val typeReply: String get() = "Type your reply..."

    // ── Promo Screen ──
    val promoAndReferrals: String get() = "Promos & Referrals"
    val promoCodes: String get() = "Promo Codes"
    val referrals: String get() = "Referrals"
    val noCoupons: String get() = "No coupons available"
    val yourReferralCode: String get() = "Your Referral Code"
    val shareAndEarn: String get() = "Share with friends and earn RM 5 each when they sign up and book"
    val totalReferrals: String get() = "Total Referrals"
    val totalEarned: String get() = "Total Earned"
    val haveReferralCode: String get() = "Have a referral code?"
    val enterReferralCode: String get() = "Enter code"
    val apply: String get() = "Apply"

    // ── Invoice Screen ──
    val invoiceTitle: String get() = "Invoice"
    val taxInvoice: String get() = "TAX INVOICE"
    val billTo: String get() = "BILL TO"
    val invoiceDate: String get() = "DATE"
    val tripDetails: String get() = "Trip Details"
    val driverLabel: String get() = "Driver"
    val priceBreakdown: String get() = "Price Breakdown"
    val subtotal: String get() = "Subtotal"
    val discountLabel: String get() = "Discount"
    val totalAmount: String get() = "Total"

    // ── Delivery Verification ──
    val verifyDelivery: String get() = "Verify Delivery"
    val enterDeliveryOtp: String get() = "Enter delivery OTP"
    val deliveryVerified: String get() = "Delivery verified successfully!"
    val viewInvoice: String get() = "View Invoice"
}
