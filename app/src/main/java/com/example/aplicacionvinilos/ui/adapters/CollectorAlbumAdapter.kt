package com.example.aplicacionvinilos.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.aplicacionvinilos.data.model.CollectorAlbum
import com.example.aplicacionvinilos.databinding.ItemCollectorAlbumBinding

class CollectorAlbumAdapter :
    ListAdapter<CollectorAlbum, CollectorAlbumAdapter.AlbumViewHolder>(DIFF) {

    class AlbumViewHolder(
        private val binding: ItemCollectorAlbumBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(album: CollectorAlbum) {
            binding.albumId.text = "Identificador: ${album.id}"
            binding.albumPrice.text = "Precio: ${album.price}"
            binding.albumStatus.text = "Estado: ${album.status}"
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AlbumViewHolder {
        val binding = ItemCollectorAlbumBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return AlbumViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AlbumViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object {
        private val DIFF = object : DiffUtil.ItemCallback<CollectorAlbum>() {
            override fun areItemsTheSame(
                old: CollectorAlbum,
                new: CollectorAlbum
            ) = old.id == new.id

            override fun areContentsTheSame(
                old: CollectorAlbum,
                new: CollectorAlbum
            ) = old == new
        }
    }
}
