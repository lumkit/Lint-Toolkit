package io.lumkit.lint.toolkit.desktop.context

import io.github.lumkit.desktop.context.Context

fun Context.getAppVersion(): String = System.getProperty("appVersion")