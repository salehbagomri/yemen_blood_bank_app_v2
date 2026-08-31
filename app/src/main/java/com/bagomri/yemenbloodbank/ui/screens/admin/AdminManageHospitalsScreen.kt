package com.bagomri.yemenbloodbank.ui.screens.admin

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.FilterListOff
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.LocationOn
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
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bagomri.yemenbloodbank.core.constants.AppColors
import com.bagomri.yemenbloodbank.core.constants.AppStrings
import com.bagomri.yemenbloodbank.core.util.IntentUtils
import com.bagomri.yemenbloodbank.data.model.Hospital
import com.bagomri.yemenbloodbank.ui.components.CustomDropdown
import com.bagomri.yemenbloodbank.ui.components.CustomTextField
import com.bagomri.yemenbloodbank.ui.components.EmptyState
import com.bagomri.yemenbloodbank.ui.components.ErrorDisplay
import com.bagomri.yemenbloodbank.ui.components.LoadingIndicator

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminManageHospitalsScreen(
    onNavigateBack: () -> Unit,
    onNavigateToAddHospital: () -> Unit,
    onNavigateToEditHospital: (String) -> Unit,
    viewModel: AdminDashboardViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    var searchQuery by remember { mutableStateOf("") }
    var selectedGovernorate by remember { mutableStateOf<String?>(null) }
    var selectedStatus by remember { mutableStateOf("all") } // all, active, inactive
    var showFilters by remember { mutableStateOf(false) }

    var hospitalToDelete by remember { mutableStateOf<Hospital?>(null) }

    if (hospitalToDelete != null) {
        AlertDialog(
            onDismissRequest = { hospitalToDelete = null },
            title = { Text("حذف المستشفى", fontWeight = FontWeight.Bold) },
            text = { Text("هل أنت متأكد من حذف المستشفى (${hospitalToDelete!!.name})؟") },
            confirmButton = {
                Button(
                    onClick = {
                        val id = hospitalToDelete!!.id
                        hospitalToDelete = null
                        viewModel.deleteHospital(id) {
                            Toast.makeText(context, "تم حذف المستشفى بنجاح", Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AppColors.Error)
                ) {
                    Text("حذف")
                }
            },
            dismissButton = {
                TextButton(onClick = { hospitalToDelete = null }) {
                    Text(AppStrings.cancel)
                }
            }
        )
    }

    val filteredHospitals = uiState.hospitals.filter { hospital ->
        val matchesSearch = searchQuery.isBlank() ||
                hospital.name.contains(searchQuery, ignoreCase = true) ||
                hospital.email.contains(searchQuery, ignoreCase = true) ||
                (hospital.phoneNumber?.contains(searchQuery) == true)

        val matchesGov = selectedGovernorate == null || hospital.governorate == selectedGovernorate
        val matchesStatus = when (selectedStatus) {
            "active" -> hospital.isActive
            "inactive" -> !hospital.isActive
            else -> true
        }

        matchesSearch && matchesGov && matchesStatus
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "إدارة المستشفيات (${uiState.totalHospitals})",
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
                onClick = onNavigateToAddHospital,
                containerColor = AppColors.Info,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "إضافة مستشفى")
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
                            placeholder = "بحث بالاسم، البريد الإلكتروني، أو الهاتف...",
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
                                    placeholder = "جميع المحافظات"
                                )

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
                                        selected = selectedStatus == "active",
                                        onClick = { selectedStatus = "active" },
                                        label = { Text("النشطة") }
                                    )
                                    FilterChip(
                                        selected = selectedStatus == "inactive",
                                        onClick = { selectedStatus = "inactive" },
                                        label = { Text("المعطلة") }
                                    )
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
                            LoadingIndicator(message = "جاري تحميل قائمة المستشفيات...")
                        }

                        uiState.errorMessage != null -> {
                            ErrorDisplay(
                                message = uiState.errorMessage!!,
                                onRetry = { viewModel.loadData() }
                            )
                        }

                        filteredHospitals.isEmpty() -> {
                            EmptyState(
                                title = "لا توجد مستشفيات",
                                message = "لا توجد مستشفيات تطابق خيارات البحث المحددة"
                            )
                        }

                        else -> {
                            LazyColumn(modifier = Modifier.fillMaxSize()) {
                                item {
                                    Spacer(modifier = Modifier.height(10.dp))
                                }

                                items(filteredHospitals, key = { it.id }) { hospital ->
                                    HospitalAdminCard(
                                        hospital = hospital,
                                        onToggleStatus = {
                                            viewModel.toggleHospitalStatus(hospital) {
                                                Toast.makeText(context, "تم تغيير حالة المستشفى", Toast.LENGTH_SHORT).show()
                                            }
                                        },
                                        onEdit = { onNavigateToEditHospital(hospital.id) },
                                        onDelete = { hospitalToDelete = hospital }
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

@Composable
private fun HospitalAdminCard(
    hospital: Hospital,
    onToggleStatus: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val context = LocalContext.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    modifier = Modifier.size(46.dp),
                    shape = RoundedCornerShape(12.dp),
                    color = if (hospital.isActive) AppColors.Info.copy(alpha = 0.15f) else AppColors.ErrorContainer
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.LocalHospital,
                            contentDescription = null,
                            tint = if (hospital.isActive) AppColors.Info else AppColors.Error,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = hospital.name,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = hospital.email,
                        style = MaterialTheme.typography.bodySmall,
                        color = AppColors.TextSecondary
                    )
                }

                Switch(
                    checked = hospital.isActive,
                    onCheckedChange = { onToggleStatus() },
                    colors = SwitchDefaults.colors(checkedThumbColor = AppColors.Success)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = AppColors.Primary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "محافظة: ${hospital.governorate}",
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                if (!hospital.phoneNumber.isNullOrEmpty()) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = { IntentUtils.makePhoneCall(context, hospital.phoneNumber) },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Call,
                                contentDescription = "اتصال",
                                tint = AppColors.Primary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = onEdit) {
                    Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("تعديل")
                }

                TextButton(
                    onClick = onDelete,
                    colors = ButtonDefaults.textButtonColors(contentColor = AppColors.Error)
                ) {
                    Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("حذف")
                }
            }
        }
    }
}
