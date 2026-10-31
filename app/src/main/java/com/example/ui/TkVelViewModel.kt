package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.*
import com.example.data.repository.TkVelRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

enum class Screen {
    STOREFRONT,
    SHOP,
    PRODUCT_DETAIL,
    COMBOS,
    CART,
    CHECKOUT,
    ORDER_SUCCESS,
    ORDER_TRACKING,
    MY_ACCOUNT,
    WISHLIST,
    CUSTOMER_SUPPORT,
    AI_ASSISTANT,
    ADMIN_PORTAL,
    INVOICE_VIEWER
}

class TkVelViewModel(
    private val repository: TkVelRepository = TkVelRepository.get()
) : ViewModel() {

    // Navigation state stack
    private val _screen = MutableStateFlow(Screen.STOREFRONT)
    val screen: StateFlow<Screen> = _screen.asStateFlow()

    private val navStack = mutableListOf(Screen.STOREFRONT)

    fun navigateTo(destination: Screen) {
        if (_screen.value != destination) {
            navStack.add(destination)
            _screen.value = destination
        }
    }

    fun navigateBack(): Boolean {
        if (navStack.size > 1) {
            navStack.removeAt(navStack.size - 1)
            _screen.value = navStack.last()
            return true
        }
        return false
    }

    // Selected product for Product Details
    private val _selectedProduct = MutableStateFlow<Product?>(null)
    val selectedProduct: StateFlow<Product?> = _selectedProduct.asStateFlow()

    fun selectProduct(product: Product) {
        _selectedProduct.value = product
        navigateTo(Screen.PRODUCT_DETAIL)
    }

    // Selected order for tracking & invoice
    private val _selectedOrder = MutableStateFlow<Order?>(null)
    val selectedOrder: StateFlow<Order?> = _selectedOrder.asStateFlow()

    fun selectOrder(order: Order) {
        _selectedOrder.value = order
        navigateTo(Screen.ORDER_TRACKING)
    }

    fun viewInvoice(order: Order) {
        _selectedOrder.value = order
        navigateTo(Screen.INVOICE_VIEWER)
    }

    // Repository flows
    val products = repository.products
    val combos = repository.combos
    val cartItems = repository.cartItems
    val currentUser = repository.currentUser
    val orders = repository.orders
    val wishlistIds = repository.wishlistIds
    val supportTickets = repository.supportTickets
    val aiMessages = repository.aiMessages
    val currentLanguage = repository.currentLanguage
    val appliedCoupon = repository.appliedCoupon
    val selectedAddress = repository.selectedAddress
    val selectedPhone = repository.selectedPhone
    val featureFlags = repository.featureFlags

    // Search & Filter state
    val searchQuery = MutableStateFlow("")
    val selectedCategory = MutableStateFlow("All")
    val selectedSortOption = MutableStateFlow("Popularity") // "Price: Low to High", "Price: High to Low", "Ratings"

    // PIN code check state in Product Detail
    val pinCodeInput = MutableStateFlow("600001")
    val pinServiceability = MutableStateFlow<ShiprocketServiceability?>(null)

    fun checkPin(pin: String) {
        pinCodeInput.value = pin
        pinServiceability.value = repository.checkPinServiceability(pin)
    }

    // Cart actions
    fun addToCart(product: Product, variant: ProductVariant? = null, qty: Int = 1) {
        repository.addToCart(product, variant, qty)
    }

    fun addComboToCart(combo: ComboProduct, qty: Int = 1) {
        repository.addComboToCart(combo, qty)
    }

    fun updateCartQty(cartItemId: String, qty: Int) {
        repository.updateCartQuantity(cartItemId, qty)
    }

    fun removeFromCart(cartItemId: String) {
        repository.removeFromCart(cartItemId)
    }

    fun applyCoupon(code: String): Pair<Boolean, String> {
        return repository.applyCoupon(code)
    }

    fun removeCoupon() {
        repository.removeCoupon()
    }

    // Wishlist
    fun toggleWishlist(productId: String) {
        repository.toggleWishlist(productId)
    }

    // Language
    fun setLanguage(langCode: String) {
        repository.setLanguage(langCode)
    }

    // Addresses & Phones
    fun selectAddress(address: Address) {
        repository.selectAddress(address)
    }

    fun selectPhone(phone: PhoneNumber) {
        repository.selectPhone(phone)
    }

    fun addAddress(address: Address) {
        repository.addAddress(address)
    }

    fun addPhone(number: String) {
        repository.addPhone(number)
    }

    // Checkout
    val checkoutPaymentMethod = MutableStateFlow("UPI") // "UPI", "UPI_QR", "CARD", "NET_BANKING", "COD"

    fun placeOrder(): Order? {
        val addr = selectedAddress.value ?: currentUser.value.savedAddresses.firstOrNull()
        val ph = selectedPhone.value ?: currentUser.value.primaryPhone
        if (addr == null || ph == null || cartItems.value.isEmpty()) return null

        val order = repository.placeOrder(
            paymentMethod = checkoutPaymentMethod.value,
            address = addr,
            phone = ph
        )
        _selectedOrder.value = order
        navigateTo(Screen.ORDER_SUCCESS)
        return order
    }

    // Support
    fun createSupportTicket(subject: String, category: String, messageText: String): SupportTicket {
        return repository.createSupportTicket(subject, category, messageText)
    }

    // AI Assistant
    fun sendAiMessage(prompt: String) {
        viewModelScope.launch {
            repository.sendAiMessage(prompt)
        }
    }

    // Admin Actions
    fun updateOrderStatus(orderId: String, newStatus: String) {
        repository.updateOrderStatus(orderId, newStatus)
    }

    fun updateProductStock(productId: String, newStock: Int) {
        repository.updateProductStock(productId, newStock)
    }

    fun toggleFeature(key: String) {
        repository.toggleFeature(key)
    }
}
