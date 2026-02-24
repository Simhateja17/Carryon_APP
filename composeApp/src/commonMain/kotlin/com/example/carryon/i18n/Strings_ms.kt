package com.example.carryon.i18n

object MsStrings : AppStrings {
    // ── Common / Shared ──
    override val appName = "Carry On"
    override val next = "Seterusnya"
    override val continueText = "Teruskan"
    override val cancel = "Batal"
    override val save = "Simpan"
    override val delete = "Padam"
    override val submit = "Hantar"
    override val search = "Cari"
    override val ok = "OK"
    override val back = "Kembali"
    override val loading = "Memuatkan..."
    override val or = "Atau"
    override val me = "Saya"
    override val recipient = "Penerima"
    override val from = "Dari"
    override val to = "Ke"
    override val email = "E-mel"
    override val notifications = "Pemberitahuan"

    // ── Navigation ──
    override val navSearch = "Cari"
    override val navMessages = "Mesej"
    override val navHome = "Utama"
    override val navProfile = "Profil"

    // ── Welcome Screen ──
    override val welcome = "Selamat Datang"
    override val welcomeSubtitle = "Nikmati Pengalaman Lebih Baik dengan "
    override val createAnAccount = "Cipta akaun"
    override val logIn = "Log Masuk"

    // ── Login Screen ──
    override val welcomeTo = "Selamat Datang ke "
    override val loginSubtitle = "Hai, Sila log masuk untuk meneruskan"
    override val emailAddress = "Alamat E-mel"
    override val enterYourEmail = "Masukkan e-mel anda"
    override val password = "Kata Laluan"
    override val forgotPassword = "Lupa Kata Laluan?"
    override val dontHaveAccount = "Belum mempunyai Akaun?"
    override val signUp = "Daftar"
    override val noAccountFound = "Tiada akaun dijumpai dengan e-mel ini. Sila daftar."
    override val unexpectedError = "Ralat tidak dijangka"

    // ── Register Screen ──
    override val registerSubtitle = "Hai, Sila daftar untuk bermula"
    override val name = "Nama"
    override val enterYourName = "Masukkan nama anda"
    override val confirmPassword = "Sahkan Kata Laluan"
    override val confirmPasswordPlaceholder = "Sahkan kata laluan"
    override val failedToSendCode = "Gagal menghantar kod pengesahan"

    // ── OTP Screen ──
    override val enterTheCode = "Masukkan Kod"
    override val verificationCodeSentTo = "Kod pengesahan telah dihantar ke"
    override val dontReceiveCode = "Tidak menerima kod?  "
    override val resendAgain = "Hantar semula"
    override val resendAgainTimer: (Int) -> String = { "Hantar semula (${it}s)" }
    override val verificationFailed = "Pengesahan gagal"
    override val pleaseEnter6DigitCode = "Sila masukkan kod 6 digit"
    override val failedToResendCode = "Gagal menghantar semula kod"

    // ── Home Screen ──
    override val weAreReadyToServe = "Kami Sedia\nMelayani Anda"
    override val pickupLocation = "Lokasi Pengambilan"
    override val useMyLocation = "Guna lokasi saya"
    override val detectingLocation = "Mengesan lokasi anda\u2026"
    override val enterPickupAddress = "Masukkan alamat pengambilan"
    override val deliveryLocation = "Lokasi Penghantaran"
    override val enterDeliveryAddress = "Masukkan alamat penghantaran"
    override val vehicleType = "Jenis Kenderaan"
    override val fastAndReliable = "Penghantaran pantas & dipercayai"
    override val ourServices = "Perkhidmatan kami"
    override val sameDayDelivery = "Penghantaran\nhari sama"
    override val overnightDelivery = "Penghantaran\nsemalaman"
    override val expressDelivery = "Penghantaran\nekspres"

    // ── Ready To Book Screen ──
    override val youAreReadyToBook = "Anda Sedia untuk Menempah"
    override val readyToBookSubtitle = "Akaun anda telah diaktifkan. Jom tempah muatan pertama anda"
    override val letsRide = "Jom Hantar"

    // ── Select Address Screen ──
    override val selectAddress = "Pilih alamat"
    override val nearbyPlaces = "Tempat berdekatan"
    override val noNearbyPlaces = "Tiada tempat berdekatan dijumpai"

