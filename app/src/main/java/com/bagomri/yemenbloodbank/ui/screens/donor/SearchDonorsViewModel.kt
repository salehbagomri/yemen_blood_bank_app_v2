package com.bagomri.yemenbloodbank.ui.screens.donor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bagomri.yemenbloodbank.core.util.ErrorHandler
import com.bagomri.yemenbloodbank.data.model.Donor
import com.bagomri.yemenbloodbank.data.model.LocationData
import com.bagomri.yemenbloodbank.data.repository.DonorRepository
import com.bagomri.yemenbloodbank.data.repository.LocationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SearchDonorsUiState(
    val isLoading: Boolean = false,
    val selectedBloodType: String? = null,
    val selectedGovernorate: String? = null,
    val selectedSubDistrict: String? = null,
    val selectedGender: String? = null,
    val sortBy: String = "name", // name, district, blood_type
    val locationData: LocationData = LocationData(),
    val subDistricts: List<String> = emptyList(),
    val donors: List<Donor> = emptyList(),
    val filteredDonors: List<Donor> = emptyList(),
    val hasSearched: Boolean = false,
    val errorMessage: String? = null
)

class SearchDonorsViewModel(
    private val donorRepository: DonorRepository = DonorRepository(),
    private val locationRepository: LocationRepository = LocationRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(SearchDonorsUiState())
    val uiState: StateFlow<SearchDonorsUiState> = _uiState.asStateFlow()

    init {
        loadLocations()
    }

    private fun loadLocations() {
        viewModelScope.launch {
            val locations = locationRepository.getActiveLocations()
            _uiState.update { it.copy(locationData = locations) }
        }
    }

    fun selectBloodType(bloodType: String?) {
        val newType = if (_uiState.value.selectedBloodType == bloodType) null else bloodType
        _uiState.update { it.copy(selectedBloodType = newType) }
        performSearch()
    }

    fun selectGovernorate(gov: String?) {
        val subDists = if (gov != null) {
            _uiState.value.locationData.districtsByGov[gov] ?: emptyList()
        } else {
            emptyList()
        }

        _uiState.update {
            it.copy(
                selectedGovernorate = gov,
                selectedSubDistrict = null,
                subDistricts = subDists
            )
        }
        performSearch()
    }

    fun selectSubDistrict(subDistrict: String?) {
        _uiState.update { it.copy(selectedSubDistrict = subDistrict) }
        performSearch()
    }

    fun selectGender(gender: String?) {
        val newGender = if (_uiState.value.selectedGender == gender) null else gender
        _uiState.update {
            it.copy(
                selectedGender = newGender,
                filteredDonors = applyLocalFilters(it.donors, newGender, it.sortBy)
            )
        }
    }

    fun selectSortBy(sortBy: String) {
        _uiState.update {
            it.copy(
                sortBy = sortBy,
                filteredDonors = applyLocalFilters(it.donors, it.selectedGender, sortBy)
            )
        }
    }

    fun performSearch() {
        val current = _uiState.value
        if (current.selectedBloodType == null && current.selectedGovernorate == null) {
            _uiState.update {
                it.copy(
                    hasSearched = false,
                    donors = emptyList(),
                    filteredDonors = emptyList()
                )
            }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, hasSearched = true, errorMessage = null) }

            val districtFilter = if (current.selectedGovernorate != null && current.selectedSubDistrict != null) {
                "${current.selectedGovernorate} - ${current.selectedSubDistrict}"
            } else {
                null
            }

            val result = donorRepository.searchDonors(
                bloodType = current.selectedBloodType,
                governorate = current.selectedGovernorate,
                district = districtFilter,
                availableOnly = true
            )

            result.fold(
                onSuccess = { donorsList ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            donors = donorsList,
                            filteredDonors = applyLocalFilters(donorsList, it.selectedGender, it.sortBy)
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

    private fun applyLocalFilters(donors: List<Donor>, gender: String?, sortBy: String): List<Donor> {
        var list = donors
        if (gender != null) {
            list = list.filter { it.gender == gender }
        }

        return when (sortBy) {
            "district" -> list.sortedBy { it.district }
            "blood_type" -> list.sortedBy { it.bloodType }
            else -> list.sortedBy { it.name }
        }
    }

    fun clearAll() {
        _uiState.update {
            SearchDonorsUiState(locationData = it.locationData)
        }
    }
}
