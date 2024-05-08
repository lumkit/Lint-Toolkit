package io.lumkit.lint.toolkit.desktop.model

import androidx.compose.runtime.Composable

data class SplashActivity(
    val title: String,
    val subtitle: String? = null,
    val screen: @Composable () -> Unit
)