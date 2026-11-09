
package com.example.aplicacionvinilos.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.aplicacionvinilos.R
import com.example.aplicacionvinilos.data.model.Collector
import com.example.aplicacionvinilos.databinding.ItemCollectorBinding
import com.example.aplicacionvinilos.ui.getCollectorInitials

class CollectorAdapter(
    private val onCollectorClick: (Collector) -> Unit
) : ListAdapter<Collector, CollectorAdapter.CollectorViewHolder>(DIFF) {

    inner class CollectorViewHolder(
        private val binding: ItemCollectorBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(collector: Collector) {
            val resources = binding.root.resources

            binding.collectorName.text = collector.name
            binding.collectorInitials.text = getCollectorInitials(collector.name)

            // getQuantityString elige singular o plural según el número
            // (1 álbum / 3 álbumes). Ver <plurals> en strings.xml.
            val albums = resources.getQuantityString(
                R.plurals.collector_albums_count,
                collector.collectorAlbums.size,
                collector.collectorAlbums.size
            )
            val artists = resources.getQuantityString(
                R.plurals.collector_artists_count,
                collector.favoritePerformers.size,
                collector.favoritePerformers.size
            )
            binding.collectorStats.text =
                resources.getString(R.string.collector_stats_format, albums, artists)

            binding.root.setOnClickListener {
                onCollectorClick(collector)
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CollectorViewHolder {
        val binding = ItemCollectorBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return CollectorViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: CollectorViewHolder,
        position: Int
    ) {
        holder.bind(getItem(position))
    }

    companion object {
        private val DIFF = object : DiffUtil.ItemCallback<Collector>() {

            override fun areItemsTheSame(
                old: Collector,
                new: Collector
            ) = old.id == new.id

            override fun areContentsTheSame(
                old: Collector,
                new: Collector
            ) = old == new
        }
    }
}
