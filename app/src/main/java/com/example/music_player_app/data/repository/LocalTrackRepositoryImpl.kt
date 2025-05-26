package com.example.music_player_app.data.repository

import com.example.music_player_app.domain.local.TrackDao
import com.example.music_player_app.domain.model.TrackEntity
import com.example.music_player_app.domain.repository.LocalTrackRepository

class LocalTrackRepositoryImpl(private val trackDao: TrackDao) : LocalTrackRepository {
    override suspend fun getAllTracks() = trackDao.getAll()
    override suspend fun insertTrack(track: TrackEntity) = trackDao.insert(track)
    override suspend fun insertTracks(tracks: List<TrackEntity>) = trackDao.insertAll(tracks)
    override suspend fun deleteTrack(trackId: Int) = trackDao.deleteById(trackId)
}