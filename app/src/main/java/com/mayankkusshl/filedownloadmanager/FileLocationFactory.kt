package com.mayankkusshl.filedownloadmanager

import android.content.Context
import android.os.Environment
import java.io.File

object FileLocationFactory {

    lateinit var zips: String
    lateinit var zipsExtracted: String
    lateinit var savedImages: String

    fun initDirectories(context: Context){
        zips = context.cacheDir.absolutePath + "/zips"
        zipsExtracted = context.cacheDir.absolutePath + "/zips/extracted"
        savedImages = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)?.absolutePath + "/savedImages"
    }

    fun getImageDirectory(): String {
        return savedImages
    }

    fun getStorageDirectory(fileLocation: String): File {
        val dir = File(fileLocation)
        if (!dir.exists())
            dir.mkdirs()
        return dir
    }
}
