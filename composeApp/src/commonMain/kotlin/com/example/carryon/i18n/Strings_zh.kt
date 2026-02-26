package com.example.carryon.i18n

object ZhStrings : AppStrings {
    // ── Common / Shared ──
    override val appName = "Carry On"
    override val next = "下一步"
    override val continueText = "继续"
    override val cancel = "取消"
    override val save = "保存"
    override val delete = "删除"
    override val submit = "提交"
    override val search = "搜索"
    override val ok = "确定"
    override val back = "返回"
    override val loading = "加载中..."
    override val or = "或"
    override val me = "我"
    override val recipient = "收件人"
    override val from = "从"
    override val to = "至"
    override val email = "电子邮件"
    override val notifications = "通知"

    // ── Navigation ──
    override val navHome = "首页"
    override val navOrders = "订单"
    override val navPayments = "支付"
    override val navAccount = "账户"

    // ── Welcome Screen ──
    override val welcome = "欢迎"
    override val welcomeSubtitle = "享受更好的使用体验 "
    override val createAnAccount = "创建账户"
    override val logIn = "登录"

    // ── Login Screen ──
    override val welcomeTo = "欢迎来到 "
    override val loginSubtitle = "您好，请登录以继续"
    override val emailAddress = "电子邮箱地址"
    override val enterYourEmail = "输入您的电子邮箱"
    override val password = "密码"
    override val forgotPassword = "忘记密码？"
    override val dontHaveAccount = "没有账户？"
    override val signUp = "注册"
    override val noAccountFound = "未找到与此邮箱关联的账户，请先注册。"
    override val unexpectedError = "意外错误"

    // ── Register Screen ──
    override val registerSubtitle = "您好，请注册以开始使用"
    override val name = "姓名"
    override val enterYourName = "输入您的姓名"
    override val confirmPassword = "确认密码"
    override val confirmPasswordPlaceholder = "确认密码"
    override val failedToSendCode = "验证码发送失败"

    // ── OTP Screen ──
    override val enterTheCode = "输入验证码"
    override val verificationCodeSentTo = "验证码已发送至"
    override val dontReceiveCode = "没有收到验证码？  "
    override val resendAgain = "重新发送"
    override val resendAgainTimer: (Int) -> String = { "重新发送 (${it}秒)" }
    override val verificationFailed = "验证失败"
    override val pleaseEnter6DigitCode = "请输入6位数验证码"
    override val failedToResendCode = "重新发送验证码失败"

    // ── Home Screen ──
    override val weAreReadyToServe = "我们已准备好\n为您服务"
    override val pickupLocation = "取件地点"
    override val useMyLocation = "使用我的位置"
    override val detectingLocation = "正在检测您的位置…"
    override val enterPickupAddress = "输入取件地址"
    override val deliveryLocation = "配送地点"
    override val enterDeliveryAddress = "输入配送地址"
    override val vehicleType = "车辆类型"
    override val fastAndReliable = "快速且可靠的配送"
    override val ourServices = "我们的服务"
    override val sameDayDelivery = "当日\n配送"
    override val overnightDelivery = "隔夜\n配送"
    override val expressDelivery = "快速\n配送"

    // ── Ready To Book Screen ──
    override val youAreReadyToBook = "您已准备好预订"
    override val readyToBookSubtitle = "您的账户已激活。让我们预订您的第一单配送吧"
    override val letsRide = "出发吧"

    // ── Select Address Screen ──
    override val selectAddress = "选择地址"
    override val nearbyPlaces = "附近地点"
    override val noNearbyPlaces = "未找到附近地点"

    // ── Booking Screen ──
    override val distance = "距离"
    override val duration = "时长"
    override val charge = "费用"
    override val pickup = "取件"
    override val delivery = "配送"
    override val pickupLocationLabel = "取件地点"
    override val deliveryLocationLabel = "配送地点"
    override val selectWhoPays = "选择付款方"
    override val paymentType = "付款方式"
    override val selectVehicle = "选择车辆"
    override val ride = "叫车"
    override val upToKg: (Int) -> String = { "最多 $it 公斤" }
    override val rmAmount: (Int) -> String = { "RM $it" }
    override val minDuration: (Int) -> String = { "$it 分钟" }

