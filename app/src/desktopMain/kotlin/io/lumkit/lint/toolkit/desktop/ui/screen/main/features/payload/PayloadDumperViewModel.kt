package io.lumkit.lint.toolkit.desktop.ui.screen.main.features.payload

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.ScrollbarAdapter
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.ui.text.input.TextFieldValue
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.github.lumkit.desktop.lifecycle.ViewModel
import io.github.lumkit.desktop.preferences.SharedPreferences
import io.lumkit.lint.toolkit.desktop.core.Const
import io.lumkit.lint.toolkit.desktop.core.shell.CommandExecutor
import io.lumkit.lint.toolkit.desktop.data.LoadState
import io.lumkit.lint.toolkit.desktop.data.PayloadPart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext

class PayloadDumperViewModel : ViewModel() {

    enum class ItemColumns(val count: Int) {
        SINGLE(1), DOUBLE(2)
    }

    private val gson = Gson()

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

    private val _listColumns = MutableStateFlow(ItemColumns.DOUBLE)
    val listColumns: StateFlow<ItemColumns> = _listColumns.asStateFlow()

    val listState = MutableStateFlow(
        LazyStaggeredGridState(
            0,
            0
        )
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
            _parts.value = gson.fromJson(result, object : TypeToken<List<PayloadPart>>() {}.type)
            _loadState.value = LoadState.Success("loadParts")
        } catch (e: Exception) {
            e.printStackTrace()
            _loadState.value = LoadState.Failure("loadParts")
        }
    }

    fun setSelectedParts(parts: List<PayloadPart>) {
        _selectedParts.value = parts
    }

}