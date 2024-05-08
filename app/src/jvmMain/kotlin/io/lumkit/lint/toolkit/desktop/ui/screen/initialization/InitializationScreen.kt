package io.lumkit.lint.toolkit.desktop.ui.screen.initialization

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import io.lumkit.lint.toolkit.desktop.ui.screen.main.MainScreen

class InitializationScreen : Screen {

    @Composable   
    override fun Content() {
        Initialization()
    }

}

@Composable
private fun Initialization() {
    val navigator = LocalNavigator.currentOrThrow
    navigator.push(MainScreen())
}