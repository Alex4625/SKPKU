package com.alzen.skpku

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

/**
 * ViewModel for the SKP Form screen.
 * Handles validation, quota checking, and saving data.
 */
@HiltViewModel
class FormSkpViewModel @Inject constructor(
    private val repository: SkpRepository,
    private val preferenceManager: PreferenceManager
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _saveSuccess = MutableSharedFlow<String>()
    val saveSuccess: SharedFlow<String> = _saveSuccess.asSharedFlow()

    private val _errorMessage = MutableSharedFlow<String>()
    val errorMessage: SharedFlow<String> = _errorMessage.asSharedFlow()

    val userId: Flow<String> = preferenceManager.userIdFlow

    fun saveData(
        isEditMode: Boolean,
        editSkp: Skp?,
        userId: String,
        selectedKategori: String,
        selectedKegiatan: String,
        selectedTingkat: String,
        selectedPeran: String,
        selectedMode: String,
        selectedPoin: Int,
        fileBytes: ByteArray?,
        fileName: String,
        mimeType: String,
        existingData: Map<String, String> // url, name, type, path
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // 1. Check Quota via API
                val quotaLimit = SkpRule.getQuotaLimit(selectedKegiatan, selectedTingkat)
                if (quotaLimit > 0) {
                    val response = repository.fetchSkpRecordsFromApi(userId, 1, 100)
                    if (response.isSuccessful) {
                        val records = response.body() ?: emptyList()
                        val count = records.count { skp ->
                            if (isEditMode && skp.id == editSkp?.id) return@count false
                            val sameKegiatan = selectedKegiatan == skp.namaKegiatan
                            var sameTingkat = true
                            if (selectedKegiatan == "Panitia dalam Kegiatan Kemahasiswaan") {
                                sameTingkat = selectedTingkat == skp.tingkat
                            }
                            sameKegiatan && sameTingkat
                        }
                        if (count >= quotaLimit) {
                            _errorMessage.emit("Kegiatan ini sudah mencapai batas maksimal: $quotaLimit")
                            _isLoading.value = false
                            return@launch
                        }
                    }
                }

                // 2. Upload File if new one selected
                var finalFileUrl = existingData["url"] ?: ""
                var finalFileName = existingData["name"] ?: ""
                var finalFileType = existingData["type"] ?: ""
                var finalStoragePath = existingData["path"] ?: ""

                if (fileBytes != null) {
                    val safeFileName = fileName.replace("[^a-zA-Z0-9._-]".toRegex(), "_")
                    val storagePath = "${System.currentTimeMillis()}_$safeFileName"
                    
                    repository.uploadFile("skp-bukti", storagePath, fileBytes, mimeType)
                    
                    finalStoragePath = storagePath
                    finalFileUrl = repository.getPublicUrl("skp-bukti", storagePath)
                    finalFileName = fileName
                    finalFileType = when {
                        mimeType == "application/pdf" -> "pdf"
                        mimeType.startsWith("image/") -> "image"
                        else -> "file"
                    }
                }

                // 3. Save Record
                val skp = Skp(
                    id = editSkp?.id,
                    userKey = userId,
                    namaKegiatan = selectedKegiatan,
                    jenisKegiatan = SkpRule.getJenisKegiatan(selectedKategori),
                    kategoriBidang = selectedKategori,
                    tingkat = selectedTingkat,
                    peran = selectedPeran,
                    modeKegiatan = selectedMode,
                    poinSkp = selectedPoin,
                    fileUrl = finalFileUrl,
                    fileName = finalFileName,
                    fileType = finalFileType,
                    storagePath = finalStoragePath,
                    tanggalInput = getTodayDate(),
                    timestamp = editSkp?.timestamp ?: System.currentTimeMillis()
                )

                val saveResponse = if (isEditMode) {
                    repository.updateSkpRecord(skp.id!!, skp)
                } else {
                    repository.insertSkpRecord(skp)
                }

                if (saveResponse.isSuccessful) {
                    // Trigger a refresh of the local database to show new data immediately
                    repository.refreshSkpRecords(userId)
                    _saveSuccess.emit(if (isEditMode) "Data SKP berhasil diupdate" else "Data SKP berhasil disimpan")
                } else {
                    _errorMessage.emit("Gagal simpan: ${saveResponse.code()}")
                }

            } catch (e: Exception) {
                _errorMessage.emit("Terjadi kesalahan: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun getTodayDate(): String {
        val format = SimpleDateFormat("dd MMMM yyyy", Locale("id", "ID"))
        return format.format(Date())
    }
}
