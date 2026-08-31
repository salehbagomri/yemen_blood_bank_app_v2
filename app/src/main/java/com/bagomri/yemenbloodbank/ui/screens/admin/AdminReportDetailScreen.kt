package com.bagomri.yemenbloodbank.ui.screens.admin

import android.widget.Toast
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ReportProblem
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
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
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
import com.bagomri.yemenbloodbank.core.util.DateUtils
import com.bagomri.yemenbloodbank.core.util.ErrorHandler
import com.bagomri.yemenbloodbank.core.util.IntentUtils
import com.bagomri.yemenbloodbank.data.model.Donor
import com.bagomri.yemenbloodbank.data.model.Report
import com.bagomri.yemenbloodbank.data.repository.DonorRepository
import com.bagomri.yemenbloodbank.data.repository.ReportRepository
import com.bagomri.yemenbloodbank.ui.components.DonorCard
import com.bagomri.yemenbloodbank.ui.components.LoadingIndicator
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminReportDetailScreen(
    reportId: String,
    onNavigateBack: () -> Unit,
    reportRepository: ReportRepository = ReportRepository(),
    donorRepository: DonorRepository = DonorRepository()
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var isLoading by remember { mutableStateOf(true) }
    var report by remember { mutableStateOf<Report?>(null) }
    var matchedDonor by remember { mutableStateOf<Donor?>(null) }

    LaunchedEffect(reportId) {
        val repResult = reportRepository.getAllReports()
        val found = repResult.getOrNull()?.find { it.id == reportId }
        report = found

        if (found != null) {
            val donorResult = if (found.donorId.isNotBlank()) {
                donorRepository.getDonorById(found.donorId)
            } else {
                donorRepository.findDonorByPhone(found.donorPhoneNumber)
            }
            matchedDonor = donorResult.getOrNull()
        }
        isLoading = false
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("تفاصيل البلاغ", fontWeight = FontWeight.Bold, color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = AppStrings.back, tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = AppColors.Primary)
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
        ) {
            if (isLoading) {
                LoadingIndicator(message = "جاري تحميل تفاصيل البلاغ...")
            } else if (report == null) {
                Text("لم يتم العثور على البلاغ", modifier = Modifier.align(Alignment.Center))
            } else {
                val rep = report!!
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // كرت معلومات البلاغ
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Surface(
                                        modifier = Modifier.size(44.dp),
                                        shape = RoundedCornerShape(10.dp),
                                        color = AppColors.WarningContainer
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Icon(
                                                imageVector = Icons.Default.ReportProblem,
                                                contentDescription = null,
                                                tint = AppColors.Warning,
                                                modifier = Modifier.size(24.dp)
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(
                                            text = rep.donorPhoneNumber,
                                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = "سبب البلاغ: ${rep.reasonText}",
                                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                                            color = AppColors.Error
                                        )
                                    }
                                }

                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = when (rep.status) {
                                        "approved" -> AppColors.SuccessContainer
                                        "rejected" -> AppColors.ErrorContainer
                                        else -> AppColors.WarningContainer
                                    }
                                ) {
                                    Text(
                                        text = when (rep.status) {
                                            "approved" -> "مقبول"
                                            "rejected" -> "مرفوض"
                                            else -> "بانتظار المراجعة"
                                        },
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        color = when (rep.status) {
                                            "approved" -> AppColors.Success
                                            "rejected" -> AppColors.Error
                                            else -> AppColors.Warning
                                        },
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                    )
                                }
                            }

                            if (!rep.notes.isNullOrEmpty()) {
                                Spacer(modifier = Modifier.height(14.dp))
                                Text("تفاصيل وملاحظات إضافية:", style = MaterialTheme.typography.labelMedium, color = AppColors.TextSecondary)
                                Spacer(modifier = Modifier.height(4.dp))
                                Surface(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(8.dp),
                                    color = AppColors.SurfaceVariant
                                ) {
                                    Text(text = rep.notes, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(12.dp))
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "تاريخ البلاغ: ${DateUtils.formatIsoToDisplay(rep.createdAt)}",
                                style = MaterialTheme.typography.labelSmall,
                                color = AppColors.TextSecondary
                            )
                        }
                    }

                    // بيانات المتبرع المرتبط
                    Text(
                        text = "بيانات المتبرع في قاعدة البيانات",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    if (matchedDonor != null) {
                        DonorCard(donor = matchedDonor!!)
                    } else {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = AppColors.SurfaceVariant)
                        ) {
                            Text(
                                text = "لم يتم العثور على سجل متبرع مطابق لهذا الرقم في قاعدة البيانات (قد يكون محذوفاً بالفعل).",
                                style = MaterialTheme.typography.bodySmall,
                                color = AppColors.TextSecondary,
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // أزرار اتخاذ القرار
                    if (rep.status == "pending") {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Button(
                                onClick = {
                                    scope.launch {
                                        reportRepository.approveReport(rep.id)
                                        Toast.makeText(context, "تم قبول البلاغ وتعطيل المتبرع", Toast.LENGTH_SHORT).show()
                                        onNavigateBack()
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = AppColors.Success),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp)
                            ) {
                                Icon(Icons.Default.Check, contentDescription = null)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("قبول البلاغ")
                            }

                            Button(
                                onClick = {
                                    scope.launch {
                                        reportRepository.rejectReport(rep.id)
                                        Toast.makeText(context, "تم رفض البلاغ", Toast.LENGTH_SHORT).show()
                                        onNavigateBack()
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = AppColors.Error),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp)
                            ) {
                                Icon(Icons.Default.Close, contentDescription = null)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("رفض البلاغ")
                            }
                        }
                    } else {
                        Button(
                            onClick = {
                                scope.launch {
                                    reportRepository.deleteReport(rep.id)
                                    Toast.makeText(context, "تم حذف البلاغ", Toast.LENGTH_SHORT).show()
                                    onNavigateBack()
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = AppColors.Error),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                        ) {
                            Icon(Icons.Default.Delete, contentDescription = null)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("حذف هذا البلاغ")
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}
