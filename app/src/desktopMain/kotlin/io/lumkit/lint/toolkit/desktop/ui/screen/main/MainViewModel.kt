package io.lumkit.lint.toolkit.desktop.ui.screen.main

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import cafe.adriel.voyager.core.screen.Screen
import io.github.lumkit.desktop.lifecycle.ViewModel
import io.lumkit.lint.toolkit.desktop.ui.screen.main.features.home.HomeScreen
import io.lumkit.lint.toolkit.desktop.ui.screen.main.features.payload.PayloadDumperScreen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import linttoolkit.app.generated.resources.Res
import linttoolkit.app.generated.resources.text_home
import linttoolkit.app.generated.resources.text_payload_dumper
import linttoolkit.app.generated.resources.text_recommend_payload_dumper
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.getString

@OptIn(ExperimentalResourceApi::class)
class MainViewModel : ViewModel() {

    private val _featureScreen = MutableStateFlow<Map<String, FeatureScreen>?>(null)
    val featureScreen = _featureScreen.asStateFlow()
    private val coroutineScope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val _searchText = MutableStateFlow("")
    val searchText = _searchText.asStateFlow()

    init {
        coroutineScope.launch {
            _featureScreen.value = mapOf(
                "HOME" to HomeScreen(
                    getString(Res.string.text_home)
                ),
                "PAYLOAD_DUMPER" to PayloadDumperScreen(
                    getString(Res.string.text_payload_dumper),
                    getString(Res.string.text_recommend_payload_dumper)
                )
            )
        }
    }

    fun setSearchText(text: String) {
        _searchText.value = text
    }
}