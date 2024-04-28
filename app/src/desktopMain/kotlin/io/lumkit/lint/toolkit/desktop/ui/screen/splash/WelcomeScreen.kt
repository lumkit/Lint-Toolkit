package io.lumkit.lint.toolkit.desktop.ui.screen.splash

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import linttoolkit.app.generated.resources.Res
import linttoolkit.app.generated.resources.app_name
import linttoolkit.app.generated.resources.welcome_screen_tips
import org.jetbrains.compose.resources.stringResource

@Composable
fun WelcomeScreen() {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically)
    ) {
        Text(
            text = String.format(stringResource(Res.string.welcome_screen_tips), stringResource(Res.string.app_name)),
            style = MaterialTheme.typography.bodyMedium
        )
    }
}