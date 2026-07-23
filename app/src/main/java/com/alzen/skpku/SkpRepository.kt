package com.alzen.skpku

import com.alzen.skpku.data.local.SkpDao
import com.alzen.skpku.data.local.SkpEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import java.net.URLEncoder
import javax.inject.Inject

/**
 * Repository class that handles data operations.
 * Implements an Offline-First strategy by using [SkpDao] as the single source of truth for the UI.
 */
class SkpRepository @Inject constructor(
    private val apiService: SupabaseApiService,
    private val skpDao: SkpDao
) {

    /**
     * Observes SKP records from the local database.
     * The UI will automatically receive updates when the database changes.
     */
    fun observeSkpRecords(): Flow<List<Skp>> {
        return skpDao.getAllSkpRecords().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    /**
     * Refreshes the local cache by fetching the latest data from Supabase.
     * Uses [SkpDao.updateLocalCache] to ensure atomic updates.
     */
    suspend fun refreshSkpRecords(userId: String) {
        try {
            val response = apiService.getSkpRecords(userKey = "eq.$userId", range = "0-999")
            if (response.isSuccessful) {
                val records = response.body() ?: emptyList()
                skpDao.updateLocalCache(records.map { it.toEntity() })
            }
        } catch (e: Exception) {
            // Handle error (log or emit to a state)
        }
    }

    // Existing support for pagination - can be adapted for offline-first if needed
    suspend fun fetchSkpRecordsFromApi(userKey: String, page: Int, pageSize: Int): retrofit2.Response<List<Skp>> {
        val start = (page - 1) * pageSize
        val end = start + pageSize - 1
        val range = "$start-$end"
        return apiService.getSkpRecords(userKey = "eq.$userKey", range = range)
    }

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

    // Helper extensions to map between Entity and Domain model
    private fun SkpEntity.toDomain() = Skp(
        id = id,
        userKey = userKey,
        namaKegiatan = namaKegiatan,
        jenisKegiatan = jenisKegiatan,
        kategoriBidang = kategoriBidang,
        tingkat = tingkat,
        peran = peran,
        modeKegiatan = modeKegiatan ?: "Tidak Ada",
        poinSkp = poinSkp,
        fileUrl = fileUrl,
        fileName = fileName,
        fileType = fileType,
        storagePath = storagePath,
        tanggalInput = tanggalInput,
        timestamp = timestamp,
        createdAt = createdAt
    )

    private fun Skp.toEntity() = SkpEntity(
        id = id ?: "",
        userKey = userKey,
        namaKegiatan = namaKegiatan,
        jenisKegiatan = jenisKegiatan,
        kategoriBidang = kategoriBidang,
        tingkat = tingkat,
        peran = peran,
        modeKegiatan = modeKegiatan,
        poinSkp = poinSkp,
        fileUrl = fileUrl,
        fileName = fileName,
        fileType = fileType,
        storagePath = storagePath,
        tanggalInput = tanggalInput,
        timestamp = timestamp,
        createdAt = createdAt
    )
}
