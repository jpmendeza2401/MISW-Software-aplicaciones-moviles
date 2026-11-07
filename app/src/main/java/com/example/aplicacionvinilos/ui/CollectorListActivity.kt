package com.example.aplicacionvinilos.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.aplicacionvinilos.R
import com.example.aplicacionvinilos.data.model.Collector
import com.example.aplicacionvinilos.databinding.ActivityCollectorListBinding
import com.example.aplicacionvinilos.ui.adapters.CollectorAdapter
import com.example.aplicacionvinilos.viewmodels.CollectorViewModel
import com.example.aplicacionvinilos.viewmodels.UiState
import com.example.aplicacionvinilos.ui.CollectorDetailActivity

// HU05: consultar el listado de coleccionistas.
class CollectorListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCollectorListBinding

    private val viewModel: CollectorViewModel by viewModels()

    private val adapter = CollectorAdapter { collector ->
        openCollectorDetail(collector)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        binding = ActivityCollectorListBinding.inflate(layoutInflater)

        setContentView(binding.root)

        // Configuración de los márgenes del sistema.
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(
                WindowInsetsCompat.Type.systemBars()
            )

            v.setPadding(
                systemBars.left,
                systemBars.top,
                systemBars.right,
                systemBars.bottom
            )

            insets
        }

        // Configuración del RecyclerView.
        binding.collectorsList.adapter = adapter

        // Botón para volver a consultar el listado.
        binding.retryButton.setOnClickListener {
            viewModel.loadCollectors()
        }

        // Configuración de navegación inferior.
        setupBottomNavigation()

        // Observación de los estados del ViewModel.
        viewModel.collectors.observe(this) {
            render(it)
        }
    }

    private fun setupBottomNavigation() {

        binding.bottomNavigation.selectedItemId =
            R.id.navigation_collectors

        binding.bottomNavigation.setOnItemSelectedListener { item ->

            when (item.itemId) {

                R.id.navigation_collectors -> true

                R.id.navigation_albums -> {

                    val intent = Intent(
                        this,
                        AlbumListActivity::class.java
                    ).apply {
                        addFlags(
                            Intent.FLAG_ACTIVITY_CLEAR_TOP or
                                    Intent.FLAG_ACTIVITY_SINGLE_TOP
                        )
                    }

                    startActivity(intent)
                    true
                }

                else -> false
            }
        }
    }

    // HU05: representa los diferentes estados de la consulta.
    private fun render(state: UiState<List<Collector>>) {

        binding.progress.visibility =
            if (state is UiState.Loading) View.VISIBLE else View.GONE

        binding.errorGroup.visibility =
            if (state is UiState.Error) View.VISIBLE else View.GONE

        val collectors = (state as? UiState.Success)?.data

        binding.collectorsList.visibility =
            if (!collectors.isNullOrEmpty()) View.VISIBLE else View.GONE

        binding.emptyText.visibility =
            if (collectors != null && collectors.isEmpty()) {
                View.VISIBLE
            } else {
                View.GONE
            }

        if (collectors != null) {
            adapter.submitList(collectors)
        }

        if (state is UiState.Error) {
            binding.errorText.text = getString(
                R.string.error_prefijo,
                state.message
            )
        }
    }
    private fun openCollectorDetail(collector: Collector) {
        val intent = Intent(
            this,
            CollectorDetailActivity::class.java
        ).apply {
            putExtra(
                CollectorDetailActivity.EXTRA_COLLECTOR_ID,
                collector.id
            )
        }

        startActivity(intent)
    }
}
