package com.bagomri.yemenbloodbank.ui.screens.admin

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
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
import com.bagomri.yemenbloodbank.core.util.DateUtils
import com.bagomri.yemenbloodbank.data.model.Donor
import com.bagomri.yemenbloodbank.ui.components.BloodTypeSelectorChip
import com.bagomri.yemenbloodbank.ui.components.CustomDropdown
import com.bagomri.yemenbloodbank.ui.components.CustomTextField
import com.bagomri.yemenbloodbank.ui.components.EmptyState
import com.bagomri.yemenbloodbank.ui.components.ErrorDisplay
import com.bagomri.yemenbloodbank.ui.components.LoadingIndicator
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AdminManageDonorsScreen(
    onNavigateBack: () -> Unit,
    onNavigateToAddDonor: () -> Unit,
    onNavigateToEditDonor: (String) -> Unit,
    viewModel: AdminDashboardViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    var searchQuery by remember { mutableStateOf("") }
    var selectedGovernorate by remember { mutableStateOf<String?>(null) }
    var selectedBloodType by remember { mutableStateOf<String?>(null) }
    var selectedStatus by remember { mutableStateOf("all") } // all, active, inactive, suspended
    var showFilters by remember { mutableStateOf(false) }

    var donorToDelete by remember { mutableStateOf<Donor?>(null) }
    var donorToSuspend by remember { mutableStateOf<Donor?>(null) }
    var donorToCancelSuspend by remember { mutableStateOf<Donor?>(null) }
    var donorToToggleActive by remember { mutableStateOf<Donor?>(null) }

    val isoFormat = remember {
        SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US).apply {
            timeZone = TimeZone.getTimeZone("UTC")
        }
    }

    // منتقي التاريخ لتحديث آخر تبرع
    val showDatePickerForDonor = { donor: Donor ->
        val cal = Calendar.getInstance()
        if (donor.lastDonationDate != null) {
            DateUtils.parseIsoDate(donor.lastDonationDate)?.let {
                cal.time = it
            }
        }
        val datePickerDialog = android.app.DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                val pickedCal = Calendar.getInstance().apply {
                    set(year, month, dayOfMonth, 12, 0, 0)
                }
                val pickedDate = pickedCal.time
                val sixMonthsLater = Calendar.getInstance().apply {
                    time = pickedDate
                    add(Calendar.DAY_OF_YEAR, 180)
                }.time

                val lastDonationStr = isoFormat.format(pickedDate)
                val willBeSuspended = Date().before(sixMonthsLater)
                val suspendedUntilStr = if (willBeSuspended) isoFormat.format(sixMonthsLater) else null

                viewModel.updateDonorDonationDate(donor.id, lastDonationStr, suspendedUntilStr) {
                    Toast.makeText(context, "تم تحديث تاريخ آخر تبرع", Toast.LENGTH_SHORT).show()
                }
            },
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH),
            cal.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.datePicker.maxDate = System.currentTimeMillis()
        datePickerDialog.show()
    }

    // حوار حذف متبرع
    if (donorToDelete != null) {
        AlertDialog(
            onDismissRequest = { donorToDelete = null },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Delete, contentDescription = null, tint = AppColors.Error)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("حذف نهائي", fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Text("هل تريد حذف ${donorToDelete!!.name} نهائياً؟\n\n⚠️ هذا الإجراء لا يمكن التراجع عنه!\n\nسيتم حذف:\n• جميع البيانات\n• السجل التاريخي\n• البلاغات المرتبطة")
            },
            confirmButton = {
                Button(
                    onClick = {
                        val id = donorToDelete!!.id
                        val name = donorToDelete!!.name
                        donorToDelete = null
                        viewModel.deleteDonor(id) {
                            Toast.makeText(context, "تم حذف $name نهائياً", Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AppColors.Error)
                ) {
                    Text("حذف نهائي")
                }
            },
            dismissButton = {
                TextButton(onClick = { donorToDelete = null }) {
                    Text(AppStrings.cancel)
                }
            }
        )
    }

    // حوار إيقاف متبرع 6 أشهر
    if (donorToSuspend != null) {
        AlertDialog(
            onDismissRequest = { donorToSuspend = null },
            title = { Text("تأكيد الإيقاف", fontWeight = FontWeight.Bold) },
            text = { Text("هل تريد إيقاف المتبرع (${donorToSuspend!!.name}) لمدة 6 أشهر؟") },
            confirmButton = {
                Button(
                    onClick = {
                        val id = donorToSuspend!!.id
                        val name = donorToSuspend!!.name
                        donorToSuspend = null
                        viewModel.suspendDonor(id) {
                            Toast.makeText(context, "تم إيقاف $name لمدة 6 أشهر", Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AppColors.Warning)
                ) {
                    Text("إيقاف")
                }
            },
            dismissButton = {
                TextButton(onClick = { donorToSuspend = null }) {
                    Text(AppStrings.cancel)
                }
            }
        )
    }

    // حوار إلغاء الإيقاف
    if (donorToCancelSuspend != null) {
        AlertDialog(
            onDismissRequest = { donorToCancelSuspend = null },
            title = { Text("إلغاء الإيقاف", fontWeight = FontWeight.Bold) },
            text = { Text("هل تريد إلغاء إيقاف المتبرع (${donorToCancelSuspend!!.name})؟") },
            confirmButton = {
                Button(
                    onClick = {
                        val id = donorToCancelSuspend!!.id
                        val name = donorToCancelSuspend!!.name
                        donorToCancelSuspend = null
                        viewModel.cancelDonorSuspension(id) {
                            Toast.makeText(context, "تم إلغاء إيقاف $name", Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AppColors.Success)
                ) {
                    Text("تأكيد")
                }
            },
            dismissButton = {
                TextButton(onClick = { donorToCancelSuspend = null }) {
                    Text(AppStrings.cancel)
                }
            }
        )
    }

    // حوار تعطيل / تفعيل الحساب
    if (donorToToggleActive != null) {
        val isActive = donorToToggleActive!!.isActive
        AlertDialog(
            onDismissRequest = { donorToToggleActive = null },
            title = { Text(if (isActive) "تعطيل الحساب" else "تفعيل الحساب", fontWeight = FontWeight.Bold) },
            text = {
                Text(
                    if (isActive)
                        "هل تريد تعطيل حساب ${donorToToggleActive!!.name}؟\n\nالحساب المعطل لن يظهر في نتائج البحث."
                    else
                        "هل تريد تفعيل حساب ${donorToToggleActive!!.name}؟"
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        val donor = donorToToggleActive!!
                        donorToToggleActive = null
                        viewModel.toggleDonorStatus(donor) {
                            Toast.makeText(context, if (isActive) "تم تعطيل الحساب" else "تم تفعيل الحساب", Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = if (isActive) AppColors.Error else AppColors.Success)
                ) {
                    Text(if (isActive) "تعطيل" else "تفعيل")
                }
            },
            dismissButton = {
                TextButton(onClick = { donorToToggleActive = null }) {
                    Text(AppStrings.cancel)
                }
            }
        )
    }

    val filteredDonors = uiState.donors.filter { donor ->
        val matchesSearch = searchQuery.isBlank() ||
                donor.name.contains(searchQuery, ignoreCase = true) ||
                donor.allPhoneNumbers.any { it.contains(searchQuery) } ||
                donor.district.contains(searchQuery, ignoreCase = true)

        val matchesGov = selectedGovernorate == null || donor.governorate == selectedGovernorate
        val matchesBlood = selectedBloodType == null || donor.bloodType == selectedBloodType
        val matchesStatus = when (selectedStatus) {
            "active" -> donor.isActive && donor.canDonateNow
            "inactive" -> !donor.isActive
            "suspended" -> donor.isSuspended
            else -> true
        }

        matchesSearch && matchesGov && matchesBlood && matchesStatus
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "إدارة المتبرعين (${uiState.totalDonors})",
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
                containerColor = AppColors.Primary,
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
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.surface,
                    shadowElevation = 2.dp
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        CustomTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            placeholder = "بحث بالاسم، رقم الهاتف، أو المديرية...",
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

                        AnimatedVisibility(visible = showFilters) {
                            Column(modifier = Modifier.padding(top = 12.dp)) {
                                CustomDropdown(
                                    selectedValue = selectedGovernorate,
                                    items = uiState.locationData.governorates,
                                    onItemSelected = { selectedGovernorate = it },
                                    label = "المحافظة",
                                    placeholder = "جميع المحافظات (22 محافظة)"
                                )

                                Spacer(modifier = Modifier.height(10.dp))

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .horizontalScroll(rememberScrollState()),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    FilterChip(
                                        selected = selectedStatus == "all",
                                        onClick = { selectedStatus = "all" },
                                        label = { Text("الكل") }
                                    )
                                    FilterChip(
                                        selected = selectedStatus == "active",
                                        onClick = { selectedStatus = "active" },
                                        label = { Text("النشطون") }
                                    )
                                    FilterChip(
                                        selected = selectedStatus == "suspended",
                                        onClick = { selectedStatus = "suspended" },
                                        label = { Text("الموقوفون") }
                                    )
                                    FilterChip(
                                        selected = selectedStatus == "inactive",
                                        onClick = { selectedStatus = "inactive" },
                                        label = { Text("المعطلون") }
                                    )
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                // فلاتر الفصائل: سطرين متناسقين (4 في كل سطر) بنسب متساوية
                                val firstRowBloodTypes = AppStrings.bloodTypes.take(4)
                                val secondRowBloodTypes = AppStrings.bloodTypes.drop(4)

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    firstRowBloodTypes.forEach { type ->
                                        BloodTypeSelectorChip(
                                            bloodType = type,
                                            isSelected = selectedBloodType == type,
                                            onSelect = {
                                                selectedBloodType = if (selectedBloodType == it) null else it
                                            },
                                            modifier = Modifier.weight(1f)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(6.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    secondRowBloodTypes.forEach { type ->
                                        BloodTypeSelectorChip(
                                            bloodType = type,
                                            isSelected = selectedBloodType == type,
                                            onSelect = {
                                                selectedBloodType = if (selectedBloodType == it) null else it
                                            },
                                            modifier = Modifier.weight(1f)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp)
                ) {
                    when {
                        uiState.isLoading -> {
                            LoadingIndicator(message = "جاري تحميل المتبرعين...")
                        }

                        uiState.errorMessage != null -> {
                            ErrorDisplay(
                                message = uiState.errorMessage!!,
                                onRetry = { viewModel.loadData() }
                            )
                        }

                        filteredDonors.isEmpty() -> {
                            EmptyState(
                                title = "لا توجد نتائج",
                                message = "لا يوجد متبرعون يطابقون خيارات البحث الحالية"
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
                                    AdminDonorCard(
                                        donor = donor,
                                        onEdit = { onNavigateToEditDonor(donor.id) },
                                        onDelete = { donorToDelete = donor },
                                        onSuspend = { donorToSuspend = donor },
                                        onCancelSuspension = { donorToCancelSuspend = donor },
                                        onUpdateDonationDate = { showDatePickerForDonor(donor) },
                                        onToggleActive = { donorToToggleActive = donor },
                                        onViewReports = {
                                            Toast.makeText(context, "قريباً - عرض البلاغات المتعلقة بهذا المتبرع", Toast.LENGTH_SHORT).show()
                                        }
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
