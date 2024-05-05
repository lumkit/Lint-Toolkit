package io.lumkit.lint.toolkit.desktop.data

import java.io.Serializable

data class PayloadPart(
    val name: String,
    val size: Long
): Serializable
