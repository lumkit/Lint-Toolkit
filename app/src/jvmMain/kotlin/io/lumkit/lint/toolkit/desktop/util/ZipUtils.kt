package io.lumkit.lint.toolkit.desktop.util

import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream

object ZipUtils {

    fun unzip(zipFilePath: String, destDirectory: String, progressCallback: (Float) -> Unit) {
        val destDir = File(destDirectory)
        if (!destDir.exists()) {
            destDir.mkdirs()
        }

        FileInputStream(zipFilePath).use { fis ->
            ZipInputStream(fis).use { zis ->
                val totalSize = File(zipFilePath).length().toFloat()
                var processedSize = 0L
                var zipEntry: ZipEntry? = zis.nextEntry
                while (zipEntry != null) {
                    val newFile = File(destDir, zipEntry.name)
                    if (zipEntry.isDirectory) {
                        newFile.mkdirs()
                    } else {
                        newFile.parentFile.mkdirs()
                        FileOutputStream(newFile).use { fos ->
                            zis.copyTo(fos)
                        }
                    }
                    // 更新已处理的文件大小并计算进度
                    processedSize += zipEntry.compressedSize
                    val progress = processedSize / totalSize
                    progressCallback(progress)

                    zis.closeEntry()
                    zipEntry = zis.nextEntry
                }
                // 完成后确保回调接收到 1f
                progressCallback(1f)
            }
        }
    }
}