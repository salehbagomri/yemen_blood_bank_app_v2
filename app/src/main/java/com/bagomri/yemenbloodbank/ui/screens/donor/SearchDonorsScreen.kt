package com.bagomri.yemenbloodbank.ui.screens.donor

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bloodtype
import androidx.compose.material.icons.filled.ClearAll
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bagomri.yemenbloodbank.core.constants.AppColors
import com.bagomri.yemenbloodbank.core.constants.AppStrings
import com.bagomri.yemenbloodbank.ui.components.BloodTypeSelectorChip
import com.bagomri.yemenbloodbank.ui.components.CustomDropdown
import com.bagomri.yemenbloodbank.ui.components.DonorCard
import com.bagomri.yemenbloodbank.ui.components.EmptyState
import com.bagomri.yemenbloodbank.ui.components.ErrorDisplay
import com.bagomri.yemenbloodbank.ui.components.LoadingIndicator

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun SearchDonorsScreen(
    onNavigateBack: () -> Unit,
    onNavigateToReport: (String, String) -> Unit,
    viewModel: SearchDonorsViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showAdvancedFilters by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = AppStrings.searchForDonors,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = AppStrings.back,
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    if (uiState.hasSearched) {
                        IconButton(onClick = { viewModel.clearAll() }) {
                            Icon(
                                imageVector = Icons.Default.ClearAll,
                                contentDescription = "مسح الكل",
                                tint = Color.White
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = AppColors.Primary
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // صندوق الفلاتر والبحث
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                border = BorderStroke(1.dp, AppColors.Border)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // 1. القائمة المنسدلة للمحافظة
                    CustomDropdown(
                        selectedValue = uiState.selectedGovernorate,
                        items = uiState.locationData.governorates,
                        onItemSelected = { viewModel.selectGovernorate(it) },
                        label = AppStrings.district,
                        placeholder = "اختر المحافظة",
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Map,
                                contentDescription = null,
                                tint = AppColors.Primary
                            )
                        }
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // 2. القائمة المنسدلة للمديرية (تظهر وتتحدث تلقائياً)
                    CustomDropdown(
                        selectedValue = uiState.selectedSubDistrict,
                        items = uiState.subDistricts,
                        onItemSelected = { viewModel.selectSubDistrict(it) },
                        label = AppStrings.subDistrict,
                        placeholder = if (uiState.selectedGovernorate == null) "اختر المحافظة أولاً" else "اختر المديرية (اختياري)",
                        enabled = uiState.selectedGovernorate != null && uiState.subDistricts.isNotEmpty(),
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = null,
                                tint = if (uiState.selectedGovernorate != null) AppColors.Primary else AppColors.TextHint
                            )
                        }
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // 3. فصيلة الدم (8 رقاقات ملوّنة)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Bloodtype,
                                contentDescription = null,
                                tint = AppColors.Primary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = AppStrings.bloodType,
                                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                                color = AppColors.TextPrimary
                            )
                        }

                        if (uiState.selectedBloodType != null) {
                            TextButton(onClick = { viewModel.selectBloodType(null) }) {
                                Text(
                                    text = "مسح الفصيلة",
                                    color = AppColors.Error,
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        AppStrings.bloodTypes.forEach { type ->
                            BloodTypeSelectorChip(
                                bloodType = type,
                                isSelected = uiState.selectedBloodType == type,
                                onSelect = { viewModel.selectBloodType(it) }
                            )
                        }
                    }

                    // خيار الفلاتر الإضافية (الترتيب والجنس)
                    if (uiState.hasSearched) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            TextButton(onClick = { showAdvancedFilters = !showAdvancedFilters }) {
                                Icon(
                                    imageVector = Icons.Default.FilterList,
                                    contentDescription = null,
                                    tint = AppColors.Primary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (showAdvancedFilters) "إخفاء خيارات الترتيب" else "خيارات الفلترة والترتيب",
                                    color = AppColors.Primary,
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                                )
                            }
                        }

                        AnimatedVisibility(visible = showAdvancedFilters) {
                            Column(modifier = Modifier.padding(top = 4.dp)) {
                                // فلتر الجنس
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "الجنس: ",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = AppColors.TextSecondary
                                    )
                                    FilterChip(
                                        selected = uiState.selectedGender == "male",
                                        onClick = { viewModel.selectGender("male") },
                                        label = { Text("ذكر") }
                                    )
                                    FilterChip(
                                        selected = uiState.selectedGender == "female",
                                        onClick = { viewModel.selectGender("female") },
                                        label = { Text("أنثى") }
                                    )
                                }

                                // الترتيب
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "الترتيب: ",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = AppColors.TextSecondary
                                    )
                                    FilterChip(
                                        selected = uiState.sortBy == "name",
                                        onClick = { viewModel.selectSortBy("name") },
                                        label = { Text("الاسم") }
                                    )
                                    FilterChip(
                                        selected = uiState.sortBy == "district",
                                        onClick = { viewModel.selectSortBy("district") },
                                        label = { Text("المنطقة") }
                                    )
                                    FilterChip(
                                        selected = uiState.sortBy == "blood_type",
                                        onClick = { viewModel.selectSortBy("blood_type") },
                                        label = { Text("الفصيلة") }
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // قسم النتائج
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
            ) {
                when {
                    uiState.isLoading -> {
                        LoadingIndicator(message = "جاري البحث عن متبرعين...")
                    }

                    uiState.errorMessage != null -> {
                        ErrorDisplay(
                            message = uiState.errorMessage!!,
                            onRetry = { viewModel.performSearch() }
                        )
                    }

                    !uiState.hasSearched -> {
                        EmptyState(
                            title = "حدد معايير البحث",
                            message = "اختر المحافظة أو فصيلة الدم لعرض المتبرعين المتاحين للتبرع فوراً",
                            icon = Icons.Default.Search
                        )
                    }

                    uiState.filteredDonors.isEmpty() -> {
                        EmptyState(
                            title = AppStrings.noDonorsFound,
                            message = AppStrings.noDonorsMessage,
                            icon = Icons.Default.Bloodtype
                        )
                    }

                    else -> {
                        LazyColumn(modifier = Modifier.fillMaxSize()) {
                            item {
                                Spacer(modifier = Modifier.height(12.dp))
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = AppColors.SuccessContainer,
                                    border = BorderStroke(1.dp, AppColors.Success.copy(alpha = 0.2f)),
                                    modifier = Modifier.padding(bottom = 8.dp)
                                ) {
                                    Text(
                                        text = "تم العثور على ${uiState.filteredDonors.size} متبرع متاح للتبرع",
                                        color = AppColors.Success,
                                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp)
                                    )
                                }
                            }

                            items(uiState.filteredDonors, key = { it.id }) { donor ->
                                DonorCard(
                                    donor = donor,
                                    onReport = {
                                        onNavigateToReport(donor.id, donor.phoneNumber)
                                    }
                                )
                            }

                            item {
                                Spacer(modifier = Modifier.height(24.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}
