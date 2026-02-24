package com.example.carryon.i18n

object TaStrings : AppStrings {
    // ── Common / Shared ──
    override val appName = "Carry On"
    override val next = "அடுத்து"
    override val continueText = "தொடரவும்"
    override val cancel = "ரத்துசெய்"
    override val save = "சேமி"
    override val delete = "நீக்கு"
    override val submit = "சமர்ப்பி"
    override val search = "தேடு"
    override val ok = "சரி"
    override val back = "பின்செல்"
    override val loading = "ஏற்றுகிறது..."
    override val or = "அல்லது"
    override val me = "நான்"
    override val recipient = "பெறுநர்"
    override val from = "இருந்து"
    override val to = "வரை"
    override val email = "மின்னஞ்சல்"
    override val notifications = "அறிவிப்புகள்"

    // ── Navigation ──
    override val navSearch = "தேடு"
    override val navMessages = "செய்திகள்"
    override val navHome = "முகப்பு"
    override val navProfile = "சுயவிவரம்"

    // ── Welcome Screen ──
    override val welcome = "வரவேற்கிறோம்"
    override val welcomeSubtitle = "சிறந்த அனுபவத்தைப் பெறுங்கள் "
    override val createAnAccount = "கணக்கை உருவாக்கு"
    override val logIn = "உள்நுழை"

    // ── Login Screen ──
    override val welcomeTo = "வரவேற்கிறோம் "
    override val loginSubtitle = "வணக்கம், தொடர உள்நுழையவும்"
    override val emailAddress = "மின்னஞ்சல் முகவரி"
    override val enterYourEmail = "உங்கள் மின்னஞ்சலை உள்ளிடவும்"
    override val password = "கடவுச்சொல்"
    override val forgotPassword = "கடவுச்சொல் மறந்துவிட்டதா?"
    override val dontHaveAccount = "கணக்கு இல்லையா?"
    override val signUp = "பதிவு செய்"
    override val noAccountFound = "இந்த மின்னஞ்சலுடன் கணக்கு இல்லை. தயவுசெய்து பதிவு செய்யவும்."
    override val unexpectedError = "எதிர்பாராத பிழை"

    // ── Register Screen ──
    override val registerSubtitle = "வணக்கம், தொடங்க பதிவு செய்யவும்"
    override val name = "பெயர்"
    override val enterYourName = "உங்கள் பெயரை உள்ளிடவும்"
    override val confirmPassword = "கடவுச்சொல்லை உறுதிப்படுத்தவும்"
    override val confirmPasswordPlaceholder = "கடவுச்சொல்லை உறுதிப்படுத்தவும்"
    override val failedToSendCode = "சரிபார்ப்பு குறியீடு அனுப்ப முடியவில்லை"

    // ── OTP Screen ──
    override val enterTheCode = "குறியீட்டை உள்ளிடவும்"
    override val verificationCodeSentTo = "சரிபார்ப்பு குறியீடு அனுப்பப்பட்டது"
    override val dontReceiveCode = "குறியீடு வரவில்லையா?  "
    override val resendAgain = "மீண்டும் அனுப்பு"
    override val resendAgainTimer: (Int) -> String = { "மீண்டும் அனுப்பு (${it}வி)" }
    override val verificationFailed = "சரிபார்ப்பு தோல்வி"
    override val pleaseEnter6DigitCode = "6 இலக்க குறியீட்டை உள்ளிடவும்"
    override val failedToResendCode = "குறியீட்டை மீண்டும் அனுப்ப முடியவில்லை"

