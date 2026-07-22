package com.alzen.skpku

/**
 * Object ini berisi aturan poin SKP.
 * Semua poin dihitung otomatis berdasarkan Buku Pedoman SKP.
 * Tujuannya agar mahasiswa tidak menginput poin secara manual.
 */
object SkpRule {

    const val KATEGORI_WAJIB = "Kegiatan Wajib Institusi"
    const val KATEGORI_ORGANISASI = "Bidang Organisasi dan Kepemimpinan"
    const val KATEGORI_PENALARAN = "Bidang Penalaran dan Keilmuan"
    const val KATEGORI_MINAT = "Bidang Minat dan Bakat"
    const val KATEGORI_PENGABDIAN = "Bidang Pengabdian Kepada Masyarakat"

    const val MODE_TIDAK_ADA = "Tidak Ada"
    const val MODE_ONLINE = "Online"
    const val MODE_OFFLINE = "Offline"

    /**
     * Daftar kategori utama SKP.
     */
    fun getKategoriList() = listOf(
        KATEGORI_WAJIB,
        KATEGORI_ORGANISASI,
        KATEGORI_PENALARAN,
        KATEGORI_MINAT,
        KATEGORI_PENGABDIAN
    )

    /**
     * Menentukan jenis kegiatan (Wajib atau Pilihan).
     */
    fun getJenisKegiatan(kategori: String) = if (kategori == KATEGORI_WAJIB) "Wajib" else "Pilihan"

    /**
     * Daftar kegiatan berdasarkan kategori yang dipilih.
     */
    fun getKegiatanList(kategori: String): List<String> = when (kategori) {
        KATEGORI_WAJIB -> listOf(
            "PKKMB", "Dies Natalis", "Upacara Bendera 17 Agustus",
            "Alumni Pulang Campus", "Seminar Institut Bisnis dan Teknologi Indonesia",
            "Seminar Ormawa Institut Bisnis dan Teknologi Indonesia"
        )
        KATEGORI_ORGANISASI -> listOf(
            "Pengurus Organisasi", "Panitia dalam Kegiatan Kemahasiswaan",
            "Pendukung / Pengisi / Partisipan Kegiatan"
        )
        KATEGORI_PENALARAN -> listOf(
            "Prestasi Lomba Karya Tulis / Inovasi", "Forum Ilmiah", "Karya Ilmiah Jurnal",
            "Karya Ilmiah Prosiding", "Karya di Majalah / Surat Kabar",
            "Menghadiri Seminar Proposal / Skripsi", "Asisten Lab 1 Semester",
            "Workshop Institusi", "SDM Projek Inovasi"
        )
        KATEGORI_MINAT -> listOf(
            "Prestasi Akademik / Non Akademik", "Latihan Rutin Ormawa",
            "Anggota Insidental Kegiatan Institut", "Model Foto / Video Publikasi",
            "Pengembangan Perangkat Pembelajaran"
        )
        KATEGORI_PENGABDIAN -> listOf(
            "Pengabdian Kepada Masyarakat", "Penanganan Bencana",
            "Studi Banding", "Magang Kerja Bukan PKL"
        )
        else -> listOf("Tidak Ada")
    }

    /**
     * Daftar tingkat berdasarkan kategori dan kegiatan.
     */
    fun getTingkatList(kategori: String, kegiatan: String): List<String> = when (kategori) {
        KATEGORI_WAJIB -> listOf("Institusi")
        KATEGORI_ORGANISASI -> when (kegiatan) {
            "Pengurus Organisasi" -> listOf("Internasional", "Nasional", "Regional", "BEM", "Himaprodi / UKM / FPK")
            "Panitia dalam Kegiatan Kemahasiswaan" -> listOf("Internasional", "Nasional", "Regional", "Institusi (PKKMB / Dies Natalis / Wisuda)", "Ormawa")
            "Pendukung / Pengisi / Partisipan Kegiatan" -> listOf("Internasional", "Nasional", "Regional", "Institusi", "Ormawa")
            else -> listOf("Tidak Ada")
        }
        KATEGORI_PENALARAN -> when (kegiatan) {
            "Prestasi Lomba Karya Tulis / Inovasi", "Forum Ilmiah", "Karya di Majalah / Surat Kabar", "SDM Projek Inovasi" ->
                listOf("Internasional", "Nasional", "Regional", "Institusi")
            "Karya Ilmiah Jurnal" -> listOf(
                "Internasional Grade A", "Internasional Grade B", "Internasional Grade C",
                "Nasional SINTA 1", "Nasional SINTA 2", "Nasional SINTA 3",
                "Nasional SINTA 4-6", "Nasional Tidak Terakreditasi"
            )
            "Karya Ilmiah Prosiding" -> listOf(
                "Internasional Terindeks Scopus", "Internasional Terindeks",
                "Internasional Tidak Terindeks ber-ISBN", "Nasional Terindeks", "Nasional Tidak Terindeks"
            )
            else -> listOf("Institusi")
        }
        KATEGORI_MINAT -> when (kegiatan) {
            "Prestasi Akademik / Non Akademik" -> listOf("Internasional", "Nasional", "Regional", "Institusi", "Ormawa")
            else -> listOf("Institusi")
        }
        KATEGORI_PENGABDIAN -> when (kegiatan) {
            "Pengabdian Kepada Masyarakat" -> listOf("Internasional", "Nasional", "Regional", "Institusi", "Ormawa")
            else -> listOf("Institusi")
        }
        else -> listOf("Tidak Ada")
    }