    // ── Booking Screen ──
    override val distance = "Jarak"
    override val duration = "Tempoh"
    override val charge = "Caj"
    override val pickup = "Pengambilan"
    override val delivery = "Penghantaran"
    override val pickupLocationLabel = "Lokasi Pengambilan"
    override val deliveryLocationLabel = "Lokasi Penghantaran"
    override val selectWhoPays = "Pilih siapa yang membayar"
    override val paymentType = "Jenis pembayaran"
    override val selectVehicle = "Pilih Kenderaan"
    override val ride = "Hantar"
    override val upToKg: (Int) -> String = { "Sehingga $it kg" }
    override val rmAmount: (Int) -> String = { "RM $it" }
    override val minDuration: (Int) -> String = { "$it min" }

    // ── Details Screen ──
    override val details = "Butiran"
    override val whatAreYouSending = "Apa yang anda hantar"
    override val selectTypeOfItem = "Pilih jenis barang (cth. gajet, dokumen)"
    override val select = "Pilih"
    override val quantity = "Kuantiti"
    override val recipientNames = "Nama Penerima"
    override val recipientContactNumber = "Nombor telefon penerima"
    override val takePictureOfPackage = "Ambil gambar bungkusan"
    override val prohibitedItems = "Barang Larangan kami termasuk: blah, blah, blah, blah, blah, blah, blah, blah, blah, blah, blah, blah, blah, blah"

    // ── Sender Receiver Screen ──
    override val requestForRide = "Permintaan Penghantaran"
    override val currentLocation = "Lokasi Semasa"
    override val office = "Pejabat"
    override val whatAreYouSendingQuestion = "Apa yang anda hantar?"
    override val sampleType = "Jenis contoh"
    override val request = "Mohon"
    override val senderName = "Nama Penghantar"
    override val overallTrack = "Jejak Keseluruhan"
    override val receiverName = "Nama Penerima"
    override val address = "Alamat"

    // ── Payment Screen ──
    override val selectPaymentMethod = "Pilih kaedah pembayaran"
    override val priceOfPackage = "Ini adalah harga bungkusan"
    override val paymentMethod = "Kaedah Pembayaran"
    override val visaCard = "Visa Card"
    override val mastercard = "Mastercard"
    override val cashOnDelivery = "Bayar Semasa Penghantaran"
    override val wallet = "Dompet"
    override val cash = "Tunai"

    // ── Payment Success Screen ──
    override val paymentSuccess = "Pembayaran Berjaya"
    override val paymentProcessedSuccessfully = "Pembayaran anda telah\nberjaya diproses"

    // ── Request For Ride Screen ──
    override val selectPickupLocation = "Pilih lokasi pengambilan"
    override val selectDeliveryLocation = "Pilih lokasi penghantaran"
    override val fairPrice = "Harga Berpatutan"
    override val taxPercent = "Cukai (5%)"
    override val reviews = "ulasan"

    // ── Track Shipment Screen ──
    override val trackYourShipment = "Jejak penghantaran anda"
    override val enterTrackingNumber = "Masukkan nombor penjejakan anda"
    override val yourPackage = "Bungkusan Anda"
    override val documents = "Dokumen"
    override val destination = "Destinasi"
    override val itemWeight = "Berat Barang"
    override val viewDetails = "Lihat butiran"
    override val sentPackage = "Bungkusan Dihantar"
    override val transit = "Transit"
    override val onAJourney = "Dalam perjalanan"
    override val accepted = "Diterima"

    // ── Tracking Screen ──
    override val orderIdLabel: (String) -> String = { "ID Pesanan: $it" }
    override val estimatedDelivery = "Anggaran penghantaran: 11:30 AM"
    override val processing = "Memproses"
    override val delivering = "Menghantar"
    override val parcels = "Bungkusan"
    override val estExpress = "Anggaran Ekspres"
    override val rateDriver = "Nilai Pemandu"
    override val orderConfirmed = "Pesanan Disahkan"
    override val orderPlaced = "Pesanan anda telah dibuat"
    override val driverAssigned = "Pemandu Ditugaskan"
    override val driverOnTheWay = "John Smith sedang dalam perjalanan"
    override val pickedUp = "Telah Diambil"
    override val packageCollected = "Bungkusan dikutip daripada penghantar"
    override val inTransit = "Dalam Transit"
    override val onTheWayToDestination = "Dalam perjalanan ke destinasi"
    override val outForDelivery = "Keluar untuk Penghantaran"
    override val nearYourLocation = "Berhampiran lokasi anda"
    override val delivered = "Dihantar"
    override val packageDeliveredSuccessfully = "Bungkusan berjaya dihantar"
    override val giveRatingForDriver = "Beri Penilaian untuk Pemandu"
    override val howWasTheDriver = "Bagaimana pemandu anda?"
    override val whatImpressedYou = "Yay! Apa yang mengagumkan anda?"
    override val goodCommunication = "Komunikasi baik"
    override val excellentService = "Perkhidmatan cemerlang"
    override val cleanAndComfy = "Bersih & Selesa"
    override val tipsForDriver = "Tips untuk menggembirakan pemandu anda"

