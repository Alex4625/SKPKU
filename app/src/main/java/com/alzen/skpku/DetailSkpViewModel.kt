package com.alzen.skpku

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailSkpViewModel @Inject constructor(
    private val repository: SkpRepository
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _deleteSuccess = MutableSharedFlow<Unit>()
    val deleteSuccess: SharedFlow<Unit> = _deleteSuccess.asSharedFlow()

    private val _errorMessage = MutableSharedFlow<String>()
    val errorMessage: SharedFlow<String> = _errorMessage.asSharedFlow()

    fun deleteSkp(skp: Skp) {
        val id = skp.id ?: return
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Delete record from database
                val response = repository.deleteSkpRecord(id)
                if (response.isSuccessful) {
                    _deleteSuccess.emit(Unit)
                } else {
                    _errorMessage.emit("Gagal hapus data: ${response.code()}")
                }
            } catch (e: Exception) {
                _errorMessage.emit("Terjadi kesalahan: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }
}
