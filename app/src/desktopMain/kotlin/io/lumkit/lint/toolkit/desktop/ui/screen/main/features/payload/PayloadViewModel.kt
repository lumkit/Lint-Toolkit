package io.lumkit.lint.toolkit.desktop.ui.screen.main.features.payload

import kotlinx.coroutines.flow.MutableStateFlow

object PayloadViewModel {

    val text = MutableStateFlow("")

    fun setText(value: String) {
        text.value = value
    }

}