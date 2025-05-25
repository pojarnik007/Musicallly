package com.example.music_player_app.presentation.alltracks

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.music_player_app.databinding.FragmentAllTracksBinding
import com.example.music_player_app.di.ServiceLocator
import com.example.music_player_app.domain.model.Track
import com.example.music_player_app.presentation.TrackViewModel
import java.io.File
import java.io.FileOutputStream

@Suppress("DEPRECATION")
class AllTracksFragment : Fragment() {
    private val viewModel: TrackViewModel by activityViewModels {
        ServiceLocator.provideTrackViewModelFactory()
    }
    private var _binding: FragmentAllTracksBinding? = null
    private val binding get() = _binding!!
    val adapter = com.example.music_player_app.presentation.TrackAdapter(
        onDelete = { trackId -> viewModel.deleteTrack(trackId) },
        onTrackClick = { track -> viewModel.selectTrack(track) }
    )

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentAllTracksBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter

        binding.buttonAddTrack.setOnClickListener {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            intent.type = "audio/*"
            startActivityForResult(intent, REQUEST_CODE_PICK_AUDIO)
        }

        viewModel.tracks.observe(viewLifecycleOwner) { tracks ->
            adapter.submitList(tracks, viewModel.selectedTrack.value?.id)
        }
        viewModel.selectedTrack.observe(viewLifecycleOwner) {
            adapter.submitList(viewModel.tracks.value ?: emptyList(), it?.id)
        }
        viewModel.loadTracks()
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_PICK_AUDIO && resultCode == Activity.RESULT_OK) {
            data?.data?.let { uri ->
                val (track, file) = getTrackAndFileFromUri(uri)
                // Теперь передаем и трек, и файл!
                viewModel.addTrack(track, file)
            }
        }
    }

    private fun getTrackAndFileFromUri(uri: Uri): Pair<Track, File> {
        var title = "Unknown"
        val context = requireContext()
        context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (cursor.moveToFirst() && nameIndex != -1) {
                title = cursor.getString(nameIndex)
            }
        }
        // Можно также извлечь duration, artist и т.д. через MediaMetadataRetriever
        // Для примера artist и duration дефолтные, можешь добавить извлечение если нужно

        // Сохраняем выбранный файл во временный
        val inputStream = context.contentResolver.openInputStream(uri)!!
        val tempFile = File.createTempFile("audio", null, context.cacheDir)
        FileOutputStream(tempFile).use { output ->
            inputStream.copyTo(output)
        }

        val track = Track(
            id = 0,
            title = title,
            artist = "Unknown",
            duration = 0
        )
        return Pair(track, tempFile)
    }

    companion object {
        private const val REQUEST_CODE_PICK_AUDIO = 111
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}