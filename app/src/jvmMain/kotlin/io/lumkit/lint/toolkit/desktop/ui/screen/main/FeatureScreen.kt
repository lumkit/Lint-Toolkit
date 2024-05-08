package io.lumkit.lint.toolkit.desktop.ui.screen.main

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.screen.Screen

abstract class FeatureScreen(
    val tag: String,
    val recommend: Boolean = false,
    val recommendText: String? = null,
    val isNavigation: Boolean = true,
    val label: String,
    val tip: String? = null,
    val navPath: Array<String> = arrayOf(tag),
    val icon: @Composable (() -> Unit)? = null,
): Screen