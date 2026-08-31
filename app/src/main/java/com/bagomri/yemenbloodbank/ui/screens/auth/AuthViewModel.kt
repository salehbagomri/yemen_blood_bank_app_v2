package com.bagomri.yemenbloodbank.ui.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bagomri.yemenbloodbank.core.datastore.PreferencesManager
import com.bagomri.yemenbloodbank.core.util.ErrorHandler
import com.bagomri.yemenbloodbank.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AuthUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val userType: String? = null, // "admin" or "hospital"
    val hospitalGovernorate: String? = null,
    val isLoginSuccessful: Boolean = false
)

class AuthViewModel(
    private val authRepository: AuthRepository = AuthRepository(),
    private val preferencesManager: PreferencesManager? = null
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun login(email: String, pass: String) {
        if (email.isBlank() || pass.isBlank()) {
            _uiState.update { it.copy(errorMessage = "يرجى إدخال البريد الإلكتروني وكلمة المرور") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            val signInResult = authRepository.signIn(email, pass)
            signInResult.fold(
                onSuccess = {
                    val userTypeResult = authRepository.getUserType()
                    val type = userTypeResult.getOrNull()

                    var hospitalGov: String? = null
                    if (type == "hospital") {
                        val govResult = authRepository.getCurrentHospitalGovernorate()
                        hospitalGov = govResult.getOrNull()
                    }

                    // حفظ في التخزين المحلي
                    preferencesManager?.setCachedUserType(type)
                    preferencesManager?.setCachedHospitalGovernorate(hospitalGov)

                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            userType = type,
                            hospitalGovernorate = hospitalGov,
                            isLoginSuccessful = true,
                            errorMessage = if (type == null) "لا يمكن تحديد نوع الحساب (غير مصرح)" else null
                        )
                    }
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = ErrorHandler.getArabicMessage(error)
                        )
                    }
                }
            )
        }
    }

    fun logout(onComplete: () -> Unit = {}) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            authRepository.signOut()
            preferencesManager?.clearSession()
            _uiState.update { AuthUiState() }
            onComplete()
        }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}
