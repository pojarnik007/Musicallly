package com.example.music_player_app.data.repository

import com.example.music_player_app.domain.model.TrackEntity
import com.example.music_player_app.domain.repository.RemoteTrackRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.*
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

class RemoteTrackRepositoryImpl(
    private val baseUrl: String = "http://192.168.213.211:3000"
) : RemoteTrackRepository {

    private val client = OkHttpClient()
    private val json = Json { ignoreUnknownKeys = true }

    override suspend fun getTracks(): List<TrackEntity> = withContext(Dispatchers.IO) {
        val request = Request.Builder()
            .url("$baseUrl/tracks")
            .build()
        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) return@use emptyList()
            val body = response.body?.string() ?: return@use emptyList()
            return@use json.decodeFromString<List<TrackEntity>>(body)
        }
    }

    override suspend fun addTrack(track: TrackEntity, audioFile: File?) = withContext(Dispatchers.IO) {
        val builder = MultipartBody.Builder().setType(MultipartBody.FORM)
            .addFormDataPart("name", track.name)
            .addFormDataPart("artist", track.artist)
            .addFormDataPart("duration", track.duration.toString())
        if (audioFile != null) {
            builder.addFormDataPart(
                "audio",
                audioFile.name,
                audioFile.asRequestBody("audio/mpeg".toMediaTypeOrNull())
            )
        }
        val requestBody = builder.build()
        val request = Request.Builder()
            .url("$baseUrl/tracks")
            .post(requestBody)
            .build()
        client.newCall(request).execute().close()
    }

    override suspend fun deleteTrack(trackId: Int) = withContext(Dispatchers.IO) {
        val request = Request.Builder()
            .url("$baseUrl/tracks/$trackId")
            .delete()
            .build()
        client.newCall(request).execute().close()
    }

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