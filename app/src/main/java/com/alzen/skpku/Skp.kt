package com.alzen.skpku

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * Model data SKP.
 * Class ini digunakan untuk menampung satu data kegiatan SKP
 * yang berasal dari tabel skp_records di Supabase.
 * Properties are immutable [val] to ensure consistency and Jetpack Compose stability.
 */
data class Skp(
    @SerializedName("id")
    val id: String? = null,
    
    @SerializedName("user_key")
    val userKey: String? = null,
    
    @SerializedName("nama_kegiatan")
    val namaKegiatan: String? = null,
    
    @SerializedName("jenis_kegiatan")
    val jenisKegiatan: String? = null,
    
    @SerializedName("kategori_bidang")
    val kategoriBidang: String? = null,
    
    @SerializedName("tingkat")
    val tingkat: String? = null,
    
    @SerializedName("peran")
    val peran: String? = null,
    
    @SerializedName("mode_kegiatan")
    val modeKegiatan: String? = "Tidak Ada",
    
    @SerializedName("poin_skp")
    val poinSkp: Int = 0,
    
    @SerializedName("file_url")
    val fileUrl: String? = null,
    
    @SerializedName("file_name")
    val fileName: String? = null,
    
    @SerializedName("file_type")
    val fileType: String? = null,
    
    @SerializedName("storage_path")
    val storagePath: String? = null,
    
    @SerializedName("tanggal_input")
    val tanggalInput: String? = null,
    
    @SerializedName("timestamp")
    val timestamp: Long = 0,
    
    @SerializedName("created_at")
    val createdAt: String? = null
) : Serializable
