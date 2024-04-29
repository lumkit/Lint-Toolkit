package io.lumkit.lint.toolkit.desktop.ui.screen.initialization

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.screen.Screen
import io.github.lumkit.desktop.preferences.LocalSharedPreferences
import io.github.lumkit.desktop.ui.LintWindow
import io.github.lumkit.desktop.ui.theme.AnimatedLintTheme
import linttoolkit.app.generated.resources.Res
import linttoolkit.app.generated.resources.app_name
import linttoolkit.app.generated.resources.ic_logo
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import java.awt.Dimension
import kotlin.system.exitProcess

class InitializationScreen : Screen {

    @Composable   
    override fun Content() {
        Initialization()
    }

}

@OptIn(ExperimentalResourceApi::class)
@Composable
private fun Initialization() {
    LintWindow(
        onCloseRequest = { exitProcess(0) },
        rememberSize = true,
        title = stringResource(Res.string.app_name),
        icon = painterResource(Res.drawable.ic_logo)
    ) {
        window.minimumSize = Dimension(800, 600)
        AnimatedLintTheme(
            modifier = Modifier.fillMaxSize()
        ) {
            Column {
                val sharedPreferences = LocalSharedPreferences.current

                sharedPreferences.toMap().forEach { (t, u) ->
                    Text("t: $t    u: $u")
                }
            }
        }
    }
}