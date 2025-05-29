package com.example.music_player_app.presentation

import android.util.Log
import androidx.lifecycle.*
import com.example.music_player_app.domain.model.TrackEntity
import com.example.music_player_app.domain.repository.LocalTrackRepository
import com.example.music_player_app.domain.repository.TrackSyncRepository
import kotlinx.coroutines.launch
import java.io.File

class TrackViewModel(
    private val localRepo: LocalTrackRepository,
    private val syncRepo: TrackSyncRepository
) : ViewModel() {

    private val _tracks = MutableLiveData<List<TrackEntity>>()
    val tracks: LiveData<List<TrackEntity>> = _tracks

    private val _selectedTrack = MutableLiveData<TrackEntity?>()
    val selectedTrack: LiveData<TrackEntity?> = _selectedTrack

    private val _playQueue = MutableLiveData<List<TrackEntity>>() // очередь для Player
    val playQueue: LiveData<List<TrackEntity>> = _playQueue

    private val _currentTrackIndex = MutableLiveData<Int?>()
    val currentTrackIndex: LiveData<Int?> = _currentTrackIndex

    fun syncTracks() {
        viewModelScope.launch {
            syncRepo.syncTracks()
            loadTracks()
        }
    }

    fun loadTracks() {
        viewModelScope.launch {
            val result = localRepo.getAllTracks()
            Log.d("TrackViewModel", "loadTracks: $result")
            _tracks.value = result
        }
    }

    fun addTrack(track: TrackEntity, audioFile: File) {
        viewModelScope.launch {
            syncRepo.addTrack(track, audioFile)
            loadTracks()
        }
    }

    fun deleteTrack(trackId: Int) {
        viewModelScope.launch {
            syncRepo.deleteTrack(trackId)
            loadTracks()
        }
    }

    fun selectTrack(track: TrackEntity) {
        _selectedTrack.value = track
        // Формируем очередь: сначала выбранный трек, потом остальные (по кругу)
        val all = _tracks.value ?: return
        val startIdx = all.indexOfFirst { it.id == track.id }
        if (startIdx != -1) {
            val reordered = all.drop(startIdx) + all.take(startIdx)
            _playQueue.value = reordered
            _currentTrackIndex.value = 0
        }
    }

    // Для кнопок prev/next в PlayerFragment
    fun nextTrack() {
        val queue = _playQueue.value ?: return
        if (queue.isEmpty()) return
        val idx = ((_currentTrackIndex.value ?: 0) + 1) % queue.size
        _currentTrackIndex.value = idx
    }

    fun prevTrack() {
        val queue = _playQueue.value ?: return
        if (queue.isEmpty()) return
        val idx = ((_currentTrackIndex.value ?: 0) - 1 + queue.size) % queue.size
        _currentTrackIndex.value = idx
    }

    // Можно добавить явный выбор индекса (например, если обновилась очередь)
    fun setCurrentTrackIndex(idx: Int) {
        _currentTrackIndex.value = idx
    }
}