    /**
     * Daftar peran berdasarkan kegiatan dan tingkat.
     */
    fun getPeranList(kategori: String, kegiatan: String, tingkat: String): List<String> = when (kategori) {
        KATEGORI_WAJIB -> listOf("Peserta")
        KATEGORI_ORGANISASI -> when (kegiatan) {
            "Pengurus Organisasi", "Panitia dalam Kegiatan Kemahasiswaan" -> listOf("Pengurus Inti", "Anggota")
            "Pendukung / Pengisi / Partisipan Kegiatan" -> listOf("Partisipan")
            else -> listOf("Tidak Ada")
        }
        KATEGORI_PENALARAN -> when (kegiatan) {
            "Prestasi Lomba Karya Tulis / Inovasi" -> if (tingkat == "Ormawa") listOf("Juara") else listOf("Juara", "Peserta")
            "Forum Ilmiah" -> listOf("Pembicara", "Peserta")
            "Karya Ilmiah Jurnal", "Karya Ilmiah Prosiding" -> listOf("Ketua", "Anggota")
            "Karya di Majalah / Surat Kabar" -> listOf("Penulis")
            "Menghadiri Seminar Proposal / Skripsi", "Workshop Institusi", "SDM Projek Inovasi" -> listOf("Peserta")
            "Asisten Lab 1 Semester" -> listOf("Asisten Lab")
            else -> listOf("Tidak Ada")
        }
        KATEGORI_MINAT -> when (kegiatan) {
            "Prestasi Akademik / Non Akademik" -> listOf("Juara", "Peserta")
            "Latihan Rutin Ormawa", "Pengembangan Perangkat Pembelajaran" -> listOf("Peserta")
            "Anggota Insidental Kegiatan Institut" -> listOf("Anggota")
            "Model Foto / Video Publikasi" -> listOf("Model")
            else -> listOf("Tidak Ada")
        }
        KATEGORI_PENGABDIAN -> when (kegiatan) {
            "Penanganan Bencana" -> listOf("Relawan")
            "Magang Kerja Bukan PKL" -> listOf("Mahasiswa Magang")
            else -> listOf("Peserta")
        }
        else -> listOf("Tidak Ada")
    }

    /**
     * Mode Online/Offline jika diperlukan.
     */
    fun getModeList(kategori: String, kegiatan: String, tingkat: String, peran: String): List<String> {
        return if (kategori == KATEGORI_PENALARAN && kegiatan == "Forum Ilmiah" && peran == "Peserta") {
            listOf(MODE_ONLINE, MODE_OFFLINE)
        } else {
            listOf(MODE_TIDAK_ADA)
        }
    }

    /**
     * Method utama untuk menghitung poin SKP.
     */
    fun calculatePoint(
        kategori: String,
        kegiatan: String,
        tingkat: String,
        peran: String,
        mode: String
    ): Int = when (kategori) {
        KATEGORI_WAJIB -> calculateWajib(kegiatan)
        KATEGORI_ORGANISASI -> calculateOrganisasi(kegiatan, tingkat, peran)
        KATEGORI_PENALARAN -> calculatePenalaran(kegiatan, tingkat, peran, mode)
        KATEGORI_MINAT -> calculateMinat(kegiatan, tingkat, peran)
        KATEGORI_PENGABDIAN -> calculatePengabdian(kegiatan, tingkat)
        else -> 0
    }

