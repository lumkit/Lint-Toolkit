package io.lumkit.lint.toolkit.desktop.ui.navigation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.PopupProperties
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import coil3.compose.AsyncImage
import io.github.lumkit.desktop.lifecycle.viewModel
import io.github.lumkit.desktop.ui.components.LintHorizontalDivider
import io.github.lumkit.desktop.ui.components.LintSideNavigationBar
import io.github.lumkit.desktop.ui.components.LintTextField
import io.github.lumkit.lint.desktop.app.generated.resources.Res
import io.github.lumkit.lint.desktop.app.generated.resources.app_name
import io.github.lumkit.lint.desktop.app.generated.resources.ic_dismiss
import io.github.lumkit.lint.desktop.app.generated.resources.ic_logo
import io.github.lumkit.lint.desktop.app.generated.resources.text_hint_search_features
import io.github.lumkit.lint.desktop.app.generated.resources.text_not_login
import io.lumkit.lint.toolkit.desktop.ui.screen.main.FeatureScreen
import io.lumkit.lint.toolkit.desktop.ui.screen.main.MainViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class, ExperimentalResourceApi::class, ExperimentalLayoutApi::class)
@Composable
fun NavigationContent(
    featureScreens: List<FeatureScreen>,
    onClose: (() -> Unit)? = null,
) {
    val navigator = LocalNavigator.currentOrThrow
    val currentScreen = navigator.lastItem
    val mainViewModel = viewModel<MainViewModel>()
    val searchText = mainViewModel.searchText.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                modifier = Modifier.fillMaxWidth(),
                colors = TopAppBarDefaults.topAppBarColors().copy(
                    containerColor = Color.Transparent,
                ),
                title = {
                    Text(text = stringResource(Res.string.app_name))
                },
                actions = {
                    if (onClose != null) {
                        Icon(
                            painter = painterResource(Res.drawable.ic_dismiss),
                            contentDescription = null,
                            modifier = Modifier.size(24.dp)
                                .clip(CircleShape)
                                .clickable {
                                    onClose()
                                }.padding(6.dp)
                        )
                    }
                    Spacer(Modifier.width(16.dp))
                }
            )
        }
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(it),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Avatar()

            val features = rememberSaveable { mutableStateListOf<FeatureScreen>() }

            LaunchedEffect(searchText) {
                snapshotFlow { searchText.value }
                    .onEach {
                        features.clear()
                        features.addAll(
                            featureScreens.filter { screen ->
                                screen.label.replace(" ", "").lowercase()
                                    .contains(searchText.value.replace(" ", "").lowercase()) &&
                                        searchText.value.isNotEmpty()
                            }.sortedBy { screen -> screen.label }
                        )
                    }
                    .launchIn(this)
            }

            Column(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            ) {
                LintTextField(
                    value = searchText.value,
                    onValueChange = mainViewModel::setSearchText,
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = {
                        Text(stringResource(Res.string.text_hint_search_features))
                    },
                    singleLine = true,
                    trailingIcon = {
                        if (searchText.value.isNotEmpty()) {
                            Icon(
                                painter = painterResource(Res.drawable.ic_dismiss),
                                contentDescription = null,
                                modifier = Modifier.padding(16.dp)
                                    .size(24.dp)
                                    .clip(CircleShape)
                                    .clickable {
                                        mainViewModel.setSearchText("")
                                    }.padding(6.dp)
                            )
                        }
                    }
                )

                DropdownMenu(
                    expanded = features.isNotEmpty(),
                    onDismissRequest = {},
                    properties = PopupProperties(
                        focusable = false
                    )
                ) {
                    features.forEach { feature ->
                        DropdownMenuItem(
                            leadingIcon = feature.icon,
                            text = {
                                Column {
                                    if (feature.navPath.size > 1) {
                                        Text(feature.label, style = MaterialTheme.typography.labelMedium)
                                        FlowRow(
                                            modifier = Modifier.horizontalScroll(rememberScrollState()),
                                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                                            verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically),
                                        ) {
                                            feature.navPath.forEachIndexed { index, tag ->
                                                val screensMap by mainViewModel.featureScreen.collectAsState()
                                                Text(
                                                    text = screensMap?.get(tag)?.label ?: "",
                                                    style = MaterialTheme.typography.labelSmall,
                                                )
                                                if (index < feature.navPath.lastIndex) {
                                                    Icon(
                                                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                                        contentDescription = null,
                                                        modifier = Modifier.size(16.dp)
                                                    )
                                                }
                                            }
                                        }
                                    } else {
                                        Text(feature.label, style = MaterialTheme.typography.labelMedium)
                                    }
                                }
                            },
                            onClick = {
                                navigator.pushSingle(feature)
                                mainViewModel.setSearchText("")
                            }
                        )
                    }
                }
            }
            LazyColumn(
                modifier = Modifier.fillMaxSize().weight(1f),
                contentPadding = PaddingValues(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(featureScreens.filter { item -> item.isNavigation }) { item ->
                    NavigationItem(
                        navigator = navigator,
                        screen = item,
                        currentScreen = currentScreen
                    )
                }
            }
            Column(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                LintHorizontalDivider(modifier = Modifier.fillMaxWidth())
                NavigationItem(
                    navigator = navigator,
                    screen = mainViewModel.featureScreen.value?.get("SETTINGS") ?: throw RuntimeException("Settings screen not found"),
                    currentScreen = currentScreen
                )
            }
        }
    }
}

@OptIn(ExperimentalResourceApi::class)
@Composable
private fun Avatar() {
    Row(
        modifier = Modifier.fillMaxWidth()
            .padding(horizontal = 8.dp)
            .clip(RoundedCornerShape(8.dp))
            .clickable {
                // TODO login or goto user info

            },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        AsyncImage(
            model = null,
            placeholder = painterResource(Res.drawable.ic_logo),
            error = painterResource(Res.drawable.ic_logo),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.padding(16.dp)
                .size(65.dp)
                .clip(CircleShape)
        )
        Column {
            Text(stringResource(Res.string.text_not_login), style = MaterialTheme.typography.titleMedium)
        }
        Spacer(Modifier.width(16.dp))
    }
}

@Composable
private fun NavigationItem(navigator: Navigator, screen: FeatureScreen, currentScreen: Screen) {
    LintSideNavigationBar(
        modifier = Modifier.fillMaxWidth(),
        expanded = false,
        selected = screen.label == (currentScreen as FeatureScreen).label,
        icon = {
            screen.icon?.invoke()
        },
        title = {
            Text(screen.label)
        },
        subtitle = if (screen.tip == null) {
            null
        } else {
            {
                Text(screen.tip)
            }
        },
        onClick = {
            navigator.pushSingle(screen)
        }
    )
}