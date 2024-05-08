package io.lumkit.lint.toolkit.desktop.ui.screen.crash

import cafe.adriel.voyager.navigator.Navigator
import io.github.lumkit.desktop.context.Context
import io.github.lumkit.lint.desktop.app.generated.resources.Res
import io.github.lumkit.lint.desktop.app.generated.resources.text_log_template
import io.lumkit.lint.toolkit.desktop.context.getAppVersion
import kotlinx.coroutines.runBlocking
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.getString
import java.io.File
import java.io.PrintWriter
import java.io.StringWriter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.UUID

object CrashHandler {

    private val DEFAULT_UNCAUGHT_EXCEPTION_HANDLER: Thread.UncaughtExceptionHandler? =
        Thread.getDefaultUncaughtExceptionHandler()

    fun init(context: Context, navigator: Navigator) {
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            try {
                tryUncaughtException(thread, throwable, context, navigator)
            } catch (e: Throwable) {
                e.printStackTrace()
                DEFAULT_UNCAUGHT_EXCEPTION_HANDLER?.uncaughtException(
                    thread,
                    throwable
                )
            }
        }
    }

    @OptIn(ExperimentalResourceApi::class)
    private fun tryUncaughtException(
        thread: Thread,
        throwable: Throwable,
        context: Context,
        navigator: Navigator,
    ) {
        val date = Date()
        val dir = File(context.getFilesDir(), "crash").apply {
            if (!exists()) {
                mkdirs()
            }
        }

        val crashFile = File(dir.absolutePath, "crash-${SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(date)}-${UUID.randomUUID()}.log")
        val appVersion = context.getAppVersion()
        val packageName = context.getPackageName()
        val osName = System.getProperty("os.name")
        val osVersion = System.getProperty("os.version")

        val stringWriter = StringWriter()
        val printWriter = PrintWriter(stringWriter)
        throwable.printStackTrace(printWriter)

        val fullStackTrace = stringWriter.toString()
        printWriter.close()

        throwable.printStackTrace()

        runBlocking {
            try {
                val errorLog = String.format(
                    getString(Res.string.text_log_template),
                    SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date),
                    osName,
                    osVersion,
                    appVersion,
                    packageName,
                    fullStackTrace
                )

                try {
                    if (!crashFile.exists()) {
                        crashFile.createNewFile()
                    }
                    crashFile.writeBytes(errorLog.toByteArray())
                }catch (e: Exception) {
                    e.printStackTrace()
                }

                navigator.replaceAll(CrashScreen(errorLog))
            }catch (e: Exception) {
                e.printStackTrace()
                DEFAULT_UNCAUGHT_EXCEPTION_HANDLER?.uncaughtException(
                    thread,
                    throwable
                )
            }
        }
    }
}