    private fun calculateWajib(kegiatan: String) = when (kegiatan) {
        "PKKMB" -> 25
        "Dies Natalis" -> 15
        "Upacara Bendera 17 Agustus" -> 10
        "Alumni Pulang Campus" -> 20
        "Seminar Institut Bisnis dan Teknologi Indonesia" -> 20
        "Seminar Ormawa Institut Bisnis dan Teknologi Indonesia" -> 10
        else -> 0
    }

    private fun calculateOrganisasi(kegiatan: String, tingkat: String, peran: String): Int = when (kegiatan) {
        "Pengurus Organisasi" -> when (tingkat to peran) {
            "Internasional" to "Pengurus Inti" -> 50
            "Internasional" to "Anggota" -> 30
            "Nasional" to "Pengurus Inti" -> 40
            "Nasional" to "Anggota" -> 20
            "Regional" to "Pengurus Inti" -> 30
            "Regional" to "Anggota" -> 10
            "BEM" to "Pengurus Inti" -> 25
            "BEM" to "Anggota" -> 15
            "Himaprodi / UKM / FPK" to "Pengurus Inti" -> 20
            "Himaprodi / UKM / FPK" to "Anggota" -> 10
            else -> 0
        }
        "Panitia dalam Kegiatan Kemahasiswaan" -> when (tingkat to peran) {
            "Internasional" to "Pengurus Inti" -> 30
            "Internasional" to "Anggota" -> 25
            "Nasional" to "Pengurus Inti" -> 25
            "Nasional" to "Anggota" -> 20
            "Regional" to "Pengurus Inti" -> 20
            "Regional" to "Anggota" -> 15
            "Institusi (PKKMB / Dies Natalis / Wisuda)" to "Pengurus Inti" -> 20
            "Institusi (PKKMB / Dies Natalis / Wisuda)" to "Anggota" -> 15
            "Ormawa" to "Pengurus Inti" -> 10
            "Ormawa" to "Anggota" -> 5
            else -> 0
        }
        "Pendukung / Pengisi / Partisipan Kegiatan" -> when (tingkat) {
            "Internasional" -> 25
            "Nasional" -> 20
            "Regional" -> 15
            "Institusi", "Ormawa" -> 10
            else -> 0
        }
        else -> 0
    }

    private fun calculatePenalaran(kegiatan: String, tingkat: String, peran: String, mode: String): Int = when (kegiatan) {
        "Prestasi Lomba Karya Tulis / Inovasi" -> when (tingkat to peran) {
            "Internasional" to "Juara" -> 50
            "Internasional" to "Peserta" -> 40
            "Nasional" to "Juara" -> 35
            "Nasional" to "Peserta" -> 25
            "Regional" to "Juara" -> 30
            "Regional" to "Peserta" -> 20
            "Institusi" to "Juara" -> 25
            "Institusi" to "Peserta" -> 15
            "Ormawa" to "Juara" -> 20
            else -> 0
        }
        "Forum Ilmiah" -> when (peran) {
            "Pembicara" -> when (tingkat) {
                "Internasional" -> 100
                "Nasional" -> 75
                "Regional" -> 50
                "Institusi" -> 25
                else -> 0
            }
            "Peserta" -> when (tingkat to mode) {
                "Internasional" to MODE_ONLINE -> 20
                "Internasional" to MODE_OFFLINE -> 30
                "Nasional" to MODE_ONLINE -> 10
                "Nasional" to MODE_OFFLINE -> 20
                "Regional" to MODE_ONLINE -> 5
                "Regional" to MODE_OFFLINE -> 15
                "Institusi" to MODE_ONLINE -> 5
                "Institusi" to MODE_OFFLINE -> 10
                else -> 0
            }
            else -> 0
        }
        "Karya Ilmiah Jurnal" -> when (tingkat to peran) {
            "Internasional Grade A" to "Ketua" -> 100
            "Internasional Grade A" to "Anggota" -> 80
            "Internasional Grade B" to "Ketua" -> 85
            "Internasional Grade B" to "Anggota" -> 60
            "Internasional Grade C" to "Ketua" -> 45
            "Internasional Grade C" to "Anggota" -> 20
            "Nasional SINTA 1" to "Ketua" -> 65
            "Nasional SINTA 1" to "Anggota" -> 40
            "Nasional SINTA 2" to "Ketua" -> 45
            "Nasional SINTA 2" to "Anggota" -> 20
            "Nasional SINTA 3" to "Ketua" -> 35
            "Nasional SINTA 3" to "Anggota" -> 25
            "Nasional SINTA 4-6" to "Ketua" -> 25
            "Nasional SINTA 4-6" to "Anggota" -> 15
            "Nasional Tidak Terakreditasi" to "Ketua" -> 20
            "Nasional Tidak Terakreditasi" to "Anggota" -> 10
            else -> 0
        }
        "Karya Ilmiah Prosiding" -> when (tingkat to peran) {
            "Internasional Terindeks Scopus" to "Ketua" -> 45
            "Internasional Terindeks Scopus" to "Anggota" -> 20
            "Internasional Terindeks" to "Ketua" -> 35
            "Internasional Terindeks" to "Anggota" -> 25
            "Internasional Tidak Terindeks ber-ISBN" to "Ketua" -> 25
            "Internasional Tidak Terindeks ber-ISBN" to "Anggota" -> 15
            "Nasional Terindeks" to "Ketua" -> 25
            "Nasional Terindeks" to "Anggota" -> 15
            "Nasional Tidak Terindeks" to "Ketua" -> 20
            "Nasional Tidak Terindeks" to "Anggota" -> 10
            else -> 0
        }
        "Karya di Majalah / Surat Kabar" -> when (tingkat) {
            "Internasional" -> 50
            "Nasional" -> 35
            "Regional" -> 25
            "Institusi" -> 10
            else -> 0
        }
        "Menghadiri Seminar Proposal / Skripsi" -> 5
        "Asisten Lab 1 Semester" -> 30
        "Workshop Institusi" -> 20
        "SDM Projek Inovasi" -> when (tingkat) {
            "Internasional" -> 100
            "Nasional" -> 75
            "Regional" -> 50
            "Institusi" -> 25
            else -> 0
        }
        else -> 0
    }

