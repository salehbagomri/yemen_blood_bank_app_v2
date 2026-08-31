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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bagomri.yemenbloodbank.core.constants.AppColors
import com.bagomri.yemenbloodbank.core.constants.AppStrings
import com.bagomri.yemenbloodbank.core.util.ErrorHandler
import com.bagomri.yemenbloodbank.data.model.District
import com.bagomri.yemenbloodbank.data.model.Governorate
import com.bagomri.yemenbloodbank.data.repository.LocationRepository
import com.bagomri.yemenbloodbank.ui.components.ErrorDisplay
import com.bagomri.yemenbloodbank.ui.components.LoadingIndicator
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminManageLocationsScreen(
    onNavigateBack: () -> Unit,
    locationRepository: LocationRepository = LocationRepository()
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var isLoading by remember { mutableStateOf(true) }
    var isRefreshing by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    var governorates by remember { mutableStateOf<List<Governorate>>(emptyList()) }
    val districtsByGov = remember { mutableStateMapOf<String, List<District>>() }
    val expandedGovs = remember { mutableStateMapOf<String, Boolean>() }

    var districtDialogTitle by remember { mutableStateOf<String?>(null) }
    var districtDialogGov by remember { mutableStateOf("") }
    var districtDialogInitial by remember { mutableStateOf("") }
    var districtDialogDistrictId by remember { mutableStateOf<String?>(null) }
    var districtDialogText by remember { mutableStateOf("") }
    var showDistrictDialog by remember { mutableStateOf(false) }

    fun loadData() {
        scope.launch {
            isLoading = true
            errorMessage = null
            val govResult = locationRepository.getAllGovernorates()
            govResult.fold(
                onSuccess = { list ->
                    governorates = list
                    isLoading = false
                },
                onFailure = { err ->
                    errorMessage = ErrorHandler.getArabicMessage(err)
                    isLoading = false
                }
            )
        }
    }

    fun loadDistrictsFor(govName: String) {
        scope.launch {
            val distResult = locationRepository.getDistrictsOf(govName)
            distResult.fold(
                onSuccess = { list ->
                    districtsByGov[govName] = list
                },
                onFailure = {
                    districtsByGov[govName] = emptyList()
                }
            )
        }
    }

    LaunchedEffect(Unit) {
        loadData()
    }

    if (showDistrictDialog) {
        AlertDialog(
            onDismissRequest = { showDistrictDialog = false },
            title = { Text(districtDialogTitle ?: "إدارة المديرية", fontWeight = FontWeight.Bold) },
            text = {
                OutlinedTextField(
                    value = districtDialogText,
                    onValueChange = { districtDialogText = it },
                    label = { Text("اسم المديرية") },
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        val name = districtDialogText.trim()
                        if (name.isNotEmpty()) {
                            showDistrictDialog = false
                            scope.launch {
                                val res = if (districtDialogDistrictId != null) {
                                    locationRepository.updateDistrict(districtDialogDistrictId!!, districtDialogGov, districtDialogInitial, name)
                                } else {
                                    locationRepository.addDistrict(districtDialogGov, name)
                                }
                                res.fold(
                                    onSuccess = {
                                        Toast.makeText(context, "تم الحفظ بنجاح", Toast.LENGTH_SHORT).show()
                                        loadDistrictsFor(districtDialogGov)
                                    },
                                    onFailure = { err ->
                                        Toast.makeText(context, ErrorHandler.getArabicMessage(err), Toast.LENGTH_LONG).show()
                                    }
                                )
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AppColors.Primary)
                ) {
                    Text("حفظ")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDistrictDialog = false }) {
                    Text(AppStrings.cancel)
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("إدارة المحافظات والمديريات", fontWeight = FontWeight.Bold, color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = AppStrings.back, tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = AppColors.Primary)
            )
        }
    ) { paddingValues ->
        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = {
                isRefreshing = true
                loadData()
                isRefreshing = false
            },
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
        ) {
            if (isLoading) {
                LoadingIndicator(message = "جاري تحميل المناطق...")
            } else if (errorMessage != null) {
                ErrorDisplay(message = errorMessage!!, onRetry = { loadData() })
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = AppColors.InfoContainer),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Info, contentDescription = null, tint = AppColors.Info)
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = "فعّل المحافظات والمديريات التي تريد إتاحتها للمستخدمين في البحث والتسجيل. الموقوفة تختفي تلقائياً.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = AppColors.Info
                                )
                            }
                        }
                    }

                    items(governorates, key = { it.name }) { gov ->
                        val isExpanded = expandedGovs[gov.name] == true
                        val districts = districtsByGov[gov.name] ?: emptyList()

                        Card(
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clickable {
                                                val next = !isExpanded
                                                expandedGovs[gov.name] = next
                                                if (next && !districtsByGov.containsKey(gov.name)) {
                                                    loadDistrictsFor(gov.name)
                                                }
                                            },
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Map,
                                            contentDescription = null,
                                            tint = if (gov.isActive) AppColors.Primary else AppColors.TextSecondary
                                        )
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Column {
                                            Text(
                                                text = gov.name,
                                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                                color = if (gov.isActive) MaterialTheme.colorScheme.onSurface else AppColors.TextSecondary
                                            )
                                            Text(
                                                text = if (gov.isActive) "مفعلة للمستخدمين" else "موقوفة",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = if (gov.isActive) AppColors.Success else AppColors.Error
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Icon(
                                            imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                            contentDescription = null,
                                            tint = AppColors.TextSecondary
                                        )
                                    }

                                    Switch(
                                        checked = gov.isActive,
                                        onCheckedChange = { active ->
                                            scope.launch {
                                                locationRepository.setGovernorateActive(gov.name, active)
                                                loadData()
                                            }
                                        },
                                        colors = SwitchDefaults.colors(checkedThumbColor = AppColors.Success)
                                    )
                                }

                                AnimatedVisibility(visible = isExpanded) {
                                    Column(modifier = Modifier.padding(top = 12.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text("المديريات المعتمدة (${districts.size}):", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                                            TextButton(
                                                onClick = {
                                                    districtDialogTitle = "إضافة مديرية جديدة في ${gov.name}"
                                                    districtDialogGov = gov.name
                                                    districtDialogInitial = ""
                                                    districtDialogDistrictId = null
                                                    districtDialogText = ""
                                                    showDistrictDialog = true
                                                }
                                            ) {
                                                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text("إضافة مديرية", fontSize = 12.sp)
                                            }
                                        }

                                        districts.forEach { dist ->
                                            Surface(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(vertical = 3.dp),
                                                shape = RoundedCornerShape(8.dp),
                                                color = AppColors.SurfaceVariant
                                            ) {
                                                Row(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .padding(horizontal = 10.dp, vertical = 6.dp),
                                                    horizontalArrangement = Arrangement.SpaceBetween,
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                                        Icon(Icons.Default.LocationOn, contentDescription = null, tint = if (dist.isActive) AppColors.Primary else AppColors.TextSecondary, modifier = Modifier.size(16.dp))
                                                        Spacer(modifier = Modifier.width(8.dp))
                                                        Text(dist.name, style = MaterialTheme.typography.bodyMedium)
                                                    }

                                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                                        IconButton(
                                                            onClick = {
                                                                districtDialogTitle = "تعديل اسم المديرية"
                                                                districtDialogGov = gov.name
                                                                districtDialogInitial = dist.name
                                                                districtDialogDistrictId = dist.id
                                                                districtDialogText = dist.name
                                                                showDistrictDialog = true
                                                            },
                                                            modifier = Modifier.size(28.dp)
                                                        ) {
                                                            Icon(Icons.Default.Edit, contentDescription = "تعديل", tint = AppColors.Primary, modifier = Modifier.size(16.dp))
                                                        }

                                                        IconButton(
                                                            onClick = {
                                                                scope.launch {
                                                                    val inUse = locationRepository.isDistrictInUse(gov.name, dist.name)
                                                                    if (inUse) {
                                                                        Toast.makeText(context, "لا يمكن حذف هذه المديرية لأن هناك متبرعين مسجلين فيها", Toast.LENGTH_LONG).show()
                                                                    } else {
                                                                        locationRepository.deleteDistrict(dist.id, gov.name, dist.name)
                                                                        Toast.makeText(context, "تم حذف المديرية بنجاح", Toast.LENGTH_SHORT).show()
                                                                        loadDistrictsFor(gov.name)
                                                                    }
                                                                }
                                                            },
                                                            modifier = Modifier.size(28.dp)
                                                        ) {
                                                            Icon(Icons.Default.Delete, contentDescription = "حذف", tint = AppColors.Error, modifier = Modifier.size(16.dp))
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(32.dp))
                    }
                }
            }
        }
    }
}
