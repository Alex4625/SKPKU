package com.alzen.skpku

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.alzen.skpku.ui.screens.MainScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        setContent {
            MainScreen(
                viewModel = viewModel,
                onAddSkp = {
                    val intent = Intent(this, FormSkpActivity::class.java)
                    intent.putExtra("mode", "create")
                    startActivity(intent)
                },
                onSkpClick = { skp ->
                    val intent = Intent(this, DetailSkpActivity::class.java)
                    intent.putExtra("skp", skp)
                    startActivity(intent)
                },
                onLogout = {
                    viewModel.logout()
                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()
                }
            )
        }
    }
}
