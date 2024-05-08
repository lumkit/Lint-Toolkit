package io.lumkit.lint.toolkit.desktop.ui.screen.splash

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.HorizontalScrollbar
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import io.github.lumkit.desktop.context.LocalContext
import io.github.lumkit.desktop.preferences.LocalSharedPreferences
import io.github.lumkit.lint.desktop.app.generated.resources.JetBrainsMono_Regular
import io.github.lumkit.lint.desktop.app.generated.resources.Res
import io.lumkit.lint.toolkit.desktop.core.tasks.AaptInstallTask
import io.lumkit.lint.toolkit.desktop.core.tasks.AdbInstallTask
import io.lumkit.lint.toolkit.desktop.core.tasks.LintRuntimesInstallTask
import io.lumkit.lint.toolkit.desktop.core.tasks.PythonInstallTask
import io.lumkit.lint.toolkit.desktop.data.LoadState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.withContext
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.Font

@OptIn(ExperimentalResourceApi::class)
@Composable
fun InstallScreen(
    onStartInstall: () -> Unit,
    onInstalled: (LoadState) -> Unit
) {
    val context = LocalContext.current
    val sharedPreferences = LocalSharedPreferences.current

    var progress by rememberSaveable { mutableStateOf(0f) }
    val log = rememberSaveable { mutableStateOf("") }
    var started by rememberSaveable { mutableStateOf(false) }

    val tasks = rememberSaveable {
        arrayListOf(
            AdbInstallTask(context, sharedPreferences, log),
            AaptInstallTask(context, sharedPreferences, log),
            PythonInstallTask(context, log),
            LintRuntimesInstallTask(context, sharedPreferences, log)
        )
    }

    val progressAnimated by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween()
    )

    val verticalScrollState = rememberScrollState()
    val verticalScrollAdapter = rememberScrollbarAdapter(verticalScrollState)

    val horizontalScrollState = rememberScrollState()
    val horizontalScrollAdapter = rememberScrollbarAdapter(horizontalScrollState)

    Column(
        modifier = Modifier.fillMaxSize()
            .border(1.dp, DividerDefaults.color),
    ) {
        LinearProgressIndicator(
            progress = { progressAnimated },
            modifier = Modifier.fillMaxWidth().height(8.dp),
        )
        Row(
            modifier = Modifier.fillMaxSize().weight(1f),
        ) {
            SelectionContainer(
                modifier = Modifier.padding(4.dp).weight(1f, fill = false),
            ) {
                Text(
                    text = log.value,
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.verticalScroll(verticalScrollState)
                        .horizontalScroll(horizontalScrollState),
                    fontFamily = FontFamily(Font(Res.font.JetBrainsMono_Regular))
                )
            }
            VerticalScrollbar(verticalScrollAdapter)
        }
        HorizontalScrollbar(horizontalScrollAdapter)
    }

    onStartInstall()
    LaunchedEffect(Unit) {
        snapshotFlow { log.value }
            .onEach {
                verticalScrollState.animateScrollTo(verticalScrollState.maxValue)
            }.launchIn(this)

        if (!started) {
            started = true
            withContext(Dispatchers.IO) {
                delay(500)
                try {
                    tasks.forEach {
                        progress = 0f
                        log.value += "Start ${it.name}\n"
                        if (it.url.isNotEmpty()) {
                            log.value += "Download Source: ${it.url}\n"
                        }
                        it.run { p ->
                            progress = p
                        }
                        progress = 1f
                        delay(1000)
                    }
                    onInstalled(LoadState.Success("installState"))
                    log.value += "\nINSTALL SUCCESSFUL"
                } catch (e: Exception) {
                    e.printStackTrace()
                    log.value += "\nInstallation Failed: ${e.message}\n"
                    onInstalled(LoadState.Failure("installState"))
                }
            }
        }
    }
}