package com.example.music_player_app.presentation

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.music_player_app.R
import com.example.music_player_app.databinding.ItemTrackBinding
import com.example.music_player_app.domain.model.TrackEntity

class TrackAdapter(
    private val onDelete: (Int) -> Unit,
    private val onTrackClick: (TrackEntity) -> Unit
) : RecyclerView.Adapter<TrackAdapter.TrackViewHolder>() {

    private var tracks: List<TrackEntity> = emptyList()
    private var selectedTrackId: Int? = null

    fun submitList(list: List<TrackEntity>, selectedId: Int? = null) {
        tracks = list
        selectedTrackId = selectedId
        notifyDataSetChanged()
    }

    class TrackViewHolder(val binding: ItemTrackBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val binding = ItemTrackBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TrackViewHolder(binding)
    }

    override fun getItemCount() = tracks.size

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        val track = tracks[position]
        holder.binding.textTitle.text = track.name
        holder.binding.textArtist.text = track.artist
        holder.binding.textDuration.text = "${track.duration}s"
        holder.binding.buttonDelete.setOnClickListener { onDelete(track.id) }
        holder.itemView.setOnClickListener {
            selectedTrackId = track.id
            onTrackClick(track)
            notifyDataSetChanged()
        }
        holder.itemView.setBackgroundResource(
            if (track.id == selectedTrackId) R.drawable.bg_track_selected else android.R.color.transparent
        )
    }
}