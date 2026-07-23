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
class AuthViewModel @Inject constructor(
    private val apiService: SupabaseApiService,
    private val preferenceManager: PreferenceManager
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _authSuccess = MutableSharedFlow<Unit>()
    val authSuccess: SharedFlow<Unit> = _authSuccess.asSharedFlow()

    private val _errorMessage = MutableSharedFlow<String>()
    val errorMessage: SharedFlow<String> = _errorMessage.asSharedFlow()

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = apiService.login(AuthRequest(email, password))
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null) {
                        // Saving to preferenceManager is enough. 
                        // The Hilt-provided Interceptor in AppModule.kt reads directly from here.
                        preferenceManager.saveAuthData(body.accessToken, body.user.id)
                        _authSuccess.emit(Unit)
                    } else {
                        _errorMessage.emit("Gagal: Body response kosong")
                    }
                } else {
                    _errorMessage.emit("Login gagal: ${response.code()}")
                }
            } catch (e: Exception) {
                _errorMessage.emit("Terjadi kesalahan: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun register(email: String, password: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = apiService.signUp(AuthRequest(email, password))
                if (response.isSuccessful) {
                    _errorMessage.emit("Registrasi berhasil. Silakan cek email konfirmasi atau langsung login.")
                } else {
                    _errorMessage.emit("Registrasi gagal: ${response.code()}")
                }
            } catch (e: Exception) {
                _errorMessage.emit("Terjadi kesalahan: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }
}
