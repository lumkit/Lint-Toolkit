package io.lumkit.lint.toolkit.desktop.ui.screen.main

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.screen.Screen
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.StringResource

@OptIn(ExperimentalResourceApi::class)
abstract class FeatureScreen constructor(
    val recommend: Boolean = false,
    val recommendText: StringResource? = null,
    val label: String,
    val path: Array<StringResource>,
    val tip: String? = null,
    val icon: @Composable (() -> Unit)? = null,
): Screen