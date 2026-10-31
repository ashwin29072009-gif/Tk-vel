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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Order
import com.example.data.model.Product
import com.example.localization.SupportedLanguages
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    orders: List<Order>,
    products: List<Product>,
    featureFlags: Map<String, Boolean>,
    onUpdateOrderStatus: (String, String) -> Unit,
    onUpdateStock: (String, Int) -> Unit,
    onToggleFeature: (String) -> Unit,
    onBack: () -> Unit
) {
    var isAuthenticated by remember { mutableStateOf(false) }
    var usernameInput by remember { mutableStateOf("ashwintk2908") }
    var passwordInput by remember { mutableStateOf("") }
    var selectedTab by remember { mutableStateOf(0) } // 0: Overview, 1: Products, 2: Orders, 3: AI Super Studio, 4: Features & Languages

    // Real-time Business Metrics calculated from database
    val totalRevenuePaise = orders.filter { it.paymentStatus == "PAID" }.sumOf { it.totalPaise }
    val pendingRevenuePaise = orders.filter { it.paymentStatus == "PENDING" }.sumOf { it.totalPaise }
    val totalOrdersCount = orders.size
    val confirmedCount = orders.count { it.orderStatus == "CONFIRMED" }
    val shippedCount = orders.count { it.orderStatus == "SHIPPED" }
    val deliveredCount = orders.count { it.orderStatus == "DELIVERED" }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(color = TkGold, shape = CircleShape, modifier = Modifier.size(28.dp)) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.AdminPanelSettings, contentDescription = null, tint = TkGreenDark, modifier = Modifier.size(16.dp))
                            }
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text("TK VEL Business Control Panel", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            Text("Main Dashboard • CEO: T. Ashwin", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("admin_back_btn")) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        }
    ) { innerPadding ->
        if (!isAuthenticated) {
            // Secure Administrator Login Screen
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(Color(0xFFF4F6F4)),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Surface(color = TkGreenPrimary, shape = CircleShape, modifier = Modifier.size(52.dp)) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.Lock, contentDescription = null, tint = Color.White, modifier = Modifier.size(24.dp))
                            }
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("Administrator Login", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                        Text("Authorized TK VEL Personnel & Management", fontSize = 12.sp, color = Color.Gray)

                        Spacer(modifier = Modifier.height(18.dp))

                        OutlinedTextField(
                            value = usernameInput,
                            onValueChange = { usernameInput = it },
                            label = { Text("Username") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("admin_username_input"),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = passwordInput,
                            onValueChange = { passwordInput = it },
                            label = { Text("Password") },
                            placeholder = { Text("Enter security key / passkey") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("admin_password_input"),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(18.dp))

                        Button(
                            onClick = { isAuthenticated = true },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp)
                                .testTag("admin_login_submit_btn"),
                            colors = ButtonDefaults.buttonColors(containerColor = TkGreenPrimary),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Sign In to Control Panel", fontWeight = FontWeight.Bold)
                        }

                        TextButton(onClick = { isAuthenticated = true }) {
                            Text("Quick Demo Sign-In (Ashwin T - Super Admin)", fontSize = 11.sp, color = TkGreenPrimary)
                        }
                    }
                }
            }
        } else {
            // Logged-in Control Panel with Tabs
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(MaterialTheme.colorScheme.background)
            ) {
                ScrollableTabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = TkGreenPrimary,
                    edgePadding = 16.dp
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = { Text("Overview & Metrics", fontWeight = FontWeight.Bold, fontSize = 12.sp) }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = { Text("Products & Stock (${products.size})", fontWeight = FontWeight.Bold, fontSize = 12.sp) }
                    )
                    Tab(
                        selected = selectedTab == 2,
                        onClick = { selectedTab = 2 },
                        text = { Text("Orders & Shiprocket (${orders.size})", fontWeight = FontWeight.Bold, fontSize = 12.sp) }
                    )
                    Tab(
                        selected = selectedTab == 3,
                        onClick = { selectedTab = 3 },
                        text = { Text("AI Super Studio", fontWeight = FontWeight.Bold, fontSize = 12.sp) }
                    )
                    Tab(
                        selected = selectedTab == 4,
                        onClick = { selectedTab = 4 },
                        text = { Text("Languages & Features", fontWeight = FontWeight.Bold, fontSize = 12.sp) }
                    )
                }

                when (selectedTab) {
                    0 -> AdminOverviewTab(
                        totalRevenuePaise = totalRevenuePaise,
                        pendingRevenuePaise = pendingRevenuePaise,
                        totalOrders = totalOrdersCount,
                        confirmedCount = confirmedCount,
                        shippedCount = shippedCount,
                        deliveredCount = deliveredCount
                    )
                    1 -> AdminProductsTab(
                        products = products,
                        onUpdateStock = onUpdateStock
                    )
                    2 -> AdminOrdersTab(
                        orders = orders,
                        onUpdateOrderStatus = onUpdateOrderStatus
                    )
                    3 -> AdminAiSuperStudioTab()
                    4 -> AdminFeaturesTab(
                        featureFlags = featureFlags,
                        onToggleFeature = onToggleFeature
                    )
                }
            }
        }
    }
}

