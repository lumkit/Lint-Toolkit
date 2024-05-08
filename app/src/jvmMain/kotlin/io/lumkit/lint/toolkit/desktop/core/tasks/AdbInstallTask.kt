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
import io.lumkit.lint.toolkit.desktop.model.LintInstallTask
import io.lumkit.lint.toolkit.desktop.net.ktor.ktorClient
import io.lumkit.lint.toolkit.desktop.util.ZipUtils
import java.io.File

class AdbInstallTask(
    private val context: Context,
    private val sharedPreferences: SharedPreferences,
    private val logState: MutableState<String>
) : LintInstallTask(
    name = "ADB Installation Task",
    url = "https://dl.google.com/android/repository/platform-tools-latest-windows.zip",
) {
    override suspend fun run(onProgressChanged: (Float) -> Unit) {
        ktorClient.prepareGet(url).execute {
            val dir = File(context.getFilesDir(), Paths runtime Const.NAME_ADB)
            if (!dir.exists()) {
                dir.mkdirs()
            }

            val cancelFile = File(dir, "adb.zip")
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

            logState.value += "Unzipping...\n"
            onProgressChanged(0f)
            ZipUtils.unzip(cancelFile.absolutePath, dir.absolutePath) { p -> onProgressChanged(p) }
            sharedPreferences.put(Const.RUNTIME_ADB_PATH, File(dir, "platform-tools").absolutePath)
            cancelFile.delete()
            onProgressChanged(1f)
            logState.value += "ADB Runtime installed.\n\n"
        }
    }
}