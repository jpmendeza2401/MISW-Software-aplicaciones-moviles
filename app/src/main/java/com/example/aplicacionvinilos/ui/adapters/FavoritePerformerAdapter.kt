package com.example.aplicacionvinilos.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.aplicacionvinilos.data.model.Performer
import com.example.aplicacionvinilos.databinding.ItemCollectorPerformerBinding

class FavoritePerformerAdapter :
    ListAdapter<Performer, FavoritePerformerAdapter.PerformerViewHolder>(DIFF) {

    class PerformerViewHolder(
        private val binding: ItemCollectorPerformerBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(performer: Performer) {
            binding.performerName.text = performer.name
            binding.performerDescription.text = performer.description

            binding.performerDate.text =
                performer.creationDate?.take(10)?.let {
                    "Fecha: $it"
                } ?: "Fecha no disponible"

            Glide.with(binding.root.context)
                .load(performer.image)
                .placeholder(android.R.drawable.ic_menu_gallery)
                .error(android.R.drawable.ic_menu_report_image)
                .centerCrop()
                .into(binding.performerImage)
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PerformerViewHolder {
        val binding = ItemCollectorPerformerBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PerformerViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: PerformerViewHolder,
        position: Int
    ) {
        holder.bind(getItem(position))
    }

    companion object {
        private val DIFF = object : DiffUtil.ItemCallback<Performer>() {
            override fun areItemsTheSame(
                old: Performer,
                new: Performer
            ) = old.id == new.id

            override fun areContentsTheSame(
                old: Performer,
                new: Performer
            ) = old == new
        }
    }
}
