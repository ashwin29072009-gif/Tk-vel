package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.HeadsetMic
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.Screen
import com.example.ui.TkVelViewModel
import com.example.ui.components.LanguageSelectorModal
import com.example.ui.components.TkVelBottomNav
import com.example.ui.components.TkVelHeader
import com.example.ui.screens.*
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.TkGold
import com.example.ui.theme.TkGreenDark
import com.example.ui.theme.TkGreenPrimary

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                TkVelApp()
            }
        }
    }
}

@Composable
fun TkVelApp(viewModel: TkVelViewModel = viewModel()) {
    val currentScreen by viewModel.screen.collectAsStateWithLifecycle()
    val products by viewModel.products.collectAsStateWithLifecycle()
    val combos by viewModel.combos.collectAsStateWithLifecycle()
    val cartItems by viewModel.cartItems.collectAsStateWithLifecycle()
    val wishlistIds by viewModel.wishlistIds.collectAsStateWithLifecycle()
    val orders by viewModel.orders.collectAsStateWithLifecycle()
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val currentLanguage by viewModel.currentLanguage.collectAsStateWithLifecycle()
    val appliedCoupon by viewModel.appliedCoupon.collectAsStateWithLifecycle()
    val selectedAddress by viewModel.selectedAddress.collectAsStateWithLifecycle()
    val selectedPhone by viewModel.selectedPhone.collectAsStateWithLifecycle()
    val selectedProduct by viewModel.selectedProduct.collectAsStateWithLifecycle()
    val selectedOrder by viewModel.selectedOrder.collectAsStateWithLifecycle()
    val pinServiceability by viewModel.pinServiceability.collectAsStateWithLifecycle()
    val supportTickets by viewModel.supportTickets.collectAsStateWithLifecycle()
    val aiMessages by viewModel.aiMessages.collectAsStateWithLifecycle()
    val featureFlags by viewModel.featureFlags.collectAsStateWithLifecycle()

    var searchQuery by remember { mutableStateOf("") }
    var showLanguageModal by remember { mutableStateOf(false) }
    var showLocationModal by remember { mutableStateOf(false) }
    var shopCategoryFilter by remember { mutableStateOf("All") }

    // Intercept hardware and gesture back navigation
    BackHandler(enabled = currentScreen != Screen.STOREFRONT) {
        viewModel.navigateBack()
    }

    val showBottomNav = currentScreen in listOf(
        Screen.STOREFRONT,
        Screen.SHOP,
        Screen.COMBOS,
        Screen.AI_ASSISTANT,
        Screen.MY_ACCOUNT
    )

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            if (currentScreen in listOf(Screen.STOREFRONT, Screen.SHOP, Screen.COMBOS)) {
                TkVelHeader(
                    currentLangCode = currentLanguage,
                    wishlistCount = wishlistIds.size,
                    cartCount = cartItems.sumOf { it.quantity },
                    deliveryCity = selectedAddress?.city ?: "Chennai",
                    deliveryPin = selectedAddress?.pinCode ?: "600017",
                    searchQuery = searchQuery,
                    onSearchChange = {
                        searchQuery = it
                        if (currentScreen != Screen.SHOP) {
                            viewModel.navigateTo(Screen.SHOP)
                        }
                    },
                    onOpenLanguageSelector = { showLanguageModal = true },
                    onOpenWishlist = { viewModel.navigateTo(Screen.SHOP) },
                    onOpenCart = { viewModel.navigateTo(Screen.CART) },
                    onOpenSupport = { viewModel.navigateTo(Screen.CUSTOMER_SUPPORT) },
                    onOpenLocationPicker = { showLocationModal = true }
                )
            }
        },
        bottomBar = {
            if (showBottomNav) {
                TkVelBottomNav(
                    currentScreen = currentScreen,
                    currentLangCode = currentLanguage,
                    onNavigate = { screen -> viewModel.navigateTo(screen) }
                )
            }
        },
        floatingActionButton = {
            if (currentScreen in listOf(Screen.STOREFRONT, Screen.SHOP, Screen.COMBOS)) {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    // Floating Support Button
                    SmallFloatingActionButton(
                        onClick = { viewModel.navigateTo(Screen.CUSTOMER_SUPPORT) },
                        containerColor = TkGold,
                        contentColor = TkGreenDark,
                        modifier = Modifier.testTag("floating_support_fab")
                    ) {
                        Icon(Icons.Default.HeadsetMic, contentDescription = "Customer Support", modifier = Modifier.size(20.dp))
                    }

                    // Floating AI Assistant Button
                    FloatingActionButton(
                        onClick = { viewModel.navigateTo(Screen.AI_ASSISTANT) },
                        containerColor = TkGreenPrimary,
                        contentColor = Color.White,
                        modifier = Modifier.testTag("floating_ai_fab")
                    ) {
                        Row(modifier = Modifier.padding(horizontal = 10.dp), verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = "TK VEL AI")
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("TK VEL AI", fontWeight = androidx.compose.ui.text.font.FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (currentScreen) {
                Screen.STOREFRONT -> {
                    StorefrontScreen(
                        currentLangCode = currentLanguage,
                        products = products,
                        combos = combos,
                        wishlistIds = wishlistIds,
                        onSelectProduct = { prod -> viewModel.selectProduct(prod) },
                        onSelectCombo = { combo ->
                            viewModel.addComboToCart(combo)
                            viewModel.navigateTo(Screen.CART)
                        },
                        onAddToCart = { prod -> viewModel.addToCart(prod) },
                        onAddComboToCart = { combo -> viewModel.addComboToCart(combo) },
                        onToggleWishlist = { id -> viewModel.toggleWishlist(id) },
                        onNavigateToShop = { category ->
                            shopCategoryFilter = category
                            viewModel.navigateTo(Screen.SHOP)
                        },
                        onNavigateToCombos = { viewModel.navigateTo(Screen.COMBOS) }
                    )
                }

                Screen.SHOP -> {
                    ShopCatalogScreen(
                        currentLangCode = currentLanguage,
                        products = products,
                        initialCategory = shopCategoryFilter,
                        searchQuery = searchQuery,
                        wishlistIds = wishlistIds,
                        onSelectProduct = { prod -> viewModel.selectProduct(prod) },
                        onAddToCart = { prod -> viewModel.addToCart(prod) },
                        onToggleWishlist = { id -> viewModel.toggleWishlist(id) },
                        onBack = { viewModel.navigateBack() }
                    )
                }

                Screen.PRODUCT_DETAIL -> {
                    selectedProduct?.let { prod ->
                        ProductDetailScreen(
                            product = prod,
                            currentLangCode = currentLanguage,
                            isWishlisted = wishlistIds.contains(prod.id),
                            pinServiceability = pinServiceability,
                            onCheckPin = { pin -> viewModel.checkPin(pin) },
                            onAddToCart = { p, v -> viewModel.addToCart(p, v) },
                            onBuyNow = { p, v ->
                                viewModel.addToCart(p, v)
                                viewModel.navigateTo(Screen.CART)
                            },
                            onToggleWishlist = { viewModel.toggleWishlist(prod.id) },
                            onBack = { viewModel.navigateBack() }
                        )
                    }
                }

                Screen.COMBOS -> {
                    CombosScreen(
                        combos = combos,
                        onSelectCombo = { combo ->
                            viewModel.addComboToCart(combo)
                            viewModel.navigateTo(Screen.CART)
                        },
                        onAddComboToCart = { combo -> viewModel.addComboToCart(combo) },
                        onBack = { viewModel.navigateBack() }
                    )
                }

                Screen.CART, Screen.CHECKOUT -> {
                    CartAndCheckoutScreen(
                        currentLangCode = currentLanguage,
                        cartItems = cartItems,
                        appliedCoupon = appliedCoupon,
                        savedAddresses = currentUser.savedAddresses,
                        selectedAddress = selectedAddress,
                        savedPhones = listOf(currentUser.primaryPhone) + currentUser.additionalPhones,
                        selectedPhone = selectedPhone,
                        onUpdateQty = { id, qty -> viewModel.updateCartQty(id, qty) },
                        onRemoveItem = { id -> viewModel.removeFromCart(id) },
                        onApplyCoupon = { code -> viewModel.applyCoupon(code) },
                        onRemoveCoupon = { viewModel.removeCoupon() },
                        onSelectAddress = { addr -> viewModel.selectAddress(addr) },
                        onAddAddress = { addr -> viewModel.addAddress(addr) },
                        onSelectPhone = { ph -> viewModel.selectPhone(ph) },
                        onPlaceOrder = { method ->
                            viewModel.checkoutPaymentMethod.value = method
                            viewModel.placeOrder()
                        },
                        onShopNow = { viewModel.navigateTo(Screen.SHOP) },
                        onBack = { viewModel.navigateBack() }
                    )
                }

                Screen.ORDER_SUCCESS, Screen.ORDER_TRACKING -> {
                    selectedOrder?.let { ord ->
                        OrderSuccessAndTrackingScreen(
                            order = ord,
                            onViewInvoice = { o -> viewModel.viewInvoice(o) },
                            onHome = { viewModel.navigateTo(Screen.STOREFRONT) },
                            onBack = { viewModel.navigateBack() }
                        )
                    }
                }

                Screen.INVOICE_VIEWER -> {
                    selectedOrder?.let { ord ->
                        InvoiceScreen(
                            order = ord,
                            onBack = { viewModel.navigateBack() }
                        )
                    }
                }

                Screen.MY_ACCOUNT -> {
                    AccountScreen(
                        user = currentUser,
                        orders = orders,
                        currentLangCode = currentLanguage,
                        onSelectOrder = { ord -> viewModel.selectOrder(ord) },
                        onOpenLanguageSelector = { showLanguageModal = true },
                        onOpenSupport = { viewModel.navigateTo(Screen.CUSTOMER_SUPPORT) },
                        onOpenAdminPortal = { viewModel.navigateTo(Screen.ADMIN_PORTAL) },
                        onNavigate = { scr -> viewModel.navigateTo(scr) },
                        onBack = { viewModel.navigateBack() }
                    )
                }

                Screen.CUSTOMER_SUPPORT -> {
                    CustomerSupportScreen(
                        tickets = supportTickets,
                        onCreateTicket = { subj, cat, msg ->
                            viewModel.createSupportTicket(subj, cat, msg)
                        },
                        onBack = { viewModel.navigateBack() }
                    )
                }

                Screen.AI_ASSISTANT -> {
                    AiShoppingAssistantScreen(
                        messages = aiMessages,
                        onSendMessage = { prompt -> viewModel.sendAiMessage(prompt) },
                        onSelectProduct = { prod -> viewModel.selectProduct(prod) },
                        onBack = { viewModel.navigateBack() }
                    )
                }

                Screen.ADMIN_PORTAL -> {
                    AdminDashboardScreen(
                        orders = orders,
                        products = products,
                        featureFlags = featureFlags,
                        onUpdateOrderStatus = { id, stat -> viewModel.updateOrderStatus(id, stat) },
                        onUpdateStock = { id, stk -> viewModel.updateProductStock(id, stk) },
                        onToggleFeature = { key -> viewModel.toggleFeature(key) },
                        onBack = { viewModel.navigateBack() }
                    )
                }

                else -> {}
            }
        }
    }

    // Modal Bottom Sheets
    if (showLanguageModal) {
        LanguageSelectorModal(
            currentLangCode = currentLanguage,
            onSelectLanguage = { lang ->
                viewModel.setLanguage(lang)
                showLanguageModal = false
            },
            onDismiss = { showLanguageModal = false }
        )
    }

    if (showLocationModal) {
        AddressManagementModal(
            savedAddresses = currentUser.savedAddresses,
            selectedAddress = selectedAddress,
            onSelect = { addr ->
                viewModel.selectAddress(addr)
                showLocationModal = false
            },
            onAddNew = { addr ->
                viewModel.addAddress(addr)
                showLocationModal = false
            },
            onDismiss = { showLocationModal = false }
        )
    }
}
