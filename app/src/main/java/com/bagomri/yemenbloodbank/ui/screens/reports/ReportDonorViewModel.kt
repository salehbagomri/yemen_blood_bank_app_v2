package com.bagomri.yemenbloodbank.ui.screens.reports

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bagomri.yemenbloodbank.core.util.ErrorHandler
import com.bagomri.yemenbloodbank.data.repository.DonorRepository
import com.bagomri.yemenbloodbank.data.repository.ReportRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ReportDonorUiState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null,
    val donorId: String = "",
    val phoneNumber: String = "",
    val selectedReason: String = "number_not_working",
    val notes: String = ""
)

class ReportDonorViewModel(
    private val reportRepository: ReportRepository = ReportRepository(),
    private val donorRepository: DonorRepository = DonorRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(ReportDonorUiState())
    val uiState: StateFlow<ReportDonorUiState> = _uiState.asStateFlow()

    fun setInitialData(donorId: String?, phone: String?) {
        _uiState.update {
            it.copy(
                donorId = donorId ?: "",
                phoneNumber = phone ?: ""
            )
        }
    }

    fun onPhoneNumberChange(phone: String) = _uiState.update { it.copy(phoneNumber = phone) }
    fun onReasonChange(reason: String) = _uiState.update { it.copy(selectedReason = reason) }
    fun onNotesChange(notes: String) = _uiState.update { it.copy(notes = notes) }

    fun submitReport() {
        val state = _uiState.value

        if (state.phoneNumber.isBlank()) {
            _uiState.update { it.copy(errorMessage = "يرجى إدخال رقم الهاتف المبلغ عنه") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            // إذا لم يكن donorId موجوداً نبحث عنه برقم الهاتف أولاً
            var resolvedDonorId = state.donorId
            if (resolvedDonorId.isBlank()) {
                val foundDonor = donorRepository.findDonorByPhone(state.phoneNumber).getOrNull()
                if (foundDonor != null) {
                    resolvedDonorId = foundDonor.id
                }
            }

            val result = reportRepository.addReport(
                donorId = resolvedDonorId,
                donorPhoneNumber = state.phoneNumber.trim(),
                reason = state.selectedReason,
                notes = state.notes.trim().ifEmpty { null }
            )

            result.fold(
                onSuccess = {
                    _uiState.update { it.copy(isLoading = false, isSuccess = true) }
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

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}
