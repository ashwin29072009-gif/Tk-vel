package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Order
import com.example.data.model.UserAccount
import com.example.localization.SupportedLanguages
import com.example.ui.Screen
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountScreen(
    user: UserAccount,
    orders: List<Order>,
    currentLangCode: String,
    onSelectOrder: (Order) -> Unit,
    onOpenLanguageSelector: () -> Unit,
    onOpenSupport: () -> Unit,
    onOpenAdminPortal: () -> Unit,
    onNavigate: (Screen) -> Unit,
    onBack: () -> Unit
) {
    val langInfo = remember(currentLangCode) { SupportedLanguages.find(currentLangCode) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Account", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("account_back_btn")) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // User Profile Card
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = TkGreenPrimary,
                            modifier = Modifier.size(56.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = user.fullName.take(2).uppercase(),
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 20.sp
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(user.fullName, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Text(user.email, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(user.primaryPhone.formatted, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }

            // Loyalty Points & Wallet Balance
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Card(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF8E1))
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.MonetizationOn, contentDescription = null, tint = TkGoldDark, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("TK VEL Coins", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("${user.loyaltyPoints} Pts", fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = TkGoldDark)
                        }
                    }

                    Card(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9))
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.AccountBalanceWallet, contentDescription = null, tint = TkGreenPrimary, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Store Credit", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("₹${user.walletBalancePaise / 100}", fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = TkGreenPrimary)
                        }
                    }
                }
            }

            // Quick Actions: Orders, Language, Support
            item {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column {
                        AccountActionItem(
                            icon = Icons.Default.Language,
                            title = "Language Preferences",
                            subtitle = "Current: ${langInfo.nativeName} (${langInfo.englishName})",
                            onClick = onOpenLanguageSelector
                        )
                        Divider()
                        AccountActionItem(
                            icon = Icons.Default.HeadsetMic,
                            title = "Customer Support & Helpline",
                            subtitle = "WhatsApp: +91 95662 35195 • Phone: +91 91765 80731",
                            onClick = onOpenSupport
                        )
                        Divider()
                        AccountActionItem(
                            icon = Icons.Default.FavoriteBorder,
                            title = "My Wishlist",
                            subtitle = "Saved items for later purchase",
                            onClick = { onNavigate(Screen.SHOP) }
                        )
                    }
                }
            }

            // My Orders Section
            item {
                Text("Recent Orders (${orders.size})", fontWeight = FontWeight.Bold, fontSize = 15.sp)
            }

            if (orders.isEmpty()) {
                item {
                    Text("No orders placed yet.", fontSize = 12.sp, color = Color.Gray)
                }
            } else {
                items(orders, key = { it.id }) { order ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelectOrder(order) }
                            .testTag("order_item_${order.orderNumber}"),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("#${order.orderNumber}", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Surface(
                                    color = if (order.orderStatus == "DELIVERED") Color(0xFFE8F5E9) else Color(0xFFE3F2FD),
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text(
                                        text = order.orderStatus,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (order.orderStatus == "DELIVERED") TkSuccess else Color(0xFF1976D2),
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = "Date: ${order.dateString}", fontSize = 11.sp, color = Color.Gray)
                            Text(text = "${order.items.size} item(s) • Total: ₹${order.totalPaise / 100}", fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                            Text(text = "Courier: ${order.courierPartner} (AWB: ${order.shiprocketAwb})", fontSize = 11.sp, color = TkGreenPrimary)

                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                TextButton(
                                    onClick = { onSelectOrder(order) },
                                    contentPadding = PaddingValues(0.dp)
                                ) {
                                    Text("Track Order >", color = TkGreenPrimary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }
            }

            // Portal Switcher: Switch to Business Control Panel & Admin
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(onClick = onOpenAdminPortal)
                        .testTag("open_admin_portal_card"),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = TkGreenDark)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(color = TkGold, shape = CircleShape, modifier = Modifier.size(38.dp)) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(Icons.Default.AdminPanelSettings, contentDescription = null, tint = TkGreenDark)
                                }
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text("TK VEL Business Control Panel", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 13.sp)
                                Text("Admin, Super Admin, Seller & Employee Portals", color = Color(0xFFC8E6C9), fontSize = 11.sp)
                            }
                        }
                        Icon(Icons.Default.ArrowForward, contentDescription = null, tint = TkGold)
                    }
                }
                Spacer(modifier = Modifier.height(30.dp))
            }
        }
    }
}

@Composable
fun AccountActionItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = icon, contentDescription = null, tint = TkGreenPrimary, modifier = Modifier.size(22.dp))
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(title, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                Text(subtitle, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color.Gray)
    }
}
