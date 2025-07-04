package edu.ucne.skyplanerent.presentation.navigation

import androidx.compose.ui.graphics.vector.ImageVector

data class BottomNavItem(
    val title: String,
    val icon: ImageVector,
    val route: Screen
)
