package io.lumkit.lint.toolkit.desktop.model

import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.StringResource

data class LintRuntimeComponent @OptIn(ExperimentalResourceApi::class) constructor(
    val name: StringResource,
    val license: StringResource,
    val selected: Boolean,
)
