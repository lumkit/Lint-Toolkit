package io.lumkit.lint.toolkit.desktop.ui.screen.settings

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.ScrollbarAdapter
import io.github.lumkit.desktop.lifecycle.ViewModel

class SettingsViewModel: ViewModel() {

    val scrollState = ScrollState(0)
    val verticalScrollAdapter = ScrollbarAdapter(scrollState)

}