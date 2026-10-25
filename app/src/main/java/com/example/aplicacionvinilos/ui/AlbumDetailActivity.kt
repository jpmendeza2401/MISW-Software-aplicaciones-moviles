package com.example.aplicacionvinilos.ui

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.aplicacionvinilos.R
import com.example.aplicacionvinilos.data.model.Album
import com.example.aplicacionvinilos.data.model.Comment
import com.example.aplicacionvinilos.data.model.Track
import com.example.aplicacionvinilos.databinding.ActivityAlbumDetailBinding
import com.example.aplicacionvinilos.ui.adapters.TextRowAdapter
import com.example.aplicacionvinilos.viewmodels.AlbumViewModel
import com.example.aplicacionvinilos.viewmodels.UiState

class AlbumDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAlbumDetailBinding
    private val viewModel: AlbumViewModel by viewModels()

    private val trackAdapter = TextRowAdapter<Track> { it.name to it.duration }
    private val commentAdapter = TextRowAdapter<Comment> {
        it.description to getString(R.string.rating_format, it.rating)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityAlbumDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

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
        binding.coverText.text = getString(R.string.cover_format, album.cover)
        binding.nameText.text = album.name
        binding.performersText.text = getString(
            R.string.performers_format, album.performers.joinToString { it.name }
        )
        binding.releaseDateText.text = getString(
            R.string.release_date_format, album.releaseDate.substringBefore(DATE_TIME_SEPARATOR)
        )
        binding.genreText.text = getString(R.string.genre_format, album.genre)
        binding.recordLabelText.text = getString(R.string.record_label_format, album.recordLabel)
        binding.descriptionText.text = album.description

        trackAdapter.submitList(album.tracks)
        binding.tracksEmptyText.visibility = if (album.tracks.isEmpty()) View.VISIBLE else View.GONE
        commentAdapter.submitList(album.comments)
        binding.commentsEmptyText.visibility = if (album.comments.isEmpty()) View.VISIBLE else View.GONE
    }

    companion object {
        const val EXTRA_ALBUM_ID = "album_id"
        private const val INVALID_ID = -1
        private const val DATE_TIME_SEPARATOR = 'T'
    }
}
