package com.bagomri.yemenbloodbank.ui.screens.donor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bagomri.yemenbloodbank.core.datastore.PreferencesManager
import com.bagomri.yemenbloodbank.core.util.ErrorHandler
import com.bagomri.yemenbloodbank.data.model.Donor
import com.bagomri.yemenbloodbank.data.model.LocationData
import com.bagomri.yemenbloodbank.data.repository.AuthRepository
import com.bagomri.yemenbloodbank.data.repository.DonorRepository
import com.bagomri.yemenbloodbank.data.repository.LocationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AddDonorUiState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null,
    val name: String = "",
    val phoneNumber: String = "",
    val phoneNumber2: String = "",
    val phoneNumber3: String = "",
    val bloodType: String = "",
    val governorate: String = "",
    val subDistrict: String = "",
    val age: String = "",
    val gender: String = "ذكر",
    val notes: String = "",
    val locationData: LocationData = LocationData(),
    val subDistricts: List<String> = emptyList(),
    val isGovernorateLocked: Boolean = false
)

class AddDonorViewModel(
    private val donorRepository: DonorRepository = DonorRepository(),
    private val locationRepository: LocationRepository = LocationRepository(),
    private val authRepository: AuthRepository = AuthRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddDonorUiState())
    val uiState: StateFlow<AddDonorUiState> = _uiState.asStateFlow()

    init {
        loadInitialData()
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            val locations = locationRepository.getActiveLocations()
            var lockedGov: String? = null
            var isLocked = false

            if (authRepository.isLoggedIn) {
                val userType = authRepository.getUserType().getOrNull()
                if (userType == "hospital") {
                    lockedGov = authRepository.getCurrentHospitalGovernorate().getOrNull()
                    if (!lockedGov.isNullOrEmpty()) {
                        isLocked = true
                    }
                }
            }

            val defaultGov = lockedGov ?: locations.governorates.firstOrNull() ?: ""
            val subDists = locations.districtsByGov[defaultGov] ?: emptyList()

            _uiState.update {
                it.copy(
                    locationData = locations,
                    governorate = defaultGov,
                    subDistricts = subDists,
                    isGovernorateLocked = isLocked
                )
            }
        }
    }

    fun onNameChange(name: String) = _uiState.update { it.copy(name = name) }
    fun onPhoneChange(phone: String) = _uiState.update { it.copy(phoneNumber = phone) }
    fun onPhone2Change(phone2: String) = _uiState.update { it.copy(phoneNumber2 = phone2) }
    fun onPhone3Change(phone3: String) = _uiState.update { it.copy(phoneNumber3 = phone3) }
    fun onBloodTypeChange(type: String) = _uiState.update { it.copy(bloodType = type) }

    fun onGovernorateChange(gov: String) {
        val subDists = _uiState.value.locationData.districtsByGov[gov] ?: emptyList()
        _uiState.update {
            it.copy(
                governorate = gov,
                subDistrict = "",
                subDistricts = subDists
            )
        }
    }

    fun onSubDistrictChange(subDistrict: String) = _uiState.update { it.copy(subDistrict = subDistrict) }
    fun onAgeChange(age: String) = _uiState.update { it.copy(age = age) }
    fun onGenderChange(gender: String) = _uiState.update { it.copy(gender = gender) }
    fun onNotesChange(notes: String) = _uiState.update { it.copy(notes = notes) }

    fun submitDonor() {
        val state = _uiState.value

        // التحقق من الحقول
        if (state.name.isBlank()) {
            _uiState.update { it.copy(errorMessage = "يرجى إدخال اسم المتبرع") }
            return
        }
        if (state.phoneNumber.isBlank() || !isValidYemeniPhone(state.phoneNumber)) {
            _uiState.update { it.copy(errorMessage = "رقم الهاتف غير صالح (9 أرقام تبدأ بـ 7)") }
            return
        }
        if (state.bloodType.isBlank()) {
            _uiState.update { it.copy(errorMessage = "يرجى اختيار فصيلة الدم") }
            return
        }
        if (state.governorate.isBlank()) {
            _uiState.update { it.copy(errorMessage = "يرجى اختيار المحافظة") }
            return
        }
        if (state.subDistrict.isBlank()) {
            _uiState.update { it.copy(errorMessage = "يرجى اختيار المديرية") }
            return
        }

        val ageInt = state.age.toIntOrNull()
        if (ageInt == null || ageInt < 17 || ageInt > 70) {
            _uiState.update { it.copy(errorMessage = "العمر يجب أن يكون بين 17 و 70 سنة") }
            return
        }

        val combinedDistrict = "${state.governorate} - ${state.subDistrict}"
        val genderCode = if (state.gender == "أنثى") "female" else "male"

        val donor = Donor(
            name = state.name.trim(),
            phoneNumber = state.phoneNumber.trim(),
            phoneNumber2 = state.phoneNumber2.trim().ifEmpty { null },
            phoneNumber3 = state.phoneNumber3.trim().ifEmpty { null },
            bloodType = state.bloodType,
            district = combinedDistrict,
            rawGovernorate = state.governorate,
            age = ageInt,
            gender = genderCode,
            notes = state.notes.trim().ifEmpty { null }
        )

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            val result = donorRepository.addDonor(donor)
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

    private fun isValidYemeniPhone(phone: String): Boolean {
        val clean = phone.trim().replace(Regex("[\\s\\-\\(\\)]"), "")
        val local = if (clean.startsWith("+967")) {
            clean.substring(4)
        } else if (clean.startsWith("967")) {
            clean.substring(3)
        } else if (clean.startsWith("00967")) {
            clean.substring(5)
        } else {
            clean
        }

        val standard = if (local.startsWith("0")) local.substring(1) else local
        return standard.length == 9 && standard.startsWith("7")
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    fun resetSuccess() {
        _uiState.update {
            it.copy(
                isSuccess = false,
                name = "",
                phoneNumber = "",
                phoneNumber2 = "",
                phoneNumber3 = "",
                bloodType = "",
                age = "",
                notes = ""
            )
        }
    }
}
