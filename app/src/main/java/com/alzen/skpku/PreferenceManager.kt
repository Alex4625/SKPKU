package com.alzen.skpku

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class PreferenceManager(private val context: Context) {

    companion object {
        val USER_KEY = stringPreferencesKey("user_key")
        val STUDENT_NAME = stringPreferencesKey("nama_mahasiswa")
    }

    val userKeyFlow: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[USER_KEY] ?: ""
    }

    val studentNameFlow: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[STUDENT_NAME] ?: ""
    }

    suspend fun saveStudentName(name: String) {
        context.dataStore.edit { preferences ->
            preferences[STUDENT_NAME] = name
        }
    }

    suspend fun getOrCreateUserKey(): String {
        var currentKey = ""
        context.dataStore.edit { preferences ->
            val key = preferences[USER_KEY]
            if (key.isNullOrEmpty()) {
                val newKey = UUID.randomUUID().toString()
                preferences[USER_KEY] = newKey
                currentKey = newKey
            } else {
                currentKey = key
            }
        }
        return currentKey
    }
}