    // ── Home Screen ──
    override val weAreReadyToServe = "நாங்கள் உங்களுக்கு\nசேவை செய்ய தயார்"
    override val pickupLocation = "பிக்அப் இடம்"
    override val useMyLocation = "என் இருப்பிடத்தைப் பயன்படுத்து"
    override val detectingLocation = "உங்கள் இருப்பிடத்தைக் கண்டறிகிறது…"
    override val enterPickupAddress = "பிக்அப் முகவரியை உள்ளிடவும்"
    override val deliveryLocation = "டெலிவரி இடம்"
    override val enterDeliveryAddress = "டெலிவரி முகவரியை உள்ளிடவும்"
    override val vehicleType = "வாகன வகை"
    override val fastAndReliable = "விரைவான & நம்பகமான டெலிவரி"
    override val ourServices = "எங்கள் சேவைகள்"
    override val sameDayDelivery = "அன்றே\nடெலிவரி"
    override val overnightDelivery = "இரவு\nடெலிவரி"
    override val expressDelivery = "விரைவு\nடெலிவரி"

    // ── Ready To Book Screen ──
    override val youAreReadyToBook = "நீங்கள் பதிவு செய்ய தயார்"
    override val readyToBookSubtitle = "உங்கள் கணக்கு செயல்படுத்தப்பட்டது. உங்கள் முதல் ஏற்றத்தை பதிவு செய்யுங்கள்"
    override val letsRide = "புறப்படுவோம்"

    // ── Select Address Screen ──
    override val selectAddress = "முகவரியைத் தேர்வு செய்"
    override val nearbyPlaces = "அருகிலுள்ள இடங்கள்"
    override val noNearbyPlaces = "அருகிலுள்ள இடங்கள் எதுவும் இல்லை"

    // ── Booking Screen ──
    override val distance = "தூரம்"
    override val duration = "காலம்"
    override val charge = "கட்டணம்"
    override val pickup = "பிக்அப்"
    override val delivery = "டெலிவரி"
    override val pickupLocationLabel = "பிக்அப் இடம்"
    override val deliveryLocationLabel = "டெலிவரி இடம்"
    override val selectWhoPays = "யார் பணம் செலுத்துவது என்பதைத் தேர்வு செய்"
    override val paymentType = "கட்டண வகை"
    override val selectVehicle = "வாகனத்தைத் தேர்வு செய்"
    override val ride = "பயணம்"
    override val upToKg: (Int) -> String = { "$it கிலோ வரை" }
    override val rmAmount: (Int) -> String = { "RM $it" }
    override val minDuration: (Int) -> String = { "$it நிமிடம்" }

    // ── Details Screen ──
    override val details = "விவரங்கள்"
    override val whatAreYouSending = "நீங்கள் என்ன அனுப்புகிறீர்கள்"
    override val selectTypeOfItem = "பொருளின் வகையைத் தேர்ந்தெடுக்கவும் (எ.கா. கேஜெட், ஆவணம்)"
    override val select = "தேர்வு"
    override val quantity = "அளவு"
    override val recipientNames = "பெறுநர் பெயர்கள்"
    override val recipientContactNumber = "பெறுநர் தொடர்பு எண்"
    override val takePictureOfPackage = "பொருளின் புகைப்படம் எடுக்கவும்"
    override val prohibitedItems = "தடைசெய்யப்பட்ட பொருட்கள்: துப்பாக்கிகள் & ஆயுதங்கள், வெடிபொருட்கள், எரியக்கூடிய பொருட்கள், சட்டவிரோத போதைப்பொருட்கள், ஆபத்தான இரசாயனங்கள், கெட்டுப்போகும் உணவுகள், உயிரினங்கள், 100ml க்கு மேற்பட்ட திரவங்கள், கூர்மையான பொருட்கள், போலி பொருட்கள் மற்றும் பண மதிப்புமிக்க பொருட்கள்."

    // ── Sender Receiver Screen ──
    override val requestForRide = "பயணத்திற்கான கோரிக்கை"
    override val currentLocation = "தற்போதைய இருப்பிடம்"
    override val office = "அலுவலகம்"
    override val whatAreYouSendingQuestion = "நீங்கள் என்ன அனுப்புகிறீர்கள்?"
    override val sampleType = "மாதிரி வகை"
    override val request = "கோரிக்கை"
    override val senderName = "அனுப்புநர் பெயர்"
    override val overallTrack = "ஒட்டுமொத்த கண்காணிப்பு"
    override val receiverName = "பெறுநர் பெயர்"
    override val address = "முகவரி"

