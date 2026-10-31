package com.example.data.repository

import com.example.data.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class TkVelRepository private constructor() {

    companion object {
        @Volatile
        private var instance: TkVelRepository? = null

        fun get(): TkVelRepository {
            return instance ?: synchronized(this) {
                instance ?: TkVelRepository().also { instance = it }
            }
        }
    }

    // Current logged in user (Default demo user Ashwin)
    private val _currentUser = MutableStateFlow(
        UserAccount(
            id = "USR_ASHWIN_01",
            fullName = "T. Ashwin",
            email = "ashwin9566235195@gmail.com",
            primaryPhone = PhoneNumber(
                id = "PH_1",
                countryCode = "+91",
                number = "9566235195",
                isPrimary = true,
                isVerified = true
            ),
            additionalPhones = listOf(
                PhoneNumber(
                    id = "PH_2",
                    countryCode = "+91",
                    number = "9176580731",
                    isPrimary = false,
                    isVerified = true
                )
            ),
            savedAddresses = listOf(
                Address(
                    id = "ADDR_1",
                    fullName = "T. Ashwin",
                    houseNumber = "No. 42",
                    buildingName = "Vel Towers",
                    apartment = "Apt 3B",
                    street = "Anna Salai Main Road",
                    area = "T. Nagar",
                    locality = "Near Panagal Park",
                    landmark = "Opposite Chennai Silks",
                    city = "Chennai",
                    district = "Chennai",
                    state = "Tamil Nadu",
                    pinCode = "600017",
                    country = "India",
                    type = "HOME",
                    isDefault = true,
                    deliveryInstructions = "Leave with security if not available"
                ),
                Address(
                    id = "ADDR_2",
                    fullName = "T. Ashwin (Office)",
                    houseNumber = "Plot 108",
                    buildingName = "TK VEL Corporate Centre",
                    apartment = "Floor 2",
                    street = "Mount Poonamallee High Road",
                    area = "Guindy Industrial Estate",
                    locality = "Guindy",
                    landmark = "Near Metro Station",
                    city = "Chennai",
                    district = "Chennai",
                    state = "Tamil Nadu",
                    pinCode = "600032",
                    country = "India",
                    type = "WORK",
                    isDefault = false,
                    deliveryInstructions = "Deliver during 9 AM - 6 PM"
                )
            ),
            role = "CUSTOMER",
            loyaltyPoints = 450,
            walletBalancePaise = 75000L, // ₹750
            referralCode = "TKVELASHWIN"
        )
    )
    val currentUser: StateFlow<UserAccount> = _currentUser.asStateFlow()

    // Active Language Code
    private val _currentLanguage = MutableStateFlow("en")
    val currentLanguage: StateFlow<String> = _currentLanguage.asStateFlow()

    fun setLanguage(code: String) {
        _currentLanguage.value = code
    }

    // Products Database
    private val _products = MutableStateFlow<List<Product>>(getSeedProducts())
    val products: StateFlow<List<Product>> = _products.asStateFlow()

    // Combos Database
    private val _combos = MutableStateFlow<List<ComboProduct>>(getSeedCombos())
    val combos: StateFlow<List<ComboProduct>> = _combos.asStateFlow()

    // Cart State
    private val _cartItems = MutableStateFlow<List<CartItem>>(emptyList())
    val cartItems: StateFlow<List<CartItem>> = _cartItems.asStateFlow()

    // Active Applied Coupon
    private val _appliedCoupon = MutableStateFlow<Coupon?>(null)
    val appliedCoupon: StateFlow<Coupon?> = _appliedCoupon.asStateFlow()

    // Selected Checkout Delivery Address & Contact Phone
    private val _selectedAddress = MutableStateFlow<Address?>(_currentUser.value.savedAddresses.firstOrNull())
    val selectedAddress: StateFlow<Address?> = _selectedAddress.asStateFlow()

    private val _selectedPhone = MutableStateFlow<PhoneNumber?>(_currentUser.value.primaryPhone)
    val selectedPhone: StateFlow<PhoneNumber?> = _selectedPhone.asStateFlow()

    // Orders Database
    private val _orders = MutableStateFlow<List<Order>>(getSeedOrders())
    val orders: StateFlow<List<Order>> = _orders.asStateFlow()

    // Wishlist Product IDs
    private val _wishlistIds = MutableStateFlow<Set<String>>(setOf("PRD_1", "PRD_3"))
    val wishlistIds: StateFlow<Set<String>> = _wishlistIds.asStateFlow()

    // Support Tickets
    private val _supportTickets = MutableStateFlow<List<SupportTicket>>(getSeedTickets())
    val supportTickets: StateFlow<List<SupportTicket>> = _supportTickets.asStateFlow()

    // AI Assistant Messages
    private val _aiMessages = MutableStateFlow<List<AiChatMessage>>(listOf(
        AiChatMessage(
            id = "MSG_1",
            isUser = false,
            text = "Welcome to TK VEL ENTERPRISES! I'm TK VEL AI, your shopping assistant. How can I help you today? Ask about our cookware, electronics, today's combo offers, check delivery PIN codes, or track your orders!",
            quickActions = listOf("Trending Deals", "Best Kitchen Combos", "Check Delivery", "Track Order"),
            timestamp = "Just now"
        )
    ))
    val aiMessages: StateFlow<List<AiChatMessage>> = _aiMessages.asStateFlow()

    // Admin Feature Flags
    private val _featureFlags = MutableStateFlow(
        mapOf(
            "flash_sale_enabled" to true,
            "cod_available" to true,
            "shiprocket_live" to true,
            "ai_super_studio" to true,
            "360_viewer_enabled" to true,
            "multilingual_enabled" to true
        )
    )
    val featureFlags: StateFlow<Map<String, Boolean>> = _featureFlags.asStateFlow()

    fun toggleFeature(key: String) {
        val current = _featureFlags.value.toMutableMap()
        current[key] = !(current[key] ?: false)
        _featureFlags.value = current
    }

    // Cart Management
    fun addToCart(product: Product, variant: ProductVariant? = null, qty: Int = 1) {
        val current = _cartItems.value.toMutableList()
        val existingIndex = current.indexOfFirst {
            !it.isCombo && it.product?.id == product.id && it.selectedVariant?.id == variant?.id
        }
        if (existingIndex >= 0) {
            val item = current[existingIndex]
            current[existingIndex] = item.copy(quantity = item.quantity + qty)
        } else {
            current.add(
                CartItem(
                    id = UUID.randomUUID().toString(),
                    product = product,
                    selectedVariant = variant ?: product.variants.firstOrNull(),
                    isCombo = false,
                    quantity = qty
                )
            )
        }
        _cartItems.value = current
    }

    fun addComboToCart(combo: ComboProduct, qty: Int = 1) {
        val current = _cartItems.value.toMutableList()
        val existingIndex = current.indexOfFirst { it.isCombo && it.comboProduct?.id == combo.id }
        if (existingIndex >= 0) {
            val item = current[existingIndex]
            current[existingIndex] = item.copy(quantity = item.quantity + qty)
        } else {
            current.add(
                CartItem(
                    id = UUID.randomUUID().toString(),
                    comboProduct = combo,
                    isCombo = true,
                    quantity = qty
                )
            )
        }
        _cartItems.value = current
    }

    fun updateCartQuantity(cartItemId: String, newQty: Int) {
        if (newQty <= 0) {
            removeFromCart(cartItemId)
            return
        }
        _cartItems.value = _cartItems.value.map {
            if (it.id == cartItemId) it.copy(quantity = newQty) else it
        }
    }

    fun removeFromCart(cartItemId: String) {
        _cartItems.value = _cartItems.value.filter { it.id != cartItemId }
    }

    fun clearCart() {
        _cartItems.value = emptyList()
        _appliedCoupon.value = null
    }

    fun applyCoupon(code: String): Pair<Boolean, String> {
        val cleanCode = code.trim().uppercase()
        val validCoupons = listOf(
            Coupon("WELCOME10", "10% Welcome Discount", discountPercent = 10, minOrderPaise = 49900L, maxDiscountPaise = 20000L),
            Coupon("TKVEL100", "Flat ₹100 Off on orders above ₹999", fixedDiscountPaise = 10000L, minOrderPaise = 99900L),
            Coupon("FESTIVE25", "25% Mega Festive Discount", discountPercent = 25, minOrderPaise = 149900L, maxDiscountPaise = 50000L),
            Coupon("SUPER50", "Flat ₹50 Instant Discount", fixedDiscountPaise = 5000L, minOrderPaise = 39900L)
        )
        val match = validCoupons.find { it.code == cleanCode }
        return if (match != null) {
            _appliedCoupon.value = match
            Pair(true, "Coupon ${match.code} applied successfully! ${match.title}")
        } else {
            Pair(false, "Invalid coupon code. Try WELCOME10, TKVEL100 or FESTIVE25")
        }
    }

    fun removeCoupon() {
        _appliedCoupon.value = null
    }

    // Wishlist
    fun toggleWishlist(productId: String) {
        val current = _wishlistIds.value.toMutableSet()
        if (current.contains(productId)) {
            current.remove(productId)
        } else {
            current.add(productId)
        }
        _wishlistIds.value = current
    }

    fun selectAddress(address: Address) {
        _selectedAddress.value = address
    }

    fun selectPhone(phone: PhoneNumber) {
        _selectedPhone.value = phone
    }

    fun addAddress(address: Address) {
        val user = _currentUser.value
        val updatedAddresses = user.savedAddresses + address
        _currentUser.value = user.copy(savedAddresses = updatedAddresses)
        _selectedAddress.value = address
    }

    fun addPhone(number: String) {
        val user = _currentUser.value
        val newPhone = PhoneNumber(
            id = UUID.randomUUID().toString(),
            countryCode = "+91",
            number = number.trim(),
            isPrimary = false,
            isVerified = true
        )
        _currentUser.value = user.copy(additionalPhones = user.additionalPhones + newPhone)
    }

    // Shiprocket PIN code checker
    fun checkPinServiceability(pinCode: String): ShiprocketServiceability {
        val cleanPin = pinCode.trim()
        val isServiceable = cleanPin.length == 6 && cleanPin.all { it.isDigit() }
        val city = when {
            cleanPin.startsWith("600") -> "Chennai"
            cleanPin.startsWith("560") -> "Bengaluru"
            cleanPin.startsWith("110") -> "Delhi NCR"
            cleanPin.startsWith("400") -> "Mumbai"
            cleanPin.startsWith("500") -> "Hyderabad"
            cleanPin.startsWith("641") -> "Coimbatore"
            cleanPin.startsWith("625") -> "Madurai"
            else -> "Metro & Tier-1/2 Hub"
        }
        val state = when {
            cleanPin.startsWith("600") || cleanPin.startsWith("641") || cleanPin.startsWith("625") -> "Tamil Nadu"
            cleanPin.startsWith("560") -> "Karnataka"
            cleanPin.startsWith("110") -> "Delhi"
            cleanPin.startsWith("400") -> "Maharashtra"
            cleanPin.startsWith("500") -> "Telangana"
            else -> "India"
        }
        return ShiprocketServiceability(
            pinCode = cleanPin,
            isServiceable = isServiceable,
            city = city,
            state = state,
            courierName = if (cleanPin.startsWith("6")) "Delhivery Air Express" else "Blue Dart Surface Express",
            estimatedDays = if (cleanPin.startsWith("600")) 1 else 2,
            shippingChargePaise = 0L, // Free Shiprocket delivery
            codAvailable = true
        )
    }

    // Order Creation with immutable snapshot & server-side calculations
    fun placeOrder(
        paymentMethod: String, // "UPI", "UPI_QR", "CARD", "NET_BANKING", "COD"
        address: Address,
        phone: PhoneNumber
    ): Order {
        val items = _cartItems.value
        var subtotal = 0L
        for (item in items) {
            subtotal += item.totalPricePaise
        }

        val deliveryCharge = if (subtotal >= 49900L) 0L else 4000L // Free delivery >= ₹499
        val codFee = if (paymentMethod == "COD") 5000L else 0L // ₹50 COD fee per prompt

        var discount = 0L
        val coupon = _appliedCoupon.value
        if (coupon != null && subtotal >= coupon.minOrderPaise) {
            discount = if (coupon.discountPercent > 0) {
                ((subtotal * coupon.discountPercent) / 100).coerceAtMost(coupon.maxDiscountPaise)
            } else {
                coupon.fixedDiscountPaise.coerceAtMost(subtotal)
            }
        }

        val taxableAmount = (subtotal - discount).coerceAtLeast(0L)
        val taxPaise = (taxableAmount * 18) / 118 // 18% inclusive GST
        val finalTotal = taxableAmount + deliveryCharge + codFee

        val orderNum = "TKV-2026-${(10000..99999).random()}"
        val awb = "SR-EXP-${(10000000..99999999).random()}"
        val now = System.currentTimeMillis()
        val dateStr = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.ENGLISH).format(Date(now))

        val itemSnapshots = items.map {
            OrderItemSnapshot(
                productId = if (it.isCombo) it.comboProduct!!.id else it.product!!.id,
                title = it.itemTitle,
                sku = if (it.isCombo) it.comboProduct!!.sku else it.product!!.sku,
                quantity = it.quantity,
                unitPricePaise = it.unitPricePaise,
                totalPaise = it.totalPricePaise,
                isCombo = it.isCombo
            )
        }

        val newOrder = Order(
            id = UUID.randomUUID().toString(),
            orderNumber = orderNum,
            dateEpoch = now,
            dateString = dateStr,
            customerName = address.fullName,
            customerEmail = _currentUser.value.email,
            customerPhone = phone.formatted,
            deliveryAddress = address,
            items = itemSnapshots,
            subtotalPaise = subtotal,
            deliveryChargePaise = deliveryCharge,
            codChargePaise = codFee,
            couponDiscountPaise = discount,
            taxPaise = taxPaise,
            totalPaise = finalTotal,
            paymentMethod = paymentMethod,
            paymentStatus = if (paymentMethod == "COD") "PENDING" else "PAID",
            orderStatus = "CONFIRMED",
            courierPartner = "Delhivery Shiprocket Surface",
            shiprocketAwb = awb,
            trackingTimeline = listOf(
                TrackingStep("Order Confirmed", "Your order has been verified and confirmed.", dateStr, isCompleted = true, isCurrent = true),
                TrackingStep("Processing at Warehouse", "Items being picked from TK VEL Central Warehouse, Guindy", "Expected Today", isCompleted = false, isCurrent = false),
                TrackingStep("Handed to Courier", "AWB $awb assigned to Delhivery Express", "Expected Tomorrow", isCompleted = false, isCurrent = false),
                TrackingStep("Out for Delivery", "Shipment will be out for delivery to ${address.city}", "Estimated 2 days", isCompleted = false, isCurrent = false),
                TrackingStep("Delivered", "Package delivered to recipient", "Pending", isCompleted = false, isCurrent = false)
            )
        )

        _orders.value = listOf(newOrder) + _orders.value
        clearCart()
        return newOrder
    }

    // Support Tickets
    fun createSupportTicket(subject: String, category: String, messageText: String): SupportTicket {
        val ticketId = "TKT-TKV-${(1000..9999).random()}"
        val now = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.ENGLISH).format(Date())
        val ticket = SupportTicket(
            id = ticketId,
            customerName = _currentUser.value.fullName,
            email = _currentUser.value.email,
            phone = _currentUser.value.primaryPhone.formatted,
            subject = subject,
            category = category,
            createdAt = now,
            messages = listOf(
                TicketMessage("CUSTOMER", messageText, now),
                TicketMessage("SUPPORT", "Thank you for contacting TK VEL ENTERPRISES Support. Our executive has been assigned to ticket $ticketId and will assist you shortly via WhatsApp or Phone.", "Just now")
            )
        )
        _supportTickets.value = listOf(ticket) + _supportTickets.value
        return ticket
    }

    // AI Assistant Queries
    fun sendAiMessage(userText: String) {
        val now = SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(Date())
        val userMsg = AiChatMessage(
            id = UUID.randomUUID().toString(),
            isUser = true,
            text = userText,
            timestamp = now
        )
        val lower = userText.lowercase()

        val matchingProducts = when {
            lower.contains("cook") || lower.contains("kadai") || lower.contains("kitchen") -> {
                _products.value.filter { it.category == "Kitchen & Cookware" }
            }
            lower.contains("budget") || lower.contains("1000") || lower.contains("under") -> {
                _products.value.filter { it.pricePaise <= 100000L }
            }
            lower.contains("combo") || lower.contains("bundle") || lower.contains("offer") -> {
                _products.value.filter { it.isFlashSale || it.isBestSeller }
            }
            lower.contains("watch") || lower.contains("earbud") || lower.contains("electronic") -> {
                _products.value.filter { it.category == "Electronics & Smart Tech" }
            }
            else -> _products.value.take(2)
        }

        val replyText = when {
            lower.contains("track") || lower.contains("order") -> {
                val latest = _orders.value.firstOrNull()
                if (latest != null) {
                    "Your latest order #${latest.orderNumber} is currently ${latest.orderStatus}. Courier Partner: ${latest.courierPartner} (AWB: ${latest.shiprocketAwb}). You can view the live timeline in My Orders!"
                } else {
                    "You don't have any pending orders right now. Feel free to browse our trending deals!"
                }
            }
            lower.contains("return") || lower.contains("refund") -> {
                "TK VEL ENTERPRISES offers a 7-day hassle-free return guarantee! You can initiate a return from your Order Tracking page, and our Shiprocket courier will arrange reverse pickup directly from your address."
            }
            lower.contains("cod") || lower.contains("cash") -> {
                "Yes! Cash on Delivery is available across all serviceable Indian PIN codes with a transparent ₹50 processing fee. UPI payments and UPI QR are 100% free with instant confirmation."
            }
            lower.contains("combo") -> {
                "We have great bundle deals! For example, the 'TK Festive Cooking Mega Combo' saves you 35% compared to purchasing individually. Check our Combo Offers tab!"
            }
            else -> {
                "Here are authentic, top-rated products from TK VEL ENTERPRISES matched to your inquiry with free Shiprocket delivery and verified quality assurance!"
            }
        }

        val aiMsg = AiChatMessage(
            id = UUID.randomUUID().toString(),
            isUser = false,
            text = replyText,
            recommendedProducts = matchingProducts,
            quickActions = listOf("View Cart", "Track My Orders", "Contact WhatsApp Support"),
            timestamp = "Just now"
        )

        _aiMessages.value = _aiMessages.value + listOf(userMsg, aiMsg)
    }

    // Admin updates
    fun updateOrderStatus(orderId: String, newStatus: String) {
        _orders.value = _orders.value.map { order ->
            if (order.id == orderId) {
                val updatedTimeline = order.trackingTimeline.map { step ->
                    if (step.title.contains(newStatus, ignoreCase = true)) {
                        step.copy(isCompleted = true, isCurrent = true)
                    } else step
                }
                order.copy(orderStatus = newStatus, trackingTimeline = updatedTimeline)
            } else order
        }
    }

    fun updateProductStock(productId: String, newStock: Int) {
        _products.value = _products.value.map {
            if (it.id == productId) it.copy(stock = newStock) else it
        }
    }

    fun addProduct(product: Product) {
        _products.value = listOf(product) + _products.value
    }

    // Seed Data
    private fun getSeedProducts(): List<Product> = listOf(
        Product(
            id = "PRD_1",
            name = "TK Royal Heavy Traditional Brass Kadai 2.5L",
            sku = "TKV-KADAI-BRS-25L",
            barcode = "8901234567890",
            brand = "TK VEL Royal Craft",
            category = "Kitchen & Cookware",
            subcategory = "Brass & Traditional Cookware",
            shortDescription = "Pure handmade tin-coated (Kalai) brass kadai for authentic South Indian cooking and Ayurvedic wellness.",
            fullDescription = "Experience the rich tradition of South Indian culinary heritage with TK VEL's Royal Traditional Brass Kadai. Mastercrafted by traditional artisans, this 2.5L heavy-bottom kadai retains heat evenly, enhancing flavors in sambar, gravies, and traditional sweets. Finished with food-grade pure tin lining (Kalai) for safety and longevity.",
            mrpPaise = 299900L,
            pricePaise = 189900L, // ₹1,899 (36% off)
            discountPercent = 36,
            stock = 24,
            rating = 4.8,
            reviewCount = 142,
            weightKg = 2.1,
            dimensionsCm = "26 x 26 x 11 cm",
            colors = listOf("Royal Gold Brass", "Antique Bronze"),
            sizes = listOf("2.0 Litres", "2.5 Litres", "3.5 Litres"),
            images = listOf(
                "https://images.unsplash.com/photo-1584269600464-37b1b58a9fe7?w=600&auto=format&fit=crop&q=80",
                "https://images.unsplash.com/photo-1556911220-e15b29be8c8f?w=600&auto=format&fit=crop&q=80",
                "https://images.unsplash.com/photo-1544816155-12df9643f363?w=600&auto=format&fit=crop&q=80",
                "https://images.unsplash.com/photo-1584269600519-112d071b35e6?w=600&auto=format&fit=crop&q=80",
                "https://images.unsplash.com/photo-1590794056226-79ef3a8147e1?w=600&auto=format&fit=crop&q=80"
            ),
            videoUrl = "https://www.w3schools.com/html/mov_bbb.mp4",
            has360View = true,
            specifications = mapOf(
                "Material" to "85% Copper, 15% Zinc Bell Metal Brass",
                "Lining" to "Pure Food-Grade Tin (Velli Kalai)",
                "Capacity" to "2.5 Litres",
                "Stove Compatibility" to "Gas Stove, Electric Coil, Woodfire",
                "Country of Origin" to "India (Tamil Nadu)"
            ),
            warrantyInfo = "2 Years Manufacturer Replacement Warranty against manufacturing defects.",
            codEligible = true,
            freeDelivery = true,
            isNewArrival = false,
            isBestSeller = true,
            isFlashSale = true,
            isFeatured = true,
            tags = listOf("cookware", "brass", "traditional", "kitchen", "kadai", "ayurvedic"),
            variants = listOf(
                ProductVariant("VAR_1_1", "Royal Gold Brass", "2.0 Litres", "TKV-KADAI-BRS-20L", 159900L, 10),
                ProductVariant("VAR_1_2", "Royal Gold Brass", "2.5 Litres", "TKV-KADAI-BRS-25L", 189900L, 24),
                ProductVariant("VAR_1_3", "Royal Gold Brass", "3.5 Litres", "TKV-KADAI-BRS-35L", 249900L, 8)
            )
        ),
        Product(
            id = "PRD_2",
            name = "TK ProPulse Smartwatch with Bluetooth Calling & AMOLED Display",
            sku = "TKV-TECH-SMP-PRO",
            barcode = "8909876543210",
            brand = "TK VEL Electronics",
            category = "Electronics & Smart Tech",
            subcategory = "Wearables & Smartwatches",
            shortDescription = "1.96-inch HD AMOLED curved screen, Bluetooth phone calling, heart rate & SpO2 monitoring, 7-day battery life.",
            fullDescription = "Stay connected and monitor your health effortlessly with the TK ProPulse Smartwatch. Featuring a vibrant 1.96\" AMOLED curved glass display with Always-On capability, crystal clear Bluetooth calling with AI noise cancellation microphone, 120+ sports tracking modes, and IP68 water resistance.",
            mrpPaise = 499900L,
            pricePaise = 229900L, // ₹2,299 (54% off)
            discountPercent = 54,
            stock = 45,
            rating = 4.7,
            reviewCount = 318,
            weightKg = 0.052,
            dimensionsCm = "4.5 x 3.8 x 1.1 cm",
            colors = listOf("Emerald Forest Green", "Matte Midnight Black", "Champagne Gold"),
            sizes = listOf("Free Size (Silicone + Mesh Strap)"),
            images = listOf(
                "https://images.unsplash.com/photo-1579586337278-3befd40fd17a?w=600&auto=format&fit=crop&q=80",
                "https://images.unsplash.com/photo-1508685096489-7aacd43bd3b1?w=600&auto=format&fit=crop&q=80",
                "https://images.unsplash.com/photo-1523275335684-37898b6baf30?w=600&auto=format&fit=crop&q=80",
                "https://images.unsplash.com/photo-1434493789847-2f02dc6ca35d?w=600&auto=format&fit=crop&q=80",
                "https://images.unsplash.com/photo-1510017803434-a899398421b3?w=600&auto=format&fit=crop&q=80"
            ),
            videoUrl = "https://www.w3schools.com/html/mov_bbb.mp4",
            has360View = true,
            specifications = mapOf(
                "Display" to "1.96\" Super AMOLED (410x502 px, 600 nits)",
                "Battery Life" to "Up to 7 Days (Normal use), 3 Days (BT Calling)",
                "Water Resistance" to "IP68 Dust & Water Resistant",
                "Sensors" to "Heart Rate, SpO2, Sleep Tracker, Accelerometer",
                "Connectivity" to "Bluetooth v5.3 BLE"
            ),
            warrantyInfo = "1 Year Doorstep Replacement Warranty.",
            codEligible = true,
            freeDelivery = true,
            isNewArrival = true,
            isBestSeller = true,
            isFlashSale = true,
            isFeatured = true,
            tags = listOf("smartwatch", "electronics", "calling", "fitness", "bluetooth"),
            variants = listOf(
                ProductVariant("VAR_2_1", "Emerald Forest Green", "Standard", "TKV-SMP-GRN", 229900L, 20),
                ProductVariant("VAR_2_2", "Matte Midnight Black", "Standard", "TKV-SMP-BLK", 229900L, 15),
                ProductVariant("VAR_2_3", "Champagne Gold", "Standard", "TKV-SMP-GLD", 249900L, 10)
            )
        ),
        Product(
            id = "PRD_3",
            name = "TK Vel Pure Cold-Pressed Virgin Gingelly / Sesame Oil 1 Litre",
            sku = "TKV-GROC-GNG-1L",
            barcode = "8904561237891",
            brand = "TK VEL Organics",
            category = "Groceries & Traditional Spices",
            subcategory = "Cold-Pressed Oils & Ghee",
            shortDescription = "Traditional wood-pressed (Marachekku) sesame oil extracted with organic palm jaggery. 100% pure and unrefined.",
            fullDescription = "Produced with care in Tamil Nadu using centuries-old wooden cold-press (Marachekku) techniques, TK VEL Pure Gingelly Oil maintains natural aroma, rich antioxidants, and heart-healthy nutrients. Contains zero chemical solvents, artificial coloring, or preservatives. Ideal for daily cooking, traditional gravies, and Ayurvedic oil pulling.",
            mrpPaise = 55000L,
            pricePaise = 42000L, // ₹420 (23% off)
            discountPercent = 23,
            stock = 85,
            rating = 4.9,
            reviewCount = 205,
            weightKg = 1.05,
            dimensionsCm = "8 x 8 x 25 cm",
            colors = listOf("Golden Amber"),
            sizes = listOf("500 ml", "1 Litre", "5 Litres Tin"),
            images = listOf(
                "https://images.unsplash.com/photo-1474979266404-7eaacbcd87c5?w=600&auto=format&fit=crop&q=80",
                "https://images.unsplash.com/photo-1608686207856-001b95cf60ca?w=600&auto=format&fit=crop&q=80",
                "https://images.unsplash.com/photo-1596040033229-a9821ebd058d?w=600&auto=format&fit=crop&q=80",
                "https://images.unsplash.com/photo-1506368249639-73a05d6f6488?w=600&auto=format&fit=crop&q=80",
                "https://images.unsplash.com/photo-1589301760014-d929f3979dbc?w=600&auto=format&fit=crop&q=80"
            ),
            videoUrl = "",
            has360View = false,
            specifications = mapOf(
                "Extraction Method" to "Wooden Cold Pressed (Marachekku / Chekku)",
                "Ingredients" to "100% Pure First-Grade Sesame Seeds, Organic Palm Jaggery",
                "Shelf Life" to "12 Months from Packaging",
                "FSSAI Certified" to "FSSAI Lic. 12423002000145",
                "Packaging" to "Food-Grade Recyclable Glass Bottle"
            ),
            warrantyInfo = "Quality Guaranteed. Sealed fresh from our Tamil Nadu pressing facility.",
            codEligible = true,
            freeDelivery = true,
            isNewArrival = false,
            isBestSeller = true,
            isFlashSale = false,
            isFeatured = true,
            tags = listOf("oil", "cold-pressed", "sesame", "gingelly", "organic", "grocery"),
            variants = listOf(
                ProductVariant("VAR_3_1", "Golden Amber", "500 ml", "TKV-OIL-500ML", 23000L, 40),
                ProductVariant("VAR_3_2", "Golden Amber", "1 Litre", "TKV-OIL-1L", 42000L, 85),
                ProductVariant("VAR_3_3", "Golden Amber", "5 Litres Tin", "TKV-OIL-5L", 199900L, 15)
            )
        ),
        Product(
            id = "PRD_4",
            name = "TK Velvet Touch 300TC Pure Combed Cotton King Bedding Set",
            sku = "TKV-HOME-BED-KNG",
            barcode = "8907891234567",
            brand = "TK VEL Living",
            category = "Home & Living",
            subcategory = "Bedsheets & Home Linens",
            shortDescription = "Luxury 300 thread count sateen weave, 100% long-staple Indian combed cotton. Includes 1 King sheet & 2 pillow covers.",
            fullDescription = "Elevate your bedroom comfort to 5-star hotel luxury with the TK Velvet Touch Bedding Collection. Woven from 100% fine long-staple Indian cotton with an exquisite sateen finish for cooling breathability, smooth silky drape, and vibrant color-fastness even after 50 washes.",
            mrpPaise = 249900L,
            pricePaise = 139900L, // ₹1,399 (44% off)
            discountPercent = 44,
            stock = 32,
            rating = 4.8,
            reviewCount = 89,
            weightKg = 1.3,
            dimensionsCm = "274 x 274 cm (108 x 108 inches)",
            colors = listOf("Emerald Green & Ivory", "Royal Gold Floral", "Classic Pearl White"),
            sizes = listOf("Queen Bed (90x100 in)", "King Bed (108x108 in)"),
            images = listOf(
                "https://images.unsplash.com/photo-1631049307264-da0ec9d70304?w=600&auto=format&fit=crop&q=80",
                "https://images.unsplash.com/photo-1522771739844-6a9f6d5f14af?w=600&auto=format&fit=crop&q=80",
                "https://images.unsplash.com/photo-1505693416388-ac5ce068fe85?w=600&auto=format&fit=crop&q=80",
                "https://images.unsplash.com/photo-1582533561751-ef6f6ab93a2e?w=600&auto=format&fit=crop&q=80",
                "https://images.unsplash.com/photo-1584100936595-c0654b55a2e2?w=600&auto=format&fit=crop&q=80"
            ),
            videoUrl = "",
            has360View = true,
            specifications = mapOf(
                "Fabric" to "100% Long-Staple Indian Combed Cotton",
                "Thread Count" to "300 TC Satin Weave",
                "Fitted Depth" to "Fits mattresses up to 12 inches depth",
                "Care Instructions" to "Machine Wash Cold, Gentle Cycle, Tumble Dry Low"
            ),
            warrantyInfo = "1 Year Color Fastness & Stitching Guarantee.",
            codEligible = true,
            freeDelivery = true,
            isNewArrival = true,
            isBestSeller = false,
            isFlashSale = false,
            isFeatured = true,
            tags = listOf("bedsheet", "cotton", "home", "luxury", "bedroom"),
            variants = listOf(
                ProductVariant("VAR_4_1", "Emerald Green & Ivory", "King Bed (108x108 in)", "TKV-BED-KNG-GRN", 139900L, 18),
                ProductVariant("VAR_4_2", "Royal Gold Floral", "King Bed (108x108 in)", "TKV-BED-KNG-GLD", 139900L, 14)
            )
        ),
        Product(
            id = "PRD_5",
            name = "TK Pure Salem Organic Sun-Dried Turmeric Powder 500g",
            sku = "TKV-GROC-TUR-500G",
            barcode = "8906549871234",
            brand = "TK VEL Organics",
            category = "Groceries & Traditional Spices",
            subcategory = "Traditional Spices & Masalas",
            shortDescription = "High curcumin (4.5%+) pure Salem farmer-sourced turmeric. Natural aroma, unpolished and chemical-free.",
            fullDescription = "Sourced directly from native farmers in Salem, Tamil Nadu, TK VEL Turmeric is naturally sun-dried and stone-ground to preserve volatile essential oils and high Curcumin content (tested > 4.5%). No artificial colors (Lead Chromate free), anti-caking agents, or fillers.",
            mrpPaise = 29900L,
            pricePaise = 18500L, // ₹185 (38% off)
            discountPercent = 38,
            stock = 110,
            rating = 4.9,
            reviewCount = 94,
            weightKg = 0.52,
            dimensionsCm = "14 x 5 x 22 cm",
            colors = listOf("Deep Golden Yellow"),
            sizes = listOf("250g Pouch", "500g Zip-lock", "1 Kg Economy Pack"),
            images = listOf(
                "https://images.unsplash.com/photo-1615485290382-441e4d049cb5?w=600&auto=format&fit=crop&q=80",
                "https://images.unsplash.com/photo-1596040033229-a9821ebd058d?w=600&auto=format&fit=crop&q=80",
                "https://images.unsplash.com/photo-1506368249639-73a05d6f6488?w=600&auto=format&fit=crop&q=80",
                "https://images.unsplash.com/photo-1474979266404-7eaacbcd87c5?w=600&auto=format&fit=crop&q=80",
                "https://images.unsplash.com/photo-1608686207856-001b95cf60ca?w=600&auto=format&fit=crop&q=80"
            ),
            videoUrl = "",
            has360View = false,
            specifications = mapOf(
                "Curcumin Content" to "Certified > 4.5%",
                "Purity" to "100% Unadulterated, No Added Color",
                "Processing" to "Solar Dried, Traditional Stone Milling",
                "Origin" to "Salem, Tamil Nadu, India"
            ),
            warrantyInfo = "100% Purity Money-Back Guarantee.",
            codEligible = true,
            freeDelivery = false,
            isNewArrival = false,
            isBestSeller = true,
            isFlashSale = false,
            isFeatured = false,
            tags = listOf("turmeric", "spices", "curcumin", "organic", "grocery")
        ),
        Product(
            id = "PRD_6",
            name = "TK SoundMaster Active Noise Cancelling Wireless Earbuds",
            sku = "TKV-TECH-EAR-ANC",
            barcode = "8903216549870",
            brand = "TK VEL Electronics",
            category = "Electronics & Smart Tech",
            subcategory = "Audio & Accessories",
            shortDescription = "Hybrid 35dB Active Noise Cancellation, 13mm Titanium drivers, 4-mic ENC calling, 40 hours playtime.",
            fullDescription = "Immerse yourself in concert-like audio with TK SoundMaster ANC earbuds. Features dual hybrid active noise cancellation up to 35dB, high-fidelity 13mm titanium diaphragm drivers with punchy deep bass, low-latency gaming mode (45ms), and ultra-fast Type-C charging.",
            mrpPaise = 399900L,
            pricePaise = 179900L, // ₹1,799 (55% off)
            discountPercent = 55,
            stock = 50,
            rating = 4.8,
            reviewCount = 186,
            weightKg = 0.045,
            dimensionsCm = "6 x 5 x 2.8 cm",
            colors = listOf("Emerald Green & Gold", "Matte Black", "Frost White"),
            sizes = listOf("Includes S/M/L Silicone Tips"),
            images = listOf(
                "https://images.unsplash.com/photo-1590658268037-6bf12165a8df?w=600&auto=format&fit=crop&q=80",
                "https://images.unsplash.com/photo-1572536147248-ac59a8abfa4b?w=600&auto=format&fit=crop&q=80",
                "https://images.unsplash.com/photo-1546435770-a3e426bf472b?w=600&auto=format&fit=crop&q=80",
                "https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=600&auto=format&fit=crop&q=80",
                "https://images.unsplash.com/photo-1484704849700-f032a568e944?w=600&auto=format&fit=crop&q=80"
            ),
            videoUrl = "https://www.w3schools.com/html/mov_bbb.mp4",
            has360View = true,
            specifications = mapOf(
                "ANC Depth" to "-35dB Hybrid Active Noise Cancellation",
                "Driver" to "13mm Dynamic Titanium Diaphragm",
                "Total Playback" to "40 Hours with charging case",
                "Fast Charging" to "10 min charge = 3 hours playback",
                "Water Resistance" to "IPX5 Splash & Sweat Resistant"
            ),
            warrantyInfo = "1 Year Brand Replacement Guarantee.",
            codEligible = true,
            freeDelivery = true,
            isNewArrival = true,
            isBestSeller = true,
            isFlashSale = true,
            isFeatured = true,
            tags = listOf("earbuds", "audio", "anc", "wireless", "electronics")
        )
    )

    private fun getSeedCombos(): List<ComboProduct> = listOf(
        ComboProduct(
            id = "CMB_1",
            title = "TK Festive Cooking Mega Combo (Brass Kadai + Sesame Oil 1L + Turmeric 500g)",
            sku = "TKV-CMB-FEST-COOK",
            description = "Complete authentic kitchen starter pack: TK Royal Traditional Brass Kadai 2.5L + Pure Wood-Pressed Sesame Oil 1L + Salem Organic Turmeric 500g. Save an extra ₹400 in this bundle!",
            componentProductIds = listOf(
                "PRD_1" to 1,
                "PRD_3" to 1,
                "PRD_5" to 1
            ),
            mrpPaise = 384800L,
            pricePaise = 219900L, // ₹2,199 (Save 43% vs MRP)
            discountPercent = 43,
            imageUrl = "https://images.unsplash.com/photo-1584269600464-37b1b58a9fe7?w=600&auto=format&fit=crop&q=80",
            freeDelivery = true,
            codEligible = true,
            stock = 18
        ),
        ComboProduct(
            id = "CMB_2",
            title = "TK Smart Tech Power Duo (ProPulse Smartwatch + SoundMaster ANC Earbuds)",
            sku = "TKV-CMB-TECH-DUO",
            description = "Ultimate wearable bundle: TK ProPulse Calling AMOLED Smartwatch + TK SoundMaster 35dB ANC Wireless Earbuds with matching Emerald Green finish.",
            componentProductIds = listOf(
                "PRD_2" to 1,
                "PRD_6" to 1
            ),
            mrpPaise = 899800L,
            pricePaise = 359900L, // ₹3,599 (60% off)
            discountPercent = 60,
            imageUrl = "https://images.unsplash.com/photo-1579586337278-3befd40fd17a?w=600&auto=format&fit=crop&q=80",
            freeDelivery = true,
            codEligible = true,
            stock = 25
        )
    )

    private fun getSeedOrders(): List<Order> = listOf(
        Order(
            id = "ORD_1",
            orderNumber = "TKV-2026-89412",
            dateEpoch = System.currentTimeMillis() - 86400000L * 2,
            dateString = "28 Oct 2026, 04:30 PM",
            customerName = "T. Ashwin",
            customerEmail = "ashwin9566235195@gmail.com",
            customerPhone = "+91 9566235195",
            deliveryAddress = _currentUser.value.savedAddresses[0],
            items = listOf(
                OrderItemSnapshot(
                    productId = "PRD_1",
                    title = "TK Royal Heavy Traditional Brass Kadai 2.5L",
                    sku = "TKV-KADAI-BRS-25L",
                    quantity = 1,
                    unitPricePaise = 189900L,
                    totalPaise = 189900L
                )
            ),
            subtotalPaise = 189900L,
            deliveryChargePaise = 0L,
            codChargePaise = 0L,
            couponDiscountPaise = 18990L,
            taxPaise = 26054L,
            totalPaise = 170910L, // ₹1,709.10
            paymentMethod = "UPI",
            paymentStatus = "PAID",
            orderStatus = "SHIPPED",
            courierPartner = "Delhivery Shiprocket Express",
            shiprocketAwb = "SR-DEL-89412091",
            trackingTimeline = listOf(
                TrackingStep("Order Confirmed", "Payment verified via UPI ID ashwin@okaxis", "28 Oct, 04:32 PM", isCompleted = true, isCurrent = false),
                TrackingStep("Packed at Warehouse", "Handcrafted quality checked and bubble-packed in Guindy Facility", "29 Oct, 10:15 AM", isCompleted = true, isCurrent = false),
                TrackingStep("In Transit with Courier", "Picked up by Delhivery, in transit to Anna Salai Hub", "29 Oct, 03:40 PM", isCompleted = true, isCurrent = true),
                TrackingStep("Out for Delivery", "Assigned to delivery agent", "Expected Tomorrow", isCompleted = false, isCurrent = false),
                TrackingStep("Delivered", "Delivered to T. Nagar address", "Estimated 31 Oct", isCompleted = false, isCurrent = false)
            )
        )
    )

    private fun getSeedTickets(): List<SupportTicket> = listOf(
        SupportTicket(
            id = "TKT-TKV-4091",
            customerName = "T. Ashwin",
            email = "ashwin9566235195@gmail.com",
            phone = "+91 9566235195",
            subject = "Delivery PIN code check for rural Tamil Nadu",
            category = "DELIVERY",
            priority = "MEDIUM",
            status = "RESOLVED",
            createdAt = "25 Oct 2026, 11:20 AM",
            messages = listOf(
                TicketMessage("CUSTOMER", "Hi TK VEL team, do you deliver Brass Cookware to PIN 624001 Dindigul?", "25 Oct, 11:20 AM"),
                TicketMessage("SUPPORT", "Hello Ashwin, Yes! All products are serviceable in Dindigul via Delhivery Surface with 2-day delivery and COD availability.", "25 Oct, 11:35 AM")
            )
        )
    )
}