    // ── Details Screen ──
    override val details = "详情"
    override val whatAreYouSending = "您要寄什么"
    override val selectTypeOfItem = "选择物品类型（如电子产品、文件）"
    override val select = "选择"
    override val quantity = "数量"
    override val recipientNames = "收件人姓名"
    override val recipientContactNumber = "收件人联系电话"
    override val takePictureOfPackage = "拍摄包裹照片"
    override val prohibitedItems = "我们的禁运物品包括：枪支及武器、爆炸物、易燃物质、违禁药品、危险化学品、易腐食品、活体动物、超过100ml的液体、尖锐物品、假冒商品及现金或贵重物品。"

    // ── Sender Receiver Screen ──
    override val requestForRide = "请求配送"
    override val currentLocation = "当前位置"
    override val office = "办公室"
    override val whatAreYouSendingQuestion = "您要寄什么？"
    override val sampleType = "示例类型"
    override val request = "请求"
    override val senderName = "寄件人姓名"
    override val overallTrack = "全程追踪"
    override val receiverName = "收件人姓名"
    override val address = "地址"

    // ── Payment Screen ──
    override val selectPaymentMethod = "选择付款方式"
    override val priceOfPackage = "这是包裹的价格"
    override val paymentMethod = "付款方式"
    override val visaCard = "Visa Card"
    override val mastercard = "Mastercard"
    override val cashOnDelivery = "货到付款"
    override val wallet = "钱包"
    override val cash = "现金"

    // ── Payment Success Screen ──
    override val paymentSuccess = "支付成功"
    override val paymentProcessedSuccessfully = "您的支付已成功处理"

    // ── Request For Ride Screen ──
    override val selectPickupLocation = "选择取件地点"
    override val selectDeliveryLocation = "选择配送地点"
    override val fairPrice = "合理价格"
    override val taxPercent = "税 (5%)"
    override val reviews = "评价"

    // ── Track Shipment Screen ──
    override val trackYourShipment = "追踪您的货件"
    override val enterTrackingNumber = "输入您的追踪编号"
    override val yourPackage = "您的包裹"
    override val documents = "文件"
    override val destination = "目的地"
    override val itemWeight = "物品重量"
    override val viewDetails = "查看详情"
    override val sentPackage = "已寄出包裹"
    override val transit = "运输中"
    override val onAJourney = "运送途中"
    override val accepted = "已接受"

    // ── Tracking Screen ──
    override val orderIdLabel: (String) -> String = { "订单编号: $it" }
    override val estimatedDelivery = "预计送达时间: 11:30 AM"
    override val processing = "处理中"
    override val delivering = "配送中"
    override val parcels = "包裹"
    override val estExpress = "预计快递"
    override val rateDriver = "评价司机"
    override val orderConfirmed = "订单已确认"
    override val orderPlaced = "您的订单已下达"
    override val driverAssigned = "司机已分配"
    override val driverOnTheWay = "John Smith 正在前往途中"
    override val pickedUp = "已取件"
    override val packageCollected = "包裹已从寄件人处取走"
    override val inTransit = "运输中"
    override val onTheWayToDestination = "正在前往目的地"
    override val outForDelivery = "正在派送"
    override val nearYourLocation = "在您附近"
    override val delivered = "已送达"
    override val packageDeliveredSuccessfully = "包裹已成功送达"
    override val giveRatingForDriver = "为司机评分"
    override val howWasTheDriver = "司机表现如何？"
    override val whatImpressedYou = "太好了！哪些方面让您满意？"
    override val goodCommunication = "沟通良好"
    override val excellentService = "服务优秀"
    override val cleanAndComfy = "整洁舒适"
    override val tipsForDriver = "给司机小费让他们更开心"

    // ── Tracking Live Screen ──
    override val mins = "分钟"
    override val deliveryPartnerDriving = "配送员正在安全驾驶\n为您送达订单"
    override val callDeliveryAgent = "拨打配送员电话"

    // ── Active Shipment Screen ──
    override val shareWithNeighbors = "与邻居共享配送"
    override val forExtraDiscount = "可享额外折扣"
    override val dispatched = "已发货"
    override val deliverBy = "预计送达"
    override val trackShipments = "追踪货件"

    // ── Delivery Details Screen ──
    override val newLabel = "新"
    override val sendersName = "寄件人姓名"
    override val sendersNumber = "寄件人电话"
    override val receiversName = "收件人姓名"
    override val receiversNumber = "收件人电话"
    override val deliveryMethod = "配送方式: "
    override val deliveryFee = "配送费: "
    override val unsuccessful = "未成功"