    // ── Tracking Live Screen ──
    override val mins = "min"
    override val deliveryPartnerDriving = "Rakan penghantaran sedang memandu\ndengan selamat untuk menghantar pesanan anda"
    override val callDeliveryAgent = "Hubungi Ejen Penghantaran"

    // ── Active Shipment Screen ──
    override val shareWithNeighbors = "Kongsi penghantaran dengan jiran"
    override val forExtraDiscount = "Untuk diskaun tambahan"
    override val dispatched = "Dihantar keluar"
    override val deliverBy = "Hantar sebelum"
    override val trackShipments = "Jejak Penghantaran"

    // ── Delivery Details Screen ──
    override val newLabel = "Baharu"
    override val sendersName = "Nama Penghantar"
    override val sendersNumber = "Nombor Penghantar"
    override val receiversName = "Nama Penerima"
    override val receiversNumber = "Nombor Penerima"
    override val deliveryMethod = "Kaedah Penghantaran: "
    override val deliveryFee = "Yuran Penghantaran: "
    override val unsuccessful = "Tidak Berjaya"

    // ── Package Details Screen ──
    override val packageLabel = "Bungkusan"

    // ── Orders Screen ──
    override val myOrders = "Pesanan Saya"
    override val all = "Semua"
    override val active = "Aktif"
    override val completed = "Selesai"
    override val cancelled = "Dibatalkan"
    override val noOrdersFound = "Tiada pesanan dijumpai"
    override val ordersWillAppearHere = "Pesanan anda akan dipaparkan di sini"
    override val findingDriver = "Mencari Pemandu"

    // ── Profile Screen ──
    override val hiThere = "Hai!"
    override val savedAddresses = "Alamat\nTersimpan"
    override val rewards = "Ganjaran"
    override val helpAndSupport = "Bantuan & Sokongan"
    override val termsAndConditions = "Terma dan Syarat"
    override val settings = "Tetapan"
    override val referYourFriend = "Rujuk Rakan Anda"
    override val logout = "Log Keluar"

    // ── Settings Screen ──
    override val account = "Akaun"
    override val editProfile = "Sunting Profil"
    override val changePassword = "Tukar Kata Laluan"
    override val savedAddressesMenu = "Alamat Tersimpan"
    override val pushNotifications = "Pemberitahuan Push"
    override val emailNotifications = "Pemberitahuan E-mel"
    override val smsNotifications = "Pemberitahuan SMS"
    override val preferences = "Keutamaan"
    override val language = "Bahasa"
    override val currency = "Mata Wang"
    override val about = "Perihal"
    override val appVersion = "Versi Aplikasi"
    override val privacyPolicy = "Dasar Privasi"
    override val termsOfService = "Terma Perkhidmatan"

    // ── Edit Profile Screen ──
    override val changePhoto = "Tukar Foto"
    override val fullName = "Nama Penuh"
    override val phoneNumber = "Nombor Telefon"
    override val phoneCannotBeChanged = "Nombor telefon tidak boleh ditukar"
    override val saveChanges = "Simpan Perubahan"
    override val success = "Berjaya"
    override val profileUpdatedSuccessfully = "Profil anda telah berjaya dikemas kini."

    // ── Saved Addresses Screen ──
    override val noSavedAddresses = "Tiada alamat tersimpan"
    override val addAddressesForQuickBooking = "Tambah alamat untuk tempahan pantas"
    override val addAddress = "+ Tambah Alamat"
    override val deleteAddress = "Padam Alamat"
    override val deleteAddressConfirmation = "Adakah anda pasti mahu memadamkan alamat ini?"
    override val pickLocationOnMap = "Pilih Lokasi di Peta"
    override val tapOnMapToSelect = "Ketik pada peta untuk memilih lokasi"
    override val confirmLocation = "Sahkan Lokasi"
    override val addNewAddress = "Tambah Alamat Baharu"
    override val labelExample = "Label (cth., Rumah Ibu)"
    override val pickOnMap = "Pilih di Peta"
    override val fullAddress = "Alamat Penuh"
    override val landmarkOptional = "Mercu Tanda (Pilihan)"
    override val contactName = "Nama Kenalan"
    override val contactPhone = "Telefon Kenalan"

