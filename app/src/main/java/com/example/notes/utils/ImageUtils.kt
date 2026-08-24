package com.example.notes.utils

import android.content.Context
import android.net.Uri
import java.io.File
import java.security.MessageDigest

fun getOrSaveImage(context: Context, uri: Uri): String? {
    return try {
        val inputStream = context.contentResolver.openInputStream(uri) ?: return null
        val bytes = inputStream.readBytes()
        inputStream.close()

        val digest = MessageDigest.getInstance("SHA-256")
        val hashBytes = digest.digest(bytes)
        val hashString = hashBytes.joinToString("") { "%02x".format(it) }

        val fileName = "img_$hashString.jpg"
        val file = File(context.filesDir, fileName)

        if (!file.exists()) {
            file.writeBytes(bytes)
        }

        file.absolutePath
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}