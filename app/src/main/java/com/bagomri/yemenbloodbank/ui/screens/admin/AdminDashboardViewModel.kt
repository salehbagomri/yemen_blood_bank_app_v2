package com.bagomri.yemenbloodbank.ui.screens.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bagomri.yemenbloodbank.core.util.ErrorHandler
import com.bagomri.yemenbloodbank.data.model.Banner
import com.bagomri.yemenbloodbank.data.model.Donor
import com.bagomri.yemenbloodbank.data.model.Hospital
import com.bagomri.yemenbloodbank.data.model.LocationData
import com.bagomri.yemenbloodbank.data.model.Report
import com.bagomri.yemenbloodbank.data.repository.AuthRepository
import com.bagomri.yemenbloodbank.data.repository.BannerRepository
import com.bagomri.yemenbloodbank.data.repository.DonorRepository
import com.bagomri.yemenbloodbank.data.repository.HospitalRepository
import com.bagomri.yemenbloodbank.data.repository.LocationRepository
import com.bagomri.yemenbloodbank.data.repository.ReportRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AdminDashboardUiState(
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val pendingReportsCount: Int = 0,
    val totalHospitals: Int = 0,
    val activeHospitals: Int = 0,
    val totalDonors: Int = 0,
    val availableDonors: Int = 0,
    val suspendedDonors: Int = 0,
    val inactiveDonors: Int = 0,
    val hospitals: List<Hospital> = emptyList(),
    val donors: List<Donor> = emptyList(),
    val reports: List<Report> = emptyList(),
    val banners: List<Banner> = emptyList(),
    val locationData: LocationData = LocationData(),
    val errorMessage: String? = null
)

class AdminDashboardViewModel(
    private val donorRepository: DonorRepository = DonorRepository(),
    private val hospitalRepository: HospitalRepository = HospitalRepository(),
    private val reportRepository: ReportRepository = ReportRepository(),
    private val bannerRepository: BannerRepository = BannerRepository(),
    private val locationRepository: LocationRepository = LocationRepository(),
    private val authRepository: AuthRepository = AuthRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(AdminDashboardUiState())
    val uiState: StateFlow<AdminDashboardUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    fun loadData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            val reportsResult = reportRepository.getAllReports()
            val hospitalsResult = hospitalRepository.getAllHospitals()
            val donorsResult = donorRepository.getAllDonors()
            val bannersResult = bannerRepository.getAllBanners()
            val locationsResult = locationRepository.getActiveLocations()

            val hospitalsList = hospitalsResult.getOrDefault(emptyList())
            val donorsList = donorsResult.getOrDefault(emptyList())
            val reportsList = reportsResult.getOrDefault(emptyList())

            val pendingReports = reportsList.count { it.status == "pending" }
            val activeHosp = hospitalsList.count { it.isActive }
            val availDonors = donorsList.count { it.isActive && it.canDonateNow }
            val suspDonors = donorsList.count { it.isSuspended }
            val inactDonors = donorsList.count { !it.isActive }

            _uiState.update {
                it.copy(
                    isLoading = false,
                    pendingReportsCount = pendingReports,
                    totalHospitals = hospitalsList.size,
                    activeHospitals = activeHosp,
                    totalDonors = donorsList.size,
                    availableDonors = availDonors,
                    suspendedDonors = suspDonors,
                    inactiveDonors = inactDonors,
                    hospitals = hospitalsList,
                    donors = donorsList,
                    reports = reportsList,
                    banners = bannersResult.getOrDefault(emptyList()),
                    locationData = locationsResult
                )
            }
        }
    }

    fun refresh() {
        viewModelScope.launch {
            _uiState.update { it.copy(isRefreshing = true) }

            val currentState = _uiState.value
            val reportsResult = reportRepository.getAllReports()
            val hospitalsResult = hospitalRepository.getAllHospitals()
            val donorsResult = donorRepository.getAllDonors()
            val bannersResult = bannerRepository.getAllBanners()

            val hospitalsList = hospitalsResult.getOrDefault(currentState.hospitals)
            val donorsList = donorsResult.getOrDefault(currentState.donors)
            val reportsList = reportsResult.getOrDefault(currentState.reports)

            val pendingReports = reportsList.count { r -> r.status == "pending" }
            val activeHosp = hospitalsList.count { h -> h.isActive }
            val availDonors = donorsList.count { d -> d.isActive && d.canDonateNow }
            val suspDonors = donorsList.count { d -> d.isSuspended }
            val inactDonors = donorsList.count { d -> !d.isActive }

            _uiState.update {
                it.copy(
                    isRefreshing = false,
                    pendingReportsCount = pendingReports,
                    totalHospitals = hospitalsList.size,
                    activeHospitals = activeHosp,
                    totalDonors = donorsList.size,
                    availableDonors = availDonors,
                    suspendedDonors = suspDonors,
                    inactiveDonors = inactDonors,
                    hospitals = hospitalsList,
                    donors = donorsList,
                    reports = reportsList,
                    banners = bannersResult.getOrDefault(currentState.banners)
                )
            }
        }
    }

    // إدارة المتبرعين
    fun deleteDonor(donorId: String, onComplete: () -> Unit) {
        viewModelScope.launch {
            donorRepository.deleteDonor(donorId)
            refresh()
            onComplete()
        }
    }

    fun toggleDonorStatus(donor: Donor, onComplete: () -> Unit) {
        viewModelScope.launch {
            donorRepository.toggleDonorStatus(donor.id, !donor.isActive)
            refresh()
            onComplete()
        }
    }

    // إدارة المستشفيات
    fun deleteHospital(hospitalId: String, onComplete: () -> Unit) {
        viewModelScope.launch {
            hospitalRepository.deleteHospital(hospitalId)
            refresh()
            onComplete()
        }
    }

    fun toggleHospitalStatus(hospital: Hospital, onComplete: () -> Unit) {
        viewModelScope.launch {
            hospitalRepository.toggleHospitalStatus(hospital.id, !hospital.isActive)
            refresh()
            onComplete()
        }
    }

    // إدارة البلاغات
    fun approveReport(reportId: String, onComplete: () -> Unit) {
        viewModelScope.launch {
            reportRepository.approveReport(reportId)
            refresh()
            onComplete()
        }
    }

    fun rejectReport(reportId: String, onComplete: () -> Unit) {
        viewModelScope.launch {
            reportRepository.rejectReport(reportId)
            refresh()
            onComplete()
        }
    }

    fun deleteReport(reportId: String, onComplete: () -> Unit) {
        viewModelScope.launch {
            reportRepository.deleteReport(reportId)
            refresh()
            onComplete()
        }
    }

    // إدارة البانرات
    fun deleteBanner(bannerId: String, onComplete: () -> Unit) {
        viewModelScope.launch {
            bannerRepository.deleteBanner(bannerId)
            refresh()
            onComplete()
        }
    }

    fun toggleBannerStatus(banner: Banner, onComplete: () -> Unit) {
        viewModelScope.launch {
            bannerRepository.toggleBannerStatus(banner.id, !banner.isActive)
            refresh()
            onComplete()
        }
    }

    fun logout(onComplete: () -> Unit) {
        viewModelScope.launch {
            authRepository.signOut()
            onComplete()
        }
    }
}
