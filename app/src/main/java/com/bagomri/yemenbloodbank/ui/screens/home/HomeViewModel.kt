package com.bagomri.yemenbloodbank.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bagomri.yemenbloodbank.data.model.Banner
import com.bagomri.yemenbloodbank.data.model.Statistics
import com.bagomri.yemenbloodbank.data.repository.BannerRepository
import com.bagomri.yemenbloodbank.data.repository.StatisticsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class HomeUiState(
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val banners: List<Banner> = emptyList(),
    val statistics: Statistics = Statistics(),
    val errorMessage: String? = null
)

class HomeViewModel(
    private val bannerRepository: BannerRepository = BannerRepository(),
    private val statisticsRepository: StatisticsRepository = StatisticsRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    fun loadData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            val bannersResult = bannerRepository.getActiveBanners()
            val statsResult = statisticsRepository.getStatistics()

            _uiState.update {
                it.copy(
                    isLoading = false,
                    banners = bannersResult.getOrDefault(emptyList()),
                    statistics = statsResult.getOrDefault(Statistics())
                )
            }
        }
    }

    fun refresh() {
        viewModelScope.launch {
            _uiState.update { it.copy(isRefreshing = true) }

            val bannersResult = bannerRepository.getActiveBanners()
            val statsResult = statisticsRepository.getStatistics()

            _uiState.update {
                it.copy(
                    isRefreshing = false,
                    banners = bannersResult.getOrDefault(it.banners),
                    statistics = statsResult.getOrDefault(it.statistics)
                )
            }
        }
    }
}
