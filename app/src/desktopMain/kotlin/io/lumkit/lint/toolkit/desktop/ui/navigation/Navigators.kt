package io.lumkit.lint.toolkit.desktop.ui.navigation

import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.Navigator

fun Navigator.pushSingle(screen: Screen) {
    if (lastItem == screen) return
    push(screen)
}