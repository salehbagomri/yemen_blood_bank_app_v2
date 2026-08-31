package com.bagomri.yemenbloodbank.ui.screens.hospital

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.FilterListOff
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
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
import com.bagomri.yemenbloodbank.ui.components.CustomTextField
import com.bagomri.yemenbloodbank.ui.components.DonorCard
import com.bagomri.yemenbloodbank.ui.components.EmptyState
import com.bagomri.yemenbloodbank.ui.components.ErrorDisplay
import com.bagomri.yemenbloodbank.ui.components.LoadingIndicator

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun HospitalManageDonorsScreen(
    onNavigateBack: () -> Unit,
    onNavigateToAddDonor: () -> Unit,
    viewModel: HospitalDashboardViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    var searchQuery by remember { mutableStateOf("") }
    var selectedBloodType by remember { mutableStateOf<String?>(null) }
    var selectedStatus by remember { mutableStateOf("all") } // all, available, suspended
    var showFilters by remember { mutableStateOf(false) }

    var donorToSuspend by remember { mutableStateOf<Donor?>(null) }
    var donorToUpdateDate by remember { mutableStateOf<Donor?>(null) }

    // Dialog لتأكيد توقيف المتبرع
    if (donorToSuspend != null) {
        AlertDialog(
            onDismissRequest = { donorToSuspend = null },
            title = { Text("تأكيد توقيف المتبرع", fontWeight = FontWeight.Bold) },
            text = {
                Text("هل تريد توقيف المتبرع (${donorToSuspend!!.name}) لمدة 6 أشهر؟ سيتم إخفاؤه من البحث العام حتى نهاية المدة.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        val id = donorToSuspend!!.id
                        donorToSuspend = null
                        viewModel.suspendDonor(id) {
                            Toast.makeText(context, "تم توقيف المتبرع لمدة 6 أشهر بنجاح", Toast.LENGTH_SHORT).show()
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

    // Dialog لتأكيد تحديث تاريخ التبرع
    if (donorToUpdateDate != null) {
        AlertDialog(
            onDismissRequest = { donorToUpdateDate = null },
            title = { Text("تسجيل تبرع بالدم", fontWeight = FontWeight.Bold) },
            text = {
                Text("سيتم تسجيل تاريخ تبرع اليوم للمتبرع (${donorToUpdateDate!!.name}) وتوقيفه تلقائياً لمدة 6 أشهر.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        val id = donorToUpdateDate!!.id
                        donorToUpdateDate = null
                        viewModel.updateDonationDate(id) {
                            Toast.makeText(context, "تم تسجيل التبرع وتحديث الحالة بنجاح", Toast.LENGTH_SHORT).show()
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

    // تصفية المتبرعين محلياً
    val filteredDonors = uiState.donors.filter { donor ->
        val matchesSearch = searchQuery.isBlank() ||
                donor.name.contains(searchQuery, ignoreCase = true) ||
                donor.allPhoneNumbers.any { it.contains(searchQuery) } ||
                donor.district.contains(searchQuery, ignoreCase = true)

        val matchesBlood = selectedBloodType == null || donor.bloodType == selectedBloodType

        val matchesStatus = when (selectedStatus) {
            "available" -> donor.canDonateNow
            "suspended" -> donor.isSuspended
            else -> true
        }

        matchesSearch && matchesBlood && matchesStatus
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (!uiState.hospitalGovernorate.isNullOrEmpty()) {
                            "متبرعو محافظة ${uiState.hospitalGovernorate}"
                        } else {
                            AppStrings.manageDonors
                        },
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
                actions = {
                    IconButton(onClick = { showFilters = !showFilters }) {
                        Icon(
                            imageVector = if (showFilters) Icons.Default.FilterListOff else Icons.Default.FilterList,
                            contentDescription = "الفلاتر",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = AppColors.Primary
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToAddDonor,
                containerColor = AppColors.Success,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.PersonAdd, contentDescription = "إضافة متبرع")
            }
        }
    ) { paddingValues ->
        PullToRefreshBox(
            isRefreshing = uiState.isRefreshing,
            onRefresh = { viewModel.refresh() },
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // شريط البحث
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.surface,
                    shadowElevation = 2.dp
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        CustomTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            placeholder = "بحث بالاسم، الهاتف، أو المديرية...",
                            leadingIcon = {
                                Icon(Icons.Default.Search, contentDescription = null, tint = AppColors.Primary)
                            },
                            trailingIcon = {
                                if (searchQuery.isNotEmpty()) {
                                    IconButton(onClick = { searchQuery = "" }) {
                                        Icon(Icons.Default.Clear, contentDescription = "مسح", tint = AppColors.TextSecondary)
                                    }
                                }
                            }
                        )

                        // الفلاتر القابلة للطي
                        AnimatedVisibility(visible = showFilters) {
                            Column(modifier = Modifier.padding(top = 12.dp)) {
                                Text(
                                    text = "تصفية حسب الحالة:",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = AppColors.TextSecondary
                                )
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 4.dp),
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
                                        label = { Text("المتاحون فقط") }
                                    )
                                    FilterChip(
                                        selected = selectedStatus == "suspended",
                                        onClick = { selectedStatus = "suspended" },
                                        label = { Text("الموقوفون فقط") }
                                    )
                                }

                                Spacer(modifier = Modifier.height(8.dp))

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
                            }
                        }
                    }
                }

                // محتوى القائمة
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp)
                ) {
                    when {
                        uiState.isLoading -> {
                            LoadingIndicator(message = "جاري تحميل قائمة المتبرعين...")
                        }

                        uiState.errorMessage != null -> {
                            ErrorDisplay(
                                message = uiState.errorMessage!!,
                                onRetry = { viewModel.loadData() }
                            )
                        }

                        filteredDonors.isEmpty() -> {
                            EmptyState(
                                title = "لا يوجد متبرعون",
                                message = if (searchQuery.isNotEmpty() || selectedBloodType != null) {
                                    "لا توجد نتائج مطابقة لمعايير البحث الحالية"
                                } else {
                                    "لم يتم تسجيل متبرعين في هذه المحافظة حتى الآن"
                                }
                            )
                        }

                        else -> {
                            LazyColumn(modifier = Modifier.fillMaxSize()) {
                                item {
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Text(
                                        text = "العدد: ${filteredDonors.size} متبرع",
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
                                    Spacer(modifier = Modifier.height(80.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
