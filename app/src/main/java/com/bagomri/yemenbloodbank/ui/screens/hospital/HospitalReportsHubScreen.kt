package com.bagomri.yemenbloodbank.ui.screens.hospital

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Bloodtype
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HospitalReportsHubScreen(
    onNavigateBack: () -> Unit,
    onNavigateToBloodTypeReport: () -> Unit,
    onNavigateToSuspendedDonors: () -> Unit,
    onNavigateToExportDonors: () -> Unit = {},
    viewModel: HospitalDashboardViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "التقارير والإحصائيات",
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
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // بطاقة معلومات المحافظة التوضيحية
            Card(
                colors = CardDefaults.cardColors(containerColor = AppColors.InfoContainer),
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, AppColors.Info.copy(alpha = 0.25f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        modifier = Modifier.size(42.dp),
                        shape = RoundedCornerShape(10.dp),
                        color = AppColors.Info
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column {
                        Text(
                            text = if (uiState.hospitalGovernorate != null) {
                                "تقارير وإحصائيات محافظة ${uiState.hospitalGovernorate}"
                            } else {
                                "تقارير وإحصائيات المستشفى"
                            },
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = AppColors.Info
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "بيانات لحظية ومحدثة لفصائل الدم، سجل الإيقاف، وتصدير الكشوفات الرسمية",
                            style = MaterialTheme.typography.bodySmall,
                            color = AppColors.TextPrimary
                        )
                    }
                }
            }

            // 1. تقرير استخراج وتصدير كشف المتبرعين
            ReportHubCard(
                title = "استخراج وتصدير كشف المتبرعين",
                description = "إنشاء كشف مفلتر رسمي وجاهز للطباعة أو التصدير بصيغة PDF أو Excel مع المشاركة الفورية",
                icon = Icons.Default.PictureAsPdf,
                gradient = listOf(Color(0xFFB71C1C), Color(0xFFC62828)),
                badge = "PDF / Excel",
                onClick = onNavigateToExportDonors
            )

            // 2. سجل المتبرعين الموقوفين (فترة الراحة 6 أشهر) - منقول من الخدمات الإدارية
            ReportHubCard(
                title = "سجل المتبرعين الموقوفين (فترة الراحة)",
                description = "قائمة المتبرعين في فترة الراحة (6 أشهر)، العد التنازلي، وتاريخ إتاحتهم للتبرع مجدداً",
                icon = Icons.Default.Schedule,
                gradient = listOf(Color(0xFFE65100), Color(0xFFF57C00)),
                badge = if (uiState.suspendedDonors.isNotEmpty()) "${uiState.suspendedDonors.size} موقوف" else "لا يوجد موقوفون",
                onClick = onNavigateToSuspendedDonors
            )

            // 3. تقرير وتوزيع فصائل الدم
            ReportHubCard(
                title = "إحصائيات وتوزيع فصائل الدم",
                description = "تحليل بياني لنسب وتوزيع فصائل الدم الثماني مع رصد الفصائل النادرة والأكثر توفراً",
                icon = Icons.Default.Bloodtype,
                gradient = listOf(Color(0xFF1565C0), Color(0xFF1976D2)),
                badge = if (uiState.donors.isNotEmpty()) "${uiState.donors.size} متبرع" else null,
                onClick = onNavigateToBloodTypeReport
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun ReportHubCard(
    title: String,
    description: String,
    icon: ImageVector,
    gradient: List<Color>,
    badge: String? = null,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.horizontalGradient(gradient))
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    modifier = Modifier.size(52.dp),
                    shape = RoundedCornerShape(14.dp),
                    color = Color.White.copy(alpha = 0.2f)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontSize = 16.sp
                            )
                        )
                    }

                    if (badge != null) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Color.White.copy(alpha = 0.22f)
                        ) {
                            Text(
                                text = badge,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp
                                ),
                                modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = description,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color.White.copy(alpha = 0.9f),
                            lineHeight = 18.sp
                        )
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                    contentDescription = null,
                    tint = Color.White.copy(alpha = 0.8f),
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}
