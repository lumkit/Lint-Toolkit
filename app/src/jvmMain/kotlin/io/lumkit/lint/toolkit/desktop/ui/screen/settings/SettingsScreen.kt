package io.lumkit.lint.toolkit.desktop.ui.screen.settings

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.lumkit.desktop.data.DarkThemeMode
import io.github.lumkit.desktop.lifecycle.viewModel
import io.github.lumkit.desktop.ui.components.LintButton
import io.github.lumkit.desktop.ui.components.LintItem
import io.github.lumkit.desktop.ui.components.LintVerticalScrollBar
import io.github.lumkit.desktop.ui.theme.LocalThemeStore
import io.github.lumkit.lint.desktop.app.generated.resources.Res
import io.github.lumkit.lint.desktop.app.generated.resources.text_settings_theme
import io.github.lumkit.lint.desktop.app.generated.resources.text_theme_dark_dark
import io.github.lumkit.lint.desktop.app.generated.resources.text_theme_dark_flow_system
import io.github.lumkit.lint.desktop.app.generated.resources.text_theme_dark_light
import io.github.lumkit.lint.desktop.app.generated.resources.text_theme_dark_mode
import io.lumkit.lint.toolkit.desktop.ui.screen.main.FeatureScreen
import io.lumkit.lint.toolkit.desktop.ui.screen.main.LocalGridColumns
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.stringResource

class SettingsScreen(
    val name: String,
) : FeatureScreen(
    tag = "SETTINGS",
    label = name,
    icon = {
        Icon(imageVector = Icons.Filled.Settings, contentDescription = null)
    },
    isNavigation = false
) {

    @Composable
    override fun Content() {
        SettingsContent()
    }

}

@Composable
private fun SettingsContent() {
    val settingsViewModel = viewModel<SettingsViewModel>()

    Row(
        modifier = Modifier.fillMaxSize().padding(16.dp),
    ) {
        Column(
            modifier = Modifier.fillMaxSize().weight(1f)
                .verticalScroll(settingsViewModel.scrollState),
            verticalArrangement = Arrangement.spacedBy(28.dp)
        ) {
            SettingsScheme()
        }
        LintVerticalScrollBar(
            settingsViewModel.verticalScrollAdapter,
            modifier = Modifier.width(8.dp)
        )
    }
}

/**
 * 主题设置
 */
@OptIn(ExperimentalResourceApi::class, ExperimentalLayoutApi::class)
@Composable
private fun SettingsScheme() {
    val themeStore = LocalThemeStore.current

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = stringResource(Res.string.text_settings_theme),
            style = MaterialTheme.typography.titleLarge
        )
        Spacer(modifier = Modifier.size(16.dp))
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            maxItemsInEachRow = LocalGridColumns.current.count,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            LintItem(
                modifier = Modifier.fillMaxWidth(),
                icon = {
                    Icon(Icons.Default.DarkMode, contentDescription = null)
                },
                label = {
                    Text(stringResource(Res.string.text_theme_dark_mode))
                },
                trailingIcon = {
                    Column {
                        var menuState by remember { mutableStateOf(false) }
                        LintButton(
                            modifier = Modifier.animateContentSize(),
                            onClick = {
                                menuState = true
                            }
                        ) {
                            Text(
                                text = when (themeStore.darkTheme) {
                                    DarkThemeMode.SYSTEM -> stringResource(Res.string.text_theme_dark_flow_system)
                                    DarkThemeMode.LIGHT -> stringResource(Res.string.text_theme_dark_light)
                                    DarkThemeMode.DARK -> stringResource(Res.string.text_theme_dark_dark)
                                }
                            )
                        }

                        DropdownMenu(
                            expanded = menuState,
                            onDismissRequest = { menuState = false }
                        ) {
                            DarkThemeMode.entries.forEach {
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            text = when (it) {
                                                DarkThemeMode.SYSTEM -> stringResource(Res.string.text_theme_dark_flow_system)
                                                DarkThemeMode.LIGHT -> stringResource(Res.string.text_theme_dark_light)
                                                DarkThemeMode.DARK -> stringResource(Res.string.text_theme_dark_dark)
                                            }
                                        )
                                    },
                                    onClick = {
                                        themeStore.darkTheme = it
                                        menuState = false
                                    }
                                )
                            }
                        }
                    }
                }
            )


        }
    }
}