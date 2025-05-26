package com.example.music_player_app.presentation.player

import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.music_player_app.R
import com.example.music_player_app.databinding.FragmentPlayerBinding
import com.example.music_player_app.presentation.TrackViewModel
import java.io.File

class PlayerFragment : Fragment() {
    private var _binding: FragmentPlayerBinding? = null
    private val binding get() = _binding!!
    private val viewModel: TrackViewModel by activityViewModels()
    private var mediaPlayer: MediaPlayer? = null
    private val handler = Handler(Looper.getMainLooper())
    private val updateSeekRunnable = object : Runnable {
        override fun run() {
            mediaPlayer?.let {
                binding.seekBar.max = it.duration
                binding.seekBar.progress = it.currentPosition
                binding.textCurrentTime.text = formatMillis(it.currentPosition)
                binding.textTotalTime.text = formatMillis(it.duration)
                handler.postDelayed(this, 500)
            }
        }
    }
    private fun formatMillis(ms: Int): String {
        val minutes = ms / 1000 / 60
        val seconds = (ms / 1000) % 60
        return String.format("%d:%02d", minutes, seconds)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentPlayerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // Следим только за currentTrackIndex
        viewModel.currentTrackIndex.observe(viewLifecycleOwner) { idx ->
            val queue = viewModel.playQueue.value ?: return@observe
            if (queue.isNotEmpty() && idx != null && idx in queue.indices) {
                playTrack(queue[idx])
            }
        }

        // В onViewCreated:
        binding.buttonPlayPause.setOnClickListener {
            mediaPlayer?.let {
                if (it.isPlaying) {
                    it.pause()
                    binding.buttonPlayPause.setImageResource(R.drawable.ic_play) // иконка play
                } else {
                    it.start()
                    binding.buttonPlayPause.setImageResource(R.drawable.ic_pause) // иконка pause
                }
            }
        }
        binding.buttonPrev.setOnClickListener {
            viewModel.prevTrack()
        }
        binding.buttonNext.setOnClickListener {
            viewModel.nextTrack()
        }
        binding.seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    mediaPlayer?.seekTo(progress)
                }
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }

    private fun playTrack(track: com.example.music_player_app.domain.model.TrackEntity) {
        handler.post(updateSeekRunnable)
        mediaPlayer?.release()
        val path = track.localPath
        if (path.isNullOrBlank() || !File(path).exists()) {
            // Можно показать ошибку
            return
        }
        try {
            mediaPlayer = MediaPlayer().apply {
                setDataSource(path)
                setOnPreparedListener {
                    start()
                    binding.buttonPlayPause.setImageResource(R.drawable.ic_pause)
                }
                setOnCompletionListener {
                    binding.buttonPlayPause.setImageResource(R.drawable.ic_play)
                }
                prepareAsync()
            }
            binding.textTitle.text = track.name
            binding.textArtist.text = track.artist

            binding.imageCover.setImageResource(R.drawable.music_note)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    override fun onDestroyView() {
        handler.removeCallbacks(updateSeekRunnable)
        super.onDestroyView()
        mediaPlayer?.release()
        _binding = null
    }
}