package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.localization.SupportedLanguages
import com.example.localization.Translations
import com.example.ui.theme.TkGold
import com.example.ui.theme.TkGreenDark
import com.example.ui.theme.TkGreenPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TkVelHeader(
    currentLangCode: String,
    wishlistCount: Int,
    cartCount: Int,
    deliveryPin: String = "600017",
    deliveryCity: String = "Chennai",
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    onOpenLanguageSelector: () -> Unit,
    onOpenWishlist: () -> Unit,
    onOpenCart: () -> Unit,
    onOpenSupport: () -> Unit,
    onOpenLocationPicker: () -> Unit,
    onSearchSubmit: () -> Unit = {}
) {
    val langInfo = remember(currentLangCode) { SupportedLanguages.find(currentLangCode) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(TkGreenDark)
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .testTag("tkvel_main_header")
    ) {
        // Top Row: Logo, Brand Text, Language Chip, Support, Wishlist & Cart
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f, fill = false)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.tkvel_logo),
                    contentDescription = "TK VEL Logo",
                    modifier = Modifier
                        .size(42.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "TK VEL",
                            color = TkGold,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 18.sp,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "ENTERPRISES",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            letterSpacing = 0.5.sp
                        )
                    }
                    Text(
                        text = "Your Trusted Online Shopping Store",
                        color = Color(0xFFC8E6C9),
                        fontSize = 10.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                // Language switcher pill
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color(0xFF144D32),
                    modifier = Modifier
                        .clickable(onClick = onOpenLanguageSelector)
                        .testTag("header_language_btn")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Language,
                            contentDescription = "Change Language",
                            tint = TkGold,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = langInfo.nativeName,
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Spacer(modifier = Modifier.width(6.dp))

                // Support icon
                IconButton(
                    onClick = onOpenSupport,
                    modifier = Modifier
                        .size(36.dp)
                        .testTag("header_support_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.HeadsetMic,
                        contentDescription = "Customer Support",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }

                // Wishlist Icon with badge
                IconButton(
                    onClick = onOpenWishlist,
                    modifier = Modifier
                        .size(36.dp)
                        .testTag("header_wishlist_btn")
                ) {
                    BadgedBox(
                        badge = {
                            if (wishlistCount > 0) {
                                Badge(containerColor = TkGold, contentColor = TkGreenDark) {
                                    Text("$wishlistCount")
                                }
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.FavoriteBorder,
                            contentDescription = "Wishlist",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                // Cart Icon with badge
                IconButton(
                    onClick = onOpenCart,
                    modifier = Modifier
                        .size(36.dp)
                        .testTag("header_cart_btn")
                ) {
                    BadgedBox(
                        badge = {
                            if (cartCount > 0) {
                                Badge(containerColor = TkGold, contentColor = TkGreenDark) {
                                    Text("$cartCount")
                                }
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.ShoppingCart,
                            contentDescription = "Cart",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Location / Delivery bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onOpenLocationPicker)
                .testTag("header_delivery_location"),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.LocationOn,
                contentDescription = "Delivery Location",
                tint = TkGold,
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "${Translations.get("deliver_to", currentLangCode)} $deliveryCity - $deliveryPin",
                color = Color(0xFFE8F5E9),
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "• ${Translations.get("change", currentLangCode)}",
                color = TkGold,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Search Bar
        TextField(
            value = searchQuery,
            onValueChange = onSearchChange,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("header_search_input"),
            placeholder = {
                Text(
                    text = Translations.get("search_hint", currentLangCode),
                    fontSize = 13.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = TkGreenPrimary
                )
            },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { onSearchChange("") }) {
                        Icon(Icons.Default.Clear, contentDescription = "Clear")
                    }
                }
            },
            shape = RoundedCornerShape(24.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            ),
            singleLine = true
        )
    }
}
