package com.example.music_player_app.data.repository

import android.content.Context
import com.example.music_player_app.domain.model.TrackEntity
import com.example.music_player_app.domain.repository.LocalTrackRepository
import com.example.music_player_app.domain.repository.RemoteTrackRepository
import com.example.music_player_app.domain.repository.TrackSyncRepository
import java.io.File

class TrackSyncRepositoryImpl(
    private val context: Context,
    private val remoteRepo: RemoteTrackRepository,
    private val localRepo: LocalTrackRepository
) : TrackSyncRepository {

    override suspend fun syncTracks() {
        // 1. Получаем список с сервера
        val remoteTracks = remoteRepo.getTracks()
        val remoteIds = remoteTracks.map { it.id }

        // 2. Получаем список локальных треков
        val localTracks = localRepo.getAllTracks()
        val localIds = localTracks.map { it.id }

        // 3. Качаем новые треки
        for (track in remoteTracks) {
            if (track.id !in localIds) {
                val bytes = remoteRepo.downloadAudio(track.id)
                if (bytes != null) {
                    val fileName = "track_${track.id}.mp3"
                    val path = saveAudioBytesToFile(context, bytes, fileName)
                    localRepo.insertTrack(track.copy(localPath = path))
                }
            }
        }

        // 4. Удаляем треки, которых нет на сервере
        val idsToKeep = remoteIds
        localRepo.deleteNotIn(idsToKeep)
    }

    override suspend fun addTrack(track: TrackEntity, audioFile: File) {
        remoteRepo.addTrack(track, audioFile)
        // После добавления на сервер — качаем обратно, чтобы был путь
        val remoteTracks = remoteRepo.getTracks()
        val serverTrack = remoteTracks.find { it.name == track.name && it.artist == track.artist }
        if (serverTrack != null) {
            val bytes = remoteRepo.downloadAudio(serverTrack.id)
            if (bytes != null) {
                val fileName = "track_${serverTrack.id}.mp3"
                val path = saveAudioBytesToFile(context, bytes, fileName)
                localRepo.insertTrack(serverTrack.copy(localPath = path))
            }
        }
    }

    override suspend fun deleteTrack(trackId: Int) {
        remoteRepo.deleteTrack(trackId)
        localRepo.deleteTrack(trackId)
    }
}

fun saveAudioBytesToFile(context: Context, bytes: ByteArray, fileName: String): String {
    val file = File(context.filesDir, fileName)
    file.writeBytes(bytes)
    return file.absolutePath
}