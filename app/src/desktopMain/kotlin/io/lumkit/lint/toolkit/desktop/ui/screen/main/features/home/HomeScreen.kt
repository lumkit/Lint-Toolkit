package io.lumkit.lint.toolkit.desktop.ui.screen.main.features.home

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import io.lumkit.lint.toolkit.desktop.ui.screen.main.FeatureScreen

class HomeScreen(
    val name: String,
    val navText: String,
) : FeatureScreen(
    label = name,
    nav = navText,
    icon = {
        Icon(imageVector = Icons.Default.Home, contentDescription = null)
    }
) {
    @Composable
    override fun Content() {
        Text("Hello World")
    }
}