    // ── Payment Screen ──
    override val selectPaymentMethod = "கட்டண முறையைத் தேர்வு செய்"
    override val priceOfPackage = "இது பொருளின் விலை"
    override val paymentMethod = "கட்டண முறை"
    override val visaCard = "Visa Card"
    override val mastercard = "Mastercard"
    override val cashOnDelivery = "டெலிவரியில் பணம் செலுத்து"
    override val wallet = "பணப்பை"
    override val cash = "பணம்"

    // ── Payment Success Screen ──
    override val paymentSuccess = "பணம் செலுத்துதல் வெற்றி"
    override val paymentProcessedSuccessfully = "உங்கள் பணம் செலுத்துதல்\nவெற்றிகரமாக செயலாக்கப்பட்டது"

    // ── Request For Ride Screen ──
    override val selectPickupLocation = "பிக்அப் இடத்தைத் தேர்வு செய்"
    override val selectDeliveryLocation = "டெலிவரி இடத்தைத் தேர்வு செய்"
    override val fairPrice = "நியாயமான விலை"
    override val taxPercent = "வரி (5%)"
    override val reviews = "மதிப்புரைகள்"

    // ── Track Shipment Screen ──
    override val trackYourShipment = "உங்கள் ஏற்றுமதியைக் கண்காணிக்கவும்"
    override val enterTrackingNumber = "கண்காணிப்பு எண்ணை உள்ளிடவும்"
    override val yourPackage = "உங்கள் பொருள்"
    override val documents = "ஆவணங்கள்"
    override val destination = "சேருமிடம்"
    override val itemWeight = "பொருளின் எடை"
    override val viewDetails = "விவரங்களைக் காண்க"
    override val sentPackage = "அனுப்பிய பொருள்"
    override val transit = "போக்குவரத்தில்"
    override val onAJourney = "பயணத்தில்"
    override val accepted = "ஏற்றுக்கொள்ளப்பட்டது"

    // ── Tracking Screen ──
    override val orderIdLabel: (String) -> String = { "ஆர்டர் எண்: $it" }
    override val estimatedDelivery = "எதிர்பார்க்கப்படும் டெலிவரி: 11:30 AM"
    override val processing = "செயலாக்கத்தில்"
    override val delivering = "டெலிவரி செய்கிறது"
    override val parcels = "பொட்டலங்கள்"
    override val estExpress = "மதிப்பிடப்பட்ட விரைவு"
    override val rateDriver = "ஓட்டுநரை மதிப்பிடு"
    override val orderConfirmed = "ஆர்டர் உறுதிப்படுத்தப்பட்டது"
    override val orderPlaced = "உங்கள் ஆர்டர் வைக்கப்பட்டது"
    override val driverAssigned = "ஓட்டுநர் நியமிக்கப்பட்டார்"
    override val driverOnTheWay = "John Smith வழியில் உள்ளார்"
    override val pickedUp = "எடுக்கப்பட்டது"
    override val packageCollected = "அனுப்புநரிடமிருந்து பொருள் சேகரிக்கப்பட்டது"
    override val inTransit = "போக்குவரத்தில்"
    override val onTheWayToDestination = "சேருமிடத்திற்கு பயணத்தில்"
    override val outForDelivery = "டெலிவரிக்கு புறப்பட்டது"
    override val nearYourLocation = "உங்கள் இருப்பிடத்திற்கு அருகில்"
    override val delivered = "டெலிவரி செய்யப்பட்டது"
    override val packageDeliveredSuccessfully = "பொருள் வெற்றிகரமாக டெலிவரி செய்யப்பட்டது"
    override val giveRatingForDriver = "ஓட்டுநருக்கு மதிப்பீடு அளிக்கவும்"
    override val howWasTheDriver = "ஓட்டுநர் எப்படி இருந்தார்?"
    override val whatImpressedYou = "என்ன உங்களைக் கவர்ந்தது?"
    override val goodCommunication = "நல்ல தொடர்பு"
    override val excellentService = "சிறந்த சேவை"
    override val cleanAndComfy = "சுத்தமான & வசதியான"
    override val tipsForDriver = "உங்கள் ஓட்டுநரை மகிழ்விக்க குறிப்புகள்"

