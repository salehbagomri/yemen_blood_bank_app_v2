package com.bagomri.yemenbloodbank.ui.screens.admin

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.ReportProblem
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bagomri.yemenbloodbank.core.constants.AppColors
import com.bagomri.yemenbloodbank.core.constants.AppStrings
import com.bagomri.yemenbloodbank.ui.components.EmptyState
import com.bagomri.yemenbloodbank.ui.components.ErrorDisplay
import com.bagomri.yemenbloodbank.ui.components.LoadingIndicator

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    onNavigateToReviewReports: () -> Unit,
    onNavigateToManageHospitals: () -> Unit,
    onNavigateToManageDonors: () -> Unit,
    onNavigateToSystemOverview: () -> Unit,
    onNavigateToManageLocations: () -> Unit,
    onNavigateToManageBanners: () -> Unit,
    onLogout: () -> Unit,
    viewModel: AdminDashboardViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showLogoutDialog by remember { mutableStateOf(false) }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("تأكيد تسجيل الخروج", fontWeight = FontWeight.Bold) },
            text = { Text("هل أنت متأكد من تسجيل الخروج من لوحة الإدارة؟") },
            confirmButton = {
                Button(
                    onClick = {
                        showLogoutDialog = false
                        onLogout()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AppColors.Error)
                ) {
                    Text("تسجيل الخروج")
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text(AppStrings.cancel)
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "لوحة تحكم الإدارة العامة",
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                },
                actions = {
                    IconButton(onClick = { showLogoutDialog = true }) {
                        Icon(
                            imageVector = Icons.Default.ExitToApp,
                            contentDescription = "تسجيل الخروج",
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
            if (uiState.isLoading) {
                LoadingIndicator(message = "جاري تحميل لوحة التحكم المركزية...")
            } else if (uiState.errorMessage != null) {
                ErrorDisplay(
                    message = uiState.errorMessage!!,
                    onRetry = { viewModel.loadData() }
                )
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Header
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    Brush.horizontalGradient(
                                        listOf(Color(0xFF880E4F), Color(0xFFC2185B))
                                    )
                                )
                                .padding(20.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    modifier = Modifier.size(54.dp),
                                    shape = CircleShape,
                                    color = Color.White.copy(alpha = 0.2f)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = Icons.Default.AdminPanelSettings,
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.size(32.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.width(16.dp))

                                Column {
                                    Text(
                                        text = "الإدارة العامة لبنك دم اليمن",
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "صلاحيات وطنية كاملة - جميع المحافظات",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color.White.copy(alpha = 0.9f)
                                    )
                                }
                            }
                        }
                    }

                    // شبكة الإحصائيات الوطنية
                    Text(
                        text = "نظرة عامة على الجمهورية",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        AdminMiniMetricCard(
                            title = "إجمالي المتبرعين",
                            value = "${uiState.totalDonors}",
                            color = Color(0xFF1976D2),
                            modifier = Modifier.weight(1f)
                        )
                        AdminMiniMetricCard(
                            title = "المتاحون للتبرع",
                            value = "${uiState.availableDonors}",
                            color = Color(0xFF388E3C),
                            modifier = Modifier.weight(1f)
                        )
                        AdminMiniMetricCard(
                            title = "الموقوفون مؤقتاً",
                            value = "${uiState.suspendedDonors}",
                            color = Color(0xFFF57C00),
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        AdminMiniMetricCard(
                            title = "البلاغات المعلقة",
                            value = "${uiState.pendingReportsCount}",
                            color = if (uiState.pendingReportsCount > 0) Color(0xFFD32F2F) else Color(0xFF388E3C),
                            modifier = Modifier.weight(1f)
                        )
                        AdminMiniMetricCard(
                            title = "المستشفيات النشطة",
                            value = "${uiState.activeHospitals}",
                            color = Color(0xFF00796B),
                            modifier = Modifier.weight(1f)
                        )
                        AdminMiniMetricCard(
                            title = "إجمالي المستشفيات",
                            value = "${uiState.totalHospitals}",
                            color = Color(0xFF5E35B1),
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // الأقسام الإدارية الرئيسية
                    Text(
                        text = "الإدارة الرئيسية",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    AdminSectionTile(
                        icon = Icons.Default.ReportProblem,
                        title = "مراجعة البلاغات الواردة",
                        subtitle = "${uiState.pendingReportsCount} بلاغ بانتظار المراجعة والتحقق",
                        iconColor = if (uiState.pendingReportsCount > 0) AppColors.Warning else AppColors.Success,
                        badgeCount = if (uiState.pendingReportsCount > 0) uiState.pendingReportsCount else null,
                        onClick = onNavigateToReviewReports
                    )

                    AdminSectionTile(
                        icon = Icons.Default.LocalHospital,
                        title = "إدارة المستشفيات",
                        subtitle = "${uiState.activeHospitals} مستشفى نشط من أصل ${uiState.totalHospitals}",
                        iconColor = AppColors.Info,
                        onClick = onNavigateToManageHospitals
                    )

                    AdminSectionTile(
                        icon = Icons.Default.People,
                        title = "إدارة جميع المتبرعين",
                        subtitle = "${uiState.totalDonors} متبرع مسجل بالجمهورية",
                        iconColor = AppColors.Primary,
                        onClick = onNavigateToManageDonors
                    )

                    AdminSectionTile(
                        icon = Icons.Default.Analytics,
                        title = "نظرة عامة على النظام والإحصائيات",
                        subtitle = "إحصائيات تفصيلية وتوزيع جغرافي للمحافظات الـ 22",
                        iconColor = Color(0xFF2E7D32),
                        onClick = onNavigateToSystemOverview
                    )

                    AdminSectionTile(
                        icon = Icons.Default.Map,
                        title = "إدارة المحافظات والمديريات",
                        subtitle = "تفعيل وتعطيل المناطق الجغرافية والتحقق من التبعية",
                        iconColor = Color(0xFF0288D1),
                        onClick = onNavigateToManageLocations
                    )

                    AdminSectionTile(
                        icon = Icons.Default.PhotoLibrary,
                        title = "إدارة البانرات والشرائح الإعلانية",
                        subtitle = "إضافة وتعديل ورفع صور البانرات التوعوية",
                        iconColor = Color(0xFF7B1FA2),
                        onClick = onNavigateToManageBanners
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

@Composable
private fun AdminMiniMetricCard(
    title: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                ),
                color = color
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                color = AppColors.TextSecondary,
                maxLines = 1
            )
        }
    }
}

@Composable
private fun AdminSectionTile(
    icon: ImageVector,
    title: String,
    subtitle: String,
    iconColor: Color,
    badgeCount: Int? = null,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(46.dp),
                shape = RoundedCornerShape(12.dp),
                color = iconColor.copy(alpha = 0.12f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = iconColor,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    if (badgeCount != null && badgeCount > 0) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Surface(
                            shape = CircleShape,
                            color = AppColors.Error
                        ) {
                            Text(
                                text = "$badgeCount",
                                color = Color.White,
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = AppColors.TextSecondary
                )
            }

            Icon(
                imageVector = Icons.Default.ArrowForwardIos,
                contentDescription = null,
                tint = AppColors.TextSecondary,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}
