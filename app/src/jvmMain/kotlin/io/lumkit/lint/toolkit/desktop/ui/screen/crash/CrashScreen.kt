package io.lumkit.lint.toolkit.desktop.ui.screen.crash

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.SwingPanel
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyShortcut
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.MenuBar
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.rememberWindowState
import cafe.adriel.voyager.core.screen.Screen
import io.github.lumkit.desktop.context.LocalContextWrapper
import io.github.lumkit.desktop.context.Toast
import io.github.lumkit.desktop.context.Toast.showToast
import io.github.lumkit.desktop.ui.LintWindow
import io.github.lumkit.desktop.ui.theme.AnimatedLintTheme
import io.github.lumkit.desktop.ui.theme.LocalThemeStore
import io.lumkit.lint.toolkit.desktop.context.copyTextToClipboard
import io.github.lumkit.lint.desktop.app.generated.resources.*
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea
import org.fife.ui.rsyntaxtextarea.SyntaxConstants
import org.fife.ui.rsyntaxtextarea.Theme
import org.fife.ui.rtextarea.RTextScrollPane
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import java.awt.BorderLayout
import java.io.IOException
import javax.swing.JPanel
import kotlin.system.exitProcess


class CrashScreen(private val log: String) : Screen {

    @Composable
    override fun Content() {
        CrashContent(log)
    }

}

@OptIn(ExperimentalResourceApi::class)
@Composable
private fun CrashContent(log: String) {
    val windowState = rememberWindowState(
        size = DpSize(
            width = 500.dp,
            height = 700.dp
        ),
        position = WindowPosition(Alignment.Center)
    )
    LintWindow(
        state = windowState,
        onCloseRequest = { exitProcess(0) },
        title = stringResource(Res.string.window_title_crash),
        icon = painterResource(Res.drawable.ic_logo),
        resizable = false
    ) {
        AnimatedLintTheme(
            modifier = Modifier.fillMaxSize()
        ) {
            val context = LocalContextWrapper.current
            val themeStore = LocalThemeStore.current
            val colorScheme = MaterialTheme.colorScheme
            SwingPanel(
                modifier = Modifier.fillMaxSize(),
                background = colorScheme.background,
                factory = {
                    val cp = JPanel(BorderLayout())

                    val textArea = RSyntaxTextArea(20, 60)
                    textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JAVA)
                    textArea.isCodeFoldingEnabled = true
                    textArea.isEnabled = false
                    textArea.text = log

                    try {
                        val theme = Theme.load(
                            javaClass.getResourceAsStream(
                                "/org/fife/ui/rsyntaxtextarea/themes/${
                                    if (themeStore.isDarkTheme) "monokai.xml" else "idea.xml"
                                }"
                            )
                        )
                        theme.apply(textArea)
                    } catch (e: IOException) {
                        // 处理异常
                        e.printStackTrace()
                    }

                    val sp = RTextScrollPane(textArea)
                    cp.add(sp)
                    cp
                },
                update = {

                }
            )

            MenuBar {
                Menu(
                    text = stringResource(Res.string.text_more),
                    mnemonic = 'M'
                ) {
                    val text = stringResource(Res.string.text_copied_log)
                    Item(
                        text = stringResource(Res.string.text_copy_log),
                        mnemonic = 'C',
                        shortcut = KeyShortcut(
                            key = Key.C,
                            ctrl = true,
                            alt = true
                        ),
                        onClick = {
                            context.copyTextToClipboard(log)
                            context.showToast(text, Toast.LENGTH_LONG)
                        }
                    )
                }
            }
        }
    }
}