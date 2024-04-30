package io.lumkit.lint.toolkit.desktop.ui.screen.main

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.screen.Screen

abstract class FeatureScreen(
    val label: String,
    val nav: String,
    val tip: String? = null,
    val icon: @Composable (() -> Unit)? = null,
): Screen