package io.lumkit.lint.toolkit.desktop.ui.screen.main.features.payload

import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.ui.text.input.TextFieldValue
import io.github.lumkit.desktop.context.ContextWrapper
import io.github.lumkit.desktop.context.Toast
import io.github.lumkit.desktop.context.Toast.showToast
import io.github.lumkit.desktop.lifecycle.ViewModel
import io.github.lumkit.desktop.preferences.SharedPreferences
import io.github.lumkit.lint.desktop.app.generated.resources.Res
import io.github.lumkit.lint.desktop.app.generated.resources.text_derive_toast
import io.github.lumkit.lint.desktop.app.generated.resources.text_task_canceled
import io.lumkit.lint.toolkit.desktop.core.Const
import io.lumkit.lint.toolkit.desktop.core.shell.CommandExecutor
import io.lumkit.lint.toolkit.desktop.data.LoadState
import io.lumkit.lint.toolkit.desktop.data.PayloadPart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.getString

class PayloadDumperViewModel : ViewModel() {

    private val _binPath = MutableStateFlow(TextFieldValue())
    val binPath: StateFlow<TextFieldValue> = _binPath.asStateFlow()

    private val _outputPath = MutableStateFlow(TextFieldValue())
    val outputPath: StateFlow<TextFieldValue> = _outputPath.asStateFlow()

    private val _parts = MutableStateFlow<List<PayloadPart>>(listOf())
    val parts: StateFlow<List<PayloadPart>> = _parts.asStateFlow()

    private val _selectedParts = MutableStateFlow(listOf<PayloadPart>())
    val selectedParts: StateFlow<List<PayloadPart>> = _selectedParts.asStateFlow()

    private val _loadState = MutableStateFlow<LoadState?>(null)
    val loadState: StateFlow<LoadState?> = _loadState.asStateFlow()

    private val _searchState = MutableStateFlow(TextFieldValue())
    val searchState: StateFlow<TextFieldValue> = _searchState.asStateFlow()

    private val _messageState = MutableStateFlow("")
    val messageState: StateFlow<String> = _messageState.asStateFlow()

    private val _dialogShowState = MutableStateFlow(false)
    val dialogShowState: StateFlow<Boolean> = _dialogShowState.asStateFlow()

    val listState = MutableStateFlow(
        LazyGridState()
    ).asStateFlow()

    fun setBinPath(path: TextFieldValue) {
        _binPath.value = path
    }

    fun setOutputPath(path: TextFieldValue) {
        _outputPath.value = path
    }

    fun setLoadState(loadState: LoadState) {
        _loadState.value = loadState
    }

    fun setSearchState(state: TextFieldValue) {
        _searchState.value = state
    }

    fun setDialogState(isShow: Boolean) {
        _dialogShowState.value = isShow
    }

    suspend fun loadParts(sharedPreferences: SharedPreferences, filePath: String) = withContext(Dispatchers.IO) {
        _loadState.value = LoadState.Loading("loadParts")
        val result = CommandExecutor.executeCommandWithWorkingDirectory(
            arrayListOf(
                "python",
                "payload_dumper.py",
                "--list",
                filePath.trim()
            ), sharedPreferences.getString(Const.RUNTIME_PAYLOAD_DUMPER) ?: ""
        ).trim()
        try {
            _parts.value = Json.decodeFromString(result)
            _loadState.value = LoadState.Success("loadParts")
        } catch (e: Exception) {
            e.printStackTrace()
            _parts.value = listOf()
            _loadState.value = LoadState.Failure("loadParts")
        }
    }

    fun setSelectedParts(parts: List<PayloadPart>) {
        _selectedParts.value = parts
    }

    @OptIn(ExperimentalResourceApi::class)
    suspend fun startDeriveImages(context: ContextWrapper, sharedPreferences: SharedPreferences) = withContext(Dispatchers.IO) {
        _dialogShowState.value = true
        _messageState.value = ""
        _loadState.value = LoadState.Loading("startDeriveImages")
        var t = 0
        var f = 0
        try {
            _selectedParts.value.sortedBy { it.name }
                .forEachIndexed { index, payloadPart ->
                    if (!_dialogShowState.value) {
                        context.showToast(getString(Res.string.text_task_canceled), time = Toast.LENGTH_LONG)
                        return@forEachIndexed
                    }
                    _messageState.value += "Start Derive Image(${index + 1}/${_selectedParts.value.size}): ${payloadPart.name}.img\n"
                    val result = CommandExecutor.executeCommandWithWorkingDirectory(
                        arrayListOf(
                            "python",
                            "payload_dumper.py",
                            "--out",
                            _outputPath.value.text.trim(),
                            "--images",
                            payloadPart.name,
                            _binPath.value.text.trim(),
                        ), sharedPreferences.getString(Const.RUNTIME_PAYLOAD_DUMPER) ?: ""
                    )
                    if (result.replace("\n", "").trimIndent().endsWith("Done")) {
                        _messageState.value += "SUCCESSFUL.\n\n"
                        t++
                    } else {
                        _messageState.value += "UNSUCCESSFUL!\n\n"
                        f++
                    }
                }
            _loadState.value = LoadState.Success("startDeriveImages")
        }catch (e: Exception) {
            e.printStackTrace()
            _loadState.value = LoadState.Failure("startDeriveImages")
        } finally {
            context.showToast(String.format(getString(Res.string.text_derive_toast), t, f, _selectedParts.value.size), time = Toast.LENGTH_LONG)
            _selectedParts.value = arrayListOf()
        }
    }
}