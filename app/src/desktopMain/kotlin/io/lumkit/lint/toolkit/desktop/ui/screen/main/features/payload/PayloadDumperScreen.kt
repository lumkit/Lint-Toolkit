package io.lumkit.lint.toolkit.desktop.ui.screen.main.features.payload

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.TableView
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import io.github.lumkit.desktop.context.LocalContextWrapper
import io.github.lumkit.desktop.lifecycle.viewModel
import io.github.lumkit.desktop.preferences.LocalSharedPreferences
import io.github.lumkit.desktop.preferences.SharedPreferences
import io.github.lumkit.desktop.ui.components.*
import io.github.lumkit.desktop.ui.dialog.LintAlert
import io.lumkit.lint.toolkit.desktop.data.LoadState
import io.lumkit.lint.toolkit.desktop.data.PayloadPart
import io.lumkit.lint.toolkit.desktop.ui.screen.main.FeatureScreen
import io.lumkit.lint.toolkit.desktop.ui.screen.main.LocalContentWidth
import io.lumkit.lint.toolkit.desktop.util.chooseFile
import io.lumkit.lint.toolkit.desktop.util.convertFileSize
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import linttoolkit.app.generated.resources.*
import linttoolkit.app.generated.resources.Res
import linttoolkit.app.generated.resources.label_payload_path
import linttoolkit.app.generated.resources.text_choose_file
import linttoolkit.app.generated.resources.text_choose_file_payload
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import javax.swing.JFileChooser
import javax.swing.filechooser.FileNameExtensionFilter

class PayloadDumperScreen(
    name: String,
    rt: String,
) : FeatureScreen(
    tag = "PAYLOAD_DUMPER",
    recommend = true,
    recommendText = rt,
    label = name,
    icon = {
        Icon(imageVector = Icons.Default.TableView, contentDescription = null)
    }
) {

    @Composable
    override fun Content() {
        PayloadDumperScreen()
    }
}

