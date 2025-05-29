package com.example.music_player_app.presentation.alltracks

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.OpenableColumns
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.music_player_app.databinding.FragmentAllTracksBinding
import com.example.music_player_app.di.ServiceLocator
import com.example.music_player_app.domain.model.TrackEntity
import com.example.music_player_app.presentation.TrackAdapter
import com.example.music_player_app.presentation.TrackViewModel
import com.example.music_player_app.presentation.player.PlayerViewModel
import java.io.File
import java.io.FileOutputStream

class AllTracksFragment : Fragment() {

    private val viewModel: TrackViewModel by activityViewModels {
        ServiceLocator.provideTrackViewModelFactory(requireContext())
    }
    private val playerViewModel: PlayerViewModel by activityViewModels()
    private var _binding: FragmentAllTracksBinding? = null
    private val binding get() = _binding!!
    private lateinit var pickAudioLauncher: ActivityResultLauncher<Intent>

    private lateinit var adapter: TrackAdapter
    private var isAdmin: Boolean = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentAllTracksBinding.inflate(inflater, container, false)
        return binding.root
    }
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // ...
        val prefs = requireActivity().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        isAdmin = prefs.getBoolean("is_admin", false)
        // 1. Всегда делай синхронизацию (или loadTracks())
        viewModel.syncTracks()
// например, если высота миниплеера 64dp + 8dp*2 = 80dp
        val paddingPx = (80 * resources.displayMetrics.density).toInt()
        binding.recyclerView.setPadding(
            binding.recyclerView.paddingLeft,
            binding.recyclerView.paddingTop,
            binding.recyclerView.paddingRight,
            paddingPx
        )
        // 2. Адаптер
        adapter = TrackAdapter(
            onDelete = { trackId -> viewModel.deleteTrack(trackId) },
            onTrackClick = { trackEntity ->
                val queue = viewModel.tracks.value ?: emptyList()
                val idx = queue.indexOfFirst { it.id == trackEntity.id }
                if (idx != -1) {
                    playerViewModel.playQueue(queue, idx)
                }
            },
            isAdmin = isAdmin
        )
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter


        pickAudioLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val uri = result.data?.data
                if (uri != null) {
                    val (track, file) = getTrackEntityAndFileFromUri(uri)
                    viewModel.addTrack(track, file)
                }
            }
        }

        // 3. Подписка на треки
        viewModel.tracks.observe(viewLifecycleOwner) { tracks ->
            Log.d("AllTracksFragment", "tracks.size = ${tracks.size}")
            val selectedId = playerViewModel.currentTrack.value?.id
            adapter.submitList(tracks, selectedId)
        }
        // 4. Миниплеер появится сам, если currentTrack не null


        adapter = TrackAdapter(
            onDelete = { trackId -> viewModel.deleteTrack(trackId) },
            onTrackClick = { trackEntity ->
                // Берём всю очередь и нужный индекс
                val queue = viewModel.tracks.value ?: emptyList()
                val idx = queue.indexOfFirst { it.id == trackEntity.id }
                if (idx != -1) {
                    playerViewModel.playQueue(queue, idx)
                }
            },
            isAdmin = isAdmin
        )

        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter

        binding.buttonAddTrack.setOnClickListener {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            intent.type = "audio/*"
            pickAudioLauncher.launch(intent)
        }

        binding.buttonAddTrack.visibility = if (isAdmin) View.VISIBLE else View.GONE
        binding.buttonRefresh.setOnClickListener { viewModel.syncTracks() }

        viewModel.tracks.observe(viewLifecycleOwner) { tracks ->
            Log.d("AllTracksFragment", "tracks.size = ${tracks.size}")
            val selectedId = playerViewModel.currentTrack.value?.id
            adapter.submitList(tracks, selectedId)
        }
        viewModel.syncTracks()
        playerViewModel.currentTrack.observe(viewLifecycleOwner) { track ->
            adapter.submitList(viewModel.tracks.value ?: emptyList(), track?.id)
        }
    }


    private fun getTrackEntityAndFileFromUri(uri: Uri): Pair<TrackEntity, File> {
        var title = "Unknown"
        val context = requireContext()
        context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (cursor.moveToFirst() && nameIndex != -1) {
                title = cursor.getString(nameIndex)
            }
        }
        val fileName = "local_${System.currentTimeMillis()}_${title}"
        val file = File(context.filesDir, fileName)
        context.contentResolver.openInputStream(uri)?.use { input ->
            FileOutputStream(file).use { output ->
                input.copyTo(output)
            }
        }
        val track = TrackEntity(
            id = 0,
            name = title,
            artist = "Unknown",
            duration = 0,
            localPath = null
        )
        return Pair(track, file)
    }

    companion object {
        private const val REQUEST_CODE_PICK_AUDIO = 111
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}