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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Order
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderSuccessAndTrackingScreen(
    order: Order,
    onViewInvoice: (Order) -> Unit,
    onHome: () -> Unit,
    onBack: () -> Unit
) {
    var showReturnDialog by remember { mutableStateOf(false) }
    var returnReason by remember { mutableStateOf("Item defective / Damaged in transit") }
    var returnSubmitted by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Order #${order.orderNumber}", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Text(order.dateString, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("tracking_back_btn")) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = onHome, modifier = Modifier.testTag("tracking_home_btn")) {
                        Icon(Icons.Default.Home, contentDescription = "Home")
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
            // Confirmation Header Card
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Surface(
                            color = TkSuccess,
                            shape = CircleShape,
                            modifier = Modifier.size(52.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(32.dp))
                            }
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Order Placed Successfully!",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = TkGreenDark
                        )
                        Text(
                            text = "Thank you for shopping with TK VEL ENTERPRISES.",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(14.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Button(
                                onClick = { onViewInvoice(order) },
                                colors = ButtonDefaults.buttonColors(containerColor = TkGreenPrimary),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.testTag("view_invoice_btn")
                            ) {
                                Icon(Icons.Default.ReceiptLong, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Download Tax Invoice", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // Shiprocket Logistics & Courier Partner Badge
            item {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.LocalShipping, contentDescription = null, tint = TkGreenPrimary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Shiprocket Express Logistics", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("Courier Partner", fontSize = 11.sp, color = Color.Gray)
                                Text(order.courierPartner, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text("Air Waybill (AWB)", fontSize = 11.sp, color = Color.Gray)
                                Text(order.shiprocketAwb, fontWeight = FontWeight.ExtraBold, color = TkGreenPrimary, fontSize = 13.sp)
                            }
                        }
                    }
                }
            }

            // Live Order Timeline
            item {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Live Order & Shipment Tracking", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Spacer(modifier = Modifier.height(14.dp))

                        order.trackingTimeline.forEachIndexed { index, step ->
                            Row(verticalAlignment = Alignment.Top) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Surface(
                                        shape = CircleShape,
                                        color = if (step.isCompleted) TkSuccess else if (step.isCurrent) TkGold else Color.LightGray,
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Icon(
                                                imageVector = if (step.isCompleted) Icons.Default.Check else Icons.Default.Circle,
                                                contentDescription = null,
                                                tint = if (step.isCompleted) Color.White else TkGreenDark,
                                                modifier = Modifier.size(14.dp)
                                            )
                                        }
                                    }
                                    if (index < order.trackingTimeline.size - 1) {
                                        Box(
                                            modifier = Modifier
                                                .width(2.dp)
                                                .height(36.dp)
                                                .background(if (step.isCompleted) TkSuccess else Color.LightGray)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(text = step.title, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    Text(text = step.description, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text(text = step.timestamp, fontSize = 10.sp, color = Color.Gray)
                                    Spacer(modifier = Modifier.height(10.dp))
                                }
                            }
                        }
                    }
                }
            }

            // Ordered Items Summary
            item {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Purchased Items (${order.items.size})", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Spacer(modifier = Modifier.height(10.dp))
                        order.items.forEach { item ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(item.title, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                                    Text("Qty: ${item.quantity} • SKU: ${item.sku}", fontSize = 11.sp, color = Color.Gray)
                                }
                                Text("₹${item.totalPaise / 100}", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            }
                            Divider(color = Color(0xFFF0F0F0))
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Total Paid (${order.paymentMethod})", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text("₹${order.totalPaise / 100}", fontWeight = FontWeight.ExtraBold, color = TkGreenPrimary, fontSize = 15.sp)
                        }
                    }
                }
            }

            // 7-Day Returns & Refund Button
            item {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Returns & Replacement", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Eligible for 7-day hassle-free return or replacement per TK VEL Policy.",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        if (!returnSubmitted) {
                            OutlinedButton(
                                onClick = { showReturnDialog = true },
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = TkError),
                                border = ButtonDefaults.outlinedButtonBorder.copy(brush = androidx.compose.ui.graphics.SolidColor(TkError))
                            ) {
                                Text("Request Return / Exchange", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        } else {
                            Surface(
                                color = Color(0xFFFFF3E0),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "Return request submitted! Our courier partner will schedule reverse pickup within 24 hours.",
                                    color = Color(0xFFE65100),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium,
                                    modifier = Modifier.padding(10.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (showReturnDialog) {
        AlertDialog(
            onDismissRequest = { showReturnDialog = false },
            title = { Text("Request Return / Replacement", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("Select reason for return:", fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    listOf(
                        "Item defective / Damaged in transit",
                        "Received wrong size or variant",
                        "Quality not as expected",
                        "Changed mind"
                    ).forEach { reason ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { returnReason = reason }
                                .padding(vertical = 4.dp)
                        ) {
                            RadioButton(selected = returnReason == reason, onClick = { returnReason = reason })
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(reason, fontSize = 12.sp)
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        returnSubmitted = true
                        showReturnDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = TkGreenPrimary)
                ) {
                    Text("Submit Return")
                }
            },
            dismissButton = {
                TextButton(onClick = { showReturnDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
