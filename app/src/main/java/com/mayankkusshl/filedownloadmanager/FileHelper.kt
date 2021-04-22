package com.mayankkusshl.filedownloadmanager

import okhttp3.Response
import java.io.*
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream


object FileHelper {

    fun saveFile(response: Response, fileName: String, dir: File): File {
        var input: InputStream? = null
        try {
            input = response.body()?.byteStream()
            val file = File(dir, fileName)
            if (!file.exists()) {
                file.createNewFile();
            }
            val bufferedInputStream = BufferedInputStream(input)
            val output: OutputStream = FileOutputStream(file)

            val data = ByteArray(1024)
            var count: Int = 0
            while (bufferedInputStream.read(data).also {
                        count = it
                    } != -1) {
                output.write(data, 0, count)
            }

            output.flush()
            output.close()
            bufferedInputStream.close()
            return file
        } catch (ignore: IOException) {
            throw ignore
        } catch (e: Exception) {
            throw e
        } finally {
            input?.close()
        }
    }

    fun unzip(sourceFile: String?, destinationFolder: String): Boolean {
        if (sourceFile == null) return false
        val BUFFER_SIZE = 8192
        var zipInputStream: ZipInputStream? = null
        try {
            zipInputStream = ZipInputStream(BufferedInputStream(FileInputStream(sourceFile)))
            var zipEntry: ZipEntry?
            var count: Int
            val buffer = ByteArray(BUFFER_SIZE)
            while (zipInputStream.nextEntry.also { zipEntry = it } != null) {
                if (zipEntry != null) {
                    var fileName: String = zipEntry!!.name
                    fileName = fileName.substring(fileName.indexOf("/") + 1)
                    val file = File(destinationFolder, fileName)
                    val dir = if (zipEntry!!.isDirectory) file else file.parentFile
                    if (!dir.isDirectory && !dir.mkdirs()) throw FileNotFoundException("Invalid path: " + dir.absolutePath)
                    if (zipEntry!!.isDirectory) continue
                    val fileOutputStream = FileOutputStream(file)
                    try {
                        while (zipInputStream.read(buffer).also { count = it } != -1) fileOutputStream.write(buffer, 0, count)
                    } finally {
                        fileOutputStream.close()
                    }
                }

            }
        } catch (ioe: IOException) {
            return false
        } finally {
            zipInputStream?.close()
        }
        return true
    }
}