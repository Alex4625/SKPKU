package com.alzen.skpku.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room entity representing an SKP record in the local database.
 * All properties are immutable [val] to ensure consistency and Compose stability.
 */
@Entity(tableName = "skp_records")
data class SkpEntity(
    @PrimaryKey
    val id: String,
    val userKey: String?,
    val namaKegiatan: String?,
    val jenisKegiatan: String?,
    val kategoriBidang: String?,
    val tingkat: String?,
    val peran: String?,
    val modeKegiatan: String?,
    val poinSkp: Int,
    val fileUrl: String?,
    val fileName: String?,
    val fileType: String?,
    val storagePath: String?,
    val tanggalInput: String?,
    val timestamp: Long,
    val createdAt: String?
)
