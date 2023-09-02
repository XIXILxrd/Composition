package com.example.composition.presentation

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.composition.domain.entity.Level

class GameViewModelFactory(
    private val application: Application,
    private val level: Level
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GameViewModel::class.java)) {
            return modelClass.cast(GameViewModel(application, level)) as T
        }

        throw RuntimeException("Unknown view model class $modelClass")
    }
}