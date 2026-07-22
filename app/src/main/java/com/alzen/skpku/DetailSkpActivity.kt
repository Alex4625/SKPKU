package com.alzen.skpku

import android.app.AlertDialog
import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.alzen.skpku.databinding.ActivityDetailSkpBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class DetailSkpActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailSkpBinding
    private val viewModel: DetailSkpViewModel by viewModels { ViewModelFactory(this) }
    private var skp: Skp? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailSkpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getDataFromIntent()
        setupClickActions()
        observeViewModel()
    }

    private fun getDataFromIntent() {
        skp = intent.getSerializableExtra("skp") as? Skp
        if (skp == null) {
            Toast.makeText(this, "Data SKP tidak ditemukan", Toast.LENGTH_SHORT).show()
            finish()
            return
        }
        showDetailData()
    }

    private fun showDetailData() {
        skp?.let { data ->
            binding.tvDetailNama.text = data.namaKegiatan
            binding.tvDetailPoin.text = "${data.poinSkp} Poin"

            val info = """
                Kategori: ${data.kategoriBidang.orEmpty().ifBlank { "-" }}
                Jenis: ${data.jenisKegiatan.orEmpty().ifBlank { "-" }}
                Tingkat: ${data.tingkat.orEmpty().ifBlank { "-" }}
                Peran: ${data.peran.orEmpty().ifBlank { "-" }}
                Mode: ${data.modeKegiatan.orEmpty().ifBlank { "-" }}
                Tanggal: ${data.tanggalInput.orEmpty().ifBlank { "-" }}
            """.trimIndent()

            binding.tvDetailInfo.text = info

            if (data.fileName.isNullOrBlank()) {
                binding.tvDetailFile.text = "Tidak ada file bukti"
                binding.btnLihatBukti.isEnabled = false
                binding.btnDownloadBukti.isEnabled = false
            } else {
                binding.tvDetailFile.text = data.fileName
                binding.btnLihatBukti.isEnabled = true
                binding.btnDownloadBukti.isEnabled = true
            }
        }
    }

    private fun setupClickActions() {
        binding.btnLihatBukti.setOnClickListener { openProofFile() }
        binding.btnDownloadBukti.setOnClickListener { downloadProofFile() }
        binding.btnEdit.setOnClickListener { openEditForm() }
        binding.btnHapus.setOnClickListener { showDeleteConfirmation() }
        binding.btnKembali.setOnClickListener { finish() }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.isLoading.collectLatest { loading ->
                        binding.btnHapus.isEnabled = !loading
                        binding.btnEdit.isEnabled = !loading
                    }
                }
                launch {
                    viewModel.deleteSuccess.collect {
                        Toast.makeText(this@DetailSkpActivity, "Data berhasil dihapus", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                }
                launch {
                    viewModel.errorMessage.collect { message ->
                        Toast.makeText(this@DetailSkpActivity, message, Toast.LENGTH_LONG).show()
                    }
                }
            }
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

    private fun showDeleteConfirmation() {
        AlertDialog.Builder(this)
            .setTitle("Hapus Data SKP")
            .setMessage("Apakah kamu yakin ingin menghapus data ini?")
            .setPositiveButton("Hapus") { _, _ -> skp?.let { viewModel.deleteSkp(it) } }
            .setNegativeButton("Batal", null)
            .show()
    }
}
