package com.alzen.skpku

import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import java.net.URLEncoder

class SkpRepository(private val apiService: SupabaseApiService) {

    suspend fun fetchSkpRecords(userKey: String) = apiService.getSkpRecords(userKey = "eq.$userKey")

    suspend fun insertSkpRecord(skp: Skp) = apiService.insertSkpRecord(skp)

    suspend fun updateSkpRecord(id: String, skp: Skp) = apiService.updateSkpRecord(
        idQuery = "eq.$id",
        skp = mapOf(
            "nama_kegiatan" to skp.namaKegiatan,
            "jenis_kegiatan" to skp.jenisKegiatan,
            "kategori_bidang" to skp.kategoriBidang,
            "tingkat" to skp.tingkat,
            "peran" to skp.peran,
            "mode_kegiatan" to skp.modeKegiatan,
            "poin_skp" to skp.poinSkp,
            "file_url" to skp.fileUrl,
            "file_name" to skp.fileName,
            "file_type" to skp.fileType,
            "storage_path" to skp.storagePath,
            "tanggal_input" to skp.tanggalInput,
            "timestamp" to skp.timestamp
        )
    )

    suspend fun deleteSkpRecord(id: String) = apiService.deleteSkpRecord("eq.$id")

    suspend fun uploadFile(bucket: String, path: String, fileBytes: ByteArray, mimeType: String) {
        val requestBody = fileBytes.toRequestBody(mimeType.toMediaTypeOrNull())
        apiService.uploadFile(bucket, path, requestBody, mimeType)
    }

    fun getPublicUrl(bucket: String, path: String): String {
        val encodedPath = URLEncoder.encode(path, "UTF-8").replace("+", "%20")
        return "${BuildConfig.SUPABASE_URL}/storage/v1/object/public/$bucket/$encodedPath"
    }
}
