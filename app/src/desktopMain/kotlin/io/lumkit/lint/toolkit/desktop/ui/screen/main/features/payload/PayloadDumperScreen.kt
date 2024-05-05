package io.lumkit.lint.toolkit.desktop.ui.screen.main.features.payload

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.TableView
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import io.github.lumkit.desktop.context.LocalContextWrapper
import io.github.lumkit.desktop.lifecycle.viewModel
import io.github.lumkit.desktop.preferences.LocalSharedPreferences
import io.github.lumkit.desktop.preferences.SharedPreferences
import io.github.lumkit.desktop.ui.components.LintButton
import io.github.lumkit.desktop.ui.components.LintFolder
import io.github.lumkit.desktop.ui.components.LintOutlinedTextField
import io.lumkit.lint.toolkit.desktop.data.LoadState
import io.lumkit.lint.toolkit.desktop.data.PayloadPart
import io.lumkit.lint.toolkit.desktop.ui.screen.main.FeatureScreen
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
import org.jetbrains.compose.resources.stringResource
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
    val coroutineScope = rememberCoroutineScope { Dispatchers.IO }
    val sharedPreferences = LocalSharedPreferences.current

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

                    }
                }
            ) {
                Text(stringResource(Res.string.text_choose_dir))
            }
        }
        Parts(payloadDumperViewModel, sharedPreferences, binPath)
    }
}

@Composable
private fun ColumnScope.Parts(
    payloadDumperViewModel: PayloadDumperViewModel,
    sharedPreferences: SharedPreferences,
    binPath: State<TextFieldValue>
) {
    val parts = payloadDumperViewModel.parts.collectAsState()
    val selectParts = payloadDumperViewModel.selectedParts.collectAsState()
    val loadState by payloadDumperViewModel.loadState.collectAsState()
    val listColumns by payloadDumperViewModel.listColumns.collectAsState()
    val listState by payloadDumperViewModel.listState.collectAsState()

    LaunchedEffect(binPath) {
        snapshotFlow { binPath.value.text }
            .onEach {
                payloadDumperViewModel.loadParts(sharedPreferences, it)
            }.flowOn(Dispatchers.IO).launchIn(this)
    }

    Box(
        modifier = Modifier.fillMaxSize().weight(1f),
        contentAlignment = Alignment.Center
    ) {
        when (loadState) {
            is LoadState.Failure -> {

            }
            is LoadState.Success -> {
                Row(
                    modifier = Modifier.fillMaxSize(),
                ) {
                    LazyVerticalStaggeredGrid(
                        modifier = Modifier.fillMaxSize(),
                        columns = StaggeredGridCells.Fixed(listColumns.count),
                        contentPadding = PaddingValues(vertical = 16.dp),
                        verticalItemSpacing = 8.dp,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        state = listState
                    ) {
                        items(parts.value) {
                            PartItem(it, selectParts, payloadDumperViewModel)
                        }
                    }
                }
            }
            else -> Unit
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

    LintFolder(
        modifier = Modifier.fillMaxWidth(),
        icon = {
            Icon(
                imageVector = Icons.Default.Image,
                contentDescription = null
            )
        },
        label = {
            Text(text = payloadPart.name)
        },
        tooltipText = {
            Text(text = String.format(stringResource(Res.string.format_payload_part_tooltips), convertFileSize(payloadPart.size)))
        },
        trailingIcon = {
            Checkbox(
                checked = selectParts.value.contains(payloadPart),
                onCheckedChange = null
            )
        },
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
    )
}