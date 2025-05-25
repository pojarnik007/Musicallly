package com.example.music_player_app.presentation.player

import android.media.MediaPlayer
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
    private var currentTrackIndex = 0
    private var tempAudioFile: File? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentPlayerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel.tracks.observe(viewLifecycleOwner) { tracks ->
            val selected = viewModel.selectedTrack.value
            val startIndex = tracks.indexOfFirst { it.id == selected?.id }.takeIf { it >= 0 } ?: 0
            if (tracks.isNotEmpty()) {
                currentTrackIndex = startIndex
                playTrack(tracks[currentTrackIndex])
            }
        }

        binding.buttonPlayPause.setOnClickListener {
            mediaPlayer?.let {
                if (it.isPlaying) it.pause() else it.start()
            }
        }
        binding.buttonPrev.setOnClickListener {
            val tracks = viewModel.tracks.value ?: return@setOnClickListener
            if (tracks.isNotEmpty()) {
                currentTrackIndex = (currentTrackIndex - 1 + tracks.size) % tracks.size
                playTrack(tracks[currentTrackIndex])
            }
        }
        binding.buttonNext.setOnClickListener {
            val tracks = viewModel.tracks.value ?: return@setOnClickListener
            if (tracks.isNotEmpty()) {
                currentTrackIndex = (currentTrackIndex + 1) % tracks.size
                playTrack(tracks[currentTrackIndex])
            }
        }
    }

    private fun playTrack(track: com.example.music_player_app.domain.model.Track) {
        mediaPlayer?.release()
        tempAudioFile?.delete()
        tempAudioFile = null

        // Загружаем аудиофайл с сервера
        viewModel.downloadAudio(track.id ?: return)
        viewModel.audioData.observe(viewLifecycleOwner) { audioBytes ->
            audioBytes?.let {
                // Сохраняем ByteArray во временный файл
                val tempFile = File.createTempFile("track", ".mp3", requireContext().cacheDir)
                tempFile.writeBytes(it)
                tempAudioFile = tempFile

                mediaPlayer = MediaPlayer().apply {
                    setDataSource(tempFile.absolutePath)
                    prepare()
                    start()
                }
            }
        }
        binding.textTitle.text = track.title
        binding.textArtist.text = track.artist
        // Для картинки можно придумать что-то аналогичное, либо оставить дефолт
        binding.imageCover.setImageResource(R.drawable.music_note)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mediaPlayer?.release()
        tempAudioFile?.delete()
        _binding = null
    }
}