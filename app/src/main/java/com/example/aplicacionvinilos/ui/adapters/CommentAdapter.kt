package com.example.aplicacionvinilos.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.aplicacionvinilos.data.model.Comment
import com.example.aplicacionvinilos.databinding.ItemCommentBinding

class CommentAdapter : ListAdapter<Comment, CommentAdapter.CommentViewHolder>(ItemDiffCallback()) {

    class CommentViewHolder(private val binding: ItemCommentBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(comment: Comment) {
            val filled = comment.rating.coerceIn(0, MAX_RATING)
            binding.commentStars.text =
                FILLED_STAR.repeat(filled) + EMPTY_STAR.repeat(MAX_RATING - filled)
            binding.commentText.text = comment.description
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = CommentViewHolder(
        ItemCommentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) =
        holder.bind(getItem(position))

    private companion object {
        const val MAX_RATING = 5
        const val FILLED_STAR = "★"
        const val EMPTY_STAR = "☆"
    }
}
