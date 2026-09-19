package com.bagomri.yemenbloodbank.ui.screens.reports

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bagomri.yemenbloodbank.core.util.ErrorHandler
import com.bagomri.yemenbloodbank.core.util.PhoneUtils
import com.bagomri.yemenbloodbank.data.model.Donor
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
    val notes: String = "",
    val donor: Donor? = null,
    val availablePhones: List<String> = emptyList()
)

class ReportDonorViewModel(
    private val reportRepository: ReportRepository = ReportRepository(),
    private val donorRepository: DonorRepository = DonorRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(ReportDonorUiState())
    val uiState: StateFlow<ReportDonorUiState> = _uiState.asStateFlow()

    fun setInitialData(donorId: String?, phone: String?) {
        val safeDonorId = donorId ?: ""
        val safePhone = phone ?: ""

        _uiState.update {
            it.copy(
                donorId = safeDonorId,
                phoneNumber = safePhone,
                availablePhones = if (safePhone.isNotBlank()) listOf(safePhone) else emptyList()
            )
        }

        if (safeDonorId.isNotBlank()) {
            viewModelScope.launch {
                val donor = donorRepository.getDonorById(safeDonorId).getOrNull()
                if (donor != null) {
                    _uiState.update { state ->
                        state.copy(
                            donor = donor,
                            availablePhones = donor.allPhoneNumbers,
                            phoneNumber = if (state.phoneNumber.isBlank()) donor.phoneNumber else state.phoneNumber
                        )
                    }
                }
            }
        } else if (safePhone.isNotBlank()) {
            viewModelScope.launch {
                val clean = PhoneUtils.cleanLocalPhone(safePhone)
                val donor = donorRepository.findDonorByPhone(clean).getOrNull()
                    ?: donorRepository.findDonorByPhone(safePhone).getOrNull()
                if (donor != null) {
                    _uiState.update { state ->
                        state.copy(
                            donorId = donor.id,
                            donor = donor,
                            availablePhones = donor.allPhoneNumbers
                        )
                    }
                }
            }
        }
    }

    fun onPhoneNumberChange(phone: String) = _uiState.update { it.copy(phoneNumber = phone) }
    fun onReasonChange(reason: String) = _uiState.update { it.copy(selectedReason = reason) }
    fun onNotesChange(notes: String) = _uiState.update { it.copy(notes = notes) }

    fun submitReport() {
        val state = _uiState.value
        val enteredPhone = state.phoneNumber.trim()

        if (enteredPhone.isBlank()) {
            _uiState.update { it.copy(errorMessage = "يرجى إدخال رقم الهاتف المراد الإبلاغ عنه") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            val cleanEntered = PhoneUtils.cleanLocalPhone(enteredPhone)
            var resolvedDonorId = state.donorId

            // 1. هل يطابق الرقم المتبرع الموجود في الذاكرة؟
            val matchesLoadedDonor = state.donor?.allPhoneNumbers?.any {
                PhoneUtils.cleanLocalPhone(it) == cleanEntered || it.trim() == enteredPhone
            } == true

            if (!matchesLoadedDonor || resolvedDonorId.isBlank()) {
                // البحث في قاعدة البيانات عن المتبرع صاحب هذا الرقم
                val foundDonor = donorRepository.findDonorByPhone(cleanEntered).getOrNull()
                    ?: donorRepository.findDonorByPhone(enteredPhone).getOrNull()
                if (foundDonor != null) {
                    resolvedDonorId = foundDonor.id
                }
            }

            // إذا ما زال غير معروف وكان donorId موجوداً أصلاً، نستخدمه
            if (resolvedDonorId.isBlank() && state.donorId.isNotBlank()) {
                resolvedDonorId = state.donorId
            }

            if (resolvedDonorId.isBlank()) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "رقم الهاتف غير مسجل في قاعدة بيانات المتبرعين"
                    )
                }
                return@launch
            }

            val result = reportRepository.addReport(
                donorId = resolvedDonorId,
                donorPhoneNumber = enteredPhone,
                reason = state.selectedReason,
                notes = state.notes.trim().ifEmpty { null }
            )

            result.fold(
                onSuccess = {
                    _uiState.update { it.copy(isLoading = false, isSuccess = true) }
                },
                onFailure = { error ->
                    Log.e("ReportDonorVM", "Failed to submit report: ${error.message}", error)
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
