package io.lumkit.lint.toolkit.desktop.ui.screen.splash

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import io.github.lumkit.desktop.data.DarkThemeMode
import io.github.lumkit.desktop.ui.components.LintCard
import io.github.lumkit.desktop.ui.theme.LocalThemeStore
import io.lumkit.lint.toolkit.desktop.ui.components.AnimatedLogo
import linttoolkit.app.generated.resources.Res
import linttoolkit.app.generated.resources.text_theme_dark
import linttoolkit.app.generated.resources.text_theme_light
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalResourceApi::class)
@Composable
fun UiThemeScreen() {
    val themeStore = LocalThemeStore.current

    Row(
        modifier = Modifier.fillMaxSize(),
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
                .weight(1f)
                .background(
                    color = if(themeStore.darkTheme == DarkThemeMode.LIGHT) {
                        MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.35f)
                    } else {
                        Color.Transparent
                    }
                )
                .clickable {
                    themeStore.darkTheme = DarkThemeMode.LIGHT
                }.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                RadioButton(selected = themeStore.darkTheme == DarkThemeMode.LIGHT, onClick = null)
                Text(stringResource(Res.string.text_theme_light))
            }
            MaterialTheme(
                colorScheme = themeStore.colorSchemes.light
            ) {
                LintCard(
                    modifier = Modifier.fillMaxSize(),
                    shape = RoundedCornerShape(12.dp),
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        AnimatedLogo(
                            modifier = Modifier.size(200.dp),
                            durationMillis = 3500,
                            isStart = themeStore.darkTheme == DarkThemeMode.LIGHT
                        )
                    }
                }
            }
        }

        Column(
            modifier = Modifier.fillMaxSize()
                .weight(1f)
                .background(
                    color = if(themeStore.darkTheme == DarkThemeMode.DARK) {
                        MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.35f)
                    } else {
                        Color.Transparent
                    }
                )
                .clickable {
                    themeStore.darkTheme = DarkThemeMode.DARK
                }.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                RadioButton(selected = themeStore.darkTheme == DarkThemeMode.DARK, onClick = null)
                Text(stringResource(Res.string.text_theme_dark))
            }
            MaterialTheme(
                colorScheme = themeStore.colorSchemes.dark
            ) {
                LintCard(
                    modifier = Modifier.fillMaxSize(),
                    shape = RoundedCornerShape(12.dp),
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        AnimatedLogo(
                            modifier = Modifier.size(200.dp),
                            durationMillis = 3500,
                            isStart = themeStore.darkTheme == DarkThemeMode.DARK
                        )
                    }
                }
            }
        }
    }
}