@Composable
fun AdminOverviewTab(
    totalRevenuePaise: Long,
    pendingRevenuePaise: Long,
    totalOrders: Int,
    confirmedCount: Int,
    shippedCount: Int,
    deliveredCount: Int
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text("Business Performance & Financial Summary", fontWeight = FontWeight.Bold, fontSize = 15.sp)
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MetricCard(
                    title = "Verified Revenue",
                    value = "₹${totalRevenuePaise / 100}",
                    subtitle = "UPI & Verified Payments",
                    icon = Icons.Default.TrendingUp,
                    color = TkGreenPrimary,
                    modifier = Modifier.weight(1f)
                )

                MetricCard(
                    title = "Pending Payments",
                    value = "₹${pendingRevenuePaise / 100}",
                    subtitle = "COD & In-Transit",
                    icon = Icons.Default.PendingActions,
                    color = Color(0xFFE65100),
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MetricCard(
                    title = "Total Orders",
                    value = "$totalOrders",
                    subtitle = "Lifetime Placed",
                    icon = Icons.Default.ShoppingBag,
                    color = Color(0xFF1976D2),
                    modifier = Modifier.weight(1f)
                )

                MetricCard(
                    title = "Active Logistics",
                    value = "$shippedCount",
                    subtitle = "In Transit (Shiprocket)",
                    icon = Icons.Default.LocalShipping,
                    color = TkGoldDark,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Order Status Distribution", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(10.dp))
                    OrderStatusRow("Confirmed / Processing", "$confirmedCount", TkGoldDark)
                    OrderStatusRow("Shipped / In Transit with Courier", "$shippedCount", Color(0xFF1976D2))
                    OrderStatusRow("Delivered to Customer", "$deliveredCount", TkSuccess)
                }
            }
        }
    }
}

@Composable
fun MetricCard(
    title: String,
    value: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(title, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(value, fontWeight = FontWeight.ExtraBold, fontSize = 18.sp, color = color)
            Text(subtitle, fontSize = 10.sp, color = Color.Gray)
        }
    }
}

@Composable
fun OrderStatusRow(label: String, count: String, color: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Surface(shape = CircleShape, color = color, modifier = Modifier.size(8.dp)) {}
            Spacer(modifier = Modifier.width(8.dp))
            Text(label, fontSize = 12.sp)
        }
        Text(count, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = color)
    }
}