    private fun calculateMinat(kegiatan: String, tingkat: String, peran: String): Int = when (kegiatan) {
        "Prestasi Akademik / Non Akademik" -> when (tingkat to peran) {
            "Internasional" to "Juara" -> 50
            "Internasional" to "Peserta" -> 40
            "Nasional" to "Juara" -> 35
            "Nasional" to "Peserta" -> 25
            "Regional" to "Juara" -> 30
            "Regional" to "Peserta" -> 20
            "Institusi" to "Juara" -> 25
            "Institusi" to "Peserta" -> 15
            "Ormawa" to "Juara" -> 20
            "Ormawa" to "Peserta" -> 5
            else -> 0
        }
        "Latihan Rutin Ormawa" -> 25
        "Anggota Insidental Kegiatan Institut" -> 15
        "Model Foto / Video Publikasi" -> 10
        "Pengembangan Perangkat Pembelajaran" -> 35
        else -> 0
    }

    private fun calculatePengabdian(kegiatan: String, tingkat: String): Int = when (kegiatan) {
        "Pengabdian Kepada Masyarakat" -> when (tingkat) {
            "Internasional" -> 60
            "Nasional" -> 50
            "Regional" -> 30
            "Institusi" -> 20
            "Ormawa" -> 10
            else -> 0
        }
        "Penanganan Bencana" -> 50
        "Studi Banding" -> 25
        "Magang Kerja Bukan PKL" -> 35
        else -> 0
    }

    fun getQuotaLimit(kegiatan: String, tingkat: String): Int = when {
        kegiatan == "Seminar Institut Bisnis dan Teknologi Indonesia" -> 3
        kegiatan == "Seminar Ormawa Institut Bisnis dan Teknologi Indonesia" -> 2
        kegiatan == "Panitia dalam Kegiatan Kemahasiswaan" && tingkat == "Ormawa" -> 6
        kegiatan == "Forum Ilmiah" -> 3
        else -> -1
    }

    fun hasQuotaLimit(kegiatan: String, tingkat: String) = getQuotaLimit(kegiatan, tingkat) > 0

    fun getWajibActivities() = listOf(
        "PKKMB", "Dies Natalis", "Upacara Bendera 17 Agustus",
        "Alumni Pulang Campus", "Seminar Institut Bisnis dan Teknologi Indonesia",
        "Seminar Ormawa Institut Bisnis dan Teknologi Indonesia"
    )
}
