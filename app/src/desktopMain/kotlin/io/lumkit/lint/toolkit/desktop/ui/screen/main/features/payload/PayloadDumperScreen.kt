package io.lumkit.lint.toolkit.desktop.ui.screen.main.features.payload

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.TableView
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import io.lumkit.lint.toolkit.desktop.ui.screen.main.FeatureScreen
import linttoolkit.app.generated.resources.Res
import linttoolkit.app.generated.resources.text_payload_dumper
import linttoolkit.app.generated.resources.text_recommend_payload_dumper
import org.jetbrains.compose.resources.ExperimentalResourceApi

@OptIn(ExperimentalResourceApi::class)
class PayloadDumperScreen(
    val name: String,
) : FeatureScreen(
    recommend = true,
    recommendText = Res.string.text_recommend_payload_dumper,
    label = name,
    path = arrayOf(
        Res.string.text_payload_dumper
    ),
    icon = {
        Icon(imageVector = Icons.Default.TableView, contentDescription = null)
    }
) {
    @Composable
    override fun Content() {

    }
}