package com.jediterm.terminal.ui

fun isWindows(): Boolean {
  return System.getProperty("os.name").lowercase().startsWith("windows")
}

fun isMacOS(): Boolean {
  return System.getProperty("os.name").lowercase().startsWith("mac")
}
