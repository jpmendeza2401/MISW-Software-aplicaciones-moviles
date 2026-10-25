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
import com.example.aplicacionvinilos.databinding.ActivityAlbumListBinding
import com.example.aplicacionvinilos.ui.adapters.AlbumListAdapter
import com.example.aplicacionvinilos.viewmodels.AlbumListViewModel
import com.example.aplicacionvinilos.viewmodels.UiState

// HU01: consultar el catálogo de álbumes. Es la pantalla de inicio de la app.
class AlbumListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAlbumListBinding
    private val viewModel: AlbumListViewModel by viewModels()
    private val adapter = AlbumListAdapter { album -> openAlbum(album) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityAlbumListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.albumsList.adapter = adapter
        binding.retryButton.setOnClickListener { viewModel.loadAlbums() }
        viewModel.albums.observe(this) { render(it) }
    }

    private fun render(state: UiState<List<Album>>) {
        binding.progress.visibility = if (state is UiState.Loading) View.VISIBLE else View.GONE
        binding.errorGroup.visibility = if (state is UiState.Error) View.VISIBLE else View.GONE
        val albums = (state as? UiState.Success)?.data
        binding.albumsList.visibility = if (!albums.isNullOrEmpty()) View.VISIBLE else View.GONE
        binding.emptyText.visibility = if (albums != null && albums.isEmpty()) View.VISIBLE else View.GONE
        if (albums != null) adapter.submitList(albums)
        if (state is UiState.Error) binding.errorText.text = getString(R.string.error_prefijo, state.message)
    }

    private fun openAlbum(album: Album) {
        // TODO (Jhon Angel, 20 %): abrir el detalle del álbum (HU02) cuando la rama
        // feature/HU02 esté en dev. Algo como:
        // startActivity(Intent(this, AlbumDetailActivity::class.java).putExtra("albumId", album.id))
        // Revisar en AlbumDetailActivity el nombre exacto del extra que espera.
    }
}
