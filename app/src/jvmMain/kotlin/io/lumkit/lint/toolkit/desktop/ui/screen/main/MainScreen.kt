package io.lumkit.lint.toolkit.desktop.ui.screen.main

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.PermanentDrawerSheet
import androidx.compose.material3.PermanentNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.rememberWindowState
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import io.github.lumkit.desktop.lifecycle.viewModel
import io.github.lumkit.desktop.ui.LintWindow
import io.github.lumkit.desktop.ui.components.LintOutlineCard
import io.github.lumkit.desktop.ui.theme.AnimatedLintTheme
import io.github.lumkit.lint.desktop.app.generated.resources.Res
import io.github.lumkit.lint.desktop.app.generated.resources.app_name
import io.github.lumkit.lint.desktop.app.generated.resources.ic_logo
import io.github.lumkit.lint.desktop.app.generated.resources.ic_navigation
import io.lumkit.lint.toolkit.desktop.data.ItemColumns
import io.lumkit.lint.toolkit.desktop.ui.components.Logo
import io.lumkit.lint.toolkit.desktop.ui.navigation.NavigationContent
import io.lumkit.lint.toolkit.desktop.ui.navigation.pushSingle
import io.lumkit.lint.toolkit.desktop.ui.window.DisplayFeature
import io.lumkit.lint.toolkit.desktop.ui.window.LintContentType
import io.lumkit.lint.toolkit.desktop.ui.window.LintNavigationType
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
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
        size = DpSize(1000.dp, 600.dp)
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

            val mainViewModel = viewModel<MainViewModel>()
            val screenMap by mainViewModel.featureScreen.collectAsState()
            val featureScreens = screenMap?.map { it.value }

            CompositionLocalProvider(
                LocalWindowSizeClass provides windowSizeClass,
                LocalDisplayFeature provides displayFeature,
            ) {
                AnimatedContent(
                    modifier = Modifier.fillMaxSize(),
                    targetState = featureScreens,
                    contentAlignment = Alignment.Center
                ) { screens ->
                    if (screens != null) {
                        Navigator(
                            screen = screens.first()
                        ) { navigation ->
                            NavigationWrapper(navigation, screens)
                        }
                    } else {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Logo(modifier = Modifier.size(96.dp))
                        }
                    }
                }
            }
        }
    }
}

val LocalDrawerState = compositionLocalOf<DrawerState> { error("not provided") }

@Composable
private fun NavigationWrapper(navigation: Navigator, featureScreens: List<FeatureScreen>) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val displayFeature = LocalDisplayFeature.current

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


val LocalGridColumns = compositionLocalOf<ItemColumns> { error("Not provided.") }
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
                        Title()
                    }
                )
            }
        ) {
            val width = remember { mutableStateOf(0.dp) }
            LintOutlineCard(
                modifier = Modifier.fillMaxSize().padding(it)
                    .drawWithContent {
                        width.value = size.width.toDp()
                        drawContent()
                    },
                colors = CardDefaults.outlinedCardColors()
                    .copy(containerColor = Color.Transparent),
            ) {
                var itemColumns by rememberSaveable { mutableStateOf(ItemColumns.DOUBLE) }
                LaunchedEffect(Unit) {
                    snapshotFlow { width.value }
                        .onEach { v ->
                            itemColumns = if (v > 580.dp) {
                                ItemColumns.DOUBLE
                            } else {
                                ItemColumns.SINGLE
                            }
                        }.launchIn(this)
                }

                CompositionLocalProvider(
                    LocalGridColumns provides itemColumns
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
}

@Composable
private fun Title() {
    val listState = rememberLazyListState()
    val navigator = LocalNavigator.currentOrThrow
    val lastItem = navigator.lastItem as FeatureScreen
    val mainViewModel = viewModel<MainViewModel>()

    val navigationPath = lastItem.navPath
    val featureScreens by mainViewModel.featureScreen.collectAsState()
    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        state = listState,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            Row(
                modifier = Modifier.height(48.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IconButton(
                    onClick = {
                        if (navigator.lastItem != mainViewModel.featureScreen.value?.get("HOME")) {
                            navigator.pop()
                        }
                    },
                    enabled = lastItem != mainViewModel.featureScreen.value?.get("HOME")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = null,
                    )
                }
            }
        }

        navigationPath.forEachIndexed { index, tag ->
            item {
                Row(
                    modifier = Modifier.height(48.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    featureScreens?.get(tag)?.let { screen ->
                        Text(
                            text = screen.label,
                            style = MaterialTheme.typography.titleMedium,
                            color = if (index < navigationPath.lastIndex) {
                                MaterialTheme.colorScheme.primary.copy(alpha = .4f)
                            } else {
                                MaterialTheme.colorScheme.primary
                            },
                            modifier = Modifier.clip(RoundedCornerShape(6.dp))
                                .clickable {
                                    navigator.pushSingle(screen)
                                }.padding(8.dp)
                        )
                    }
                }
            }
            if (index < navigationPath.lastIndex) {
                item {
                    Row(
                        modifier = Modifier.height(48.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}