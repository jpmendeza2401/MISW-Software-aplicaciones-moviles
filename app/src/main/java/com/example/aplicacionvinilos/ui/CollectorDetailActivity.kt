
package com.example.aplicacionvinilos.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.aplicacionvinilos.R
import com.example.aplicacionvinilos.data.model.Collector
import com.example.aplicacionvinilos.databinding.ActivityCollectorDetailBinding
import com.example.aplicacionvinilos.ui.adapters.CollectorAlbumAdapter
import com.example.aplicacionvinilos.ui.adapters.CollectorCommentAdapter
import com.example.aplicacionvinilos.ui.adapters.FavoritePerformerAdapter
import com.example.aplicacionvinilos.viewmodels.CollectorDetailViewModel
import com.example.aplicacionvinilos.viewmodels.UiState

class CollectorDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCollectorDetailBinding

    private val viewModel: CollectorDetailViewModel by viewModels()

    private lateinit var albumAdapter: CollectorAlbumAdapter
    private lateinit var performerAdapter: FavoritePerformerAdapter
    private lateinit var commentAdapter: CollectorCommentAdapter

    companion object {
        const val EXTRA_COLLECTOR_ID = "collector_id"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        binding = ActivityCollectorDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())

            view.setPadding(
                systemBars.left,
                systemBars.top,
                systemBars.right,
                systemBars.bottom
            )

            insets
        }

        val collectorId = intent.getIntExtra(EXTRA_COLLECTOR_ID, -1)

        setupToolbar()
        setupAdapters()
        setupTabs()
        setupBottomNavigation()

        viewModel.collector.observe(this) { state ->
            render(state)
        }

        viewModel.loadCollector(collectorId)
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun setupAdapters() {
        albumAdapter = CollectorAlbumAdapter()
        performerAdapter = FavoritePerformerAdapter()
        commentAdapter = CollectorCommentAdapter()

        binding.albumsList.layoutManager = LinearLayoutManager(this)
        binding.performersList.layoutManager = LinearLayoutManager(this)
        binding.commentsList.layoutManager = LinearLayoutManager(this)

        binding.albumsList.adapter = albumAdapter
        binding.performersList.adapter = performerAdapter
        binding.commentsList.adapter = commentAdapter
    }

    private fun setupTabs() {
        binding.detailTabs.addOnTabSelectedListener(
            object : com.google.android.material.tabs.TabLayout.OnTabSelectedListener {

                override fun onTabSelected(
                    tab: com.google.android.material.tabs.TabLayout.Tab
                ) {
                    binding.albumsList.visibility =
                        if (tab.position == 0) View.VISIBLE else View.GONE

                    binding.performersList.visibility =
                        if (tab.position == 1) View.VISIBLE else View.GONE

                    binding.commentsList.visibility =
                        if (tab.position == 2) View.VISIBLE else View.GONE
                }

                override fun onTabUnselected(
                    tab: com.google.android.material.tabs.TabLayout.Tab
                ) = Unit

                override fun onTabReselected(
                    tab: com.google.android.material.tabs.TabLayout.Tab
                ) = Unit
            }
        )
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
                    finish()
                    true
                }

                else -> false
            }
        }
    }

    private fun render(state: UiState<Collector>) {

        binding.progress.visibility =
            if (state is UiState.Loading) View.VISIBLE else View.GONE

        binding.errorText.visibility =
            if (state is UiState.Error) View.VISIBLE else View.GONE

        val collector = (state as? UiState.Success)?.data

        if (collector != null) {
            showCollector(collector)
        }

        if (state is UiState.Error) {
            binding.errorText.text = state.message
        }
    }

    private fun showCollector(collector: Collector) {

        binding.collectorName.text = collector.name

        binding.collectorInitials.text =
            getCollectorInitials(collector.name)

        binding.collectorId.text =
            getString(R.string.collector_id_format, collector.id)

        binding.collectorTelephone.text =
            "Teléfono: ${collector.telephone}"

        binding.collectorEmail.text =
            "Correo: ${collector.email}"

        albumAdapter.submitList(collector.collectorAlbums)
        performerAdapter.submitList(collector.favoritePerformers)
        commentAdapter.submitList(collector.comments)
    }
}
