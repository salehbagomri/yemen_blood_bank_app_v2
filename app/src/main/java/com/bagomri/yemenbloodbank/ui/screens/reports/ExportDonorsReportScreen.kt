package com.bagomri.yemenbloodbank.ui.screens.reports

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.Bloodtype
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.TableChart
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bagomri.yemenbloodbank.core.constants.AppColors
import com.bagomri.yemenbloodbank.core.util.DonorReportExporter
import com.bagomri.yemenbloodbank.core.util.PhoneUtils
import com.bagomri.yemenbloodbank.data.model.Donor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExportDonorsReportScreen(
    onNavigateBack: () -> Unit,
    viewModel: ExportDonorsViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "📑 استخراج كشف المتبرعين",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = Color.White
                        )
                        Text(
                            text = uiState.organizationName,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.85f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "رجوع",
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.resetFilters() }) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "إعادة ضبط الفلاتر",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = AppColors.Primary
                )
            )
        },
        bottomBar = {
            ExportBottomActionBar(
                matchingCount = uiState.filteredDonors.size,
                isExporting = uiState.isExporting,
                onExportPdf = {
                    viewModel.exportPdf(context)
                },
                onExportCsv = {
                    viewModel.exportCsv(context)
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. بطاقة العداد الحي والمعاينة
            LiveCounterCard(
                totalMatching = uiState.filteredDonors.size,
                totalAll = uiState.allDonors.size,
                governorate = uiState.selectedGovernorate ?: uiState.hospitalGovernorate ?: "عموم المحافظات",
                userType = uiState.userType,
                onPreviewClick = { viewModel.togglePreviewSheet(true) }
            )

            // تنبيه الحوكمة الجغرافية للمستشفى
            if (uiState.userType == "hospital") {
                HospitalScopeBanner(governorate = uiState.hospitalGovernorate ?: "المحافظة المحددة")
            }

            // 2. بطاقة فصائل الدم
            BloodTypesFilterCard(
                selectedTypes = uiState.selectedBloodTypes,
                onToggleType = { viewModel.toggleBloodType(it) },
                onSelectUniversal = { viewModel.selectUniversalDonors() },
                onSelectAll = { viewModel.selectAllBloodTypes() }
            )

            // 3. بطاقة الجاهزية والأهلية
            AvailabilityFilterCard(
                currentFilter = uiState.availabilityFilter,
                onSelectFilter = { viewModel.setAvailabilityFilter(it) }
            )

            // 4. بطاقة الموقع الجغرافي
            LocationFilterCard(
                userType = uiState.userType,
                governorates = uiState.availableGovernorates,
                districts = if (!uiState.selectedGovernorate.isNullOrEmpty()) {
                    uiState.districtsMap[uiState.selectedGovernorate] ?: emptyList()
                } else emptyList(),
                selectedGovernorate = uiState.selectedGovernorate,
                selectedDistrict = uiState.selectedDistrict,
                onGovernorateSelected = { viewModel.setGovernorate(it) },
                onDistrictSelected = { viewModel.setDistrict(it) }
            )

            // 5. بطاقة خيارات التقرير المتقدمة
            ReportOptionsCard(
                selectedGender = uiState.selectedGender,
                sortOption = uiState.sortOption,
                includeSecondaryPhones = uiState.includeSecondaryPhones,
                includeNotes = uiState.includeNotes,
                onGenderSelected = { viewModel.setGender(it) },
                onSortSelected = { viewModel.setSortOption(it) },
                onToggleSecondaryPhones = { viewModel.toggleIncludeSecondaryPhones() },
                onToggleNotes = { viewModel.toggleIncludeNotes() }
            )

            Spacer(modifier = Modifier.height(30.dp))
        }
    }

    // نافذة المعاينة السريعة للمتبرعين (BottomSheet)
    if (uiState.showPreviewSheet) {
        ModalBottomSheet(
            onDismissRequest = { viewModel.togglePreviewSheet(false) },
            sheetState = sheetState,
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            DonorsPreviewContent(
                donors = uiState.filteredDonors,
                onClose = { viewModel.togglePreviewSheet(false) }
            )
        }
    }

    // نافذة نجاح التصدير مع خيارات المشاركة المباشرة
    if (uiState.exportSuccessMessage != null && uiState.lastExportedFile != null) {
        val file = uiState.lastExportedFile!!
        val mimeType = uiState.lastExportedMimeType ?: "application/pdf"

        AlertDialog(
            onDismissRequest = { viewModel.clearExportMessages() },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = AppColors.Success,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "تم إنشاء الكشف بنجاح",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = uiState.exportSuccessMessage ?: "",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = file.name,
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                            modifier = Modifier.padding(10.dp),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    Text(
                        text = "يمكنك مشاركة الملف مباشرة عبر واتساب والإيميل أو فتحه للمعاينة والطباعة.",
                        style = MaterialTheme.typography.bodySmall,
                        color = AppColors.TextSecondary
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        DonorReportExporter.shareReport(
                            context = context,
                            file = file,
                            mimeType = mimeType,
                            chooserTitle = "مشاركة كشف المتبرعين"
                        )
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AppColors.Primary)
                ) {
                    Icon(imageVector = Icons.Default.Share, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("مشاركة فورية")
                }
            },
            dismissButton = {
                Row {
                    OutlinedButton(
                        onClick = {
                            DonorReportExporter.openReport(context = context, file = file, mimeType = mimeType)
                        }
                    ) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.OpenInNew, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("فتح الملف")
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    TextButton(onClick = { viewModel.clearExportMessages() }) {
                        Text("إغلاق")
                    }
                }
            }
        )
    }

    // تنبيه بالخطأ إن وجد
    if (uiState.errorMessage != null) {
        AlertDialog(
            onDismissRequest = { viewModel.clearExportMessages() },
            title = { Text("تنبيه", fontWeight = FontWeight.Bold, color = AppColors.Error) },
            text = { Text(uiState.errorMessage ?: "") },
            confirmButton = {
                Button(onClick = { viewModel.clearExportMessages() }) {
                    Text("حسناً")
                }
            }
        )
    }
}

