package com.example.aplicacionvinilos.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.aplicacionvinilos.R
import com.example.aplicacionvinilos.data.model.Album
import com.example.aplicacionvinilos.databinding.ItemAlbumBinding

// HU01: cada tarjeta muestra portada, nombre y artista del álbum.
// El clic se expone como lambda; la navegación la decide la Activity.
class AlbumListAdapter(
    private val onAlbumClick: (Album) -> Unit
) : ListAdapter<Album, AlbumListAdapter.AlbumViewHolder>(DIFF) {

    class AlbumViewHolder(private val binding: ItemAlbumBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(album: Album, onAlbumClick: (Album) -> Unit) {
            val context = binding.root.context
            binding.albumName.text = album.name
            binding.albumCover.contentDescription =
                context.getString(R.string.portada_album, album.name)
            binding.albumArtist.text = album.performers.joinToString(", ") { it.name }
                .ifEmpty { context.getString(R.string.sin_artista) }
            val px = (COVER_DP * binding.root.resources.displayMetrics.density).toInt()
            Glide.with(binding.albumCover)
                .load(album.cover)
                .override(px, px)
                .centerCrop()
                .into(binding.albumCover)
            binding.root.setOnClickListener { onAlbumClick(album) }
        }

        fun clear() = Glide.with(binding.albumCover).clear(binding.albumCover)
    }

    // Libera el bitmap de la celda reciclada (menos memoria al hacer scroll).
    override fun onViewRecycled(holder: AlbumViewHolder) {
        holder.clear()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlbumViewHolder {
        val binding = ItemAlbumBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AlbumViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AlbumViewHolder, position: Int) {
        holder.bind(getItem(position), onAlbumClick)
    }

    companion object {
        private const val COVER_DP = 64
        private val DIFF = object : DiffUtil.ItemCallback<Album>() {
            override fun areItemsTheSame(old: Album, new: Album) = old.id == new.id
            override fun areContentsTheSame(old: Album, new: Album) = old == new
        }
    }
}
