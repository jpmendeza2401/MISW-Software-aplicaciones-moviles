package com.example.aplicacionvinilos.ui

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.doAfterTextChanged
import com.bumptech.glide.Glide
import com.example.aplicacionvinilos.R
import com.example.aplicacionvinilos.data.model.Album
import com.example.aplicacionvinilos.data.model.Genre
import com.example.aplicacionvinilos.data.model.RecordLabel
import com.example.aplicacionvinilos.databinding.ActivityCreateAlbumBinding
import com.example.aplicacionvinilos.viewmodels.AlbumValidator
import com.example.aplicacionvinilos.viewmodels.CreateAlbumViewModel
import com.example.aplicacionvinilos.viewmodels.UiState
import com.google.android.material.datepicker.MaterialDatePicker
import java.util.Calendar
import java.util.TimeZone

// HU07: formulario para crear un álbum (solo coleccionista).
class CreateAlbumActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCreateAlbumBinding
    private val viewModel: CreateAlbumViewModel by viewModels()
    private var releaseDate = "" // yyyy-MM-dd

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(Color.TRANSPARENT)
        )
        binding = ActivityCreateAlbumBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val bars = insets.getInsets(
                WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.ime()
            )
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom)
            insets
        }

        releaseDate = savedInstanceState?.getString(KEY_DATE).orEmpty()
        binding.dateInput.setText(releaseDate)

        binding.toolbar.setNavigationOnClickListener { finish() }
        setupDropdowns()
        setupDatePicker()
        setupCoverPreview()
        listOf(binding.nameInput, binding.descriptionInput, binding.genreInput, binding.labelInput)
            .forEach { it.doAfterTextChanged { viewModel.onFormChanged() } }

        binding.saveButton.setOnClickListener {
            hideKeyboard()
            viewModel.submit(
                name = binding.nameInput.text.toString(),
                cover = binding.coverInput.text.toString(),
                releaseDate = releaseDate,
                description = binding.descriptionInput.text.toString(),
                genre = binding.genreInput.text.toString(),
                recordLabel = binding.labelInput.text.toString()
            )
        }

        viewModel.validationError.observe(this) { showValidationError(it) }
        viewModel.state.observe(this) { render(it) }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(KEY_DATE, releaseDate)
    }

    // Cancela la carga de la portada para que Glide no retenga la vista.
    override fun onDestroy() {
        Glide.with(applicationContext).clear(binding.coverPreview)
        super.onDestroy()
    }

    // Un adapter por lista; solo se permiten los valores de los enums.
    private fun setupDropdowns() {
        binding.genreInput.setAdapter(
            ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, Genre.labels)
        )
        binding.labelInput.setAdapter(
            ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, RecordLabel.labels)
        )
        binding.genreInput.setOnClickListener { binding.genreInput.showDropDown() }
        binding.labelInput.setOnClickListener { binding.labelInput.showDropDown() }
    }

    private fun setupDatePicker() {
        binding.dateInput.setOnClickListener {
            if (supportFragmentManager.findFragmentByTag(DATE_TAG) != null) return@setOnClickListener
            val picker = MaterialDatePicker.Builder.datePicker()
                .setTitleText(R.string.crear_album_fecha)
                .build()
            picker.addOnPositiveButtonClickListener { millis ->
                releaseDate = formatDate(millis)
                binding.dateInput.setText(releaseDate)
                viewModel.onFormChanged()
            }
            picker.show(supportFragmentManager, DATE_TAG)
        }
    }

    // Solo pide la imagen si la URL parece válida, para no lanzar cargas inútiles.
    private fun setupCoverPreview() {
        binding.coverInput.doAfterTextChanged {
            viewModel.onFormChanged()
            val url = it?.toString()?.trim().orEmpty()
            if (AlbumValidator.isValidCover(url)) {
                Glide.with(this).load(url).override(PREVIEW_PX).centerCrop().into(binding.coverPreview)
            } else {
                Glide.with(this).clear(binding.coverPreview)
            }
        }
    }

    private fun render(state: UiState<Album>) {
        val loading = state is UiState.Loading
        binding.saveButton.isEnabled = !loading
        binding.saveButton.setText(
            if (loading) R.string.crear_album_guardando else R.string.crear_album_boton
        )
        when (state) {
            is UiState.Success -> {
                Toast.makeText(this, R.string.crear_album_exito, Toast.LENGTH_SHORT).show()
                viewModel.consumeState()
                setResult(RESULT_OK)
                finish()
            }
            is UiState.Error -> {
                binding.errorText.text = getString(R.string.crear_album_error, state.message)
                binding.errorText.visibility = View.VISIBLE
            }
            else -> Unit
        }
    }

    private fun showValidationError(error: AlbumValidator.Error?) {
        if (error == null) {
            binding.errorText.visibility = View.GONE
            return
        }
        binding.errorText.text = when (error) {
            AlbumValidator.Error.INVALID_NAME ->
                getString(R.string.crear_album_error_nombre, AlbumValidator.MIN_NAME_LENGTH)
            AlbumValidator.Error.INVALID_COVER -> getString(R.string.crear_album_error_portada)
            AlbumValidator.Error.INVALID_DATE -> getString(R.string.crear_album_error_fecha)
            AlbumValidator.Error.EMPTY_DESCRIPTION -> getString(R.string.crear_album_error_descripcion)
            AlbumValidator.Error.INVALID_GENRE -> getString(R.string.crear_album_error_genero)
            AlbumValidator.Error.INVALID_LABEL -> getString(R.string.crear_album_error_sello)
        }
        binding.errorText.visibility = View.VISIBLE
    }

    // El picker entrega medianoche UTC; se formatea en UTC para no correr el día.
    private fun formatDate(millis: Long): String {
        val c = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply { timeInMillis = millis }
        return "%04d-%02d-%02d".format(
            c.get(Calendar.YEAR), c.get(Calendar.MONTH) + 1, c.get(Calendar.DAY_OF_MONTH)
        )
    }

    private fun hideKeyboard() {
        getSystemService(InputMethodManager::class.java)
            ?.hideSoftInputFromWindow(binding.root.windowToken, 0)
    }

    private companion object {
        const val KEY_DATE = "release_date"
        const val DATE_TAG = "date_picker"
        const val PREVIEW_PX = 360
    }
}
