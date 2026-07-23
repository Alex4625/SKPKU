package com.alzen.skpku

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.alzen.skpku.ui.screens.LoginScreen
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginActivity : ComponentActivity() {

    private val viewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Auto-login check
        lifecycleScope.launch {
            val preferenceManager = PreferenceManager(applicationContext)
            val token = preferenceManager.accessTokenFlow.first()
            if (token.isNotEmpty()) {
                // Token is handled dynamically by Hilt Interceptor
                navigateToMain()
            }
        }

        setContent {
            LoginScreen(
                viewModel = viewModel,
                onLoginSuccess = { navigateToMain() }
            )
        }
    }

    private fun navigateToMain() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}
