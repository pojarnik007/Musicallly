package com.example.music_player_app.data.repository

import com.example.music_player_app.domain.model.Track
import com.example.music_player_app.domain.repository.RemoteTrackRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.*
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class RemoteTrackRepositoryImpl(
    private val baseUrl: String = "http://192.168.0.102:3000"
) : RemoteTrackRepository {

    private val client = OkHttpClient()
    private val json = Json { ignoreUnknownKeys = true }

    override suspend fun getTracks(): List<Track> = withContext(Dispatchers.IO) {
        val request = Request.Builder()
            .url("$baseUrl/tracks")
            .build()
        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) return@use emptyList()
            val body = response.body?.string() ?: return@use emptyList()
            return@use json.decodeFromString(body)
        }
    }

    // Теперь addTrack принимает не только Track, но и файл аудио
    override suspend fun addTrack(track: Track, audioFile: File) = withContext(Dispatchers.IO) {
        val requestBodyBuilder = MultipartBody.Builder().setType(MultipartBody.FORM)
            .addFormDataPart("name", track.title)
            .addFormDataPart("artist", track.artist)
            .addFormDataPart("duration", track.duration.toString())
            .addFormDataPart(
                "audio",
                audioFile.name,
                audioFile.asRequestBody("audio/mpeg".toMediaTypeOrNull())
            )
        val requestBody = requestBodyBuilder.build()
        val request = Request.Builder()
            .url("$baseUrl/tracks")
            .post(requestBody)
            .build()
        client.newCall(request).execute().close()
    }

    override suspend fun deleteTrack(trackId: String) = withContext(Dispatchers.IO) {
        val request = Request.Builder()
            .url("$baseUrl/tracks/$trackId")
            .delete()
            .build()
        client.newCall(request).execute().close()
    }

    // Метод для получения аудиофайла по id трека
    override suspend fun downloadAudio(trackId: Int): ByteArray? = withContext(Dispatchers.IO) {
        val request = Request.Builder()
            .url("$baseUrl/tracks/$trackId/audio")
            .build()
        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) return@use null
            return@use response.body?.bytes()
        }
    }
}