// -------------------------------------------------------------
// مكونات واجهة التصفية والتصدير
// -------------------------------------------------------------

@Composable
private fun LiveCounterCard(
    totalMatching: Int,
    totalAll: Int,
    governorate: String,
    userType: String,
    onPreviewClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.horizontalGradient(
                        listOf(Color(0xFFB71C1C), Color(0xFFD32F2F))
                    )
                )
                .padding(18.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = Color.White.copy(alpha = 0.2f),
                            modifier = Modifier.size(44.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Bloodtype,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(26.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "نتائج التصفية الجاهزة للتصدير",
                                style = MaterialTheme.typography.bodySmall.copy(color = Color.White.copy(alpha = 0.9f))
                            )
                            Text(
                                text = "$totalMatching متبرع",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color.White,
                                    fontSize = 24.sp
                                )
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = Color.White.copy(alpha = 0.25f),
                        modifier = Modifier.clickable(onClick = onPreviewClick)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Visibility,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "معاينة",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
                HorizontalDivider(color = Color.White.copy(alpha = 0.2f))
                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "النطاق: $governorate",
                        style = MaterialTheme.typography.bodySmall.copy(color = Color.White.copy(alpha = 0.9f))
                    )
                    Text(
                        text = "من إجمالي: $totalAll مسجل",
                        style = MaterialTheme.typography.bodySmall.copy(color = Color.White.copy(alpha = 0.8f))
                    )
                }
            }
        }
    }
}

@Composable
private fun HospitalScopeBanner(governorate: String) {
    Surface(
        color = AppColors.InfoContainer,
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, AppColors.Info.copy(alpha = 0.3f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = null,
                tint = AppColors.Info,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = "حساب المستشفى مقيد تلقائياً بمحافظة ($governorate) لضمان حوكمة البيانات وسرية السجلات.",
                style = MaterialTheme.typography.bodySmall.copy(color = AppColors.Info, lineHeight = 18.sp)
            )
        }
    }
}

