package com.example.music_player_app.presentation.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.music_player_app.databinding.ItemTrackBinding
import com.example.music_player_app.domain.model.Track

class TrackAdapter(
    private val onDelete: (String) -> Unit) : RecyclerView.Adapter<TrackAdapter.TrackViewHolder>() {

    private var tracks: List<Track> = emptyList()
    fun submitList(list: List<Track>) { tracks = list; notifyDataSetChanged() }

    class TrackViewHolder(val binding: ItemTrackBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val binding = ItemTrackBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TrackViewHolder(binding)
    }
    override fun getItemCount() = tracks.size
    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        val track = tracks[position]
        holder.binding.textTitle.text = track.title
        holder.binding.textArtist.text = track.artist
        holder.binding.textDuration.text = "${track.duration}s"
        holder.binding.buttonDelete.setOnClickListener {
            onDelete(track.id)
        }
    }
}