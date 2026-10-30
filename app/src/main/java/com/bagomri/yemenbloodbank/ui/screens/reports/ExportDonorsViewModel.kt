package com.bagomri.yemenbloodbank.ui.screens.reports

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bagomri.yemenbloodbank.core.util.DonorReportExporter
import com.bagomri.yemenbloodbank.core.util.ErrorHandler
import com.bagomri.yemenbloodbank.data.model.Donor
import com.bagomri.yemenbloodbank.data.repository.AuthRepository
import com.bagomri.yemenbloodbank.data.repository.DonorRepository
import com.bagomri.yemenbloodbank.data.repository.HospitalRepository
import com.bagomri.yemenbloodbank.data.repository.LocationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File

enum class AvailabilityFilter(val label: String) {
    ALL("الجميع"),
    ELIGIBLE_NOW("جاهزون للتبرع الآن"),
    SUSPENDED("في فترة راحة / موقوفون")
}

enum class SortOption(val label: String) {
    NEWEST("الأحدث تسجيلاً"),
    NAME_ASC("الاسم أبجدياً"),
    BLOOD_TYPE("فصيلة الدم")
}

data class ExportDonorsUiState(
    val isLoading: Boolean = false,
    val isExporting: Boolean = false,
    val userType: String = "hospital", // "admin" or "hospital"
    val organizationName: String = "بنك دم اليمن",
    val hospitalGovernorate: String? = null,
    val allDonors: List<Donor> = emptyList(),
    val filteredDonors: List<Donor> = emptyList(),
    val availableGovernorates: List<String> = emptyList(),
    val districtsMap: Map<String, List<String>> = emptyMap(),
    // فلاتر التصفية
    val selectedBloodTypes: Set<String> = emptySet(), // Empty = جميع الفصائل
    val availabilityFilter: AvailabilityFilter = AvailabilityFilter.ALL,
    val selectedGovernorate: String? = null,
    val selectedDistrict: String? = null,
    val selectedGender: String? = null, // null = الكل, "male", "female"
    val sortOption: SortOption = SortOption.NEWEST,
    // خيارات الإخراج
    val includeSecondaryPhones: Boolean = true,
    val includeNotes: Boolean = false,
    // حالة الإخراج والتنبيهات
    val exportSuccessMessage: String? = null,
    val errorMessage: String? = null,
    val lastExportedFile: File? = null,
    val lastExportedMimeType: String? = null,
    val showPreviewSheet: Boolean = false
)