@Composable
private fun BloodTypesFilterCard(
    selectedTypes: Set<String>,
    onToggleType: (String) -> Unit,
    onSelectUniversal: () -> Unit,
    onSelectAll: () -> Unit
) {
    val allBloodTypes = listOf("A+", "A-", "B+", "B-", "O+", "O-", "AB+", "AB-")
    val row1 = allBloodTypes.take(4)
    val row2 = allBloodTypes.takeLast(4)

    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "فصائل الدم المطلوبة",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                )

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    TextButton(onClick = onSelectUniversal) {
                        Text("طوارئ (O-/O+)", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold))
                    }
                    TextButton(onClick = onSelectAll) {
                        Text("الكل", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // السطر الأول (4 فصائل متوازنة)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                row1.forEach { type ->
                    val isSelected = selectedTypes.isEmpty() || selectedTypes.contains(type)
                    BloodChip(
                        type = type,
                        isSelected = isSelected,
                        onClick = { onToggleType(type) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // السطر الثاني (4 فصائل متوازنة)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                row2.forEach { type ->
                    val isSelected = selectedTypes.isEmpty() || selectedTypes.contains(type)
                    BloodChip(
                        type = type,
                        isSelected = isSelected,
                        onClick = { onToggleType(type) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun BloodChip(
    type: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = if (isSelected) AppColors.Primary else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        border = BorderStroke(
            1.dp,
            if (isSelected) AppColors.Primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
        ),
        modifier = modifier
            .height(38.dp)
            .clickable(onClick = onClick)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = type,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }
    }
}

@Composable
private fun AvailabilityFilterCard(
    currentFilter: AvailabilityFilter,
    onSelectFilter: (AvailabilityFilter) -> Unit
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "حالة الجاهزية والأهلية الطبية",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
            )
            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                AvailabilityChip(
                    label = "جاهزون للتبرع",
                    sub = "لم يتبرع منذ 6 أشهر",
                    icon = Icons.Default.CheckCircle,
                    isSelected = currentFilter == AvailabilityFilter.ELIGIBLE_NOW,
                    activeColor = AppColors.Success,
                    onClick = { onSelectFilter(AvailabilityFilter.ELIGIBLE_NOW) },
                    modifier = Modifier.weight(1f)
                )

                AvailabilityChip(
                    label = "في فترة راحة",
                    sub = "موقوف مؤقتاً",
                    icon = Icons.Default.Schedule,
                    isSelected = currentFilter == AvailabilityFilter.SUSPENDED,
                    activeColor = AppColors.Warning,
                    onClick = { onSelectFilter(AvailabilityFilter.SUSPENDED) },
                    modifier = Modifier.weight(1f)
                )

                AvailabilityChip(
                    label = "الجميع",
                    sub = "كل السجلات",
                    icon = Icons.Default.FilterList,
                    isSelected = currentFilter == AvailabilityFilter.ALL,
                    activeColor = AppColors.Primary,
                    onClick = { onSelectFilter(AvailabilityFilter.ALL) },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun AvailabilityChip(
    label: String,
    sub: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean,
    activeColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = if (isSelected) activeColor.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
        border = BorderStroke(
            1.5.dp,
            if (isSelected) activeColor else MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)
        ),
        modifier = modifier
            .clickable(onClick = onClick)
            .padding(2.dp)
    ) {
        Column(
            modifier = Modifier.padding(vertical = 10.dp, horizontal = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isSelected) activeColor else AppColors.TextSecondary,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = if (isSelected) activeColor else MaterialTheme.colorScheme.onSurface
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = sub,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontSize = 9.sp,
                    color = AppColors.TextSecondary
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LocationFilterCard(
    userType: String,
    governorates: List<String>,
    districts: List<String>,
    selectedGovernorate: String?,
    selectedDistrict: String?,
    onGovernorateSelected: (String?) -> Unit,
    onDistrictSelected: (String?) -> Unit
) {
    var govExpanded by remember { mutableStateOf(false) }
    var distExpanded by remember { mutableStateOf(false) }

    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "النطاق الجغرافي للكشف",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
            )
            Spacer(modifier = Modifier.height(12.dp))

            // إذا كان المستخدم أدمن: يمكنه تغيير المحافظة
            if (userType == "admin") {
                ExposedDropdownMenuBox(
                    expanded = govExpanded,
                    onExpandedChange = { govExpanded = it }
                ) {
                    OutlinedTextField(
                        value = selectedGovernorate ?: "كافة محافظات الجمهورية",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("المحافظة") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = govExpanded) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )
                    ExposedDropdownMenu(
                        expanded = govExpanded,
                        onDismissRequest = { govExpanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("كافة محافظات الجمهورية") },
                            onClick = {
                                onGovernorateSelected(null)
                                govExpanded = false
                            }
                        )
                        governorates.forEach { gov ->
                            DropdownMenuItem(
                                text = { Text(gov) },
                                onClick = {
                                    onGovernorateSelected(gov)
                                    govExpanded = false
                                }
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
            }

            // قائمة المديريات (متاحة للأدمن والمستشفى)
            ExposedDropdownMenuBox(
                expanded = distExpanded,
                onExpandedChange = { distExpanded = it }
            ) {
                OutlinedTextField(
                    value = selectedDistrict ?: "كافة المديريات",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("المديرية") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = distExpanded) },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )
                ExposedDropdownMenu(
                    expanded = distExpanded,
                    onDismissRequest = { distExpanded = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("كافة المديريات") },
                        onClick = {
                            onDistrictSelected(null)
                            distExpanded = false
                        }
                    )
                    districts.forEach { dist ->
                        DropdownMenuItem(
                            text = { Text(dist) },
                            onClick = {
                                onDistrictSelected(dist)
                                distExpanded = false
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ReportOptionsCard(
    selectedGender: String?,
    sortOption: SortOption,
    includeSecondaryPhones: Boolean,
    includeNotes: Boolean,
    onGenderSelected: (String?) -> Unit,
    onSortSelected: (SortOption) -> Unit,
    onToggleSecondaryPhones: () -> Unit,
    onToggleNotes: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "خيارات إضافية للكشف",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
            )

            Spacer(modifier = Modifier.height(10.dp))

            // فلتر الجنس
            Text(text = "تصفية الجنس:", style = MaterialTheme.typography.bodySmall, color = AppColors.TextSecondary)
            Spacer(modifier = Modifier.height(4.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf(null to "الكل", "male" to "ذكور", "female" to "إناث").forEach { (key, label) ->
                    val isSelected = selectedGender == key
                    FilterChip(
                        selected = isSelected,
                        onClick = { onGenderSelected(key) },
                        label = { Text(label) },
                        leadingIcon = if (isSelected) {
                            { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp)) }
                        } else null
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // ترتيب البيانات
            Text(text = "ترتيب السجلات:", style = MaterialTheme.typography.bodySmall, color = AppColors.TextSecondary)
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SortOption.values().forEach { option ->
                    val isSelected = sortOption == option
                    FilterChip(
                        selected = isSelected,
                        onClick = { onSortSelected(option) },
                        label = { Text(option.label) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))
            Spacer(modifier = Modifier.height(8.dp))

            // خيارات التبديل (Switches)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onToggleSecondaryPhones)
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f).padding(end = 12.dp)) {
                    Text(
                        text = "تضمين أرقام الهواتف البديلة",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
                    )
                    Text(
                        text = "إظهار الرقم الثاني والثالث للمتبرع إن وجد",
                        style = MaterialTheme.typography.bodySmall,
                        color = AppColors.TextSecondary
                    )
                }
                Switch(
                    checked = includeSecondaryPhones,
                    onCheckedChange = { onToggleSecondaryPhones() },
                    thumbContent = if (includeSecondaryPhones) {
                        {
                            Icon(
                                imageVector = Icons.Filled.Check,
                                contentDescription = null,
                                modifier = Modifier.size(SwitchDefaults.IconSize),
                                tint = AppColors.Primary
                            )
                        }
                    } else null,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = AppColors.Primary,
                        checkedBorderColor = Color.Transparent,
                        uncheckedThumbColor = Color.White,
                        uncheckedTrackColor = Color(0xFFE2E8F0),
                        uncheckedBorderColor = Color(0xFFCBD5E1)
                    )
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onToggleNotes)
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f).padding(end = 12.dp)) {
                    Text(
                        text = "تضمين الملاحظات الطبية",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
                    )
                    Text(
                        text = "إظهار الملاحظات المسجلة في ملف المتبرع",
                        style = MaterialTheme.typography.bodySmall,
                        color = AppColors.TextSecondary
                    )
                }
                Switch(
                    checked = includeNotes,
                    onCheckedChange = { onToggleNotes() },
                    thumbContent = if (includeNotes) {
                        {
                            Icon(
                                imageVector = Icons.Filled.Check,
                                contentDescription = null,
                                modifier = Modifier.size(SwitchDefaults.IconSize),
                                tint = AppColors.Primary
                            )
                        }
                    } else null,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = AppColors.Primary,
                        checkedBorderColor = Color.Transparent,
                        uncheckedThumbColor = Color.White,
                        uncheckedTrackColor = Color(0xFFE2E8F0),
                        uncheckedBorderColor = Color(0xFFCBD5E1)
                    )
                )
            }
        }
    }
}

@Composable
private fun ExportBottomActionBar(
    matchingCount: Int,
    isExporting: Boolean,
    onExportPdf: () -> Unit,
    onExportCsv: () -> Unit
) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 10.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Button(
                onClick = onExportPdf,
                enabled = !isExporting && matchingCount > 0,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AppColors.Primary,
                    disabledContainerColor = AppColors.Primary.copy(alpha = 0.4f)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                if (isExporting) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                    Spacer(modifier = Modifier.width(10.dp))
                    Text("جارٍ إنشاء المستند الرسمي...", fontWeight = FontWeight.Bold)
                } else {
                    Icon(imageVector = Icons.Default.PictureAsPdf, contentDescription = null, modifier = Modifier.size(22.dp))
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "تصدير ومشاركة PDF رسمي ($matchingCount متبرع)",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }

            OutlinedButton(
                onClick = onExportCsv,
                enabled = !isExporting && matchingCount > 0,
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.2.dp, Color(0xFF2E7D32)),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.TableChart,
                    contentDescription = null,
                    tint = Color(0xFF2E7D32),
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "تصدير كملف إكسل Excel (.csv)",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2E7D32)
                    )
                )
            }
        }
    }
}

