package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.Order
import com.example.ui.theme.TkGold
import com.example.ui.theme.TkGreenDark
import com.example.ui.theme.TkGreenPrimary
import com.example.ui.theme.TkGreenLight
import com.example.ui.theme.TkSuccess

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InvoiceScreen(
    order: Order,
    onBack: () -> Unit
) {
    var downloadedSnackbar by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tax Invoice", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("invoice_back_btn")) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { downloadedSnackbar = true }) {
                        Icon(Icons.Default.Download, contentDescription = "Download")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        },
        snackbarHost = {
            if (downloadedSnackbar) {
                Snackbar(
                    modifier = Modifier.padding(16.dp),
                    action = {
                        TextButton(onClick = { downloadedSnackbar = false }) {
                            Text("OK", color = TkGold)
                        }
                    }
                ) {
                    Text("Invoice saved to Downloads: Invoice_${order.orderNumber}.pdf")
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color(0xFFF2F4F2)),
            contentPadding = PaddingValues(16.dp)
        ) {
            item {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        // Header: Logo & Business Details
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top
                        ) {
                            Column {
                                Image(
                                    painter = painterResource(id = R.drawable.tkvel_logo),
                                    contentDescription = "TK VEL Logo",
                                    modifier = Modifier
                                        .size(48.dp)
                                        .clip(RoundedCornerShape(8.dp)),
                                    contentScale = ContentScale.Crop
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "TK VEL ENTERPRISES",
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 16.sp,
                                    color = TkGreenPrimary
                                )
                                Text(
                                    text = "Your Trusted Online Shopping Store\nGuindy Industrial Estate, Chennai - 600032\nTamil Nadu, India\nGSTIN: 33AAACT0924K1Z8\nEmail: ashwintkenterprises@gmail.com",
                                    fontSize = 10.sp,
                                    color = Color.DarkGray,
                                    lineHeight = 14.sp
                                )
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Surface(
                                    color = TkGreenLight,
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text(
                                        text = "TAX INVOICE",
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 12.sp,
                                        color = TkGreenDark,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(text = "Invoice #: INV-${order.orderNumber.takeLast(5)}", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                Text(text = "Order #: ${order.orderNumber}", fontSize = 11.sp)
                                Text(text = "Date: ${order.dateString}", fontSize = 10.sp, color = Color.Gray)
                                Text(text = "AWB: ${order.shiprocketAwb}", fontSize = 10.sp, color = TkGreenPrimary)
                            }
                        }

                        Divider(modifier = Modifier.padding(vertical = 12.dp))

                        // Customer & Shipping Info
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = "Billed & Shipped To:", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = TkGreenDark)
                                Text(text = order.customerName, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                                Text(text = order.deliveryAddress.formattedOneLine, fontSize = 11.sp, color = Color.DarkGray)
                                Text(text = "Phone: ${order.customerPhone}", fontSize = 11.sp, color = Color.DarkGray)
                                Text(text = "Email: ${order.customerEmail}", fontSize = 11.sp, color = Color.DarkGray)
                            }

                            Column(modifier = Modifier.weight(0.9f), horizontalAlignment = Alignment.End) {
                                Text(text = "Payment Summary:", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = TkGreenDark)
                                Text(text = "Method: ${order.paymentMethod}", fontSize = 11.sp)
                                Text(
                                    text = "Status: ${order.paymentStatus}",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (order.paymentStatus == "PAID") TkSuccess else Color(0xFFE65100)
                                )
                                Text(text = "Place of Supply: ${order.deliveryAddress.state} (33)", fontSize = 10.sp, color = Color.Gray)
                            }
                        }

                        Divider(modifier = Modifier.padding(vertical = 12.dp))

                        // Items Table Header
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFFF5F5F5), RoundedCornerShape(4.dp))
                                .padding(horizontal = 6.dp, vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "Item Description", fontWeight = FontWeight.Bold, fontSize = 11.sp, modifier = Modifier.weight(2f))
                            Text(text = "Qty", fontWeight = FontWeight.Bold, fontSize = 11.sp, modifier = Modifier.weight(0.5f), textAlign = TextAlign.Center)
                            Text(text = "Rate (₹)", fontWeight = FontWeight.Bold, fontSize = 11.sp, modifier = Modifier.weight(0.8f), textAlign = TextAlign.End)
                            Text(text = "Total (₹)", fontWeight = FontWeight.Bold, fontSize = 11.sp, modifier = Modifier.weight(0.9f), textAlign = TextAlign.End)
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        // Items List
                        order.items.forEach { item ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 6.dp, vertical = 6.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(2f)) {
                                    Text(text = item.title, fontSize = 11.sp, fontWeight = FontWeight.Medium)
                                    Text(text = "SKU: ${item.sku}", fontSize = 9.sp, color = Color.Gray)
                                }
                                Text(text = "${item.quantity}", fontSize = 11.sp, modifier = Modifier.weight(0.5f), textAlign = TextAlign.Center)
                                Text(text = "₹${item.unitPricePaise / 100}", fontSize = 11.sp, modifier = Modifier.weight(0.8f), textAlign = TextAlign.End)
                                Text(text = "₹${item.totalPaise / 100}", fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(0.9f), textAlign = TextAlign.End)
                            }
                            Divider(color = Color(0xFFEEEEEE))
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Financial totals
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 6.dp),
                            horizontalAlignment = Alignment.End
                        ) {
                            InvoiceSummaryRow("Items Subtotal:", "₹${order.subtotalPaise / 100}")
                            if (order.couponDiscountPaise > 0) {
                                InvoiceSummaryRow("Coupon Discount:", "-₹${order.couponDiscountPaise / 100}", isGreen = true)
                            }
                            InvoiceSummaryRow("Delivery Charges:", if (order.deliveryChargePaise == 0L) "FREE" else "₹${order.deliveryChargePaise / 100}")
                            if (order.codChargePaise > 0) {
                                InvoiceSummaryRow("COD Convenience Fee:", "₹${order.codChargePaise / 100}")
                            }
                            InvoiceSummaryRow("CGST (9%):", "₹${order.taxPaise / 200}")
                            InvoiceSummaryRow("SGST (9%):", "₹${order.taxPaise / 200}")

                            Divider(modifier = Modifier.padding(vertical = 6.dp))

                            Row(
                                modifier = Modifier.width(220.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = "Grand Total:", fontWeight = FontWeight.ExtraBold, fontSize = 14.sp)
                                Text(text = "₹${order.totalPaise / 100}", fontWeight = FontWeight.ExtraBold, fontSize = 15.sp, color = TkGreenPrimary)
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        // Footer declaration
                        Text(
                            text = "Declaration: This is a computer-generated tax invoice issued by TK VEL ENTERPRISES. No signature required. All disputes subject to Chennai jurisdiction.",
                            fontSize = 9.sp,
                            color = Color.Gray,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun InvoiceSummaryRow(label: String, value: String, isGreen: Boolean = false) {
    Row(
        modifier = Modifier
            .width(220.dp)
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontSize = 11.sp, color = Color.DarkGray)
        Text(
            text = value,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            color = if (isGreen) TkSuccess else Color.Black
        )
    }
}
