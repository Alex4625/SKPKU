package com.alzen.skpku

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * Model data SKP.
 * Class ini digunakan untuk menampung satu data kegiatan SKP
 * yang berasal dari tabel skp_records di Supabase.
 */
data class Skp(
    @SerializedName("id")
    var id: String? = null,
    
    @SerializedName("user_key")
    var userKey: String? = null,
    
    @SerializedName("nama_kegiatan")
    var namaKegiatan: String? = null,
    
    @SerializedName("jenis_kegiatan")
    var jenisKegiatan: String? = null,
    
    @SerializedName("kategori_bidang")
    var kategoriBidang: String? = null,
    
    @SerializedName("tingkat")
    var tingkat: String? = null,
    
    @SerializedName("peran")
    var peran: String? = null,
    
    @SerializedName("mode_kegiatan")
    var modeKegiatan: String? = "Tidak Ada",
    
    @SerializedName("poin_skp")
    var poinSkp: Int = 0,
    
    @SerializedName("file_url")
    var fileUrl: String? = null,
    
    @SerializedName("file_name")
    var fileName: String? = null,
    
    @SerializedName("file_type")
    var fileType: String? = null,
    
    @SerializedName("storage_path")
    var storagePath: String? = null,
    
    @SerializedName("tanggal_input")
    var tanggalInput: String? = null,
    
    @SerializedName("timestamp")
    var timestamp: Long = 0,
    
    @SerializedName("created_at")
    var createdAt: String? = null
) : Serializable