    // ── Tracking Live Screen ──
    override val mins = "நிமிடங்கள்"
    override val deliveryPartnerDriving = "டெலிவரி கூட்டாளர் உங்கள் ஆர்டரை\nபாதுகாப்பாக டெலிவரி செய்கிறார்"
    override val callDeliveryAgent = "டெலிவரி முகவரை அழைக்கவும்"

    // ── Active Shipment Screen ──
    override val shareWithNeighbors = "அண்டை வீட்டாருடன் டெலிவரியைப் பகிரவும்"
    override val forExtraDiscount = "கூடுதல் தள்ளுபடிக்கு"
    override val dispatched = "அனுப்பப்பட்டது"
    override val deliverBy = "டெலிவரி தேதி"
    override val trackShipments = "ஏற்றுமதிகளைக் கண்காணி"

    // ── Delivery Details Screen ──
    override val newLabel = "புதிய"
    override val sendersName = "அனுப்புநர் பெயர்"
    override val sendersNumber = "அனுப்புநர் எண்"
    override val receiversName = "பெறுநர் பெயர்"
    override val receiversNumber = "பெறுநர் எண்"
    override val deliveryMethod = "டெலிவரி முறை: "
    override val deliveryFee = "டெலிவரி கட்டணம்: "
    override val unsuccessful = "தோல்வி"

    // ── Package Details Screen ──
    override val packageLabel = "பொருள்"

    // ── Orders Screen ──
    override val myOrders = "என் ஆர்டர்கள்"
    override val all = "அனைத்தும்"
    override val active = "செயலில்"
    override val completed = "நிறைவடைந்தது"
    override val cancelled = "ரத்துசெய்யப்பட்டது"
    override val noOrdersFound = "ஆர்டர்கள் எதுவும் இல்லை"
    override val ordersWillAppearHere = "உங்கள் ஆர்டர்கள் இங்கே தோன்றும்"
    override val findingDriver = "ஓட்டுநர் தேடுகிறது"

    // ── Profile Screen ──
    override val hiThere = "வணக்கம்!"
    override val savedAddresses = "சேமித்த\nமுகவரிகள்"
    override val rewards = "வெகுமதிகள்"
    override val helpAndSupport = "உதவி & ஆதரவு"
    override val termsAndConditions = "விதிமுறைகள் மற்றும் நிபந்தனைகள்"
    override val settings = "அமைப்புகள்"
    override val referYourFriend = "உங்கள் நண்பரைப் பரிந்துரைக்கவும்"
    override val logout = "வெளியேறு"

    // ── Settings Screen ──
    override val account = "கணக்கு"
    override val editProfile = "சுயவிவரத்தைத் திருத்து"
    override val changePassword = "கடவுச்சொல்லை மாற்று"
    override val savedAddressesMenu = "சேமித்த முகவரிகள்"
    override val pushNotifications = "புஷ் அறிவிப்புகள்"
    override val emailNotifications = "மின்னஞ்சல் அறிவிப்புகள்"
    override val smsNotifications = "SMS அறிவிப்புகள்"
    override val preferences = "விருப்பத்தேர்வுகள்"
    override val language = "மொழி"
    override val currency = "நாணயம்"
    override val about = "பற்றி"
    override val appVersion = "ஆப்ப் பதிப்பு"
    override val privacyPolicy = "தனியுரிமைக் கொள்கை"
    override val termsOfService = "சேவை விதிமுறைகள்"