class ExportDonorsViewModel(
    private val donorRepository: DonorRepository = DonorRepository(),
    private val authRepository: AuthRepository = AuthRepository(),
    private val hospitalRepository: HospitalRepository = HospitalRepository(),
    private val locationRepository: LocationRepository = LocationRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(ExportDonorsUiState())
    val uiState: StateFlow<ExportDonorsUiState> = _uiState.asStateFlow()

    init {
        loadInitialData()
    }

    fun loadInitialData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                // 1. تحديد نوع المستخدم
                val userTypeResult = authRepository.getUserType()
                val type = userTypeResult.getOrNull() ?: "hospital"

                // 2. تحميل الموقع والصلاحيات
                var gov: String? = null
                var orgName = "بنك دم اليمن"

                if (type == "hospital") {
                    val govResult = authRepository.getCurrentHospitalGovernorate()
                    gov = govResult.getOrNull()

                    val currentUid = authRepository.currentUserId
                    if (currentUid != null) {
                        val hospitalProfile = hospitalRepository.getHospitalById(currentUid).getOrNull()
                        if (hospitalProfile != null && hospitalProfile.name.isNotBlank()) {
                            orgName = hospitalProfile.name
                        } else {
                            orgName = "المستشفى ($gov)"
                        }
                    }
                } else {
                    orgName = "الإدارة العامة لبنك دم اليمن"
                }

                // 3. جلب قائمة المناطق
                val locationData = locationRepository.getActiveLocations()

                // 4. جلب المتبرعين وفقاً للنطاق الجغرافي
                val donorsResult = if (type == "hospital" && !gov.isNullOrEmpty()) {
                    donorRepository.getDonorsByGovernorate(gov)
                } else {
                    donorRepository.getAllDonors()
                }

                val loadedDonors = donorsResult.getOrDefault(emptyList())

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        userType = type,
                        organizationName = orgName,
                        hospitalGovernorate = gov,
                        selectedGovernorate = gov,
                        availableGovernorates = locationData.governorates,
                        districtsMap = locationData.districtsByGov,
                        allDonors = loadedDonors
                    )
                }

                applyFilters()
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = ErrorHandler.getArabicMessage(e)
                    )
                }
            }
        }
    }

    // --- توابع التحكم بالفلاتر ---

    fun toggleBloodType(bloodType: String) {
        _uiState.update { current ->
            val updated = current.selectedBloodTypes.toMutableSet()
            if (updated.contains(bloodType)) {
                updated.remove(bloodType)
            } else {
                updated.add(bloodType)
            }
            current.copy(selectedBloodTypes = updated)
        }
        applyFilters()
    }

    fun selectUniversalDonors() {
        _uiState.update { it.copy(selectedBloodTypes = setOf("O-", "O+")) }
        applyFilters()
    }

    fun selectAllBloodTypes() {
        _uiState.update { it.copy(selectedBloodTypes = emptySet()) }
        applyFilters()
    }

    fun setAvailabilityFilter(filter: AvailabilityFilter) {
        _uiState.update { it.copy(availabilityFilter = filter) }
        applyFilters()
    }

    fun setGovernorate(gov: String?) {
        if (_uiState.value.userType == "hospital") return // ممنوع للمستشفى تغيير المحافظة
        _uiState.update { it.copy(selectedGovernorate = gov, selectedDistrict = null) }
        applyFilters()
    }

    fun setDistrict(district: String?) {
        _uiState.update { it.copy(selectedDistrict = district) }
        applyFilters()
    }

    fun setGender(gender: String?) {
        _uiState.update { it.copy(selectedGender = gender) }
        applyFilters()
    }

    fun setSortOption(sort: SortOption) {
        _uiState.update { it.copy(sortOption = sort) }
        applyFilters()
    }

    fun toggleIncludeSecondaryPhones() {
        _uiState.update { it.copy(includeSecondaryPhones = !it.includeSecondaryPhones) }
    }

    fun toggleIncludeNotes() {
        _uiState.update { it.copy(includeNotes = !it.includeNotes) }
    }

    fun togglePreviewSheet(show: Boolean) {
        _uiState.update { it.copy(showPreviewSheet = show) }
    }

    fun clearExportMessages() {
        _uiState.update {
            it.copy(
                exportSuccessMessage = null,
                errorMessage = null,
                lastExportedFile = null,
                lastExportedMimeType = null
            )
        }
    }

    fun resetFilters() {
        val gov = _uiState.value.hospitalGovernorate
        _uiState.update {
            it.copy(
                selectedBloodTypes = emptySet(),
                availabilityFilter = AvailabilityFilter.ALL,
                selectedGovernorate = gov,
                selectedDistrict = null,
                selectedGender = null,
                sortOption = SortOption.NEWEST,
                includeSecondaryPhones = true,
                includeNotes = false
            )
        }
        applyFilters()
    }

    // --- منطق الفلترة الحي والمباشر ---

    private fun applyFilters() {
        val state = _uiState.value
        var result = state.allDonors

        // 1. فلترة المحافظة
        if (!state.selectedGovernorate.isNullOrEmpty()) {
            result = result.filter { donor ->
                donor.governorate.equals(state.selectedGovernorate, ignoreCase = true)
            }
        }

        // 2. فلترة المديرية
        if (!state.selectedDistrict.isNullOrEmpty()) {
            result = result.filter { donor ->
                donor.subDistrict.equals(state.selectedDistrict, ignoreCase = true) ||
                donor.district.contains(state.selectedDistrict, ignoreCase = true)
            }
        }

        // 3. فلترة فصائل الدم
        if (state.selectedBloodTypes.isNotEmpty()) {
            result = result.filter { donor ->
                state.selectedBloodTypes.contains(donor.bloodType)
            }
        }

        // 4. فلترة حالة الجاهزية والأهلية
        result = when (state.availabilityFilter) {
            AvailabilityFilter.ALL -> result
            AvailabilityFilter.ELIGIBLE_NOW -> result.filter { it.canDonateNow }
            AvailabilityFilter.SUSPENDED -> result.filter { it.isSuspended || !it.canDonateNow }
        }

        // 5. فلترة الجنس
        if (!state.selectedGender.isNullOrEmpty()) {
            result = result.filter { donor ->
                donor.gender.equals(state.selectedGender, ignoreCase = true)
            }
        }

        // 6. الترتيب
        result = when (state.sortOption) {
            SortOption.NEWEST -> result.sortedByDescending { it.createdAt ?: "" }
            SortOption.NAME_ASC -> result.sortedBy { it.name }
            SortOption.BLOOD_TYPE -> result.sortedBy { it.bloodType }
        }

        _uiState.update { it.copy(filteredDonors = result) }
    }

    // --- توليد نص ملخص الفلاتر ---

    private fun buildFilterSummary(): String {
        val state = _uiState.value
        val parts = mutableListOf<String>()

        if (state.selectedBloodTypes.isEmpty()) {
            parts.add("كافة الفصائل")
        } else {
            parts.add("فصائل: ${state.selectedBloodTypes.joinToString("، ")}")
        }

        parts.add(state.availabilityFilter.label)

        if (!state.selectedGovernorate.isNullOrEmpty()) {
            val dist = state.selectedDistrict ?: "كافة المديريات"
            parts.add("${state.selectedGovernorate} ($dist)")
        } else {
            parts.add("كافة المحافظات")
        }

        if (state.selectedGender == "male") parts.add("ذكور فقط")
        if (state.selectedGender == "female") parts.add("إناث فقط")

        return parts.joinToString(" • ")
    }

    // --- عمليات التصدير ---

    fun exportPdf(context: Context, onComplete: ((File) -> Unit)? = null) {
        val state = _uiState.value
        if (state.filteredDonors.isEmpty()) {
            _uiState.update { it.copy(errorMessage = "لا يوجد متبرعون يطابقون هذه الفلاتر للتصدير") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isExporting = true, errorMessage = null, exportSuccessMessage = null) }
            val gov = state.selectedGovernorate ?: state.hospitalGovernorate ?: "عموم الجمهورية اليمنية"
            val summary = buildFilterSummary()

            val result = DonorReportExporter.generatePdfReport(
                context = context,
                donors = state.filteredDonors,
                title = "كشف متبرعي الدم",
                organizationName = state.organizationName,
                governorate = gov,
                filterSummary = summary,
                includeSecondaryPhones = state.includeSecondaryPhones,
                includeNotes = state.includeNotes
            )

            result.onSuccess { file ->
                _uiState.update {
                    it.copy(
                        isExporting = false,
                        exportSuccessMessage = "تم إنشاء ملف PDF بنجاح (${state.filteredDonors.size} متبرع)",
                        lastExportedFile = file,
                        lastExportedMimeType = "application/pdf"
                    )
                }
                onComplete?.invoke(file)
            }.onFailure { err ->
                _uiState.update {
                    it.copy(
                        isExporting = false,
                        errorMessage = "تعذر توليد ملف PDF: ${err.message}"
                    )
                }
            }
        }
    }

    fun exportCsv(context: Context, onComplete: ((File) -> Unit)? = null) {
        val state = _uiState.value
        if (state.filteredDonors.isEmpty()) {
            _uiState.update { it.copy(errorMessage = "لا يوجد متبرعون يطابقون هذه الفلاتر للتصدير") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isExporting = true, errorMessage = null, exportSuccessMessage = null) }
            val gov = state.selectedGovernorate ?: state.hospitalGovernorate ?: "عموم الجمهورية اليمنية"
            val summary = buildFilterSummary()

            val result = DonorReportExporter.generateCsvReport(
                context = context,
                donors = state.filteredDonors,
                title = "كشف متبرعي الدم",
                organizationName = state.organizationName,
                governorate = gov,
                filterSummary = summary
            )

            result.onSuccess { file ->
                _uiState.update {
                    it.copy(
                        isExporting = false,
                        exportSuccessMessage = "تم إنشاء جدول إكسل CSV بنجاح (${state.filteredDonors.size} متبرع)",
                        lastExportedFile = file,
                        lastExportedMimeType = "text/csv"
                    )
                }
                onComplete?.invoke(file)
            }.onFailure { err ->
                _uiState.update {
                    it.copy(
                        isExporting = false,
                        errorMessage = "تعذر توليد ملف الإكسل: ${err.message}"
                    )
                }
            }
        }
    }
}
