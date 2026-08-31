package com.bagomri.yemenbloodbank.ui.screens.admin

import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Bloodtype
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.ReportProblem
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bagomri.yemenbloodbank.core.constants.AppColors
import com.bagomri.yemenbloodbank.core.constants.AppStrings
import com.bagomri.yemenbloodbank.ui.components.ErrorDisplay
import com.bagomri.yemenbloodbank.ui.components.LoadingIndicator

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminSystemOverviewScreen(
    onNavigateBack: () -> Unit,
    viewModel: AdminDashboardViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("نظرة عامة على النظام والتحليلات", fontWeight = FontWeight.Bold, color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = AppStrings.back, tint = Color.White)
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.refresh() }) {
                        Icon(Icons.Default.Refresh, contentDescription = AppStrings.refresh, tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = AppColors.Primary)
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
            if (uiState.isLoading) {
                LoadingIndicator(message = "جاري تجميع الإحصائيات الوطنية...")
            } else if (uiState.errorMessage != null) {
                ErrorDisplay(message = uiState.errorMessage!!, onRetry = { viewModel.loadData() })
            } else {
                // إحصائيات توزيع المحافظات
                val govDistribution = AppStrings.yemenGovernorates.associateWith { gov ->
                    uiState.donors.count { it.governorate == gov }
                }.filter { it.value > 0 }

                // إحصائيات فصائل الدم
                val bloodDistribution = AppStrings.bloodTypes.associateWith { type ->
                    uiState.donors.count { it.bloodType == type }
                }

                val sortedGovs = govDistribution.entries.sortedByDescending { it.value }

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text("المؤشرات الوطنية الشاملة", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        OverviewCard(title = "إجمالي المتبرعين", value = "${uiState.totalDonors}", icon = Icons.Default.People, color = Color(0xFF1976D2), modifier = Modifier.weight(1f))
                        OverviewCard(title = "المتاحون للتبرع", value = "${uiState.availableDonors}", icon = Icons.Default.CheckCircle, color = Color(0xFF388E3C), modifier = Modifier.weight(1f))
                    }

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        OverviewCard(title = "الموقوفون مؤقتاً", value = "${uiState.suspendedDonors}", icon = Icons.Default.Schedule, color = Color(0xFFF57C00), modifier = Modifier.weight(1f))
                        OverviewCard(title = "البلاغات المعلقة", value = "${uiState.pendingReportsCount}", icon = Icons.Default.ReportProblem, color = Color(0xFFD32F2F), modifier = Modifier.weight(1f))
                    }

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        OverviewCard(title = "المستشفيات المسجلة", value = "${uiState.totalHospitals}", icon = Icons.Default.LocalHospital, color = Color(0xFF7B1FA2), modifier = Modifier.weight(1f))
                        OverviewCard(title = "المستشفيات النشطة", value = "${uiState.activeHospitals}", icon = Icons.Default.CheckCircle, color = Color(0xFF00796B), modifier = Modifier.weight(1f))
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // توزيع فصائل الدم على مستوى الجمهورية
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Bloodtype, contentDescription = null, tint = AppColors.Primary)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("توزيع فصائل الدم في الجمهورية", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            bloodDistribution.forEach { (type, count) ->
                                val pct = if (uiState.totalDonors > 0) (count.toFloat() / uiState.totalDonors) * 100f else 0f
                                val color = AppColors.getBloodTypeColor(type)

                                Column(modifier = Modifier.padding(bottom = 12.dp)) {
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Text("$type ($count متبرع)", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold))
                                        Text("${String.format("%.1f", pct)}%", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, color = color))
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    LinearProgressIndicator(
                                        progress = { if (uiState.totalDonors > 0) count.toFloat() / uiState.totalDonors else 0f },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(6.dp),
                                        color = color,
                                        trackColor = AppColors.SurfaceVariant
                                    )
                                }
                            }
                        }
                    }

                    // التوزيع الجغرافي للمحافظات
                    if (sortedGovs.isNotEmpty()) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Map, contentDescription = null, tint = AppColors.Primary)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("المحافظات الأكثر تسجيلاً للمتبرعين", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                                }

                                Spacer(modifier = Modifier.height(14.dp))

                                for (entry in sortedGovs) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 8.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(entry.key, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold))
                                        Surface(
                                            shape = RoundedCornerShape(8.dp),
                                            color = AppColors.PrimaryContainer
                                        ) {
                                            Text(
                                                text = "${entry.value} متبرع",
                                                color = AppColors.Primary,
                                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

@Composable
private fun OverviewCard(
    title: String,
    value: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Surface(
                modifier = Modifier.size(36.dp),
                shape = RoundedCornerShape(8.dp),
                color = color.copy(alpha = 0.12f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(20.dp))
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, fontSize = 22.sp),
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(text = title, style = MaterialTheme.typography.labelSmall, color = AppColors.TextSecondary)
        }
    }
}