    // ── Edit Profile Screen ──
    override val changePhoto = "புகைப்படத்தை மாற்று"
    override val fullName = "முழு பெயர்"
    override val phoneNumber = "தொலைபேசி எண்"
    override val phoneCannotBeChanged = "தொலைபேசி எண்ணை மாற்ற முடியாது"
    override val saveChanges = "மாற்றங்களைச் சேமி"
    override val success = "வெற்றி"
    override val profileUpdatedSuccessfully = "உங்கள் சுயவிவரம் வெற்றிகரமாகப் புதுப்பிக்கப்பட்டது."

    // ── Saved Addresses Screen ──
    override val noSavedAddresses = "சேமித்த முகவரிகள் இல்லை"
    override val addAddressesForQuickBooking = "விரைவான பதிவுக்கு முகவரிகளைச் சேர்க்கவும்"
    override val addAddress = "+ முகவரியைச் சேர்"
    override val deleteAddress = "முகவரியை நீக்கு"
    override val deleteAddressConfirmation = "இந்த முகவரியை நீக்க விரும்புகிறீர்களா?"
    override val pickLocationOnMap = "வரைபடத்தில் இடத்தைத் தேர்வு செய்"
    override val tapOnMapToSelect = "இடத்தைத் தேர்வு செய்ய வரைபடத்தில் தட்டவும்"
    override val confirmLocation = "இடத்தை உறுதிப்படுத்து"
    override val addNewAddress = "புதிய முகவரியைச் சேர்"
    override val labelExample = "லேபிள் (எ.கா., அம்மா வீடு)"
    override val pickOnMap = "வரைபடத்தில் தேர்வு செய்"
    override val fullAddress = "முழு முகவரி"
    override val landmarkOptional = "அடையாளம் (விருப்பம்)"
    override val contactName = "தொடர்பு பெயர்"
    override val contactPhone = "தொடர்பு தொலைபேசி"

    // ── Help Screen ──
    override val contactUs = "எங்களை தொடர்பு கொள்ளுங்கள்"
    override val callUs = "எங்களை அழைக்கவும்"
    override val available247 = "24/7 கிடைக்கும்"
    override val chatSupport = "அரட்டை ஆதரவு"
    override val typicallyReplies = "பொதுவாக 5 நிமிடங்களில் பதில்"
    override val emailUs = "மின்னஞ்சல் அனுப்பவும்"
    override val quickHelp = "விரைவு உதவி"
    override val trackOrder = "ஆர்டரைக் கண்காணி"
    override val paymentIssue = "கட்டணப் பிரச்சினை"
    override val cancelOrder = "ஆர்டரை ரத்துசெய்"
    override val refundStatus = "பணத்திரும்ப நிலை"
    override val faq = "அடிக்கடி கேட்கப்படும் கேள்விகள்"
    override val faqBookDelivery = "டெலிவரியை எப்படி பதிவு செய்வது?"
    override val faqBookDeliveryAnswer = "முகப்பு திரையில் பிக்அப் மற்றும் டெலிவரி இடங்களை உள்ளிட்டு, வாகன வகையைத் தேர்ந்தெடுத்து, 'இப்போது பதிவு செய்' என்பதைத் தட்டவும். உறுதிப்படுத்தும் முன் மதிப்பிடப்பட்ட விலையைக் காணலாம்."
    override val faqPriceCalculated = "விலை எவ்வாறு கணக்கிடப்படுகிறது?"
    override val faqPriceCalculatedAnswer = "விலையில் அடிப்படை கட்டணம் மற்றும் வாகன வகை மற்றும் தூரத்தின் அடிப்படையில் கிலோமீட்டர் கட்டணம் அடங்கும். உச்ச நேரங்களில் கூடுதல் கட்டணம் இருக்கலாம்."
    override val faqScheduleDelivery = "பின்னர் டெலிவரியை திட்டமிட முடியுமா?"
    override val faqScheduleDeliveryAnswer = "ஆம்! முகப்பு திரையில் 'திட்டமிடு' என்பதைத் தட்டி எதிர்கால தேதி மற்றும் நேரத்திற்கு டெலிவரியை பதிவு செய்யலாம்."
    override val faqTrackDelivery = "என் டெலிவரியை எப்படி கண்காணிப்பது?"
    override val faqTrackDeliveryAnswer = "உங்கள் பதிவு உறுதிப்படுத்தப்பட்டு ஓட்டுநர் நியமிக்கப்பட்டவுடன், கண்காணிப்பு திரையில் நேரடியாகக் கண்காணிக்கலாம்."
    override val faqPaymentMethods = "எந்த கட்டண முறைகள் ஏற்றுக்கொள்ளப்படுகின்றன?"
    override val faqPaymentMethodsAnswer = "பணம், UPI, கிரெடிட்/டெபிட் கார்டுகள் மற்றும் வாலட் கட்டணங்களை ஏற்கிறோம்."
    override val faqCancelBooking = "பதிவை எவ்வாறு ரத்து செய்வது?"
    override val faqCancelBookingAnswer = "ஓட்டுநர் உங்கள் பொருளை எடுக்கும் முன் கண்காணிப்பு திரையிலிருந்து பதிவை ரத்து செய்யலாம். ரத்து கட்டணம் விதிக்கப்படலாம்."
    override val faqDamagedPackage = "என் பொருள் சேதமடைந்தால் என்ன செய்வது?"
    override val faqDamagedPackageAnswer = "அனைத்து டெலிவரிகளுக்கும் காப்பீடு உள்ளது. டெலிவரிக்குப் பிறகு 24 மணி நேரத்திற்குள் எங்கள் ஆதரவுக் குழுவைத் தொடர்பு கொள்ளவும்."
    override val faqContactDriver = "ஓட்டுநரை எப்படி தொடர்பு கொள்வது?"
    override val faqContactDriverAnswer = "ஓட்டுநர் உங்கள் பதிவுக்கு நியமிக்கப்பட்டவுடன், கண்காணிப்பு திரையிலிருந்து நேரடியாக அழைக்கலாம்."

