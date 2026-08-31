package com.bagomri.yemenbloodbank.ui.screens.hospital

import android.widget.Toast
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.LocationCity
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bagomri.yemenbloodbank.core.constants.AppColors
import com.bagomri.yemenbloodbank.core.constants.AppStrings
import com.bagomri.yemenbloodbank.data.model.Donor
import com.bagomri.yemenbloodbank.ui.components.BloodTypeSelectorChip
import com.bagomri.yemenbloodbank.ui.components.CustomDropdown
import com.bagomri.yemenbloodbank.ui.components.CustomTextField
import com.bagomri.yemenbloodbank.ui.components.DonorCard
import com.bagomri.yemenbloodbank.ui.components.EmptyState
import com.bagomri.yemenbloodbank.ui.components.LoadingIndicator

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun HospitalAdvancedSearchScreen(
    onNavigateBack: () -> Unit,
    viewModel: HospitalDashboardViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    var keyword by remember { mutableStateOf("") }
    var selectedBloodType by remember { mutableStateOf<String?>(null) }
    var selectedSubDistrict by remember { mutableStateOf<String?>(null) }
    var selectedStatus by remember { mutableStateOf("all") }
    var selectedGender by remember { mutableStateOf<String?>(null) }

    var donorToSuspend by remember { mutableStateOf<Donor?>(null) }
    var donorToUpdateDate by remember { mutableStateOf<Donor?>(null) }

    // Dialog توقيف المتبرع
    if (donorToSuspend != null) {
        AlertDialog(
            onDismissRequest = { donorToSuspend = null },
            title = { Text("تأكيد توقيف المتبرع", fontWeight = FontWeight.Bold) },
            text = { Text("هل تريد توقيف المتبرع (${donorToSuspend!!.name}) لمدة 6 أشهر؟") },
            confirmButton = {
                Button(
                    onClick = {
                        val id = donorToSuspend!!.id
                        donorToSuspend = null
                        viewModel.suspendDonor(id) {
                            Toast.makeText(context, "تم التوقيف بنجاح", Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AppColors.Warning)
                ) {
                    Text("توقيف 6 أشهر")
                }
            },
            dismissButton = {
                TextButton(onClick = { donorToSuspend = null }) {
                    Text(AppStrings.cancel)
                }
            }
        )
    }

    // Dialog تسجيل تبرع
    if (donorToUpdateDate != null) {
        AlertDialog(
            onDismissRequest = { donorToUpdateDate = null },
            title = { Text("تسجيل تبرع بالدم", fontWeight = FontWeight.Bold) },
            text = { Text("سيتم تسجيل تبرع اليوم للمتبرع (${donorToUpdateDate!!.name}) وتوقيفه 6 أشهر.") },
            confirmButton = {
                Button(
                    onClick = {
                        val id = donorToUpdateDate!!.id
                        donorToUpdateDate = null
                        viewModel.updateDonationDate(id) {
                            Toast.makeText(context, "تم تسجيل التبرع بنجاح", Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AppColors.Success)
                ) {
                    Text("تسجيل التبرع")
                }
            },
            dismissButton = {
                TextButton(onClick = { donorToUpdateDate = null }) {
                    Text(AppStrings.cancel)
                }
            }
        )
    }

    // استخراج المديريات المتوفرة للمحافظة
    val availableDistricts = remember(uiState.hospitalGovernorate) {
        if (!uiState.hospitalGovernorate.isNullOrEmpty()) {
            AppStrings.governorateDistricts[uiState.hospitalGovernorate] ?: emptyList()
        } else {
            emptyList()
        }
    }

    val filteredDonors = uiState.donors.filter { donor ->
        val matchesKeyword = keyword.isBlank() ||
                donor.name.contains(keyword, ignoreCase = true) ||
                donor.allPhoneNumbers.any { it.contains(keyword) }

        val matchesBlood = selectedBloodType == null || donor.bloodType == selectedBloodType
        val matchesDistrict = selectedSubDistrict == null || donor.district.contains(selectedSubDistrict!!)
        val matchesStatus = when (selectedStatus) {
            "available" -> donor.canDonateNow
            "suspended" -> donor.isSuspended
            else -> true
        }
        val matchesGender = selectedGender == null || donor.gender == selectedGender

        matchesKeyword && matchesBlood && matchesDistrict && matchesStatus && matchesGender
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "البحث المتقدم بالمحافظة",
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = AppStrings.back,
                            tint = Color.White
                        )
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
            // كرت خيارات البحث المتقدم
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    CustomTextField(
                        value = keyword,
                        onValueChange = { keyword = it },
                        placeholder = "بحث بالاسم أو رقم الهاتف...",
                        leadingIcon = {
                            Icon(Icons.Default.Search, contentDescription = null, tint = AppColors.Primary)
                        },
                        trailingIcon = {
                            if (keyword.isNotEmpty()) {
                                IconButton(onClick = { keyword = "" }) {
                                    Icon(Icons.Default.Clear, contentDescription = "مسح", tint = AppColors.TextSecondary)
                                }
                            }
                        }
                    )

                    if (availableDistricts.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(12.dp))
                        CustomDropdown(
                            selectedValue = selectedSubDistrict,
                            items = availableDistricts,
                            onItemSelected = { selectedSubDistrict = it },
                            label = AppStrings.subDistrict,
                            placeholder = "جميع مديريات المحافظة",
                            leadingIcon = {
                                Icon(Icons.Default.LocationCity, contentDescription = null, tint = AppColors.Primary)
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "فصيلة الدم:",
                        style = MaterialTheme.typography.labelMedium,
                        color = AppColors.TextSecondary
                    )

                    FlowRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        AppStrings.bloodTypes.forEach { type ->
                            BloodTypeSelectorChip(
                                bloodType = type,
                                isSelected = selectedBloodType == type,
                                onSelect = {
                                    selectedBloodType = if (selectedBloodType == it) null else it
                                }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilterChip(
                            selected = selectedStatus == "all",
                            onClick = { selectedStatus = "all" },
                            label = { Text("الكل") }
                        )
                        FilterChip(
                            selected = selectedStatus == "available",
                            onClick = { selectedStatus = "available" },
                            label = { Text("متاح") }
                        )
                        FilterChip(
                            selected = selectedStatus == "suspended",
                            onClick = { selectedStatus = "suspended" },
                            label = { Text("موقوف") }
                        )
                    }
                }
            }

            // نتائج البحث
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
            ) {
                if (uiState.isLoading) {
                    LoadingIndicator(message = "جاري تحميل البيانات...")
                } else if (filteredDonors.isEmpty()) {
                    EmptyState(
                        title = "لا توجد نتائج",
                        message = "لا يوجد متبرعون يطابقون خيارات الفلترة المحددة"
                    )
                } else {
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        item {
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "نتائج البحث: ${filteredDonors.size} متبرع",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = AppColors.TextSecondary,
                                modifier = Modifier.padding(bottom = 6.dp)
                            )
                        }

                        items(filteredDonors, key = { it.id }) { donor ->
                            DonorCard(
                                donor = donor,
                                showAdminActions = true,
                                onSuspend = { donorToSuspend = donor },
                                onUpdateDonationDate = { donorToUpdateDate = donor }
                            )
                        }

                        item {
                            Spacer(modifier = Modifier.height(32.dp))
                        }
                    }
                }
            }
        }
    }
}
