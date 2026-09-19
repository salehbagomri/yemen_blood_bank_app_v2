package com.bagomri.yemenbloodbank.ui.screens.admin

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.PhoneDisabled
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bagomri.yemenbloodbank.core.constants.AppColors
import com.bagomri.yemenbloodbank.core.constants.AppStrings
import com.bagomri.yemenbloodbank.core.util.DateUtils
import com.bagomri.yemenbloodbank.core.util.IntentUtils
import com.bagomri.yemenbloodbank.core.util.PhoneUtils
import com.bagomri.yemenbloodbank.data.model.Donor
import com.bagomri.yemenbloodbank.data.model.Report
import com.bagomri.yemenbloodbank.ui.components.EmptyState
import com.bagomri.yemenbloodbank.ui.components.ErrorDisplay
import com.bagomri.yemenbloodbank.ui.components.LoadingIndicator

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminReviewReportsScreen(
    onNavigateBack: () -> Unit,
    onNavigateToReportDetail: (String) -> Unit,
    viewModel: AdminDashboardViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    var selectedStatus by remember { mutableStateOf("pending") } // pending, approved, rejected, all
    var searchQuery by remember { mutableStateOf("") }
    var reportToDelete by remember { mutableStateOf<Report?>(null) }

    // إحصائيات سريعة
    val pendingCount = remember(uiState.reports) { uiState.reports.count { it.status == "pending" } }
    val approvedCount = remember(uiState.reports) { uiState.reports.count { it.status == "approved" } }
    val rejectedCount = remember(uiState.reports) { uiState.reports.count { it.status == "rejected" } }

    val filteredReports = uiState.reports.filter { report ->
        val matchesStatus = when (selectedStatus) {
            "pending" -> report.status == "pending"
            "approved" -> report.status == "approved"
            "rejected" -> report.status == "rejected"
            else -> true
        }
        if (!matchesStatus) return@filter false

        if (searchQuery.isBlank()) return@filter true

        val q = searchQuery.trim().lowercase()
        val donor = uiState.donors.find { it.id == report.donorId }
        val donorName = donor?.name?.lowercase().orEmpty()
        val donorPhone = donor?.phoneNumber.orEmpty()
        val donorPhone2 = donor?.phoneNumber2.orEmpty()
        val donorPhone3 = donor?.phoneNumber3.orEmpty()
        val reason = report.reasonText.lowercase()
        val gov = donor?.governorate?.lowercase().orEmpty()
        val district = donor?.district?.lowercase().orEmpty()

        donorName.contains(q) || donorPhone.contains(q) || donorPhone2.contains(q) ||
                donorPhone3.contains(q) || reason.contains(q) || gov.contains(q) || district.contains(q)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "مراجعة البلاغات الواردة",
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
                    IconButton(onClick = { viewModel.refresh() }) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = AppStrings.refresh,
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
                    Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)) {
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            placeholder = { Text("ابحث باسم المتبرع، رقمه، المحافظة أو سبب البلاغ...") },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = "بحث",
                                    tint = AppColors.Primary
                                )
                            },
                            trailingIcon = {
                                if (searchQuery.isNotEmpty()) {
                                    IconButton(onClick = { searchQuery = "" }) {
                                        Icon(
                                            imageVector = Icons.Default.Clear,
                                            contentDescription = "مسح",
                                            tint = AppColors.TextSecondary
                                        )
                                    }
                                }
                            },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = AppColors.Primary,
                                unfocusedBorderColor = AppColors.Divider
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // شريط الفلاتر
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            FilterChip(
                                selected = selectedStatus == "pending",
                                onClick = { selectedStatus = "pending" },
                                label = { Text("معلقة ($pendingCount)") },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = AppColors.WarningContainer,
                                    selectedLabelColor = AppColors.Warning
                                )
                            )
                            FilterChip(
                                selected = selectedStatus == "approved",
                                onClick = { selectedStatus = "approved" },
                                label = { Text("مقبولة ($approvedCount)") },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = AppColors.SuccessContainer,
                                    selectedLabelColor = AppColors.Success
                                )
                            )
                            FilterChip(
                                selected = selectedStatus == "rejected",
                                onClick = { selectedStatus = "rejected" },
                                label = { Text("مرفوضة ($rejectedCount)") },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = AppColors.ErrorContainer,
                                    selectedLabelColor = AppColors.Error
                                )
                            )
                            FilterChip(
                                selected = selectedStatus == "all",
                                onClick = { selectedStatus = "all" },
                                label = { Text("الكل (${uiState.reports.size})") }
                            )
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
                            LoadingIndicator(message = "جاري تحميل البلاغات...")
                        }

                        uiState.errorMessage != null -> {
                            ErrorDisplay(
                                message = uiState.errorMessage!!,
                                onRetry = { viewModel.loadData() }
                            )
                        }

                        filteredReports.isEmpty() -> {
                            EmptyState(
                                title = if (searchQuery.isNotBlank()) "لا توجد نتائج بحث" else "لا توجد بلاغات",
                                message = if (searchQuery.isNotBlank()) "لم يتم العثور على بلاغات تطابق كلمة البحث" else "لا توجد بلاغات تطابق الحالة المحددة حالياً",
                                icon = Icons.Default.CheckCircle
                            )
                        }

                        else -> {
                            LazyColumn(modifier = Modifier.fillMaxSize()) {
                                item {
                                    Spacer(modifier = Modifier.height(12.dp))
                                }

                                items(filteredReports, key = { it.id }) { report ->
                                    val matchedDonor = uiState.donors.find { it.id == report.donorId }
                                    ReportAdminCard(
                                        report = report,
                                        donor = matchedDonor,
                                        onApproveAndDeactivate = {
                                            viewModel.approveReport(
                                                reportId = report.id,
                                                deactivateDonorId = report.donorId
                                            ) {
                                                Toast.makeText(context, "تم قبول البلاغ وتعطيل المتبرع بنجاح", Toast.LENGTH_SHORT).show()
                                            }
                                        },
                                        onApproveOnly = {
                                            viewModel.approveReport(
                                                reportId = report.id,
                                                deactivateDonorId = null
                                            ) {
                                                Toast.makeText(context, "تم قبول البلاغ فقط (دون تعطيل المتبرع)", Toast.LENGTH_SHORT).show()
                                            }
                                        },
                                        onReject = {
                                            viewModel.rejectReport(report.id) {
                                                Toast.makeText(context, "تم رفض البلاغ", Toast.LENGTH_SHORT).show()
                                            }
                                        },
                                        onResetToPending = {
                                            viewModel.resetReportToPending(report.id) {
                                                Toast.makeText(context, "تمت إعادة البلاغ لقيد المراجعة", Toast.LENGTH_SHORT).show()
                                            }
                                        },
                                        onDelete = {
                                            reportToDelete = report
                                        },
                                        onClick = { onNavigateToReportDetail(report.id) }
                                    )
                                    Spacer(modifier = Modifier.height(10.dp))
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
    }

    // تأكيد الحذف
    reportToDelete?.let { rep ->
        AlertDialog(
            onDismissRequest = { reportToDelete = null },
            title = { Text("حذف البلاغ", fontWeight = FontWeight.Bold) },
            text = { Text("هل أنت متأكد من رغبتك في حذف هذا البلاغ نهائياً من النظام؟") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteReport(rep.id) {
                            Toast.makeText(context, "تم حذف البلاغ", Toast.LENGTH_SHORT).show()
                        }
                        reportToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AppColors.Error)
                ) {
                    Text("حذف")
                }
            },
            dismissButton = {
                TextButton(onClick = { reportToDelete = null }) {
                    Text("إلغاء")
                }
            }
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ReportAdminCard(
    report: Report,
    donor: Donor?,
    onApproveAndDeactivate: () -> Unit,
    onApproveOnly: () -> Unit,
    onReject: () -> Unit,
    onResetToPending: () -> Unit,
    onDelete: () -> Unit,
    onClick: () -> Unit
) {
    val context = LocalContext.current
    val isPending = report.status == "pending"

    val donorPhone = donor?.phoneNumber
    val reportPhone = report.donorPhoneNumber
    val displayPhone = if (!donorPhone.isNullOrBlank()) {
        donorPhone
    } else if (reportPhone.isNotBlank()) {
        reportPhone
    } else {
        "غير مسجل"
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // الصف العلوي: فصيلة الدم + اسم المتبرع وحالته + شارة حالة البلاغ
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // دائرة فصيلة الدم
                val bgCol = if (donor != null) AppColors.getBloodTypeContainerColor(donor.bloodType) else AppColors.SurfaceVariant
                val textCol = if (donor != null) AppColors.getBloodTypeColor(donor.bloodType) else AppColors.TextSecondary

                Surface(
                    modifier = Modifier.size(46.dp),
                    shape = CircleShape,
                    color = bgCol
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        if (donor != null) {
                            Text(
                                text = donor.bloodType,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 16.sp,
                                color = textCol
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = null,
                                tint = AppColors.Warning,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                // اسم المتبرع وحالته في النظام
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = donor?.name ?: "متبرع غير متوفر بقاعدة البيانات",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        if (donor != null) {
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = if (donor.isActive) AppColors.SuccessContainer else AppColors.SurfaceVariant
                            ) {
                                Text(
                                    text = if (donor.isActive) "حساب مفعل" else "حساب معطل",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (donor.isActive) AppColors.Success else AppColors.TextSecondary,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }

                            if (donor.isSuspended) {
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = AppColors.WarningContainer
                                ) {
                                    Text(
                                        text = "موقوف مؤقتاً",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = AppColors.Warning,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                // شارة حالة البلاغ
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = when (report.status) {
                        "approved" -> AppColors.SuccessContainer
                        "rejected" -> AppColors.ErrorContainer
                        else -> AppColors.WarningContainer
                    }
                ) {
                    Text(
                        text = report.statusText,
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = when (report.status) {
                            "approved" -> AppColors.Success
                            "rejected" -> AppColors.Error
                            else -> AppColors.Warning
                        },
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // كرت سبب البلاغ والأولوية
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                color = when (report.priority) {
                    "critical" -> AppColors.ErrorContainer
                    "high" -> AppColors.WarningContainer
                    else -> AppColors.SurfaceVariant
                }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = when (report.reason) {
                                "deceased" -> Icons.Default.Close
                                "refuses_to_donate" -> Icons.Default.Block
                                "wrong_number", "number_not_working" -> Icons.Default.PhoneDisabled
                                else -> Icons.Default.Warning
                            },
                            contentDescription = null,
                            tint = when (report.priority) {
                                "critical" -> AppColors.Error
                                "high" -> AppColors.Warning
                                else -> AppColors.Primary
                            },
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "السبب: ${report.reasonText}",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                            color = when (report.priority) {
                                "critical" -> AppColors.Error
                                "high" -> AppColors.Warning
                                else -> MaterialTheme.colorScheme.onSurface
                            }
                        )
                    }

                    // أولوية البلاغ
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = when (report.priority) {
                            "critical" -> AppColors.Error
                            "high" -> AppColors.Warning
                            else -> AppColors.SecondaryContainer
                        }
                    ) {
                        Text(
                            text = report.priorityText,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = when (report.priority) {
                                "critical", "high" -> Color.White
                                else -> AppColors.Primary
                            },
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // معلومات الاتصال والموقع
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    // رقم الهاتف
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Phone,
                            contentDescription = null,
                            tint = AppColors.TextSecondary,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = PhoneUtils.formatDisplayPhone(displayPhone),
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    // الموقع إن توفر
                    if (donor != null && (!donor.governorate.isBlank() || !donor.district.isBlank())) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = null,
                                tint = AppColors.TextSecondary,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = donor.displayLocation,
                                style = MaterialTheme.typography.labelSmall,
                                color = AppColors.TextSecondary
                            )
                        }
                    }
                }

                // أزرار الاتصال السريع
                if (displayPhone.isNotBlank() && displayPhone != "غير مسجل") {
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        IconButton(
                            onClick = { IntentUtils.makePhoneCall(context, displayPhone) },
                            modifier = Modifier.size(34.dp)
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

            Spacer(modifier = Modifier.height(10.dp))

            // التاريخ والأزرار الإجرائية
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = DateUtils.formatIsoToDisplay(report.createdAt),
                    style = MaterialTheme.typography.labelSmall,
                    color = AppColors.TextSecondary
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    if (isPending) {
                        Button(
                            onClick = onApproveAndDeactivate,
                            colors = ButtonDefaults.buttonColors(containerColor = AppColors.Success),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.height(34.dp)
                        ) {
                            Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("قبول وتعطيل", fontSize = 12.sp)
                        }

                        OutlinedButton(
                            onClick = onReject,
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = AppColors.Error),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.height(34.dp)
                        ) {
                            Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("رفض", fontSize = 12.sp)
                        }
                    } else {
                        TextButton(
                            onClick = onResetToPending,
                            modifier = Modifier.height(32.dp)
                        ) {
                            Text("إعادة للمراجعة", fontSize = 11.sp, color = AppColors.Primary)
                        }

                        IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                            Icon(
                                Icons.Default.Delete,
                                contentDescription = "حذف",
                                tint = AppColors.Error,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
