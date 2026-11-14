package com.example.aplicacionvinilos.ui.adapters

import android.annotation.SuppressLint
import androidx.recyclerview.widget.DiffUtil

// Shared by TrackAdapter and CommentAdapter: items are data classes, so equality is enough.
@SuppressLint("DiffUtilEquals") // los items son data classes: == compara por contenido
class ItemDiffCallback<T : Any> : DiffUtil.ItemCallback<T>() {
    override fun areItemsTheSame(oldItem: T, newItem: T) = oldItem == newItem
    override fun areContentsTheSame(oldItem: T, newItem: T) = oldItem == newItem
}
