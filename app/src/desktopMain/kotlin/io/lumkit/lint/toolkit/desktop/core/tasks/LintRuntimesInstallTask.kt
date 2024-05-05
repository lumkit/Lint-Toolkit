package io.lumkit.lint.toolkit.desktop.core.tasks

import androidx.compose.runtime.MutableState
import io.github.lumkit.desktop.context.Context
import io.github.lumkit.desktop.preferences.SharedPreferences
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.utils.io.*
import io.ktor.utils.io.core.*
import io.lumkit.lint.toolkit.desktop.core.Const
import io.lumkit.lint.toolkit.desktop.core.Paths
import io.lumkit.lint.toolkit.desktop.core.shell.CommandExecutor
import io.lumkit.lint.toolkit.desktop.model.LintInstallTask
import io.lumkit.lint.toolkit.desktop.net.ktor.ktorClient
import io.lumkit.lint.toolkit.desktop.util.ZipUtils
import java.io.File

class LintRuntimesInstallTask(
    private val context: Context,
    private val sharedPreferences: SharedPreferences,
    private val logState: MutableState<String>
) : LintInstallTask(
    name = "Lint Runtimes Installation Task",
    url = "",
) {
    override suspend fun run(onProgressChanged: (Float) -> Unit) {

        logState.value += "Downloading Payload Dumper Scripts: https://gitee.com/lumyuan/Lint-Toolkit-Repository/releases/download/1.0.0/payload_dumper.zip\n"

        // payload dumper url
        ktorClient.prepareGet("https://gitee.com/lumyuan/Lint-Toolkit-Repository/releases/download/1.0.0/payload_dumper.zip").execute {
            val dir = File(context.getFilesDir(), Paths runtime Const.NAME_PAYLOAD_DUMPER)
            if (!dir.exists()) {
                dir.mkdirs()
            }

            val cancelFile = File(dir, "${Const.NAME_PAYLOAD_DUMPER}.zip")
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

            logState.value += "Zipping...\n"
            ZipUtils.unzip(cancelFile.absolutePath, dir.absolutePath) { p -> onProgressChanged(p) }
            cancelFile.delete()
            logState.value += "Payload Dumper Scripts zip finished.\n\n"
            sharedPreferences.putString(Const.RUNTIME_PAYLOAD_DUMPER, dir.absolutePath)

            val result = CommandExecutor.executeCommandWithWorkingDirectory(
                arrayListOf(
                    "python",
                    "-m",
                    "pip",
                    "install",
                    "-r",
                    "requirements.txt"
                ), dir.absolutePath
            )
            logState.value += result
        }


    }
}