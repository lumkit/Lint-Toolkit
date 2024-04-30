package io.lumkit.lint.toolkit.desktop.ui.screen.main.features.payload

import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import io.github.lumkit.desktop.ui.components.LintOutlinedTextField
import io.lumkit.lint.toolkit.desktop.ui.screen.main.FeatureScreen

class PayloadDumperScreen(
    val name: String,
    val navText: String,
) : FeatureScreen(
    label = name,
    nav = navText,
    icon = {
        Icon(imageVector = Icons.Default.TableView, contentDescription = null)
    }
) {
    @Composable
    override fun Content() {
        Column {
            Text(name)
            val text by PayloadViewModel.text.collectAsState()
            LintOutlinedTextField(
                value = text,
                onValueChange = { PayloadViewModel.setText(it) },
            )

            Text(text)
        }
    }
}