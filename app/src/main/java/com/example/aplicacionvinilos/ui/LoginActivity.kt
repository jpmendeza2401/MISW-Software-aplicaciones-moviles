package com.example.aplicacionvinilos.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.doAfterTextChanged
import com.example.aplicacionvinilos.R
import com.example.aplicacionvinilos.data.model.Collector
import com.example.aplicacionvinilos.databinding.ActivityLoginBinding
import com.example.aplicacionvinilos.util.EmailValidator
import com.example.aplicacionvinilos.viewmodels.LoginViewModel
import com.example.aplicacionvinilos.viewmodels.UiState

// Login simulado por correo. Pantalla de inicio de la app.
class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val viewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom)
            insets
        }

        binding.loginButton.setOnClickListener { submit() }
        binding.guestButton.setOnClickListener { enterAsGuest() }
        binding.emailInput.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                submit()
                true
            } else false
        }
        binding.emailInput.doAfterTextChanged { viewModel.onEmailChanged() }

        viewModel.emailError.observe(this) { error ->
            binding.emailLayout.error = when (error) {
                EmailValidator.Result.EMPTY -> getString(R.string.login_email_vacio)
                EmailValidator.Result.INVALID_FORMAT -> getString(R.string.login_email_invalido)
                else -> null
            }
        }
        viewModel.state.observe(this) { render(it) }
    }

    // Visitante: no pide datos ni llama al backend; CollectorSession queda vacía (current == null).
    private fun enterAsGuest() {
        viewModel.enterAsGuest()
        goToHome()
    }

    private fun goToHome() {
        startActivity(
            Intent(this, AlbumListActivity::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        )
        finish()
    }

    private fun submit() = viewModel.login(binding.emailInput.text?.toString().orEmpty())

    private fun render(state: UiState<Collector>) {
        val loading = state is UiState.Loading
        binding.progress.visibility = if (loading) View.VISIBLE else View.GONE
        binding.loginButton.isEnabled = !loading
        binding.guestButton.isEnabled = !loading
        binding.emailInput.isEnabled = !loading
        binding.errorText.visibility = if (state is UiState.Error) View.VISIBLE else View.GONE
        when (state) {
            is UiState.Error -> binding.errorText.text = state.message
            is UiState.Success -> {
                viewModel.consumeState()
                goToHome()
            }
            else -> Unit
        }
    }
}
