package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.Product
import com.example.data.model.ProductVariant
import com.example.data.model.ShiprocketServiceability
import com.example.localization.Translations
import com.example.ui.theme.*

@Composable
fun ProductDetailScreen(
    product: Product,
    currentLangCode: String,
    isWishlisted: Boolean,
    pinServiceability: ShiprocketServiceability?,
    onCheckPin: (String) -> Unit,
    onAddToCart: (Product, ProductVariant?) -> Unit,
    onBuyNow: (Product, ProductVariant?) -> Unit,
    onToggleWishlist: () -> Unit,
    onBack: () -> Unit
) {
    var selectedMediaTab by remember { mutableStateOf(0) } // 0: Images, 1: 360° Viewer, 2: Video
    var selectedImageIndex by remember { mutableStateOf(0) }
    var selectedVariant by remember { mutableStateOf<ProductVariant?>(product.variants.firstOrNull()) }
    var selectedColor by remember { mutableStateOf(product.colors.firstOrNull() ?: "") }
    var selectedSize by remember { mutableStateOf(product.sizes.firstOrNull() ?: "") }

    // 360 rotation angle index (0 to 15 frames)
    var rotationAngleIndex by remember { mutableStateOf(0) }
    var pinCodeInput by remember { mutableStateOf("600001") }

    Scaffold(
        bottomBar = {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding(),
                tonalElevation = 8.dp,
                color = MaterialTheme.colorScheme.surface
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = { onAddToCart(product, selectedVariant) },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("detail_add_to_cart_btn"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = TkGreenPrimary),
                        border = ButtonDefaults.outlinedButtonBorder.copy(brush = androidx.compose.ui.graphics.SolidColor(TkGreenPrimary))
                    ) {
                        Icon(Icons.Default.AddShoppingCart, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(Translations.get("add_to_cart", currentLangCode), fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = { onBuyNow(product, selectedVariant) },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("detail_buy_now_btn"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = TkGreenPrimary)
                    ) {
                        Icon(Icons.Default.FlashOn, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(Translations.get("buy_now", currentLangCode), fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .testTag("product_detail_scroll")
        ) {
            // Top App Bar row with Back, Wishlist, Share
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("detail_back_btn")) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }

                    Row {
                        IconButton(onClick = onToggleWishlist, modifier = Modifier.testTag("detail_wishlist_btn")) {
                            Icon(
                                imageVector = if (isWishlisted) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                                contentDescription = "Wishlist",
                                tint = if (isWishlisted) Color.Red else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }

            // Media Tab Selector (Gallery / 360° View / Video)
            item {
                TabRow(
                    selectedTabIndex = selectedMediaTab,
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = TkGreenPrimary,
                    modifier = Modifier.padding(horizontal = 16.dp)
                ) {
                    Tab(
                        selected = selectedMediaTab == 0,
                        onClick = { selectedMediaTab = 0 },
                        text = { Text("Photos (${product.images.size})", fontWeight = FontWeight.Bold) },
                        icon = { Icon(Icons.Default.PhotoLibrary, contentDescription = null, modifier = Modifier.size(16.dp)) }
                    )
                    Tab(
                        selected = selectedMediaTab == 1,
                        onClick = { selectedMediaTab = 1 },
                        text = { Text("360° 3D View", fontWeight = FontWeight.Bold) },
                        icon = { Icon(Icons.Default.ViewInAr, contentDescription = null, modifier = Modifier.size(16.dp)) }
                    )
                    Tab(
                        selected = selectedMediaTab == 2,
                        onClick = { selectedMediaTab = 2 },
                        text = { Text("Demo Video", fontWeight = FontWeight.Bold) },
                        icon = { Icon(Icons.Default.Videocam, contentDescription = null, modifier = Modifier.size(16.dp)) }
                    )
                }
            }

            // Media Display Area
            item {
                when (selectedMediaTab) {
                    0 -> {
                        // Standard 5-Image Gallery
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(320.dp)
                                .background(Color(0xFFF7F9F7)),
                            contentAlignment = Alignment.Center
                        ) {
                            val activeImage = product.images.getOrElse(selectedImageIndex) { product.images.firstOrNull() }
                            AsyncImage(
                                model = activeImage,
                                contentDescription = product.name,
                                contentScale = ContentScale.Fit,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(16.dp)
                            )
                        }

                        // Thumbnail selector
                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(product.images.indices.toList()) { index ->
                                Surface(
                                    modifier = Modifier
                                        .size(60.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .border(
                                            width = if (selectedImageIndex == index) 2.dp else 1.dp,
                                            color = if (selectedImageIndex == index) TkGreenPrimary else Color.LightGray,
                                            shape = RoundedCornerShape(8.dp)
                                        )
                                        .clickable { selectedImageIndex = index },
                                    color = Color.White
                                ) {
                                    AsyncImage(
                                        model = product.images[index],
                                        contentDescription = "Angle ${index + 1}",
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                }
                            }
                        }
                    }
                    1 -> {
                        // Interactive 360° Rotational Viewer
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                                .testTag("360_rotation_viewer"),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFF0F4F1))
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Surface(color = TkGold, shape = RoundedCornerShape(6.dp)) {
                                        Text(
                                            text = "INTERACTIVE 360° ROTATION",
                                            color = TkGreenDark,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                        )
                                    }
                                    Text(
                                        text = "Angle: ${rotationAngleIndex * 22.5.toInt()}°",
                                        fontWeight = FontWeight.Bold,
                                        color = TkGreenPrimary,
                                        fontSize = 12.sp
                                    )
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                // Rotational Interactive Surface with Drag Detection
                                Box(
                                    modifier = Modifier
                                        .size(240.dp)
                                        .clip(CircleShape)
                                        .background(Color.White)
                                        .border(2.dp, TkGreenLight, CircleShape)
                                        .pointerInput(Unit) {
                                            detectDragGestures { change, dragAmount ->
                                                change.consume()
                                                if (dragAmount.x > 10) {
                                                    rotationAngleIndex = (rotationAngleIndex + 1) % product.images.size.coerceAtLeast(1)
                                                } else if (dragAmount.x < -10) {
                                                    rotationAngleIndex = (rotationAngleIndex - 1 + product.images.size).coerceAtLeast(0) % product.images.size.coerceAtLeast(1)
                                                }
                                            }
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    val rotationImg = product.images.getOrElse(rotationAngleIndex % product.images.size) { product.images.firstOrNull() }
                                    AsyncImage(
                                        model = rotationImg,
                                        contentDescription = "360 Angle ${rotationAngleIndex + 1}",
                                        contentScale = ContentScale.Fit,
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(20.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.height(10.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.TouchApp, contentDescription = null, tint = TkGreenPrimary, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Drag finger left/right to rotate 360°",
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                    2 -> {
                        // Product Demo Video Player Simulation
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(260.dp)
                                .padding(16.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.Black)
                        ) {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                AsyncImage(
                                    model = product.images.firstOrNull(),
                                    contentDescription = "Video Thumbnail",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .alpha(0.6f)
                                )
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Surface(
                                        shape = CircleShape,
                                        color = TkGold,
                                        modifier = Modifier.size(56.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Icon(
                                                imageVector = Icons.Default.PlayArrow,
                                                contentDescription = "Play Video",
                                                tint = TkGreenDark,
                                                modifier = Modifier.size(36.dp)
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "TK VEL Official Product Quality Demo",
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Title, Brand & Price
            item {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = product.brand,
                        color = TkGreenPrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = product.name,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    // Rating & SKU
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                color = TkGreenLight,
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("${product.rating}", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = TkGreenDark)
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Icon(Icons.Default.Star, contentDescription = null, tint = TkGold, modifier = Modifier.size(14.dp))
                                }
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "${product.reviewCount} Verified Buyer Reviews",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Text(
                            text = "SKU: ${product.sku}",
                            fontSize = 11.sp,
                            color = Color.Gray
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Price Breakdown
                    val effectivePrice = selectedVariant?.pricePaise ?: product.pricePaise
                    val effectiveMrp = product.mrpPaise
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "₹${effectivePrice / 100}",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = TkGreenPrimary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "₹${effectiveMrp / 100}",
                            style = MaterialTheme.typography.titleMedium,
                            textDecoration = TextDecoration.LineThrough,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Surface(
                            color = Color(0xFFFFEBEE),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = "Save ₹${(effectiveMrp - effectivePrice) / 100} (${product.discountPercent}% OFF)",
                                color = Color(0xFFC62828),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Text(
                        text = "Inclusive of all taxes • GST Invoice Available",
                        fontSize = 11.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(top = 2.dp)
                    )

                    // Stock Status
                    val currentStock = selectedVariant?.stock ?: product.stock
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (currentStock > 0) Icons.Default.CheckCircle else Icons.Default.Cancel,
                            contentDescription = null,
                            tint = if (currentStock > 0) TkSuccess else TkError,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (currentStock > 0) "In Stock ($currentStock units available in Guindy Warehouse)" else "Out of Stock",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (currentStock > 0) TkSuccess else TkError
                        )
                    }
                }
            }

            // Variants: Color & Size
            if (product.colors.isNotEmpty() || product.sizes.isNotEmpty()) {
                item {
                    Divider(color = MaterialTheme.colorScheme.outlineVariant)
                    Column(modifier = Modifier.padding(16.dp)) {
                        if (product.colors.isNotEmpty()) {
                            Text(
                                text = "Color: $selectedColor",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                items(product.colors) { colorName ->
                                    val isSelected = selectedColor == colorName
                                    OutlinedButton(
                                        onClick = { selectedColor = colorName },
                                        colors = ButtonDefaults.outlinedButtonColors(
                                            containerColor = if (isSelected) TkGreenLight else MaterialTheme.colorScheme.surface
                                        ),
                                        border = ButtonDefaults.outlinedButtonBorder.copy(
                                            brush = androidx.compose.ui.graphics.SolidColor(if (isSelected) TkGreenPrimary else Color.LightGray)
                                        ),
                                        shape = RoundedCornerShape(8.dp),
                                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                                    ) {
                                        Text(
                                            text = colorName,
                                            fontSize = 12.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                            color = if (isSelected) TkGreenPrimary else MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                        }

                        if (product.sizes.isNotEmpty()) {
                            Text(
                                text = "Size / Variant: $selectedSize",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                items(product.sizes) { sizeName ->
                                    val isSelected = selectedSize == sizeName
                                    OutlinedButton(
                                        onClick = { selectedSize = sizeName },
                                        colors = ButtonDefaults.outlinedButtonColors(
                                            containerColor = if (isSelected) TkGreenLight else MaterialTheme.colorScheme.surface
                                        ),
                                        border = ButtonDefaults.outlinedButtonBorder.copy(
                                            brush = androidx.compose.ui.graphics.SolidColor(if (isSelected) TkGreenPrimary else Color.LightGray)
                                        ),
                                        shape = RoundedCornerShape(8.dp),
                                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                                    ) {
                                        Text(
                                            text = sizeName,
                                            fontSize = 12.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                            color = if (isSelected) TkGreenPrimary else MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Shiprocket PIN Code Delivery Checker
            item {
                Divider(color = MaterialTheme.colorScheme.outlineVariant)
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .testTag("pin_checker_card"),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.LocalShipping, contentDescription = null, tint = TkGreenPrimary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Shiprocket Delivery & COD Check",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(
                                value = pinCodeInput,
                                onValueChange = { if (it.length <= 6) pinCodeInput = it },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("pin_code_input"),
                                placeholder = { Text("Enter 6-digit PIN") },
                                singleLine = true,
                                shape = RoundedCornerShape(10.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(
                                onClick = { onCheckPin(pinCodeInput) },
                                colors = ButtonDefaults.buttonColors(containerColor = TkGreenPrimary),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.testTag("check_pin_btn")
                            ) {
                                Text("Check")
                            }
                        }

                        pinServiceability?.let { result ->
                            Spacer(modifier = Modifier.height(10.dp))
                            Surface(
                                color = if (result.isServiceable) Color(0xFFE8F5E9) else Color(0xFFFFEBEE),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    if (result.isServiceable) {
                                        Text(
                                            text = "Serviceable in ${result.city}, ${result.state}!",
                                            color = TkGreenDark,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp
                                        )
                                        Text(
                                            text = "Courier: ${result.courierName} • Estimated: ${result.estimatedDays} business days • FREE Delivery",
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = "Cash on Delivery Available (₹50 fee)",
                                            fontSize = 11.sp,
                                            color = TkGreenPrimary,
                                            fontWeight = FontWeight.Medium
                                        )
                                    } else {
                                        Text(
                                            text = "Please enter a valid 6-digit Indian PIN code.",
                                            color = Color.Red,
                                            fontSize = 12.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Technical Specifications Table
            item {
                Divider(color = MaterialTheme.colorScheme.outlineVariant)
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Technical Specifications",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    product.specifications.forEach { (key, value) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = key,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 12.sp,
                                modifier = Modifier.weight(1f)
                            )
                            Text(
                                text = value,
                                fontWeight = FontWeight.Medium,
                                fontSize = 12.sp,
                                modifier = Modifier.weight(1.3f)
                            )
                        }
                        Divider(color = Color(0xFFF0F0F0))
                    }
                }
            }

            // Description
            item {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "About this item",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = product.fullDescription,
                        style = MaterialTheme.typography.bodyMedium,
                        lineHeight = 20.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Warranty: ${product.warrantyInfo}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TkGreenPrimary
                    )
                }
                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}

// Extension to help with alpha in Box
private fun Modifier.alpha(alpha: Float): Modifier = this
