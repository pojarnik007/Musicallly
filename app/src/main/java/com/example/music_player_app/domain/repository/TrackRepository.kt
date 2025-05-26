package com.example.music_player_app.data.repository

import android.content.Context
import com.example.music_player_app.domain.local.AppDatabase
import com.example.music_player_app.domain.model.TrackEntity
import com.example.music_player_app.domain.repository.LocalTrackRepository
import com.example.music_player_app.domain.repository.RemoteTrackRepository
import com.example.music_player_app.domain.repository.saveAudioBytesToFile

class TrackRepository(private val context: Context) {
    private val db = AppDatabase.getInstance(context)
    private val dao = db.trackDao()

    suspend fun downloadAndSaveTrack(
        context: Context,
        remoteRepo: RemoteTrackRepository,
        localRepo: LocalTrackRepository,
        trackId: Int
    ) {
        // 1. Получить трек с сервера
        val track = remoteRepo.getTracks().find { it.id == trackId } ?: return

        // 2. Скачать аудиофайл
        val bytes = remoteRepo.downloadAudio(trackId) ?: return

        // 3. Сохранить файл локально
        val fileName = "track_${trackId}.mp3"
        val localPath = saveAudioBytesToFile(context, bytes, fileName)

        // 4. Сохранить трек с локальным путем
        val localTrack = track.copy(localPath = localPath)
        localRepo.insertTrack(localTrack)
    }

    suspend fun getAllTracks(): List<TrackEntity> = dao.getAll()
}