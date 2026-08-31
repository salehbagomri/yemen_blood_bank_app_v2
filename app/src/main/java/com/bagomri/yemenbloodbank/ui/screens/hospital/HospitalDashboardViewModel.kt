package com.bagomri.yemenbloodbank.ui.screens.hospital

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bagomri.yemenbloodbank.core.util.ErrorHandler
import com.bagomri.yemenbloodbank.data.model.DashboardStatistics
import com.bagomri.yemenbloodbank.data.model.Donor
import com.bagomri.yemenbloodbank.data.repository.AuthRepository
import com.bagomri.yemenbloodbank.data.repository.DonorRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class HospitalDashboardUiState(
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val hospitalGovernorate: String? = null,
    val statistics: DashboardStatistics = DashboardStatistics(),
    val suspendedDonors: List<Donor> = emptyList(),
    val donors: List<Donor> = emptyList(),
    val errorMessage: String? = null
)

class HospitalDashboardViewModel(
    private val donorRepository: DonorRepository = DonorRepository(),
    private val authRepository: AuthRepository = AuthRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(HospitalDashboardUiState())
    val uiState: StateFlow<HospitalDashboardUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    fun loadData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            val govResult = authRepository.getCurrentHospitalGovernorate()
            val gov = govResult.getOrNull()

            val statsResult = donorRepository.getGovernorateStats(gov)
            val suspendedResult = donorRepository.getSuspendedDonors(gov)
            val donorsResult = donorRepository.getDonorsByGovernorate(gov)

            _uiState.update {
                it.copy(
                    isLoading = false,
                    hospitalGovernorate = gov,
                    statistics = statsResult.getOrDefault(DashboardStatistics()),
                    suspendedDonors = suspendedResult.getOrDefault(emptyList()),
                    donors = donorsResult.getOrDefault(emptyList())
                )
            }
        }
    }

    fun refresh() {
        viewModelScope.launch {
            _uiState.update { it.copy(isRefreshing = true) }

            val gov = _uiState.value.hospitalGovernorate
            val statsResult = donorRepository.getGovernorateStats(gov)
            val suspendedResult = donorRepository.getSuspendedDonors(gov)
            val donorsResult = donorRepository.getDonorsByGovernorate(gov)

            _uiState.update {
                it.copy(
                    isRefreshing = false,
                    statistics = statsResult.getOrDefault(it.statistics),
                    suspendedDonors = suspendedResult.getOrDefault(it.suspendedDonors),
                    donors = donorsResult.getOrDefault(it.donors)
                )
            }
        }
    }

    fun suspendDonor(donorId: String, onComplete: () -> Unit) {
        viewModelScope.launch {
            donorRepository.suspendDonor(donorId)
            refresh()
            onComplete()
        }
    }

    fun updateDonationDate(donorId: String, onComplete: () -> Unit) {
        viewModelScope.launch {
            donorRepository.updateDonationDate(donorId)
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