    // ── Help Screen ──
    override val contactUs = "Hubungi Kami"
    override val callUs = "Hubungi Kami"
    override val available247 = "Tersedia 24/7"
    override val chatSupport = "Sokongan Sembang"
    override val typicallyReplies = "Biasanya membalas dalam 5 minit"
    override val emailUs = "E-mel Kami"
    override val quickHelp = "Bantuan Pantas"
    override val trackOrder = "Jejak Pesanan"
    override val paymentIssue = "Isu Pembayaran"
    override val cancelOrder = "Batal Pesanan"
    override val refundStatus = "Status Bayaran Balik"
    override val faq = "Soalan Lazim"
    override val faqBookDelivery = "Bagaimana cara menempah penghantaran?"
    override val faqBookDeliveryAnswer = "Masukkan lokasi pengambilan dan penghantaran anda di skrin utama, pilih jenis kenderaan, dan ketik 'Tempah Sekarang'. Anda akan melihat anggaran harga sebelum mengesahkan."
    override val faqPriceCalculated = "Bagaimana harga dikira?"
    override val faqPriceCalculatedAnswer = "Harga termasuk tambang asas ditambah caj per kilometer berdasarkan jenis kenderaan dan jarak. Semasa waktu puncak, mungkin ada caj tambahan."
    override val faqScheduleDelivery = "Bolehkah saya menjadualkan penghantaran untuk kemudian?"
    override val faqScheduleDeliveryAnswer = "Ya! Di skrin utama, ketik pada 'Jadual' untuk menempah penghantaran pada tarikh dan masa akan datang."
    override val faqTrackDelivery = "Bagaimana cara menjejak penghantaran saya?"
    override val faqTrackDeliveryAnswer = "Setelah tempahan anda disahkan dan pemandu ditugaskan, anda boleh menjejak penghantaran secara langsung di skrin penjejakan."
    override val faqPaymentMethods = "Apakah kaedah pembayaran yang diterima?"
    override val faqPaymentMethodsAnswer = "Kami menerima Tunai, UPI, Kad Kredit/Debit, dan pembayaran Dompet."
    override val faqCancelBooking = "Bagaimana cara membatalkan tempahan?"
    override val faqCancelBookingAnswer = "Anda boleh membatalkan tempahan dari skrin penjejakan sebelum pemandu mengambil bungkusan anda. Caj pembatalan mungkin dikenakan."
    override val faqDamagedPackage = "Bagaimana jika bungkusan saya rosak?"
    override val faqDamagedPackageAnswer = "Kami mempunyai perlindungan insurans untuk semua penghantaran. Hubungi pasukan sokongan kami dalam tempoh 24 jam selepas penghantaran untuk memfailkan tuntutan."
    override val faqContactDriver = "Bagaimana cara menghubungi pemandu?"
    override val faqContactDriverAnswer = "Anda boleh menghubungi pemandu terus dari skrin penjejakan setelah mereka ditugaskan untuk tempahan anda."

    // ── Calculate Screen ──
    override val calculate = "Kira"
    override val domestic = "Domestik"
    override val international = "Antarabangsa"
    override val whatAreYouSendingCalc = "Apa yang anda hantar?"
    override val products = "Produk"
    override val boxes = "Kotak"
    override val addAddressPlaceholder = "Tambah alamat"
    override val deliveryOption = "Pilihan Penghantaran"
    override val kilogram = "Kilogram"
    override val freeCheck = "Semakan Percuma"

    // ── History Screen ──
    override val deliveryInProgress = "Anda mempunyai 1 penghantaran sedang berjalan"
    override val toDeliveryLocation = "ke lokasi penghantaran"
    override val inProgress = "Sedang berjalan"
    override val whatWouldYouLikeToDo = "Apa yang anda ingin lakukan?"
    override val instantDelivery = "Penghantaran Segera"
    override val instantDeliveryDesc = "Kurier hanya mengambil bungkusan anda dan menghantar dengan segera"
    override val scheduleDelivery = "Jadualkan Penghantaran"
    override val scheduleDeliveryDesc = "Kurier datang mengambil pada tarikh dan masa yang anda tentukan"
    override val history = "Sejarah"
    override val viewAll = "Lihat semua"
    override val dropOff = "Hantar"
    override val recipientLabel: (String) -> String = { "Penerima: $it" }

    // ── Driver Rating Screen ──
    override val sayNiceToDriver = "Katakan sesuatu yang baik kepada pemandu anda"
    override val enterYourTips = "Masukkan tip anda di sini"

    // ── Language Selection Dialog ──
    override val selectYourLanguage = "Pilih Bahasa Anda"
}
