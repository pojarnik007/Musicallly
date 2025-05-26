package com.example.music_player_app.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.music_player_app.domain.model.TrackEntity
import com.example.music_player_app.domain.repository.LocalTrackRepository
import com.example.music_player_app.domain.repository.TrackSyncRepository
import kotlinx.coroutines.launch

class TrackViewModel(
    private val localRepo: LocalTrackRepository,
    private val syncRepo: TrackSyncRepository
) : ViewModel() {

    private val _tracks = MutableLiveData<List<TrackEntity>>()
    val tracks: LiveData<List<TrackEntity>> = _tracks

    private val _selectedTrack = MutableLiveData<TrackEntity?>()
    val selectedTrack: LiveData<TrackEntity?> = _selectedTrack

    fun loadTracks() {
        viewModelScope.launch {
            _tracks.value = localRepo.getAllTracks()
        }
    }

    fun syncTracks() {
        viewModelScope.launch {
            syncRepo.syncTracks()
            loadTracks()
        }
    }

    fun addTrack(track: TrackEntity) {
        viewModelScope.launch {
            localRepo.insertTrack(track)
            loadTracks()
        }
    }

    fun deleteTrack(trackId: Int) {
        viewModelScope.launch {
            localRepo.deleteTrack(trackId)
            loadTracks()
        }
    }

    fun selectTrack(track: TrackEntity) {
        _selectedTrack.value = track
    }
}