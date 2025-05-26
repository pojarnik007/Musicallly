package com.example.music_player_app.domain.repository

import android.content.Context
import java.io.File
import java.io.FileOutputStream
import java.net.URL

fun saveAudioBytesToFile(context: Context, bytes: ByteArray, fileName: String): String {
    val file = File(context.filesDir, fileName)
    file.writeBytes(bytes)
    return file.absolutePath
}