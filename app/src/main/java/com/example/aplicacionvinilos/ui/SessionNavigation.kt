package com.example.aplicacionvinilos.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.aplicacionvinilos.viewmodels.SessionViewModel

// Cerrar sesión desde cualquier pantalla: el ViewModel vacía la sesión y la Activity
// vuelve al login borrando el back stack.
fun AppCompatActivity.logout() {
    ViewModelProvider(this)[SessionViewModel::class.java].logout()
    startActivity(
        Intent(this, LoginActivity::class.java)
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
    )
    finish()
}
