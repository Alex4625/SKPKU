package com.alzen.skpku

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Manages user preferences and authentication data using EncryptedSharedPreferences.
 * This provides a secure way to store sensitive information like access tokens.
 */
class PreferenceManager(context: Context) {

    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val sharedPreferences: SharedPreferences = EncryptedSharedPreferences.create(
        context,
        "secure_settings",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    private val _accessTokenFlow = MutableStateFlow(getAccessTokenSync())
    val accessTokenFlow: Flow<String> = _accessTokenFlow.asStateFlow()

    private val _userIdFlow = MutableStateFlow(getUserIdSync())
    val userIdFlow: Flow<String> = _userIdFlow.asStateFlow()

    private val _studentNameFlow = MutableStateFlow(getStudentNameSync())
    val studentNameFlow: Flow<String> = _studentNameFlow.asStateFlow()

    companion object {
        private const val KEY_ACCESS_TOKEN = "access_token"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_STUDENT_NAME = "nama_mahasiswa"
    }

    fun saveAuthData(token: String, userId: String) {
        sharedPreferences.edit().apply {
            putString(KEY_ACCESS_TOKEN, token)
            putString(KEY_USER_ID, userId)
            apply()
        }
        _accessTokenFlow.value = token
        _userIdFlow.value = userId
    }

    fun saveStudentName(name: String) {
        sharedPreferences.edit().putString(KEY_STUDENT_NAME, name).apply()
        _studentNameFlow.value = name
    }

    private fun getAccessTokenSync(): String = sharedPreferences.getString(KEY_ACCESS_TOKEN, "") ?: ""
    
    private fun getUserIdSync(): String = sharedPreferences.getString(KEY_USER_ID, "") ?: ""
    
    private fun getStudentNameSync(): String = sharedPreferences.getString(KEY_STUDENT_NAME, "") ?: ""

    fun getAccessToken(): String = getAccessTokenSync()

    fun clearAuthData() {
        sharedPreferences.edit().apply {
            remove(KEY_ACCESS_TOKEN)
            remove(KEY_USER_ID)
            apply()
        }
        _accessTokenFlow.value = ""
        _userIdFlow.value = ""
    }
}