    // ── Calculate Screen ──
    override val calculate = "கணக்கிடு"
    override val domestic = "உள்நாட்டு"
    override val international = "சர்வதேச"
    override val whatAreYouSendingCalc = "நீங்கள் என்ன அனுப்புகிறீர்கள்?"
    override val products = "பொருட்கள்"
    override val boxes = "பெட்டிகள்"
    override val addAddressPlaceholder = "முகவரியைச் சேர்"
    override val deliveryOption = "டெலிவரி விருப்பம்"
    override val kilogram = "கிலோகிராம்"
    override val freeCheck = "இலவச சோதனை"

    // ── History Screen ──
    override val deliveryInProgress = "1 டெலிவரி நடைபெற்றுக்கொண்டிருக்கிறது"
    override val toDeliveryLocation = "டெலிவரி இடத்திற்கு"
    override val inProgress = "நடைபெறுகிறது"
    override val whatWouldYouLikeToDo = "நீங்கள் என்ன செய்ய விரும்புகிறீர்கள்?"
    override val instantDelivery = "உடனடி டெலிவரி"
    override val instantDeliveryDesc = "கூரியர் உங்கள் பொருளை மட்டும் எடுத்து உடனடியாக டெலிவரி செய்கிறார்"
    override val scheduleDelivery = "திட்டமிட்ட டெலிவரி"
    override val scheduleDeliveryDesc = "கூரியர் நீங்கள் குறிப்பிட்ட தேதி மற்றும் நேரத்தில் வருவார்"
    override val history = "வரலாறு"
    override val viewAll = "அனைத்தையும் காண்க"
    override val dropOff = "இறக்கு"
    override val recipientLabel: (String) -> String = { "பெறுநர்: $it" }

    // ── Driver Rating Screen ──
    override val sayNiceToDriver = "உங்கள் ஓட்டுநருக்கு நல்ல வார்த்தைகள் சொல்லுங்கள்"
    override val enterYourTips = "உங்கள் குறிப்புகளை இங்கே உள்ளிடவும்"

    // ── Language Selection Dialog ──
    override val selectYourLanguage = "உங்கள் மொழியைத் தேர்வு செய்யவும்"
}
