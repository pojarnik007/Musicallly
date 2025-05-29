package com.example.music_player_app.presentation.player

import android.app.Application
import android.media.MediaPlayer
import androidx.lifecycle.*
import com.example.music_player_app.domain.model.TrackEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

class PlayerViewModel(app: Application) : AndroidViewModel(app) {
    private var mediaPlayer: MediaPlayer? = null

    private val _currentTrack = MutableLiveData<TrackEntity?>()
    val currentTrack: LiveData<TrackEntity?> = _currentTrack

    private val _isPlaying = MutableLiveData(false)
    val isPlaying: LiveData<Boolean> = _isPlaying

    private val _position = MutableLiveData(0)
    val position: LiveData<Int> = _position

    private val _duration = MutableLiveData(0)
    val duration: LiveData<Int> = _duration

    private var playQueue: List<TrackEntity> = emptyList()
    private var queueIndex: Int = -1

    private val updateSeekRunnable = object : Runnable {
        override fun run() {
            mediaPlayer?.let {
                _position.postValue(it.currentPosition)
                _duration.postValue(it.duration)
                if (it.isPlaying) {
                    itHandler.postDelayed(this, 500)
                }
            }
        }
    }
    private val itHandler = android.os.Handler(android.os.Looper.getMainLooper())

    fun playQueue(list: List<TrackEntity>, startIdx: Int = 0) {
        playQueue = list
        queueIndex = startIdx
        playAtIndex(queueIndex)
    }


    fun playTrack(track: TrackEntity, queue: List<TrackEntity>? = null) {
        if (queue != null) {
            playQueue = queue
            queueIndex = queue.indexOfFirst { it.id == track.id }
        } else if (playQueue.isEmpty()) {
            playQueue = listOf(track)
            queueIndex = 0
        } else {
            queueIndex = playQueue.indexOfFirst { it.id == track.id }
        }
        playAtIndex(queueIndex)
    }

    private fun playAtIndex(idx: Int) {
        if (playQueue.isEmpty() || idx !in playQueue.indices) return
        val track = playQueue[idx]
        _currentTrack.value = track
        queueIndex = idx
        release()
        val path = track.localPath
        if (path.isNullOrBlank() || !File(path).exists()) return
        mediaPlayer = MediaPlayer().apply {
            setDataSource(path)
            setOnPreparedListener {
                start()
                _isPlaying.postValue(true)
                _duration.postValue(duration)
                itHandler.post(updateSeekRunnable)
            }
            setOnCompletionListener {
                next()
            }
            prepareAsync()
        }
    }


    fun togglePlayPause() {
        mediaPlayer?.let {
            if (it.isPlaying) {
                it.pause()
                _isPlaying.value = false
            } else {
                it.start()
                _isPlaying.value = true
                itHandler.post(updateSeekRunnable)
            }
        }
    }

    fun seekTo(pos: Int) {
        mediaPlayer?.seekTo(pos)
    }

    fun prev() {
        if (playQueue.isEmpty()) return
        queueIndex = if ((queueIndex - 1) < 0) playQueue.size - 1 else queueIndex - 1
        playAtIndex(queueIndex)
    }

    fun next() {
        if (playQueue.isEmpty()) return
        queueIndex = (queueIndex + 1) % playQueue.size
        playAtIndex(queueIndex)
    }

    fun isMiniPlayerVisible(): Boolean = _currentTrack.value != null

    fun getPlayQueue(): List<TrackEntity> = playQueue
    fun getCurrentIndex(): Int = queueIndex

    private fun release() {
        _isPlaying.postValue(false)
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
        itHandler.removeCallbacks(updateSeekRunnable)
    }

    override fun onCleared() {
        super.onCleared()
        release()
    }
}