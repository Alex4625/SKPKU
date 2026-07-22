package com.alzen.skpku

import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.view.View
import android.webkit.MimeTypeMap
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.alzen.skpku.databinding.ActivityFormSkpBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class FormSkpActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFormSkpBinding
    private val viewModel: FormSkpViewModel by viewModels { ViewModelFactory(this) }

    private var selectedKategori = ""
    private var selectedKegiatan = ""
    private var selectedTingkat = ""
    private var selectedPeran = ""
    private var selectedMode = SkpRule.MODE_TIDAK_ADA
    private var selectedPoin = 0

    private var selectedFileUri: Uri? = null
    private var selectedFileName = ""
    private var selectedMimeType = ""

    private var isEditMode = false
    private var editSkp: Skp? = null
    private var userKey = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFormSkpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        checkMode()
        setupUI()
        observeViewModel()
    }

    private fun checkMode() {
        val mode = intent.getStringExtra("mode")
        if ("edit".equals(mode, ignoreCase = true)) {
            isEditMode = true
            editSkp = intent.getSerializableExtra("skp") as? Skp
            binding.tvFormTitle.text = "Edit Data SKP"
            editSkp?.let {
                if (!it.fileName.isNullOrEmpty()) {
                    binding.tvFileName.text = "File saat ini: ${it.fileName}"
                }
            }
        } else {
            isEditMode = false
            binding.tvFormTitle.text = "Tambah Data SKP"
        }
    }

    private fun setupUI() {
        setupKategoriSpinner()
        setupClickActions()
    }

    private fun setupKategoriSpinner() {
        val kategoriList = SkpRule.getKategoriList()
        setSpinnerData(binding.spKategori, kategoriList)

        binding.spKategori.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedKategori = kategoriList[position]
                setupKegiatanSpinner()
                if (isEditMode && editSkp != null) {
                    setSpinnerSelection(binding.spKategori, editSkp?.kategoriBidang)
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
        if (isEditMode && editSkp != null) {
            setSpinnerSelection(binding.spKategori, editSkp?.kategoriBidang)
        }
    }

    private fun setupKegiatanSpinner() {
        val kegiatanList = SkpRule.getKegiatanList(selectedKategori)
        setSpinnerData(binding.spKegiatan, kegiatanList)

        binding.spKegiatan.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedKegiatan = kegiatanList[position]
                setupTingkatSpinner()
                if (isEditMode && editSkp != null) {
                    setSpinnerSelection(binding.spKegiatan, editSkp?.namaKegiatan)
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
        if (isEditMode && editSkp != null) {
            setSpinnerSelection(binding.spKegiatan, editSkp?.namaKegiatan)
        }
    }

    private fun setupTingkatSpinner() {
        val tingkatList = SkpRule.getTingkatList(selectedKategori, selectedKegiatan)
        setSpinnerData(binding.spTingkat, tingkatList)

        binding.spTingkat.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedTingkat = tingkatList[position]
                setupPeranSpinner()
                if (isEditMode && editSkp != null) {
                    setSpinnerSelection(binding.spTingkat, editSkp?.tingkat)
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
        binding.spTingkat.isEnabled = tingkatList.size > 1
        if (isEditMode && editSkp != null) {
            setSpinnerSelection(binding.spTingkat, editSkp?.tingkat)
        }
    }

    private fun setupPeranSpinner() {
        val peranList = SkpRule.getPeranList(selectedKategori, selectedKegiatan, selectedTingkat)
        setSpinnerData(binding.spPeran, peranList)

        binding.spPeran.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedPeran = peranList[position]
                setupModeSpinner()
                if (isEditMode && editSkp != null) {
                    setSpinnerSelection(binding.spPeran, editSkp?.peran)
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
        binding.spPeran.isEnabled = peranList.size > 1
        if (isEditMode && editSkp != null) {
            setSpinnerSelection(binding.spPeran, editSkp?.peran)
        }
    }

    private fun setupModeSpinner() {
        val modeList = SkpRule.getModeList(selectedKategori, selectedKegiatan, selectedTingkat, selectedPeran)
        setSpinnerData(binding.spMode, modeList)

        binding.spMode.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedMode = modeList[position]
                calculatePointNow()
                if (isEditMode && editSkp != null) {
                    setSpinnerSelection(binding.spMode, editSkp?.modeKegiatan)
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
        binding.spMode.isEnabled = !(modeList.size == 1 && modeList[0] == SkpRule.MODE_TIDAK_ADA)
        if (isEditMode && editSkp != null) {
            setSpinnerSelection(binding.spMode, editSkp?.modeKegiatan)
        }
        calculatePointNow()
    }

    private fun calculatePointNow() {
        selectedPoin = SkpRule.calculatePoint(selectedKategori, selectedKegiatan, selectedTingkat, selectedPeran, selectedMode)
        binding.tvPoinOtomatis.text = "$selectedPoin Poin"
    }

    private fun setupClickActions() {
        binding.btnPilihFile.setOnClickListener { openFilePicker() }
        binding.btnSimpan.setOnClickListener { validateBeforeSave() }
        binding.btnBatal.setOnClickListener { finish() }
    }

    private fun openFilePicker() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            type = "*/*"
            addCategory(Intent.CATEGORY_OPENABLE)
            putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("image/jpeg", "image/png", "application/pdf"))
        }
        startActivityForResult(intent, 101)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 101 && resultCode == RESULT_OK && data != null) {
            data.data?.let { uri ->
                selectedFileUri = uri
                selectedFileName = getFileName(uri)
                selectedMimeType = contentResolver.getType(uri) ?: getMimeTypeFromFileName(selectedFileName)
                
                if (!isAllowedMimeType(selectedMimeType)) {
                    selectedFileUri = null
                    Toast.makeText(this, "Format file harus JPG, PNG, atau PDF", Toast.LENGTH_LONG).show()
                    return
                }
                
                if (getFileSize(uri) > 5 * 1024 * 1024) {
                    selectedFileUri = null
                    Toast.makeText(this, "Ukuran file maksimal 5 MB", Toast.LENGTH_LONG).show()
                    return
                }
                binding.tvFileName.text = "File dipilih: $selectedFileName"
            }
        }
    }

    private fun validateBeforeSave() {
        if (selectedKategori.isEmpty() || selectedKegiatan.isEmpty() || selectedTingkat.isEmpty() || 
            selectedPeran.isEmpty() || selectedMode.isEmpty()) {
            Toast.makeText(this, "Lengkapi semua pilihan terlebih dahulu", Toast.LENGTH_SHORT).show()
            return
        }
        if (selectedPoin <= 0) {
            Toast.makeText(this, "Poin belum valid. Cek pilihan kegiatan.", Toast.LENGTH_SHORT).show()
            return
        }
        if (!isEditMode && selectedFileUri == null) {
            Toast.makeText(this, "Pilih file bukti terlebih dahulu", Toast.LENGTH_SHORT).show()
            return
        }
        
        lifecycleScope.launch {
            val key = viewModel.userKey.first()
            viewModel.saveData(
                context = this@FormSkpActivity,
                isEditMode = isEditMode,
                editSkp = editSkp,
                userKey = key,
                selectedKategori = selectedKategori,
                selectedKegiatan = selectedKegiatan,
                selectedTingkat = selectedTingkat,
                selectedPeran = selectedPeran,
                selectedMode = selectedMode,
                selectedPoin = selectedPoin,
                fileUri = selectedFileUri,
                fileName = selectedFileName,
                mimeType = selectedMimeType,
                existingData = mapOf(
                    "url" to (editSkp?.fileUrl ?: ""),
                    "name" to (editSkp?.fileName ?: ""),
                    "type" to (editSkp?.fileType ?: ""),
                    "path" to (editSkp?.storagePath ?: "")
                )
            )
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.isLoading.collectLatest { loading ->
                        setLoading(loading)
                    }
                }
                launch {
                    viewModel.saveSuccess.collect { message ->
                        Toast.makeText(this@FormSkpActivity, message, Toast.LENGTH_SHORT).show()
                        finish()
                    }
                }
                launch {
                    viewModel.errorMessage.collect { message ->
                        Toast.makeText(this@FormSkpActivity, message, Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    private fun setLoading(loading: Boolean) {
        binding.progressUpload.visibility = if (loading) View.VISIBLE else View.GONE
        binding.btnSimpan.isEnabled = !loading
        binding.btnPilihFile.isEnabled = !loading
        binding.btnBatal.isEnabled = !loading
    }

    private fun setSpinnerData(spinner: Spinner, data: List<String>) {
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, data)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
    }

    private fun setSpinnerSelection(spinner: Spinner, value: String?) {
        value?.let {
            for (i in 0 until spinner.count) {
                if (spinner.getItemAtPosition(i).toString() == it) {
                    spinner.setSelection(i)
                    return
                }
            }
        }
    }

    private fun getFileName(uri: Uri): String {
        var result = "bukti_skp"
        contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (nameIndex != -1 && cursor.moveToFirst()) {
                result = cursor.getString(nameIndex)
            }
        }
        return result
    }

    private fun getFileSize(uri: Uri): Long {
        var size = 0L
        contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
            if (sizeIndex != -1 && cursor.moveToFirst()) {
                size = cursor.getLong(sizeIndex)
            }
        }
        return size
    }

    private fun isAllowedMimeType(mimeType: String) = mimeType in listOf("image/jpeg", "image/png", "application/pdf")

    private fun getMimeTypeFromFileName(fileName: String): String {
        val extension = fileName.substringAfterLast('.', "").lowercase()
        return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension) ?: ""
    }
}
