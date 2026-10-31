package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.*
import com.example.localization.Translations
import com.example.ui.theme.*
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartAndCheckoutScreen(
    currentLangCode: String,
    cartItems: List<CartItem>,
    appliedCoupon: Coupon?,
    savedAddresses: List<Address>,
    selectedAddress: Address?,
    savedPhones: List<PhoneNumber>,
    selectedPhone: PhoneNumber?,
    onUpdateQty: (String, Int) -> Unit,
    onRemoveItem: (String) -> Unit,
    onApplyCoupon: (String) -> Pair<Boolean, String>,
    onRemoveCoupon: () -> Unit,
    onSelectAddress: (Address) -> Unit,
    onAddAddress: (Address) -> Unit,
    onSelectPhone: (PhoneNumber) -> Unit,
    onPlaceOrder: (String) -> Unit,
    onShopNow: () -> Unit,
    onBack: () -> Unit
) {
    var isCheckingOut by remember { mutableStateOf(false) }
    var couponInput by remember { mutableStateOf("") }
    var couponMessage by remember { mutableStateOf<String?>(null) }
    var selectedPaymentMethod by remember { mutableStateOf("UPI") } // "UPI", "UPI_QR", "CARD", "NET_BANKING", "COD"
    var showAddressModal by remember { mutableStateOf(false) }
    var showQrDialog by remember { mutableStateOf(false) }

    // Pricing calculation in integer paise
    val subtotalPaise = cartItems.sumOf { it.totalPricePaise }
    val deliveryFeePaise = if (subtotalPaise >= 49900L || subtotalPaise == 0L) 0L else 4000L
    val codFeePaise = if (selectedPaymentMethod == "COD" && isCheckingOut) 5000L else 0L

    val couponDiscountPaise = if (appliedCoupon != null && subtotalPaise >= appliedCoupon.minOrderPaise) {
        if (appliedCoupon.discountPercent > 0) {
            ((subtotalPaise * appliedCoupon.discountPercent) / 100).coerceAtMost(appliedCoupon.maxDiscountPaise)
        } else {
            appliedCoupon.fixedDiscountPaise.coerceAtMost(subtotalPaise)
        }
    } else 0L

    val taxablePaise = (subtotalPaise - couponDiscountPaise).coerceAtLeast(0L)
    val taxPaise = (taxablePaise * 18) / 118 // 18% inclusive GST
    val finalTotalPaise = taxablePaise + deliveryFeePaise + codFeePaise

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (isCheckingOut) "Secure Checkout" else Translations.get("cart", currentLangCode),
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            if (isCheckingOut) isCheckingOut = false else onBack()
                        },
                        modifier = Modifier.testTag("cart_back_btn")
                    ) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        },
        bottomBar = {
            if (cartItems.isNotEmpty()) {
                Surface(
                    tonalElevation = 8.dp,
                    color = MaterialTheme.colorScheme.surface,
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Total Payable",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "₹${finalTotalPaise / 100}",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.ExtraBold,
                                color = TkGreenPrimary
                            )
                        }

                        if (!isCheckingOut) {
                            Button(
                                onClick = { isCheckingOut = true },
                                colors = ButtonDefaults.buttonColors(containerColor = TkGreenPrimary),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .height(46.dp)
                                    .testTag("proceed_checkout_btn")
                            ) {
                                Text(Translations.get("checkout", currentLangCode), fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.width(6.dp))
                                Icon(Icons.Default.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp))
                            }
                        } else {
                            Button(
                                onClick = {
                                    if (selectedPaymentMethod == "UPI_QR") {
                                        showQrDialog = true
                                    } else {
                                        onPlaceOrder(selectedPaymentMethod)
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = TkGreenPrimary),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .height(46.dp)
                                    .testTag("confirm_place_order_btn")
                            ) {
                                Text(Translations.get("place_order", currentLangCode), fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        if (cartItems.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.RemoveShoppingCart,
                        contentDescription = null,
                        tint = Color.Gray,
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = Translations.get("empty_cart", currentLangCode),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Explore our traditional cookware, electronics & organic groceries",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = onShopNow,
                        colors = ButtonDefaults.buttonColors(containerColor = TkGreenPrimary),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Start Shopping")
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(MaterialTheme.colorScheme.background),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                if (!isCheckingOut) {
                    // Cart Items List
                    items(cartItems, key = { it.id }) { item ->
                        CartItemRow(
                            item = item,
                            onUpdateQty = { newQty -> onUpdateQty(item.id, newQty) },
                            onRemove = { onRemoveItem(item.id) }
                        )
                    }

                    // Coupon Entry Section
                    item {
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text(
                                    text = "Have a Coupon Code?",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    OutlinedTextField(
                                        value = couponInput,
                                        onValueChange = { couponInput = it.uppercase() },
                                        placeholder = { Text("e.g. WELCOME10, TKVEL100", fontSize = 12.sp) },
                                        modifier = Modifier
                                            .weight(1f)
                                            .testTag("coupon_input"),
                                        singleLine = true,
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Button(
                                        onClick = {
                                            if (couponInput.isNotBlank()) {
                                                val (success, msg) = onApplyCoupon(couponInput)
                                                couponMessage = msg
                                            }
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = TkGreenPrimary),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.testTag("apply_coupon_btn")
                                    ) {
                                        Text("Apply")
                                    }
                                }

                                appliedCoupon?.let { coupon ->
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(Color(0xFFE8F5E9), RoundedCornerShape(6.dp))
                                            .padding(horizontal = 8.dp, vertical = 6.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(Icons.Default.Check, contentDescription = null, tint = TkGreenPrimary, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(
                                                text = "${coupon.code} Applied: ${coupon.title}",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = TkGreenDark
                                            )
                                        }
                                        IconButton(onClick = onRemoveCoupon, modifier = Modifier.size(24.dp)) {
                                            Icon(Icons.Default.Close, contentDescription = "Remove Coupon", modifier = Modifier.size(16.dp))
                                        }
                                    }
                                }

                                couponMessage?.let {
                                    if (appliedCoupon == null) {
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text(text = it, color = TkError, fontSize = 11.sp)
                                    }
                                }
                            }
                        }
                    }
                } else {
                    // CHECKOUT VIEW
                    // 1. Delivery Address Step
                    item {
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            modifier = Modifier.testTag("checkout_address_card")
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.LocationOn, contentDescription = null, tint = TkGreenPrimary)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("1. Delivery Address", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    }
                                    TextButton(onClick = { showAddressModal = true }) {
                                        Text("+ Add / Change", color = TkGreenPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    }
                                }

                                selectedAddress?.let { addr ->
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(text = addr.fullName, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                                    Text(text = addr.formattedOneLine, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Surface(
                                        color = TkGreenLight,
                                        shape = RoundedCornerShape(4.dp),
                                        modifier = Modifier.padding(top = 4.dp)
                                    ) {
                                        Text(
                                            text = addr.type,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = TkGreenDark,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // 2. Contact Phone Step
                    item {
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Phone, contentDescription = null, tint = TkGreenPrimary)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("2. Delivery Contact Phone", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                savedPhones.forEach { phone ->
                                    val isSelected = selectedPhone?.id == phone.id
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable { onSelectPhone(phone) }
                                            .padding(vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        RadioButton(
                                            selected = isSelected,
                                            onClick = { onSelectPhone(phone) }
                                        )
                                        Text(text = phone.formatted, fontSize = 13.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal)
                                        if (phone.isPrimary) {
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(text = "(Primary)", fontSize = 11.sp, color = Color.Gray)
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // 3. Payment Method Step
                    item {
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            modifier = Modifier.testTag("checkout_payment_card")
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Payment, contentDescription = null, tint = TkGreenPrimary)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("3. Payment Method", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                }
                                Spacer(modifier = Modifier.height(8.dp))

                                PaymentOptionRow(
                                    title = "UPI Instant (Google Pay, PhonePe, Paytm)",
                                    subtitle = "Zero convenience fee • Instant order confirmation",
                                    icon = Icons.Default.QrCodeScanner,
                                    selected = selectedPaymentMethod == "UPI",
                                    onSelect = { selectedPaymentMethod = "UPI" }
                                )

                                PaymentOptionRow(
                                    title = "UPI Dynamic QR Code",
                                    subtitle = "Scan and pay using any Indian banking / UPI app",
                                    icon = Icons.Default.QrCode,
                                    selected = selectedPaymentMethod == "UPI_QR",
                                    onSelect = { selectedPaymentMethod = "UPI_QR" }
                                )

                                PaymentOptionRow(
                                    title = "Credit / Debit Card",
                                    subtitle = "Visa, Mastercard, RuPay, Maestro accepted",
                                    icon = Icons.Default.CreditCard,
                                    selected = selectedPaymentMethod == "CARD",
                                    onSelect = { selectedPaymentMethod = "CARD" }
                                )

                                PaymentOptionRow(
                                    title = "Cash on Delivery (COD)",
                                    subtitle = "Pay upon doorstep delivery • ₹50 courier handling fee",
                                    icon = Icons.Default.LocalAtm,
                                    selected = selectedPaymentMethod == "COD",
                                    onSelect = { selectedPaymentMethod = "COD" }
                                )
                            }
                        }
                    }
                }

                // Price Breakdown Card
                item {
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        modifier = Modifier.testTag("price_breakdown_card")
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(text = "Price Summary", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Spacer(modifier = Modifier.height(10.dp))

                            PriceSummaryLine("Items Subtotal", "₹${subtotalPaise / 100}")
                            if (couponDiscountPaise > 0) {
                                PriceSummaryLine("Coupon Savings (${appliedCoupon?.code})", "-₹${couponDiscountPaise / 100}", isDiscount = true)
                            }
                            PriceSummaryLine(
                                "Delivery Charge",
                                if (deliveryFeePaise == 0L) "FREE (Shiprocket)" else "₹${deliveryFeePaise / 100}",
                                isFree = deliveryFeePaise == 0L
                            )
                            if (codFeePaise > 0) {
                                PriceSummaryLine("Cash on Delivery (COD) Fee", "₹${codFeePaise / 100}")
                            }
                            PriceSummaryLine("Taxes (18% GST Included)", "₹${taxPaise / 100}", isMuted = true)

                            Divider(modifier = Modifier.padding(vertical = 10.dp), color = MaterialTheme.colorScheme.outlineVariant)

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Total Amount", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                Text(
                                    text = "₹${finalTotalPaise / 100}",
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 18.sp,
                                    color = TkGreenPrimary
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Modal to Add / Change Address (GPS or Manual)
    if (showAddressModal) {
        AddressManagementModal(
            savedAddresses = savedAddresses,
            selectedAddress = selectedAddress,
            onSelect = {
                onSelectAddress(it)
                showAddressModal = false
            },
            onAddNew = {
                onAddAddress(it)
                showAddressModal = false
            },
            onDismiss = { showAddressModal = false }
        )
    }

    // UPI QR Code Dialog
    if (showQrDialog) {
        AlertDialog(
            onDismissRequest = { showQrDialog = false },
            title = {
                Text("TK VEL Official UPI QR", fontWeight = FontWeight.Bold)
            },
            text = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Scan with Google Pay, PhonePe, Paytm, or BHIM",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Box(
                        modifier = Modifier
                            .size(200.dp)
                            .background(Color.White, RoundedCornerShape(12.dp))
                            .border(2.dp, TkGreenPrimary, RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.QrCode2,
                                contentDescription = "QR Code",
                                modifier = Modifier.size(140.dp),
                                tint = TkGreenDark
                            )
                            Text(
                                text = "VPA: tkvelenterprises@okaxis",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Payable: ₹${finalTotalPaise / 100}",
                        fontWeight = FontWeight.ExtraBold,
                        color = TkGreenPrimary,
                        fontSize = 16.sp
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showQrDialog = false
                        onPlaceOrder("UPI_QR")
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = TkGreenPrimary)
                ) {
                    Text("Payment Completed")
                }
            },
            dismissButton = {
                TextButton(onClick = { showQrDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun CartItemRow(
    item: CartItem,
    onUpdateQty: (Int) -> Unit,
    onRemove: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val img = if (item.isCombo) item.comboProduct?.imageUrl else item.product?.images?.firstOrNull()
            AsyncImage(
                model = img,
                contentDescription = item.itemTitle,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(72.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFF1F5F2))
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.itemTitle,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2
                )
                if (item.selectedVariant != null) {
                    Text(
                        text = "${item.selectedVariant.color} • ${item.selectedVariant.size}",
                        fontSize = 11.sp,
                        color = Color.Gray
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "₹${item.unitPricePaise / 100}",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = TkGreenPrimary
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = { onUpdateQty(item.quantity - 1) },
                        modifier = Modifier
                            .size(28.dp)
                            .background(Color(0xFFEEEEEE), CircleShape)
                    ) {
                        Icon(Icons.Default.Remove, contentDescription = "Decrease", modifier = Modifier.size(14.dp))
                    }
                    Text(
                        text = "${item.quantity}",
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 12.dp)
                    )
                    IconButton(
                        onClick = { onUpdateQty(item.quantity + 1) },
                        modifier = Modifier
                            .size(28.dp)
                            .background(Color(0xFFEEEEEE), CircleShape)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Increase", modifier = Modifier.size(14.dp))
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    IconButton(onClick = onRemove) {
                        Icon(Icons.Default.DeleteOutline, contentDescription = "Remove", tint = Color.Red, modifier = Modifier.size(20.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun PaymentOptionRow(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    selected: Boolean,
    onSelect: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onSelect)
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(selected = selected, onClick = onSelect)
        Spacer(modifier = Modifier.width(6.dp))
        Icon(imageVector = icon, contentDescription = null, tint = TkGreenPrimary, modifier = Modifier.size(24.dp))
        Spacer(modifier = Modifier.width(10.dp))
        Column {
            Text(text = title, fontSize = 13.sp, fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium)
            Text(text = subtitle, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
fun PriceSummaryLine(
    label: String,
    value: String,
    isDiscount: Boolean = false,
    isFree: Boolean = false,
    isMuted: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            color = if (isMuted) Color.Gray else MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = value,
            fontSize = 12.sp,
            fontWeight = if (isDiscount || isFree) FontWeight.Bold else FontWeight.Normal,
            color = when {
                isDiscount -> Color(0xFFC62828)
                isFree -> TkSuccess
                isMuted -> Color.Gray
                else -> MaterialTheme.colorScheme.onSurface
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddressManagementModal(
    savedAddresses: List<Address>,
    selectedAddress: Address?,
    onSelect: (Address) -> Unit,
    onAddNew: (Address) -> Unit,
    onDismiss: () -> Unit
) {
    var isAddingNew by remember { mutableStateOf(false) }

    // Form inputs for manual address
    var nameInput by remember { mutableStateOf("T. Ashwin") }
    var houseInput by remember { mutableStateOf("") }
    var buildingInput by remember { mutableStateOf("") }
    var streetInput by remember { mutableStateOf("") }
    var areaInput by remember { mutableStateOf("") }
    var landmarkInput by remember { mutableStateOf("") }
    var cityInput by remember { mutableStateOf("Chennai") }
    var districtInput by remember { mutableStateOf("Chennai") }
    var stateInput by remember { mutableStateOf("Tamil Nadu") }
    var pinInput by remember { mutableStateOf("600017") }
    var typeInput by remember { mutableStateOf("HOME") }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 28.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isAddingNew) "Add Delivery Address" else "Select Delivery Address",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close")
                }
            }

            if (!isAddingNew) {
                // Two Buttons: "Use My Current Location" and "Enter Address Manually"
                Row(modifier = Modifier.fillMaxWidth()) {
                    Button(
                        onClick = {
                            // GPS Assisted auto-fill
                            houseInput = "Flat 4A"
                            buildingInput = "Golden Enclave"
                            streetInput = "1st Main Road"
                            areaInput = "Guindy"
                            landmarkInput = "Opposite Metro Station"
                            cityInput = "Chennai"
                            districtInput = "Chennai"
                            stateInput = "Tamil Nadu"
                            pinInput = "600032"
                            isAddingNew = true
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = TkGold, contentColor = TkGreenDark),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.MyLocation, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Use GPS Location", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    OutlinedButton(
                        onClick = { isAddingNew = true },
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Enter Manually", fontSize = 11.sp)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                LazyColumn(modifier = Modifier.heightIn(max = 300.dp)) {
                    items(savedAddresses) { addr ->
                        val isSelected = selectedAddress?.id == addr.id
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clickable { onSelect(addr) },
                            shape = RoundedCornerShape(10.dp),
                            color = if (isSelected) TkGreenLight else MaterialTheme.colorScheme.surface,
                            border = ButtonDefaults.outlinedButtonBorder.copy(
                                brush = androidx.compose.ui.graphics.SolidColor(if (isSelected) TkGreenPrimary else Color.LightGray)
                            )
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(text = addr.fullName, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    Text(text = addr.type, fontSize = 10.sp, color = TkGreenPrimary, fontWeight = FontWeight.Bold)
                                }
                                Text(text = addr.formattedOneLine, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }
            } else {
                // Manual Address Form (All Indian Fields)
                LazyColumn(
                    modifier = Modifier.heightIn(max = 420.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        OutlinedTextField(
                            value = nameInput,
                            onValueChange = { nameInput = it },
                            label = { Text("Full Name") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                    }
                    item {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = houseInput,
                                onValueChange = { houseInput = it },
                                label = { Text("House / Flat No.") },
                                modifier = Modifier.weight(1f),
                                singleLine = true
                            )
                            OutlinedTextField(
                                value = buildingInput,
                                onValueChange = { buildingInput = it },
                                label = { Text("Building Name") },
                                modifier = Modifier.weight(1f),
                                singleLine = true
                            )
                        }
                    }
                    item {
                        OutlinedTextField(
                            value = streetInput,
                            onValueChange = { streetInput = it },
                            label = { Text("Street / Road") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                    }
                    item {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = areaInput,
                                onValueChange = { areaInput = it },
                                label = { Text("Area / Locality") },
                                modifier = Modifier.weight(1f),
                                singleLine = true
                            )
                            OutlinedTextField(
                                value = landmarkInput,
                                onValueChange = { landmarkInput = it },
                                label = { Text("Landmark") },
                                modifier = Modifier.weight(1f),
                                singleLine = true
                            )
                        }
                    }
                    item {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = cityInput,
                                onValueChange = { cityInput = it },
                                label = { Text("City") },
                                modifier = Modifier.weight(1f),
                                singleLine = true
                            )
                            OutlinedTextField(
                                value = pinInput,
                                onValueChange = { pinInput = it },
                                label = { Text("PIN Code (6 digits)") },
                                modifier = Modifier.weight(1f),
                                singleLine = true
                            )
                        }
                    }
                    item {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = districtInput,
                                onValueChange = { districtInput = it },
                                label = { Text("District") },
                                modifier = Modifier.weight(1f),
                                singleLine = true
                            )
                            OutlinedTextField(
                                value = stateInput,
                                onValueChange = { stateInput = it },
                                label = { Text("State") },
                                modifier = Modifier.weight(1f),
                                singleLine = true
                            )
                        }
                    }
                    item {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf("HOME", "WORK", "OTHER").forEach { tag ->
                                FilterChip(
                                    selected = typeInput == tag,
                                    onClick = { typeInput = tag },
                                    label = { Text(tag) }
                                )
                            }
                        }
                    }
                    item {
                        Spacer(modifier = Modifier.height(10.dp))
                        Button(
                            onClick = {
                                val newAddress = Address(
                                    id = UUID.randomUUID().toString(),
                                    fullName = nameInput,
                                    houseNumber = houseInput,
                                    buildingName = buildingInput,
                                    apartment = "",
                                    street = streetInput,
                                    area = areaInput,
                                    locality = areaInput,
                                    landmark = landmarkInput,
                                    city = cityInput,
                                    district = districtInput,
                                    state = stateInput,
                                    pinCode = pinInput,
                                    type = typeInput,
                                    isDefault = true
                                )
                                onAddNew(newAddress)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = TkGreenPrimary),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Save Address")
                        }
                    }
                }
            }
        }
    }
}
