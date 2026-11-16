package com.example.aplicacionvinilos.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.aplicacionvinilos.data.model.Track
import com.example.aplicacionvinilos.databinding.ItemTrackBinding

class TrackAdapter : ListAdapter<Track, TrackAdapter.TrackViewHolder>(ItemDiffCallback()) {

    class TrackViewHolder(private val binding: ItemTrackBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(track: Track) {
            binding.trackName.text = track.name
            binding.trackDuration.text = track.duration
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = TrackViewHolder(
        ItemTrackBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) =
        holder.bind(getItem(position))
}