@OptIn(ExperimentalResourceApi::class)
@Composable
private fun PayloadDumperScreen() {
    val context = LocalContextWrapper.current
    val payloadDumperViewModel = viewModel<PayloadDumperViewModel>()
    val binPath = payloadDumperViewModel.binPath.collectAsState()
    val outputPath = payloadDumperViewModel.outputPath.collectAsState()
    val parts = payloadDumperViewModel.parts.collectAsState()
    val selectParts = payloadDumperViewModel.selectedParts.collectAsState()
    val searchState = payloadDumperViewModel.searchState.collectAsState()
    val loadState = payloadDumperViewModel.loadState.collectAsState()
    val coroutineScope = rememberCoroutineScope { Dispatchers.IO }
    val sharedPreferences = LocalSharedPreferences.current
    val isShow = payloadDumperViewModel.dialogShowState.collectAsState()
    val messageState by payloadDumperViewModel.messageState.collectAsState()
    val dialogState = remember { mutableStateOf(isShow.value) }

    LaunchedEffect(Unit) {
        snapshotFlow { isShow.value }
            .onEach { dialogState.value = it }
            .launchIn(this)
    }

    Column(
        modifier = Modifier.fillMaxSize()
            .padding(16.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            LintOutlinedTextField(
                modifier = Modifier.fillMaxWidth().weight(1f),
                value = binPath.value,
                onValueChange = payloadDumperViewModel::setBinPath,
                label = {
                    Text(stringResource(Res.string.label_payload_path))
                },
                singleLine = true,
            )
            LintButton(
                onClick = {
                    coroutineScope.launch {
                        chooseFile(
                            contextWrapper = context,
                            currentDirKey = "binPath",
                            fileFilter = FileNameExtensionFilter(getString(Res.string.text_choose_file_payload), "bin"),
                        )?.let {
                            val absolutePath = it.absolutePath
                            payloadDumperViewModel.setBinPath(
                                TextFieldValue(
                                    text = absolutePath,
                                    selection = TextRange(absolutePath.length)
                                )
                            )
                            if (outputPath.value.text.trim().isEmpty()) {
                                payloadDumperViewModel.setOutputPath(
                                    TextFieldValue(
                                        text = it.parent,
                                        selection = TextRange(absolutePath.length)
                                    )
                                )
                            }
                        }
                    }
                }
            ) {
                Text(stringResource(Res.string.text_choose_file))
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            LintOutlinedTextField(
                modifier = Modifier.fillMaxWidth().weight(1f),
                value = outputPath.value,
                onValueChange = payloadDumperViewModel::setOutputPath,
                label = {
                    Text(stringResource(Res.string.label_payload_output_path))
                },
                singleLine = true,
            )
            LintButton(
                onClick = {
                    coroutineScope.launch {
                        coroutineScope.launch {
                            chooseFile(
                                contextWrapper = context,
                                currentDirKey = "imageOutputPath",
                                mode = JFileChooser.DIRECTORIES_ONLY
                            )?.let {
                                val absolutePath = it.absolutePath
                                payloadDumperViewModel.setOutputPath(
                                    TextFieldValue(
                                        text = absolutePath,
                                        selection = TextRange(absolutePath.length)
                                    )
                                )
                            }
                        }
                    }
                }
            ) {
                Text(stringResource(Res.string.text_choose_dir))
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            LintOutlinedTextField(
                modifier = Modifier.fillMaxWidth().weight(1f),
                value = searchState.value,
                onValueChange = payloadDumperViewModel::setSearchState,
                label = {
                    Text(stringResource(Res.string.text_search_image))
                },
                singleLine = true,
                trailingIcon = {
                    if (searchState.value.text.isNotEmpty()) {
                        Icon(
                            painter = painterResource(Res.drawable.ic_dismiss),
                            contentDescription = null,
                            modifier = Modifier.padding(16.dp)
                                .size(24.dp)
                                .clip(CircleShape)
                                .clickable {
                                    payloadDumperViewModel.setSearchState(TextFieldValue())
                                }.padding(6.dp)
                        )
                    }
                }
            )
            Row(
                modifier = Modifier.clip(RoundedCornerShape(4.dp))
                    .clickable {
                        if (selectParts.value.containsAll(parts.value) && parts.value.isNotEmpty()) {
                            payloadDumperViewModel.setSelectedParts(arrayListOf())
                        } else {
                            payloadDumperViewModel.setSelectedParts(parts.value)
                        }
                    }.padding(4.dp)
                    .alpha(if (parts.value.isNotEmpty()) 1f else .4f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Checkbox(
                    checked = selectParts.value.containsAll(parts.value) && parts.value.isNotEmpty(),
                    onCheckedChange = null
                )
                Text(stringResource(Res.string.text_select_all))
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Parts(payloadDumperViewModel, sharedPreferences, binPath)
        AnimatedVisibility(
            visible = parts.value.isNotEmpty(),
        ) {
            LintButton(
                modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                onClick = {
                    coroutineScope.launch {
                        payloadDumperViewModel.startDeriveImages(context, sharedPreferences)
                    }
                },
                enabled = selectParts.value.isNotEmpty(),
            ) {
                Text(text = stringResource(Res.string.text_derive_images))
            }
        }
    }

    LintAlert(
        visible = dialogState,
        title = stringResource(Res.string.alert_payload_dumper),
        confirmButtonText = stringResource(Res.string.text_confirm),
        onConfirm = {
            if (loadState.value?.name == "startDeriveImages" && loadState.value is LoadState.Success) {
                payloadDumperViewModel.setDialogState(false)
            }
        },
        cancelButtonText = stringResource(Res.string.text_cancel),
        onCancel = {
            payloadDumperViewModel.setDialogState(false)
        },
        isCancel = false
    ) {
        SelectionContainer { Text(messageState) }
    }
}

@OptIn(ExperimentalResourceApi::class)
@Composable
private fun ColumnScope.Parts(
    payloadDumperViewModel: PayloadDumperViewModel,
    sharedPreferences: SharedPreferences,
    binPath: State<TextFieldValue>
) {
    val parts = payloadDumperViewModel.parts.collectAsState()
    val selectParts = payloadDumperViewModel.selectedParts.collectAsState()
    val listColumns by payloadDumperViewModel.listColumns.collectAsState()
    val listState by payloadDumperViewModel.listState.collectAsState()
    val searchState by payloadDumperViewModel.searchState.collectAsState()

    LaunchedEffect(binPath) {
        snapshotFlow { binPath.value.text }
            .onEach {
                payloadDumperViewModel.loadParts(sharedPreferences, it)
            }.flowOn(Dispatchers.IO).launchIn(this)
        snapshotFlow { LocalContentWidth.value }
            .onEach {
                payloadDumperViewModel.setListColumns(
                    if (it.width > 580.dp) {
                        PayloadDumperViewModel.ItemColumns.DOUBLE
                    } else {
                        PayloadDumperViewModel.ItemColumns.SINGLE
                    }
                )

            }.launchIn(this)
    }

    Box(
        modifier = Modifier.fillMaxSize().weight(1f),
        contentAlignment = Alignment.Center
    ) {
        AnimatedContent(
            modifier = Modifier.fillMaxSize(),
            targetState = parts.value.isNotEmpty(),
            contentAlignment = Alignment.Center
        ) {
            if (it) {
                Row(
                    modifier = Modifier.fillMaxSize(),
                ) {
                    val verticalScrollbarAdapter = rememberScrollbarAdapter(listState)
                    LazyVerticalGrid(
                        modifier = Modifier.fillMaxSize().weight(1f),
                        columns = GridCells.Fixed(listColumns.count),
                        contentPadding = PaddingValues(vertical = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        state = listState
                    ) {
                        items(parts.value.filter { item ->
                            item.name.lowercase().contains(searchState.text.trim().lowercase())
                        }) { item ->
                            PartItem(item, selectParts, payloadDumperViewModel)
                        }
                    }
                    LintVerticalScrollBar(
                        modifier = Modifier.fillMaxHeight(),
                        adapter = verticalScrollbarAdapter,
                    )
                }
            } else {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (binPath.value.text.isEmpty()) {
                            stringResource(Res.string.text_please_choose_a_bin_file)
                        } else {
                            stringResource(Res.string.text_no_data)
                        },
                        style = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.outline),
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalResourceApi::class)
@Composable
private fun PartItem(
    payloadPart: PayloadPart,
    selectParts: State<List<PayloadPart>>,
    payloadDumperViewModel: PayloadDumperViewModel
) {
    LintOutlineCard(
        modifier = Modifier.fillMaxWidth(),
        onClick = {
            if (selectParts.value.contains(payloadPart)) {
                payloadDumperViewModel.setSelectedParts(
                    selectParts.value.filter { it != payloadPart }
                )
            } else {
                val list = ArrayList(selectParts.value)
                list.add(payloadPart)
                payloadDumperViewModel.setSelectedParts(list)
            }
        }
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Icon(
                imageVector = Icons.Default.Image,
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )
            Column(
                modifier = Modifier.fillMaxWidth().weight(1f),
            ) {
                Text(text = payloadPart.name, style = MaterialTheme.typography.bodyLarge)
                Text(
                    text = String.format(
                        stringResource(Res.string.format_payload_part_tooltips),
                        convertFileSize(payloadPart.size)
                    ), style = MaterialTheme.typography.labelMedium
                )
            }
            Checkbox(
                checked = selectParts.value.contains(payloadPart),
                onCheckedChange = null
            )
        }
    }
}