package com.example.music_player_app.data.repository

import com.example.music_player_app.domain.local.TrackDao
import com.example.music_player_app.domain.model.TrackEntity
import com.example.music_player_app.domain.repository.RemoteTrackRepository
import com.example.music_player_app.domain.repository.TrackSyncRepository

class TrackSyncRepositoryImpl(
    private val remoteRepo: RemoteTrackRepository,
    private val trackDao: TrackDao
) : TrackSyncRepository {

    override suspend fun syncTracks() {
        val serverTracks = remoteRepo.getTracks()
        val serverIds = serverTracks.map { it.id }
        trackDao.insertAll(serverTracks)
        if (serverIds.isNotEmpty()) {
            trackDao.deleteNotIn(serverIds)
        }
    }

    override suspend fun addTrackRemote(track: TrackEntity) {
        remoteRepo.addTrack(track)
    }

    override suspend fun deleteTrackRemote(trackId: Int) {
        remoteRepo.deleteTrack(trackId)
    }
}