    // ── Package Details Screen ──
    override val packageLabel = "包裹"

    // ── Orders Screen ──
    override val myOrders = "我的订单"
    override val all = "全部"
    override val active = "进行中"
    override val completed = "已完成"
    override val cancelled = "已取消"
    override val noOrdersFound = "未找到订单"
    override val ordersWillAppearHere = "您的订单将显示在这里"
    override val findingDriver = "正在寻找司机"

    // ── Profile Screen ──
    override val hiThere = "您好！"
    override val savedAddresses = "已保存\n地址"
    override val rewards = "奖励"
    override val helpAndSupport = "帮助与支持"
    override val termsAndConditions = "条款与条件"
    override val settings = "设置"
    override val referYourFriend = "推荐给朋友"
    override val logout = "退出登录"

    // ── Settings Screen ──
    override val account = "账户"
    override val editProfile = "编辑资料"
    override val changePassword = "修改密码"
    override val savedAddressesMenu = "已保存地址"
    override val pushNotifications = "推送通知"
    override val emailNotifications = "电子邮件通知"
    override val smsNotifications = "短信通知"
    override val preferences = "偏好设置"
    override val language = "语言"
    override val currency = "货币"
    override val about = "关于"
    override val appVersion = "应用版本"
    override val privacyPolicy = "隐私政策"
    override val termsOfService = "服务条款"

    // ── Edit Profile Screen ──
    override val changePhoto = "更换照片"
    override val fullName = "全名"
    override val phoneNumber = "电话号码"
    override val phoneCannotBeChanged = "电话号码无法更改"
    override val saveChanges = "保存更改"
    override val success = "成功"
    override val profileUpdatedSuccessfully = "您的个人资料已成功更新。"

    // ── Saved Addresses Screen ──
    override val noSavedAddresses = "没有已保存的地址"
    override val addAddressesForQuickBooking = "添加地址以快速预订"
    override val addAddress = "+ 添加地址"
    override val deleteAddress = "删除地址"
    override val deleteAddressConfirmation = "您确定要删除此地址吗？"
    override val pickLocationOnMap = "在地图上选择位置"
    override val tapOnMapToSelect = "点击地图以选择位置"
    override val confirmLocation = "确认位置"
    override val addNewAddress = "添加新地址"
    override val labelExample = "标签（例如：妈妈家）"
    override val pickOnMap = "在地图上选择"
    override val fullAddress = "完整地址"
    override val landmarkOptional = "地标（可选）"
    override val contactName = "联系人姓名"
    override val contactPhone = "联系电话"

    // ── Help Screen ──
    override val contactUs = "联系我们"
    override val callUs = "拨打电话"
    override val available247 = "全天候服务"
    override val chatSupport = "在线客服"
    override val typicallyReplies = "通常5分钟内回复"
    override val emailUs = "发送邮件"
    override val quickHelp = "快速帮助"
    override val trackOrder = "追踪订单"
    override val paymentIssue = "支付问题"
    override val cancelOrder = "取消订单"
    override val refundStatus = "退款状态"
    override val faq = "常见问题"
    override val faqBookDelivery = "如何预订配送？"
    override val faqBookDeliveryAnswer = "在首页输入取件和配送地址，选择车辆类型，然后点击\"立即预订\"。确认前您将看到预估价格。"
    override val faqPriceCalculated = "价格是如何计算的？"
    override val faqPriceCalculatedAnswer = "价格包括基础费用加上根据车辆类型和距离计算的每公里费用。高峰时段可能会有额外的加价费用。"
    override val faqScheduleDelivery = "可以预约稍后的配送吗？"
    override val faqScheduleDeliveryAnswer = "可以！在首页点击\"预约\"即可预订未来日期和时间的配送。"
    override val faqTrackDelivery = "如何追踪我的配送？"
    override val faqTrackDeliveryAnswer = "一旦您的预订确认并且司机已分配，您就可以在追踪页面实时追踪配送状态。"
    override val faqPaymentMethods = "接受哪些付款方式？"
    override val faqPaymentMethodsAnswer = "我们接受现金、DuitNow、信用卡/借记卡和钱包付款。"
    override val faqCancelBooking = "如何取消预订？"
    override val faqCancelBookingAnswer = "在司机取走您的包裹之前，您可以在追踪页面取消预订。可能会收取取消费用。"
    override val faqDamagedPackage = "如果我的包裹损坏了怎么办？"
    override val faqDamagedPackageAnswer = "我们为所有配送提供保险。请在收到配送后24小时内联系我们的客服团队提交索赔。"
    override val faqContactDriver = "如何联系司机？"
    override val faqContactDriverAnswer = "当司机被分配到您的订单后，您可以在追踪页面直接拨打司机电话。"

