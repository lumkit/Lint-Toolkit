package io.lumkit.lint.toolkit.desktop.core.tasks

import androidx.compose.runtime.MutableState
import io.github.lumkit.desktop.context.Context
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.utils.io.*
import io.ktor.utils.io.core.*
import io.lumkit.lint.toolkit.desktop.core.Paths
import io.lumkit.lint.toolkit.desktop.core.shell.CommandExecutor
import io.lumkit.lint.toolkit.desktop.model.LintInstallTask
import io.lumkit.lint.toolkit.desktop.net.ktor.ktorClient
import java.io.File

class PythonInstallTask(
    private val context: Context,
    private val logState: MutableState<String>
) : LintInstallTask(
    name = "Python Installation Task",
    url = "https://gitee.com/lumyuan/Lint-Toolkit-Repository/releases/download/1.0.0/python-3.12.3-amd64.exe",
) {
    override suspend fun run(onProgressChanged: (Float) -> Unit) {
        val isInstalled = CommandExecutor.executeCommand(arrayListOf("python", "--version")).startsWith("Python")
        if (isInstalled) {
            logState.value += "Skip Python installation task: Python runtime is already installed on the device.\n\n"
            return
        }

        ktorClient.prepareGet(url).execute {
            val dir = File(context.getFilesDir(), Paths runtime "python-3.12.3-amd64")
            if (!dir.exists()) {
                dir.mkdirs()
            }

            val cancelFile = File(dir, "python-3.12.3-amd64.temp")
            if (cancelFile.exists()) {
                cancelFile.delete()
            }
            cancelFile.createNewFile()

            val channel: ByteReadChannel = it.body()
            while (!channel.isClosedForRead) {
                val packet = channel.readRemaining(DEFAULT_BUFFER_SIZE.toLong())
                while (!packet.isEmpty) {
                    val bytes = packet.readBytes()
                    cancelFile.appendBytes(bytes)
                    it.contentLength()?.let { length ->
                        onProgressChanged(cancelFile.length().toFloat() / length.toFloat())
                    }
                }
            }

            logState.value += "Installing...\n"
            val appFile = File(dir, "python-3.12.3-amd64.exe")
            cancelFile.renameTo(appFile)

            val command = """
                Start-Process -FilePath "${appFile.absolutePath}" -ArgumentList '/quiet PrependPath=1' -Wait;
                Write-Output '1';
            """.trimIndent()

            val result = CommandExecutor.executeCommandWithWorkingDirectory(
                arrayListOf("powershell.exe", "-Command", command),
                dir.absolutePath
            ).trim()

            if (result != "1") {
                throw RuntimeException("${appFile.name} installation failed.")
            }
            appFile.delete()
            logState.value += "Python Runtime installed.\n\n"
        }
    }
}