package io.lumkit.lint.toolkit.desktop.ui.navigation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.snapshots.SnapshotStateList
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
import io.github.lumkit.desktop.ui.components.LintSideNavigationBar
import io.github.lumkit.desktop.ui.components.LintTextField
import io.lumkit.lint.toolkit.desktop.ui.screen.main.FeatureScreen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import linttoolkit.app.generated.resources.*
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class, ExperimentalResourceApi::class)
@Composable
fun NavigationContent(
    searchText: MutableState<String>,
    featureScreens: SnapshotStateList<FeatureScreen>,
    onClose: (() -> Unit)? = null,
) {
    val navigator = LocalNavigator.currentOrThrow
    val currentScreen = navigator.lastItem

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
                    }.flowOn(Dispatchers.IO)
                    .launchIn(this)
            }

            Column(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            ) {
                LintTextField(
                    value = searchText.value,
                    onValueChange = { searchText.value = it },
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
                                        searchText.value = ""
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
                                Text(feature.label)
                            },
                            onClick = {
                                navigator.push(feature)
                                searchText.value = ""
                            }
                        )
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxSize(),
            ) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize().weight(1f),
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items(featureScreens) { item ->
                        NavigationItem(
                            navigator = navigator,
                            screen = item,
                            currentScreen = currentScreen
                        )
                    }
                }
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
            navigator.push(screen)
        }
    )
}