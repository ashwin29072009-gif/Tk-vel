package com.example.data.model

data class ProductVariant(
    val id: String,
    val color: String,
    val size: String,
    val sku: String,
    val pricePaise: Long,
    val stock: Int
)

data class Product(
    val id: String,
    val name: String,
    val sku: String,
    val barcode: String,
    val brand: String,
    val category: String,
    val subcategory: String,
    val shortDescription: String,
    val fullDescription: String,
    val mrpPaise: Long,
    val pricePaise: Long,
    val discountPercent: Int,
    val stock: Int,
    val rating: Double,
    val reviewCount: Int,
    val weightKg: Double,
    val dimensionsCm: String,
    val colors: List<String>,
    val sizes: List<String>,
    val images: List<String>, // 5 angle images
    val videoUrl: String,
    val has360View: Boolean = true,
    val specifications: Map<String, String>,
    val warrantyInfo: String,
    val codEligible: Boolean = true,
    val freeDelivery: Boolean = true,
    val isNewArrival: Boolean = false,
    val isBestSeller: Boolean = false,
    val isFlashSale: Boolean = false,
    val isFeatured: Boolean = false,
    val tags: List<String> = emptyList(),
    val variants: List<ProductVariant> = emptyList()
)

data class ComboProduct(
    val id: String,
    val title: String,
    val sku: String,
    val description: String,
    val componentProductIds: List<Pair<String, Int>>, // ProductId to Quantity
    val mrpPaise: Long,
    val pricePaise: Long,
    val discountPercent: Int,
    val imageUrl: String,
    val freeDelivery: Boolean = true,
    val codEligible: Boolean = true,
    val stock: Int = 15
)

data class CartItem(
    val id: String,
    val product: Product? = null,
    val selectedVariant: ProductVariant? = null,
    val comboProduct: ComboProduct? = null,
    val isCombo: Boolean = false,
    val quantity: Int = 1
) {
    val unitPricePaise: Long
        get() = if (isCombo) comboProduct?.pricePaise ?: 0L else selectedVariant?.pricePaise ?: product?.pricePaise ?: 0L

    val itemTitle: String
        get() = if (isCombo) comboProduct?.title ?: "Combo" else product?.name ?: "Product"

    val totalPricePaise: Long
        get() = unitPricePaise * quantity
}

data class Address(
    val id: String,
    val fullName: String,
    val houseNumber: String,
    val buildingName: String,
    val apartment: String,
    val street: String,
    val area: String,
    val locality: String,
    val landmark: String,
    val city: String,
    val district: String,
    val state: String,
    val pinCode: String,
    val country: String = "India",
    val type: String = "HOME", // "HOME", "WORK", "OTHER"
    val isDefault: Boolean = false,
    val deliveryInstructions: String = ""
) {
    val formattedOneLine: String
        get() = listOfNotNull(
            houseNumber.ifBlank { null },
            buildingName.ifBlank { null },
            street.ifBlank { null },
            area.ifBlank { null },
            landmark.ifBlank { null },
            "$city, $state - $pinCode"
        ).joinToString(", ")
}

data class PhoneNumber(
    val id: String,
    val countryCode: String = "+91",
    val number: String,
    val isPrimary: Boolean = false,
    val isVerified: Boolean = true
) {
    val formatted: String get() = "$countryCode $number"
}

data class UserAccount(
    val id: String,
    val fullName: String,
    val email: String,
    val primaryPhone: PhoneNumber,
    val additionalPhones: List<PhoneNumber> = emptyList(),
    val savedAddresses: List<Address> = emptyList(),
    val role: String = "CUSTOMER", // "CUSTOMER", "ADMIN", "SUPER_ADMIN", "SELLER", "EMPLOYEE"
    val loyaltyPoints: Int = 250,
    val walletBalancePaise: Long = 50000L, // ₹500
    val referralCode: String = "TKVELASHWIN"
)

data class OrderItemSnapshot(
    val productId: String,
    val title: String,
    val sku: String,
    val quantity: Int,
    val unitPricePaise: Long,
    val totalPaise: Long,
    val isCombo: Boolean = false
)

data class TrackingStep(
    val title: String,
    val description: String,
    val timestamp: String,
    val isCompleted: Boolean,
    val isCurrent: Boolean
)

data class Order(
    val id: String,
    val orderNumber: String,
    val dateEpoch: Long,
    val dateString: String,
    val customerName: String,
    val customerEmail: String,
    val customerPhone: String,
    val deliveryAddress: Address,
    val items: List<OrderItemSnapshot>,
    val subtotalPaise: Long,
    val deliveryChargePaise: Long,
    val codChargePaise: Long,
    val couponDiscountPaise: Long,
    val taxPaise: Long,
    val totalPaise: Long,
    val paymentMethod: String, // "UPI", "UPI_QR", "CARD", "NET_BANKING", "COD"
    val paymentStatus: String, // "PAID", "PENDING", "REFUNDED"
    val orderStatus: String,   // "CONFIRMED", "PROCESSING", "PACKED", "SHIPPED", "OUT_FOR_DELIVERY", "DELIVERED", "CANCELLED", "RETURN_REQUESTED", "REFUNDED"
    val courierPartner: String,
    val shiprocketAwb: String,
    val trackingTimeline: List<TrackingStep>
)

data class Review(
    val id: String,
    val productId: String,
    val customerName: String,
    val rating: Int,
    val title: String,
    val comment: String,
    val date: String,
    val verifiedPurchase: Boolean = true,
    val helpfulCount: Int = 12
)

data class Coupon(
    val code: String,
    val title: String,
    val discountPercent: Int = 0,
    val fixedDiscountPaise: Long = 0L,
    val minOrderPaise: Long = 0L,
    val maxDiscountPaise: Long = 100000L,
    val expiryText: String = "Valid till 31 Dec 2026"
)

data class TicketMessage(
    val sender: String, // "CUSTOMER", "SUPPORT"
    val text: String,
    val timestamp: String
)

data class SupportTicket(
    val id: String,
    val customerName: String,
    val email: String,
    val phone: String,
    val subject: String,
    val category: String, // "ORDER", "PAYMENT", "DELIVERY", "RETURN", "GENERAL"
    val priority: String = "HIGH",
    val status: String = "OPEN", // "OPEN", "IN_PROGRESS", "RESOLVED"
    val createdAt: String,
    val messages: List<TicketMessage> = emptyList()
)

data class ShiprocketServiceability(
    val pinCode: String,
    val isServiceable: Boolean,
    val city: String,
    val state: String,
    val courierName: String,
    val estimatedDays: Int,
    val shippingChargePaise: Long,
    val codAvailable: Boolean
)

data class AiChatMessage(
    val id: String,
    val isUser: Boolean,
    val text: String,
    val recommendedProducts: List<Product> = emptyList(),
    val quickActions: List<String> = emptyList(),
    val timestamp: String
)
