package com.bagomri.yemenbloodbank.ui.screens.admin

import android.widget.Toast
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Report
import androidx.compose.material.icons.filled.Warning
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
import com.bagomri.yemenbloodbank.core.util.DateUtils
import com.bagomri.yemenbloodbank.core.util.IntentUtils
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

    val filteredReports = uiState.reports.filter { report ->
        when (selectedStatus) {
            "pending" -> report.status == "pending"
            "approved" -> report.status == "approved"
            "rejected" -> report.status == "rejected"
            else -> true
        }
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
                // شريط تصفية الحالة
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.surface,
                    shadowElevation = 2.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilterChip(
                            selected = selectedStatus == "pending",
                            onClick = { selectedStatus = "pending" },
                            label = { Text("معلقة (${uiState.pendingReportsCount})") }
                        )
                        FilterChip(
                            selected = selectedStatus == "approved",
                            onClick = { selectedStatus = "approved" },
                            label = { Text("مقبولة") }
                        )
                        FilterChip(
                            selected = selectedStatus == "rejected",
                            onClick = { selectedStatus = "rejected" },
                            label = { Text("مرفوضة") }
                        )
                        FilterChip(
                            selected = selectedStatus == "all",
                            onClick = { selectedStatus = "all" },
                            label = { Text("الكل") }
                        )
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
                                title = "لا توجد بلاغات",
                                message = "لا توجد بلاغات تطابق الحالة المحددة حالياً",
                                icon = Icons.Default.Check
                            )
                        }

                        else -> {
                            LazyColumn(modifier = Modifier.fillMaxSize()) {
                                item {
                                    Spacer(modifier = Modifier.height(10.dp))
                                }

                                items(filteredReports, key = { it.id }) { report ->
                                    ReportAdminCard(
                                        report = report,
                                        onApprove = {
                                            viewModel.approveReport(report.id) {
                                                Toast.makeText(context, "تم قبول البلاغ وتعطيل المتبرع", Toast.LENGTH_SHORT).show()
                                            }
                                        },
                                        onReject = {
                                            viewModel.rejectReport(report.id) {
                                                Toast.makeText(context, "تم رفض البلاغ", Toast.LENGTH_SHORT).show()
                                            }
                                        },
                                        onDelete = {
                                            viewModel.deleteReport(report.id) {
                                                Toast.makeText(context, "تم حذف البلاغ", Toast.LENGTH_SHORT).show()
                                            }
                                        },
                                        onClick = { onNavigateToReportDetail(report.id) }
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
    }
}

@Composable
private fun ReportAdminCard(
    report: Report,
    onApprove: () -> Unit,
    onReject: () -> Unit,
    onDelete: () -> Unit,
    onClick: () -> Unit
) {
    val context = LocalContext.current
    val isPending = report.status == "pending"

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clickable(onClick = onClick),
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
                    color = when (report.status) {
                        "pending" -> AppColors.WarningContainer
                        "approved" -> AppColors.SuccessContainer
                        else -> AppColors.ErrorContainer
                    }
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = when (report.status) {
                                "pending" -> Icons.Default.Warning
                                "approved" -> Icons.Default.Check
                                else -> Icons.Default.Close
                            },
                            contentDescription = null,
                            tint = when (report.status) {
                                "pending" -> AppColors.Warning
                                "approved" -> AppColors.Success
                                else -> AppColors.Error
                            },
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "رقم المتبرع: ${report.donorPhoneNumber}",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "السبب: ${report.reasonText}",
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                        color = AppColors.Error
                    )
                }

                IconButton(
                    onClick = { IntentUtils.makePhoneCall(context, report.donorPhoneNumber) },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Call,
                        contentDescription = "اتصال",
                        tint = AppColors.Primary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            if (!report.notes.isNullOrEmpty()) {
                Spacer(modifier = Modifier.height(10.dp))
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    color = AppColors.SurfaceVariant
                ) {
                    Text(
                        text = "ملاحظات المبلغ: ${report.notes}",
                        style = MaterialTheme.typography.bodySmall,
                        color = AppColors.TextSecondary,
                        modifier = Modifier.padding(10.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

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

                Row {
                    if (isPending) {
                        Button(
                            onClick = onApprove,
                            colors = ButtonDefaults.buttonColors(containerColor = AppColors.Success),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.height(34.dp)
                        ) {
                            Text("قبول وتعطيل", fontSize = 12.sp)
                        }

                        Spacer(modifier = Modifier.width(6.dp))

                        Button(
                            onClick = onReject,
                            colors = ButtonDefaults.buttonColors(containerColor = AppColors.Error),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.height(34.dp)
                        ) {
                            Text("رفض", fontSize = 12.sp)
                        }
                    } else {
                        IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                            Icon(Icons.Default.Delete, contentDescription = "حذف", tint = AppColors.TextSecondary, modifier = Modifier.size(18.dp))
                        }
                    }
                }
            }
        }
    }
}
