package io.lumkit.lint.toolkit.desktop.core.shell

object CommandExecutor {
    fun executeCommand(command: List<String>): String {
        val processBuilder = ProcessBuilder(command)
        val process = processBuilder.start()
        process.inputStream.bufferedReader().use { reader ->
            return reader.readText()
        }
    }

    fun executeCommandWithWorkingDirectory(command: List<String>, workingDirectory: String): String {
        val processBuilder = ProcessBuilder(command)
        processBuilder.directory(java.io.File(workingDirectory))
        val process = processBuilder.start()
        process.inputStream.bufferedReader().use { reader ->
            return reader.readText()
        }
    }
}