    // ── Calculate Screen ──
    override val calculate = "计算"
    override val domestic = "国内"
    override val international = "国际"
    override val whatAreYouSendingCalc = "您要寄什么？"
    override val products = "商品"
    override val boxes = "箱子"
    override val addAddressPlaceholder = "添加地址"
    override val deliveryOption = "配送选项"
    override val kilogram = "公斤"
    override val freeCheck = "免费检查"

    // ── History Screen ──
    override val deliveryInProgress = "您有1个配送正在进行中"
    override val toDeliveryLocation = "至配送地点"
    override val inProgress = "进行中"
    override val whatWouldYouLikeToDo = "您想做什么？"
    override val instantDelivery = "即时配送"
    override val instantDeliveryDesc = "快递员只取您的包裹并即时送达"
    override val scheduleDelivery = "预约配送"
    override val scheduleDeliveryDesc = "快递员在您指定的日期和时间上门取件"
    override val history = "历史记录"
    override val viewAll = "查看全部"
    override val dropOff = "送达"
    override val recipientLabel: (String) -> String = { "收件人: $it" }

    // ── Driver Rating Screen ──
    override val sayNiceToDriver = "给司机说些好话吧"
    override val enterYourTips = "在此输入您的小费"

    // ── Language Selection Dialog ──
    override val selectYourLanguage = "选择您的语言"

    // ── Wallet Screen ──
    override val walletTitle = "我的钱包"
    override val walletBalance = "钱包余额"
    override val topUp = "充值"
    override val topUpWallet = "钱包充值"
    override val enterAmount = "输入金额 (RM)"
    override val transactionHistory = "交易记录"
    override val noTransactions = "暂无交易"

    // ── Chat Screen ──
    override val chatWithDriver = "与司机聊天"
    override val typeMessage = "输入消息..."
    override val send = "发送"
    override val noMessagesYet = "暂无消息"
    override val startConversation = "发送消息开始对话"

    // ── Support Screen ──
    override val supportTitle = "帮助与支持"
    override val noTickets = "没有支持工单"
    override val noTicketsSubtitle = "需要帮助？创建工单"
    override val createTicket = "创建支持工单"
    override val categoryLabel = "类别"
    override val subjectLabel = "主题"
    override val describeIssue = "描述您的问题..."
    override val deliveryIssue = "配送"
    override val paymentIssueCat = "支付"
    override val driverComplaint = "司机"
    override val refundRequest = "退款"
    override val appBug = "应用故障"
    override val otherCategory = "其他"
    override val closeTicket = "关闭"
    override val typeReply = "输入回复..."

    // ── Promo Screen ──
    override val promoAndReferrals = "优惠与推荐"
    override val promoCodes = "优惠码"
    override val referrals = "推荐"
    override val noCoupons = "暂无优惠券"
    override val yourReferralCode = "您的推荐码"
    override val shareAndEarn = "与朋友分享，双方注册并下单后各获 RM 5"
    override val totalReferrals = "总推荐数"
    override val totalEarned = "总收入"
    override val haveReferralCode = "有推荐码？"
    override val enterReferralCode = "输入推荐码"
    override val apply = "使用"

    // ── Invoice Screen ──
    override val invoiceTitle = "发票"
    override val taxInvoice = "税务发票"
    override val billTo = "收票人"
    override val invoiceDate = "日期"
    override val tripDetails = "行程详情"
    override val driverLabel = "司机"
    override val priceBreakdown = "价格明细"
    override val subtotal = "小计"
    override val discountLabel = "折扣"
    override val totalAmount = "总计"

    // ── Delivery Verification ──
    override val verifyDelivery = "验证配送"
    override val enterDeliveryOtp = "输入配送 OTP"
    override val deliveryVerified = "配送验证成功！"
    override val viewInvoice = "查看发票"
}
