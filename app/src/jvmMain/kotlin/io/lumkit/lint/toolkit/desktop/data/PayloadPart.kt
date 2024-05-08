package io.lumkit.lint.toolkit.desktop.data

import kotlinx.serialization.Serializable

@Serializable
data class PayloadPart(
    val name: String,
    val size: Long
)