@Composable
private fun DonorsPreviewContent(
    donors: List<Donor>,
    onClose: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(500.dp)
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "معاينة قائمة المتبرعين",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    text = "إجمالي المطابقين: ${donors.size} متبرع",
                    style = MaterialTheme.typography.bodySmall,
                    color = AppColors.TextSecondary
                )
            }
            IconButton(onClick = onClose) {
                Icon(imageVector = Icons.Default.Close, contentDescription = "إغلاق")
            }
        }

        Spacer(modifier = Modifier.height(12.dp))
        HorizontalDivider()

        if (donors.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("لا توجد نتائج مطابقة لهذه المعايير", color = AppColors.TextSecondary)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(donors) { donor ->
                    DonorPreviewItem(donor = donor)
                }
            }
        }
    }
}

@Composable
private fun DonorPreviewItem(donor: Donor) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = AppColors.Primary.copy(alpha = 0.12f),
                    modifier = Modifier.size(38.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = donor.bloodType,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = AppColors.Primary
                            )
                        )
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = donor.name,
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "${PhoneUtils.cleanLocalPhone(donor.phoneNumber)} • ${donor.subDistrict}",
                        style = MaterialTheme.typography.bodySmall,
                        color = AppColors.TextSecondary
                    )
                }
            }

            Surface(
                shape = RoundedCornerShape(12.dp),
                color = if (donor.canDonateNow) AppColors.SuccessContainer else AppColors.WarningContainer
            ) {
                Text(
                    text = if (donor.canDonateNow) "متاح" else "في راحة",
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = if (donor.canDonateNow) AppColors.Success else AppColors.Warning,
                        fontSize = 10.sp
                    ),
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                )
            }
        }
    }
}
