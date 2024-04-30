package io.lumkit.lint.toolkit.desktop.ui.screen.main

import androidx.compose.animation.*
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.rememberWindowState
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.Navigator
import io.github.lumkit.desktop.ui.LintWindow
import io.github.lumkit.desktop.ui.theme.AnimatedLintTheme
import io.lumkit.lint.toolkit.desktop.ui.navigation.NavigationContent
import io.lumkit.lint.toolkit.desktop.ui.screen.main.features.home.HomeScreen
import io.lumkit.lint.toolkit.desktop.ui.screen.main.features.payload.PayloadDumperScreen
import io.lumkit.lint.toolkit.desktop.ui.window.DisplayFeature
import io.lumkit.lint.toolkit.desktop.ui.window.LintContentType
import io.lumkit.lint.toolkit.desktop.ui.window.LintNavigationType
import kotlinx.coroutines.launch
import linttoolkit.app.generated.resources.*
import linttoolkit.app.generated.resources.Res
import linttoolkit.app.generated.resources.app_name
import linttoolkit.app.generated.resources.ic_logo
import linttoolkit.app.generated.resources.ic_navigation
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import java.awt.Dimension
import kotlin.system.exitProcess

class MainScreen : Screen {

    @Composable
    override fun Content() {
        MainScreenContent()
    }

}

val LocalWindowSizeClass = compositionLocalOf<WindowSizeClass> { error("not provided") }
val LocalDisplayFeature = compositionLocalOf<DisplayFeature> { error("not provided") }

@OptIn(ExperimentalResourceApi::class, ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
private fun MainScreenContent() {
    val windowState = rememberWindowState(
        position = WindowPosition.Aligned(Alignment.Center),
    )

    LintWindow(
        onCloseRequest = { exitProcess(0) },
        rememberSize = true,
        state = windowState,
        title = stringResource(Res.string.app_name),
        icon = painterResource(Res.drawable.ic_logo)
    ) {
        window.minimumSize = Dimension(500, 700)

        AnimatedLintTheme(
            modifier = Modifier.fillMaxSize()
        ) {
            val windowSizeClass = WindowSizeClass.calculateFromSize(windowState.size)
            val displayFeature: DisplayFeature

            when (windowSizeClass.widthSizeClass) {
                WindowWidthSizeClass.Compact -> {
                    displayFeature = DisplayFeature(
                        navigationType = LintNavigationType.DRAWER,
                        contentType = LintContentType.SINGLE
                    )
                }

                WindowWidthSizeClass.Medium -> {
                    displayFeature = DisplayFeature(
                        navigationType = LintNavigationType.DRAWER,
                        contentType = LintContentType.SINGLE
                    )
                }

                WindowWidthSizeClass.Expanded -> {
                    displayFeature = DisplayFeature(
                        navigationType = LintNavigationType.TABLE,
                        contentType = LintContentType.DOUBLE
                    )
                }

                else -> {
                    displayFeature = DisplayFeature(
                        navigationType = LintNavigationType.TABLE,
                        contentType = LintContentType.DOUBLE
                    )
                }
            }

            val featureScreens = rememberSaveable { mutableStateListOf<FeatureScreen>() }

            if (featureScreens.isEmpty()) {
                featureScreens.addAll(
                    arrayListOf(
                        HomeScreen(
                            name = stringResource(Res.string.text_home),
                            navText = stringResource(Res.string.text_home),
                        ),
                        PayloadDumperScreen(
                            name = stringResource(Res.string.text_payload_dumper),
                            navText = stringResource(Res.string.text_payload_dumper),
                        )
                    )
                )
            }

            CompositionLocalProvider(
                LocalWindowSizeClass provides windowSizeClass,
                LocalDisplayFeature provides displayFeature,
            ) {
                Navigator(
                    screen = featureScreens.first()
                ) { navigation ->
                    NavigationWrapper(navigation, featureScreens)
                }
            }
        }
    }
}

val LocalDrawerState = compositionLocalOf<DrawerState> { error("not provided") }

@Composable
private fun NavigationWrapper(navigation: Navigator, featureScreens: SnapshotStateList<FeatureScreen>) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val displayFeature = LocalDisplayFeature.current

    val searchText = rememberSaveable { mutableStateOf("") }

    CompositionLocalProvider(
        LocalDrawerState provides drawerState,
    ) {
        AnimatedContent(
            modifier = Modifier.fillMaxSize(),
            targetState = displayFeature.navigationType == LintNavigationType.TABLE,
            contentAlignment = Alignment.Center,
            transitionSpec = {
                fadeIn(tween()).togetherWith(fadeOut(tween()))
            }
        ) {
            if (it) {
                PermanentNavigationDrawer(
                    drawerContent = {
                        PermanentDrawerSheet(
                            modifier = Modifier.sizeIn(minWidth = 300.dp),
                            drawerContainerColor = Color.Transparent,
                        ) {
                            NavigationContent(
                                featureScreens = featureScreens,
                                searchText = searchText
                            )
                        }
                    },
                    content = {
                        LintAppContent(screen = navigation.lastItem)
                    }
                )
            } else {
                ModalNavigationDrawer(
                    drawerState = drawerState,
                    gesturesEnabled = false,
                    drawerContent = {
                        ModalDrawerSheet(
                            modifier = Modifier.sizeIn(minWidth = 300.dp),
                            drawerShape = RoundedCornerShape(topEnd = 8.dp, bottomEnd = 8.dp),
                            drawerContainerColor = MaterialTheme.colorScheme.background
                        ) {
                            NavigationContent(
                                searchText = searchText,
                                featureScreens = featureScreens,
                                onClose = { scope.launch { drawerState.close() } }
                            )
                        }
                    },
                    content = {
                        LintAppContent(
                            onOpen = { scope.launch { drawerState.open() } },
                            screen = navigation.lastItem
                        )
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalResourceApi::class)
@Composable
fun LintAppContent(
    onOpen: (() -> Unit)? = null,
    screen: Screen,
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Scaffold(
            modifier = Modifier.sizeIn(maxWidth = 1000.dp),
            topBar = {
                TopAppBar(
                    colors = TopAppBarDefaults.topAppBarColors().copy(
                        containerColor = Color.Transparent,
                    ),
                    navigationIcon = {
                        Row {
                            if (onOpen != null) {
                                Spacer(Modifier.width(16.dp))
                                Icon(
                                    painter = painterResource(Res.drawable.ic_navigation),
                                    contentDescription = null,
                                    modifier = Modifier.size(28.dp)
                                        .clip(CircleShape)
                                        .clickable {
                                            onOpen()
                                        }.padding(6.dp)
                                )
                            }
                            Spacer(Modifier.width(16.dp))
                        }
                    },
                    title = {

                    }
                )
            }
        ) {
            Row(
                modifier = Modifier.fillMaxSize().padding(it),
            ) {
                AnimatedContent(
                    targetState = screen,
                    transitionSpec = {
                        (fadeIn(tween(easing = FastOutSlowInEasing)) + expandVertically(tween(easing = FastOutSlowInEasing)) + scaleIn(
                            tween()
                        )).togetherWith(
                            shrinkVertically(tween(easing = FastOutSlowInEasing)) + fadeOut(
                                tween(easing = FastOutSlowInEasing)
                            ) + scaleOut(tween())
                        )
                    },
                    modifier = Modifier.fillMaxSize(),
                ) { screen ->
                    screen.Content()
                }
            }
        }
    }
}