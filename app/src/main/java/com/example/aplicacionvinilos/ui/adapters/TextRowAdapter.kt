package com.example.aplicacionvinilos.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.aplicacionvinilos.databinding.ItemTextRowBinding

// Shared by tracks (name/duration) and comments (description/rating); each caller maps its item to two lines.
class TextRowAdapter<T : Any>(
    private val toRow: (T) -> Pair<String, String>
) : ListAdapter<T, TextRowAdapter<T>.RowViewHolder>(object : DiffUtil.ItemCallback<T>() {
    override fun areItemsTheSame(oldItem: T, newItem: T) = oldItem == newItem
    override fun areContentsTheSame(oldItem: T, newItem: T) = oldItem == newItem
}) {

    inner class RowViewHolder(private val binding: ItemTextRowBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: T) {
            val (primary, secondary) = toRow(item)
            binding.primaryText.text = primary
            binding.secondaryText.text = secondary
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = RowViewHolder(
        ItemTextRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: RowViewHolder, position: Int) =
        holder.bind(getItem(position))
}
