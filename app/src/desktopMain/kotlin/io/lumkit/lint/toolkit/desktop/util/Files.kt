package io.lumkit.lint.toolkit.desktop.util

import io.github.lumkit.desktop.Const
import io.github.lumkit.desktop.context.ContextWrapper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import javax.swing.JFileChooser
import javax.swing.filechooser.FileFilter

suspend fun chooseFile(contextWrapper: ContextWrapper? = null, currentDirKey: String? = null, fileFilter: FileFilter? = null, mode: Int = JFileChooser.FILES_ONLY): File? = withContext(Dispatchers.IO) {
    val sharedPreferences = contextWrapper?.getSharedPreferences(Const.APP_CONFIGURATION)
    val jf: JFileChooser = JFileChooser().apply {
        this.fileFilter = fileFilter
        isAcceptAllFileFilterUsed = false
        currentDirKey?.let {
            sharedPreferences?.getString(it)?.let {
                currentDirectory = File(it)
            }
        }
        fileSelectionMode = mode
    }
    val result = jf.showOpenDialog(contextWrapper?.getWindow())
    if (result == JFileChooser.APPROVE_OPTION) {
        jf.selectedFile?.apply {
            currentDirKey?.let { sharedPreferences?.putString(it, parent) }
        }
    } else {
        null
    }
}

private val units = arrayOf("B", "KB", "MB", "GB", "TB", "PB")

fun convertFileSize(sizeInBytes: Long): String {
    var size = sizeInBytes.toDouble()
    var unitIndex = 0
    while (size >= 1024 && unitIndex < units.size - 1) {
        size /= 1024
        unitIndex++
    }
    return String.format("%.2f %s", size, units[unitIndex])
}