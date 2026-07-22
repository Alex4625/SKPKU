package com.alzen.skpku

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class ViewModelFactory(private val context: Context) : ViewModelProvider.Factory {

    private val preferenceManager by lazy { PreferenceManager(context) }
    private val repository by lazy { SkpRepository(RetrofitClient.instance) }

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(MainViewModel::class.java) -> {
                MainViewModel(repository, preferenceManager) as T
            }
            modelClass.isAssignableFrom(FormSkpViewModel::class.java) -> {
                FormSkpViewModel(repository, preferenceManager) as T
            }
            modelClass.isAssignableFrom(DetailSkpViewModel::class.java) -> {
                DetailSkpViewModel(repository) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
