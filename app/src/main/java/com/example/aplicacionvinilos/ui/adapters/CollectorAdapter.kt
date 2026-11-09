
package com.example.aplicacionvinilos.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.aplicacionvinilos.R
import com.example.aplicacionvinilos.data.model.Collector
import com.example.aplicacionvinilos.databinding.ItemCollectorBinding

class CollectorAdapter(
    private val onCollectorClick: (Collector) -> Unit
) : ListAdapter<Collector, CollectorAdapter.CollectorViewHolder>(DIFF) {

    inner class CollectorViewHolder(
        private val binding: ItemCollectorBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(collector: Collector) {
            binding.collectorName.text = collector.name

            binding.collectorId.text =
                binding.root.context.getString(
                    R.string.collector_id_format,
                    collector.id
                )

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