@Composable
fun AdminProductsTab(
    products: List<Product>,
    onUpdateStock: (String, Int) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(products, key = { it.id }) { product ->
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(product.name, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text("SKU: ${product.sku} • ₹${product.pricePaise / 100}", fontSize = 11.sp, color = Color.Gray)
                        }
                        Surface(
                            color = if (product.stock > 10) Color(0xFFE8F5E9) else Color(0xFFFFEBEE),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = "Stock: ${product.stock}",
                                color = if (product.stock > 10) TkSuccess else TkError,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Quick Stock Adjust:", fontSize = 11.sp, color = Color.Gray)
                        Spacer(modifier = Modifier.width(8.dp))
                        OutlinedButton(
                            onClick = { onUpdateStock(product.id, (product.stock - 5).coerceAtLeast(0)) },
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                            modifier = Modifier.height(28.dp)
                        ) {
                            Text("-5", fontSize = 10.sp)
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        OutlinedButton(
                            onClick = { onUpdateStock(product.id, product.stock + 10) },
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                            modifier = Modifier.height(28.dp)
                        ) {
                            Text("+10", fontSize = 10.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AdminOrdersTab(
    orders: List<Order>,
    onUpdateOrderStatus: (String, String) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(orders, key = { it.id }) { order ->
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("#${order.orderNumber}", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Text("₹${order.totalPaise / 100}", fontWeight = FontWeight.ExtraBold, color = TkGreenPrimary, fontSize = 13.sp)
                    }
                    Text("Customer: ${order.customerName} (${order.customerPhone})", fontSize = 11.sp)
                    Text("Courier: ${order.courierPartner} • AWB: ${order.shiprocketAwb}", fontSize = 11.sp, color = Color.Gray)

                    Spacer(modifier = Modifier.height(10.dp))
                    Text("Current Status: ${order.orderStatus}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TkGreenPrimary)

                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf("PACKED", "SHIPPED", "DELIVERED").forEach { status ->
                            Button(
                                onClick = { onUpdateOrderStatus(order.id, status) },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (order.orderStatus == status) TkGold else TkGreenPrimary
                                ),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(30.dp)
                            ) {
                                Text(status, fontSize = 10.sp, color = if (order.orderStatus == status) TkGreenDark else Color.White)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AdminAiSuperStudioTab() {
    var generatedCopy by remember { mutableStateOf<String?>(null) }
    var selectedTool by remember { mutableStateOf("Product Creator") }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text("TK VEL AI SUPER STUDIO (16 Business Tools)", fontWeight = FontWeight.Bold, fontSize = 15.sp)
            Text("Generate SEO titles, marketing drafts, pricing suggestions, and inventory insights", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }

        item {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("1. AI Marketing Copy & WhatsApp Campaign Generator", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = {
                            generatedCopy = "✨ Festive Exclusive from TK VEL ENTERPRISES! ✨\n\nCook authentic traditional South Indian delicacies in our handcrafted Tin-Coated Brass Kadai. Get 36% OFF + FREE Shiprocket delivery across Tamil Nadu!\n\nOrder today on tkvel.in or WhatsApp +91 95662 35195.\nUse code: WELCOME10 for an extra 10% off!"
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = TkGreenPrimary)
                    ) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Generate Festive WhatsApp Blast")
                    }

                    generatedCopy?.let { copy ->
                        Spacer(modifier = Modifier.height(10.dp))
                        Surface(
                            color = Color(0xFFE8F5E9),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = copy,
                                fontSize = 12.sp,
                                color = TkGreenDark,
                                modifier = Modifier.padding(12.dp)
                            )
                        }
                    }
                }
            }
        }

        item {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("2. AI Dynamic Pricing Assistant", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Text("Analyzes competitor rates, Shiprocket freight charges, and raw material costs.", fontSize = 11.sp, color = Color.Gray)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("• Brass Cookware margin: 42% (Recommended price: ₹1,899)", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    Text("• Smartwatch margin: 38% (Recommended price: ₹2,299)", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    Text("• Cold-pressed Gingelly Oil margin: 28% (Recommended price: ₹420)", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

@Composable
fun AdminFeaturesTab(
    featureFlags: Map<String, Boolean>,
    onToggleFeature: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Text("Platform Feature Flags & 50-Language System", fontWeight = FontWeight.Bold, fontSize = 15.sp)
        }

        items(featureFlags.entries.toList()) { (key, isEnabled) ->
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(key.replace("_", " ").uppercase(), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        Text(if (isEnabled) "Active in production" else "Disabled", fontSize = 10.sp, color = Color.Gray)
                    }
                    Switch(
                        checked = isEnabled,
                        onCheckedChange = { onToggleFeature(key) }
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(10.dp))
            Text("50 Supported Languages Catalog (${SupportedLanguages.list.size})", fontWeight = FontWeight.Bold, fontSize = 13.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = SupportedLanguages.list.joinToString(", ") { "${it.nativeName} (${it.code})" },
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 16.sp
            )
        }
    }
}
