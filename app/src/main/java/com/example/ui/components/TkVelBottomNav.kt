package com.example.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.localization.Translations
import com.example.ui.Screen
import com.example.ui.theme.TkGold
import com.example.ui.theme.TkGreenDark
import com.example.ui.theme.TkGreenPrimary

@Composable
fun TkVelBottomNav(
    currentScreen: Screen,
    currentLangCode: String,
    onNavigate: (Screen) -> Unit
) {
    NavigationBar(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .testTag("tkvel_bottom_nav"),
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 6.dp
    ) {
        // Home
        NavigationBarItem(
            selected = currentScreen == Screen.STOREFRONT,
            onClick = { onNavigate(Screen.STOREFRONT) },
            icon = {
                Icon(
                    imageVector = if (currentScreen == Screen.STOREFRONT) Icons.Filled.Home else Icons.Outlined.Home,
                    contentDescription = "Home"
                )
            },
            label = { Text("Home") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = TkGreenDark,
                indicatorColor = TkGold,
                selectedTextColor = TkGreenDark
            ),
            modifier = Modifier.testTag("nav_home")
        )

        // Shop / Catalog
        NavigationBarItem(
            selected = currentScreen == Screen.SHOP,
            onClick = { onNavigate(Screen.SHOP) },
            icon = {
                Icon(
                    imageVector = if (currentScreen == Screen.SHOP) Icons.Filled.Storefront else Icons.Outlined.Storefront,
                    contentDescription = "Shop"
                )
            },
            label = { Text("Shop") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = TkGreenDark,
                indicatorColor = TkGold,
                selectedTextColor = TkGreenDark
            ),
            modifier = Modifier.testTag("nav_shop")
        )

        // Combos
        NavigationBarItem(
            selected = currentScreen == Screen.COMBOS,
            onClick = { onNavigate(Screen.COMBOS) },
            icon = {
                Icon(
                    imageVector = if (currentScreen == Screen.COMBOS) Icons.Filled.LocalOffer else Icons.Outlined.LocalOffer,
                    contentDescription = "Combos"
                )
            },
            label = { Text("Combos") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = TkGreenDark,
                indicatorColor = TkGold,
                selectedTextColor = TkGreenDark
            ),
            modifier = Modifier.testTag("nav_combos")
        )

        // AI Assistant
        NavigationBarItem(
            selected = currentScreen == Screen.AI_ASSISTANT,
            onClick = { onNavigate(Screen.AI_ASSISTANT) },
            icon = {
                Icon(
                    imageVector = if (currentScreen == Screen.AI_ASSISTANT) Icons.Filled.AutoAwesome else Icons.Outlined.AutoAwesome,
                    contentDescription = "TK VEL AI"
                )
            },
            label = { Text("TK VEL AI") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = TkGreenDark,
                indicatorColor = TkGold,
                selectedTextColor = TkGreenDark
            ),
            modifier = Modifier.testTag("nav_ai")
        )

        // Account / Admin
        NavigationBarItem(
            selected = currentScreen == Screen.MY_ACCOUNT || currentScreen == Screen.ADMIN_PORTAL,
            onClick = { onNavigate(Screen.MY_ACCOUNT) },
            icon = {
                Icon(
                    imageVector = if (currentScreen == Screen.MY_ACCOUNT || currentScreen == Screen.ADMIN_PORTAL) Icons.Filled.Person else Icons.Outlined.Person,
                    contentDescription = "Account"
                )
            },
            label = { Text("Account") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = TkGreenDark,
                indicatorColor = TkGold,
                selectedTextColor = TkGreenDark
            ),
            modifier = Modifier.testTag("nav_account")
        )
    }
}
