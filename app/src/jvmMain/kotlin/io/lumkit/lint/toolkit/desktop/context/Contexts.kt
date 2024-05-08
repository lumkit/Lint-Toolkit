package io.lumkit.lint.toolkit.desktop.context

import io.github.lumkit.desktop.context.Context
import java.awt.Toolkit
import java.awt.datatransfer.Clipboard
import java.awt.datatransfer.StringSelection

fun Context.getAppVersion(): String = System.getProperty("appVersion")

fun Context.copyTextToClipboard(text: String) {
    val stringSelection = StringSelection(text)
    val clipboard: Clipboard = Toolkit.getDefaultToolkit().systemClipboard
    clipboard.setContents(stringSelection, null)
}