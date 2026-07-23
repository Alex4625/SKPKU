package com.alzen.skpku

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: SkpRepository,
    private val preferenceManager: PreferenceManager
) : ViewModel() {

    /**
     * Single source of truth for the SKP list, observing the local Room database.
     */
    val allSkpList: StateFlow<List<Skp>> = repository.observeSkpRecords()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableSharedFlow<String>()
    val errorMessage: SharedFlow<String> = _errorMessage.asSharedFlow()

    val studentName: Flow<String> = preferenceManager.studentNameFlow
    val userId: Flow<String> = preferenceManager.userIdFlow

    /**
     * Refreshes the data from the network.
     * Following the Offline-First strategy, we trigger a network fetch which
     * will update the local database, and the UI will reflect changes via [allSkpList].
     */
    fun refreshData(userId: String) {
        if (userId.isEmpty()) return
        
        viewModelScope.launch {
            _isLoading.value = true
            try {
                repository.refreshSkpRecords(userId)
            } catch (e: Exception) {
                _errorMessage.emit("Gagal menyegarkan data: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun saveStudentName(name: String) {
        viewModelScope.launch {
            preferenceManager.saveStudentName(name)
        }
    }

    fun logout() {
        viewModelScope.launch {
            preferenceManager.clearAuthData()
            // Token is cleared from PreferenceManager, and the Interceptor will pick up the change.
        }
    }
}
