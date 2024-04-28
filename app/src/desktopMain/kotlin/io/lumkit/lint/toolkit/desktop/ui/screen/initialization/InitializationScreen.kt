package io.lumkit.lint.toolkit.desktop.ui.screen.initialization

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.screen.Screen

class InitializationScreen : Screen {

    @Composable
    override fun Content() {
        Initialization()
    }

}

@Composable
private fun Initialization() {
    println("<top>.Initialization")
}