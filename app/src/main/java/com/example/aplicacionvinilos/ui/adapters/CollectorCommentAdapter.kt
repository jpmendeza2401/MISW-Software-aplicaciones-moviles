package com.example.aplicacionvinilos.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.aplicacionvinilos.data.model.Comment
import com.example.aplicacionvinilos.databinding.ItemCollectorCommentBinding

class CollectorCommentAdapter :
    ListAdapter<Comment, CollectorCommentAdapter.CommentViewHolder>(DIFF) {

    class CommentViewHolder(
        private val binding: ItemCollectorCommentBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(comment: Comment) {
            binding.commentRating.text =
                "★".repeat(comment.rating.coerceIn(0, 5)) +
                        "☆".repeat(5 - comment.rating.coerceIn(0, 5))

            binding.commentDescription.text = comment.description
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CommentViewHolder {
        val binding = ItemCollectorCommentBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CommentViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: CommentViewHolder,
        position: Int
    ) {
        holder.bind(getItem(position))
    }

    companion object {
        private val DIFF = object : DiffUtil.ItemCallback<Comment>() {
            override fun areItemsTheSame(
                old: Comment,
                new: Comment
            ) = old.id == new.id

            override fun areContentsTheSame(
                old: Comment,
                new: Comment
            ) = old == new
        }
    }
}
