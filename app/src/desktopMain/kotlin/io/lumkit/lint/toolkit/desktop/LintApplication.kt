package io.lumkit.lint.toolkit.desktop

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.window.ApplicationScope
import cafe.adriel.voyager.navigator.Navigator
import io.github.lumkit.desktop.lintApplication
import io.lumkit.lint.toolkit.desktop.ui.screen.splash.SplashScreen

val LocalApplication = compositionLocalOf<ApplicationScope> { error("not provided") }

fun main() = lintApplication(
    packageName = System.getProperty("appPackageName")
) {
    CompositionLocalProvider(
        LocalApplication provides this
    ) {
        Navigator(SplashScreen())
    }
}