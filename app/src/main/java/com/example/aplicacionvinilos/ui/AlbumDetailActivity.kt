package com.example.aplicacionvinilos.ui

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.example.aplicacionvinilos.data.model.Album
import com.example.aplicacionvinilos.databinding.ActivityAlbumDetailBinding
import com.example.aplicacionvinilos.ui.adapters.CommentAdapter
import com.example.aplicacionvinilos.ui.adapters.TrackAdapter
import com.example.aplicacionvinilos.viewmodels.AlbumViewModel
import com.example.aplicacionvinilos.viewmodels.UiState

class AlbumDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAlbumDetailBinding
    private val viewModel: AlbumViewModel by viewModels()

    private val trackAdapter = TrackAdapter()
    private val commentAdapter = CommentAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(Color.TRANSPARENT)
        )
        binding = ActivityAlbumDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.toolbar.setNavigationOnClickListener { finish() }
        binding.tracksList.adapter = trackAdapter
        binding.commentsList.adapter = commentAdapter
        viewModel.album.observe(this) { render(it) }

        val albumId = intent.getIntExtra(EXTRA_ALBUM_ID, INVALID_ID)
        if (albumId == INVALID_ID) {
            binding.errorText.visibility = View.VISIBLE
            return
        }
        if (savedInstanceState == null) viewModel.loadAlbum(albumId)
    }

    private fun render(state: UiState<Album>) {
        binding.progress.visibility = if (state is UiState.Loading) View.VISIBLE else View.GONE
        binding.errorText.visibility = if (state is UiState.Error) View.VISIBLE else View.GONE
        binding.content.visibility = if (state is UiState.Success) View.VISIBLE else View.GONE
        if (state is UiState.Success) show(state.data)
    }

    private fun show(album: Album) {
        Glide.with(this).load(album.cover).centerCrop().into(binding.coverImage)
        binding.nameText.text = album.name
        binding.performersText.text = album.performers.joinToString { it.name }
        binding.releaseDateText.text = album.releaseDate.substringBefore(YEAR_SEPARATOR)
        binding.genreText.text = album.genre
        binding.recordLabelText.text = album.recordLabel
        binding.descriptionText.text = album.description

        trackAdapter.submitList(album.tracks)
        binding.tracksEmptyText.visibility = if (album.tracks.isEmpty()) View.VISIBLE else View.GONE
        commentAdapter.submitList(album.comments)
        binding.commentsEmptyText.visibility = if (album.comments.isEmpty()) View.VISIBLE else View.GONE
    }

    companion object {
        const val EXTRA_ALBUM_ID = "album_id"
        private const val INVALID_ID = -1
        private const val YEAR_SEPARATOR = '-'
    }
}
