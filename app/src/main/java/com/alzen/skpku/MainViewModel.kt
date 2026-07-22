package com.alzen.skpku

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class MainViewModel(
    private val repository: SkpRepository,
    private val preferenceManager: PreferenceManager
) : ViewModel() {

    private val _allSkpList = MutableStateFlow<List<Skp>>(emptyList())
    val allSkpList: StateFlow<List<Skp>> = _allSkpList.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableSharedFlow<String>()
    val errorMessage: SharedFlow<String> = _errorMessage.asSharedFlow()

    val studentName: Flow<String> = preferenceManager.studentNameFlow
    val userKey: Flow<String> = preferenceManager.userKeyFlow

    fun loadData(userKey: String) {
        if (userKey.isEmpty()) return
        
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = repository.fetchSkpRecords(userKey)
                if (response.isSuccessful) {
                    _allSkpList.value = response.body() ?: emptyList()
                } else {
                    _errorMessage.emit("Gagal load data: ${response.code()}")
                }
            } catch (e: Exception) {
                _errorMessage.emit("Terjadi kesalahan: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    suspend fun getOrCreateUserKey(): String {
        return preferenceManager.getOrCreateUserKey()
    }

    fun saveStudentName(name: String) {
        viewModelScope.launch {
            preferenceManager.saveStudentName(name)
        }
    }
}
