package com.example.aplicacionvinilos.ui

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.aplicacionvinilos.R
import com.example.aplicacionvinilos.data.model.Band
import com.example.aplicacionvinilos.databinding.ActivityMainBinding
import com.example.aplicacionvinilos.viewmodels.AlbumBandViewModel
import com.example.aplicacionvinilos.viewmodels.UiState

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: AlbumBandViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.searchButton.setOnClickListener { search() }
        viewModel.band.observe(this) { render(it) }
    }

    private fun search() {
        val albumId = binding.albumIdInput.text.toString().toIntOrNull()
        val bandId = binding.bandIdInput.text.toString().toIntOrNull()
        if (albumId == null || bandId == null) {
            binding.resultText.text = getString(R.string.error_ids_invalidos)
            return
        }
        viewModel.loadBand(albumId, bandId)
    }

    private fun render(state: UiState<Band>) {
        binding.progress.visibility = if (state is UiState.Loading) View.VISIBLE else View.GONE
        binding.searchButton.isEnabled = state !is UiState.Loading
        binding.resultText.text = when (state) {
            is UiState.Idle, is UiState.Loading -> ""
            is UiState.Success -> format(state.data)
            is UiState.Error -> getString(R.string.error_prefijo, state.message)
        }
    }

    private fun format(band: Band): String = getString(
        R.string.resultado_banda,
        band.id,
        band.name,
        band.creationDate ?: "-",
        band.image,
        band.description
    )
}
