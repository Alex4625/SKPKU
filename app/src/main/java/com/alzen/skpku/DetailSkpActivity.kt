package com.alzen.skpku

import android.app.AlertDialog
import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.alzen.skpku.ui.screens.DetailSkpScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailSkpActivity : ComponentActivity() {

    private val viewModel: DetailSkpViewModel by viewModels()
    private var skp: Skp? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        skp = intent.getSerializableExtra("skp") as? Skp
        if (skp == null) {
            Toast.makeText(this, "Data SKP tidak ditemukan", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        setContent {
            DetailSkpScreen(
                skp = skp!!,
                viewModel = viewModel,
                onBack = { finish() },
                onEdit = { openEditForm() },
                onViewProof = { openProofFile() },
                onDownloadProof = { downloadProofFile() }
            )
        }
    }

    private fun openProofFile() {
        val url = skp?.fileUrl
        if (url.isNullOrBlank()) {
            Toast.makeText(this, "URL file bukti tidak tersedia", Toast.LENGTH_SHORT).show()
            return
        }

        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(this, "Tidak bisa membuka file bukti", Toast.LENGTH_SHORT).show()
        }
    }

    private fun downloadProofFile() {
        val url = skp?.fileUrl
        if (url.isNullOrBlank()) {
            Toast.makeText(this, "URL file bukti tidak tersedia", Toast.LENGTH_SHORT).show()
            return
        }

        try {
            val fileName = skp?.fileName ?: "bukti_skp"
            val request = DownloadManager.Request(Uri.parse(url)).apply {
                setTitle("Download Bukti SKP")
                setDescription(fileName)
                setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName)
            }

            val downloadManager = getSystemService(Context.DOWNLOAD_SERVICE) as? DownloadManager
            downloadManager?.enqueue(request)
            Toast.makeText(this, "Download dimulai. Cek folder Downloads.", Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            Toast.makeText(this, "Gagal download: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun openEditForm() {
        val intent = Intent(this, FormSkpActivity::class.java).apply {
            putExtra("mode", "edit")
            putExtra("skp", skp)
        }
        startActivity(intent)
    }
}
