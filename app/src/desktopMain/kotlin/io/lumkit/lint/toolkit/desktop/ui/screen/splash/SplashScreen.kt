package io.lumkit.lint.toolkit.desktop.ui.screen.splash

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
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
import io.lumkit.lint.toolkit.desktop.LocalApplication
import io.lumkit.lint.toolkit.desktop.core.Const
import io.lumkit.lint.toolkit.desktop.model.SplashActivity
import io.lumkit.lint.toolkit.desktop.ui.components.Logo
import io.lumkit.lint.toolkit.desktop.ui.screen.initialization.InitializationScreen
import io.lumkit.lint.toolkit.desktop.ui.theme.LintToolkitTheme
import kotlinx.coroutines.launch
import linttoolkit.app.generated.resources.*
import linttoolkit.app.generated.resources.Res
import linttoolkit.app.generated.resources.app_name
import linttoolkit.app.generated.resources.ic_logo
import linttoolkit.app.generated.resources.title_splash
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

class SplashScreen : Screen {

    @Composable
    override fun Content() {
        val sharedPreferences = LocalSharedPreferences.current
        val navigator = LocalNavigator.currentOrThrow

        if (sharedPreferences.get<Boolean?>(Const.INITIALIZATION) == true) {
            navigator.push(InitializationScreen())
        } else {
            Splash()
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
private fun Splash() {
    val applicationScope = LocalApplication.current
    LintWindow(
        onCloseRequest = applicationScope::exitApplication,
        title = String.format("%s %s", stringResource(Res.string.app_name), stringResource(Res.string.title_splash)),
        icon = painterResource(Res.drawable.ic_logo),
        resizable = false
    ) {
        LintToolkitTheme(
            modifier = Modifier.fillMaxSize(),
        ) {
            val activities = arrayOf(
                SplashActivity(
                    title = stringResource(Res.string.text_welcome),
                    subtitle = stringResource(Res.string.app_name),
                    screen = { WelcomeScreen() }
                ),
                SplashActivity(
                    title = stringResource(Res.string.select_ui_theme),
                    subtitle = stringResource(Res.string.select_ui_theme_tip),
                    screen = { UiThemeScreen() }
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
                                enabled = pagerState.currentPage > 0
                            ) {
                                Text(stringResource(Res.string.previous))
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
                                    0 -> true
                                    else -> false
                                }
                            ) {
                                Text(stringResource(Res.string.next))
                            }
                            LintOutlinedButton(
                                onClick = applicationScope::exitApplication,
                            ) {
                                Text(stringResource(Res.string.cancel))
                            }
                            LintButton(
                                onClick = {

                                },
                                enabled = pagerState.currentPage == activities.size - 1
                            ) {
                                Text(stringResource(Res.string.finish))
                            }
                        }
                    }
                }
            ) {
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.padding(it).fillMaxSize().padding(16.dp),
                    userScrollEnabled = false
                ) {
                    activities[it].screen()
                }
            }
        }
    }
}