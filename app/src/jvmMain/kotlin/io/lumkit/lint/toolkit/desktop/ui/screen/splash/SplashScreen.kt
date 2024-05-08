package io.lumkit.lint.toolkit.desktop.ui.screen.splash

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import io.github.lumkit.desktop.preferences.LocalSharedPreferences
import io.github.lumkit.desktop.ui.LintWindow
import io.github.lumkit.desktop.ui.components.LintButton
import io.github.lumkit.desktop.ui.components.LintOutlinedButton
import io.github.lumkit.desktop.ui.theme.AnimatedLintTheme
import io.github.lumkit.lint.desktop.app.generated.resources.Res
import io.github.lumkit.lint.desktop.app.generated.resources.app_name
import io.github.lumkit.lint.desktop.app.generated.resources.ic_logo
import io.github.lumkit.lint.desktop.app.generated.resources.label_install_components
import io.github.lumkit.lint.desktop.app.generated.resources.label_license_agreement
import io.github.lumkit.lint.desktop.app.generated.resources.label_license_agreement_tips
import io.github.lumkit.lint.desktop.app.generated.resources.label_select_ui_theme
import io.github.lumkit.lint.desktop.app.generated.resources.label_select_ui_theme_tip
import io.github.lumkit.lint.desktop.app.generated.resources.label_splash
import io.github.lumkit.lint.desktop.app.generated.resources.text_cancel
import io.github.lumkit.lint.desktop.app.generated.resources.text_finish
import io.github.lumkit.lint.desktop.app.generated.resources.text_next
import io.github.lumkit.lint.desktop.app.generated.resources.text_previous
import io.github.lumkit.lint.desktop.app.generated.resources.text_welcome
import io.lumkit.lint.toolkit.desktop.core.Const
import io.lumkit.lint.toolkit.desktop.data.LoadState
import io.lumkit.lint.toolkit.desktop.model.SplashActivity
import io.lumkit.lint.toolkit.desktop.ui.components.Logo
import io.lumkit.lint.toolkit.desktop.ui.screen.initialization.InitializationScreen
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import kotlin.system.exitProcess

class SplashScreen : Screen {

    @Composable
    override fun Content() {
        val sharedPreferences = LocalSharedPreferences.current
        val navigator = LocalNavigator.currentOrThrow

        if (sharedPreferences.getString(Const.INITIALIZATION) == "true") {
            navigator.replaceAll(InitializationScreen())
        } else {
            Splash()
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class, ExperimentalResourceApi::class)
@Composable
private fun Splash() {
    val sharedPreferences = LocalSharedPreferences.current
    val navigator = LocalNavigator.currentOrThrow

    LintWindow(
        onCloseRequest = { exitProcess(0) },
        title = String.format("%s %s", stringResource(Res.string.app_name), stringResource(Res.string.label_splash)),
        icon = painterResource(Res.drawable.ic_logo),
        resizable = false
    ) {
        AnimatedLintTheme(
            modifier = Modifier.fillMaxSize(),
        ) {
            var agreeAllComponents by remember { mutableStateOf(false) }
            var installState by remember { mutableStateOf<LoadState>(LoadState.Loading("installState")) }
            var onStartInstall by remember { mutableStateOf(false) }

            val activities = arrayOf(
                SplashActivity(
                    title = stringResource(Res.string.text_welcome),
                    subtitle = stringResource(Res.string.app_name),
                    screen = { WelcomeScreen() }
                ),
                SplashActivity(
                    title = stringResource(Res.string.label_select_ui_theme),
                    subtitle = stringResource(Res.string.label_select_ui_theme_tip),
                    screen = { UiThemeScreen() }
                ),
                SplashActivity(
                    title = stringResource(Res.string.label_license_agreement),
                    subtitle = stringResource(Res.string.label_license_agreement_tips),
                    screen = { LicenseAgreementScreen { agreeAllComponents = it } }
                ),
                SplashActivity(
                    title = stringResource(Res.string.label_install_components),
                    screen = { InstallScreen(onStartInstall = { onStartInstall = true }) { installState = it } }
                ),
            )

            val pagerState = rememberPagerState { activities.size }
            val coroutineScope = rememberCoroutineScope()

            Scaffold(
                modifier = Modifier.fillMaxSize(),
                topBar = {
                    TopAppBar(
                        colors = TopAppBarDefaults.topAppBarColors().copy(containerColor = Color.Transparent),
                        navigationIcon = {
                            Logo(Modifier.padding(16.dp).size(48.dp))
                        },
                        title = {
                            val activity = activities[pagerState.currentPage]
                            Column {
                                Text(text = activity.title, style = MaterialTheme.typography.titleMedium)
                                activity.subtitle?.let {
                                    Text(
                                        text = it,
                                        style = MaterialTheme.typography.labelMedium.copy(color = MaterialTheme.colorScheme.outline)
                                    )
                                }
                            }
                        }
                    )
                },
                bottomBar = {
                    BottomAppBar {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp, alignment = Alignment.End),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            LintOutlinedButton(
                                onClick = {
                                    if (pagerState.currentPage > 0) {
                                        coroutineScope.launch {
                                            pagerState.animateScrollToPage(pagerState.currentPage - 1)
                                        }
                                    }
                                },
                                enabled = pagerState.currentPage > 0 && !onStartInstall
                            ) {
                                Text(stringResource(Res.string.text_previous))
                            }
                            LintButton(
                                onClick = {
                                    if (pagerState.currentPage < pagerState.pageCount - 1) {
                                        coroutineScope.launch {
                                            pagerState.animateScrollToPage(pagerState.currentPage + 1)
                                        }
                                    }
                                },
                                enabled = when(pagerState.currentPage) {
                                    0, 1 -> true
                                    2 -> agreeAllComponents && pagerState.currentPage < pagerState.pageCount - 1
                                    else -> false
                                }
                            ) {
                                Text(stringResource(Res.string.text_next))
                            }
                            LintOutlinedButton(
                                onClick = { exitProcess(0) },
                            ) {
                                Text(stringResource(Res.string.text_cancel))
                            }
                            LintButton(
                                onClick = {
                                    if (pagerState.currentPage == activities.size - 1 && installState is LoadState.Success) {
                                        sharedPreferences.put(Const.INITIALIZATION, true)
                                        navigator.push(InitializationScreen())
                                    }
                                },
                                enabled = pagerState.currentPage == activities.size - 1 && installState is LoadState.Success
                            ) {
                                Text(stringResource(Res.string.text_finish))
                            }
                        }
                    }
                }
            ) {
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.padding(it).fillMaxSize().padding(16.dp),
                    userScrollEnabled = false,
                ) { index ->
                    activities[index].screen()
                }
            }
        }
    }
}