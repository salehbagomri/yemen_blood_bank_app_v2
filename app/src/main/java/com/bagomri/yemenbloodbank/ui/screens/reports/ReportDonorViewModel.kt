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
            var targetDonor: Donor? = null

            // 1. التحقق أولاً مما إذا كان الرقم يطابق المتبرع المحمل حالياً
            val currentDonor = state.donor
            val matchesCurrentDonor = currentDonor?.allPhoneNumbers?.any {
                PhoneUtils.cleanLocalPhone(it) == cleanEntered || it.trim() == enteredPhone
            } == true

            if (matchesCurrentDonor && currentDonor != null) {
                targetDonor = currentDonor
            } else {
                // 2. التحقق المباشر من قاعدة البيانات لمعرفة ما إذا كان الرقم مسجلاً لأي متبرع
                val dbDonor = donorRepository.findDonorByPhone(cleanEntered).getOrNull()
                    ?: donorRepository.findDonorByPhone(enteredPhone).getOrNull()
                if (dbDonor != null) {
                    targetDonor = dbDonor
                }
            }

            // إذا لم يتم العثور على الرقم بقاعدة البيانات مطلقاً: نرفض البلاغ ونظهر رسالة تنبيه واضحة
            if (targetDonor == null) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "عذراً، هذا الرقم غير مسجل لأي متبرع في قاعدة البيانات. لا يمكن إرسال بلاغ عن رقم غير موجود."
                    )
                }
                return@launch
            }

            val result = reportRepository.addReport(
                donorId = targetDonor.id,
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
