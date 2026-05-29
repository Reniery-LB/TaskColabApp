package com.taskcolab.app.core.navigation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.taskcolab.app.data.auth.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AppLogoutViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {
    fun logout(onFinished: () -> Unit) {
        viewModelScope.launch {
            authRepository.logout()
            onFinished()
        }
    }
}
