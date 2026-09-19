package com.bagomri.yemenbloodbank.ui.screens.admin

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
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
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.LockReset
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.PhoneDisabled
import androidx.compose.material.icons.filled.ReportProblem
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.bagomri.yemenbloodbank.core.util.IntentUtils
import com.bagomri.yemenbloodbank.core.util.PhoneUtils
import com.bagomri.yemenbloodbank.data.model.Donor
import com.bagomri.yemenbloodbank.data.model.Report
import com.bagomri.yemenbloodbank.data.repository.DonorRepository
import com.bagomri.yemenbloodbank.data.repository.ReportRepository
import com.bagomri.yemenbloodbank.ui.components.LoadingIndicator
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminReportDetailScreen(
    reportId: String,
    onNavigateBack: () -> Unit,
    onNavigateToEditDonor: (String) -> Unit = {},
    reportRepository: ReportRepository = ReportRepository(),
    donorRepository: DonorRepository = DonorRepository()
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var isLoading by remember { mutableStateOf(true) }
    var report by remember { mutableStateOf<Report?>(null) }
    var matchedDonor by remember { mutableStateOf<Donor?>(null) }

    var showDeleteReportDialog by remember { mutableStateOf(false) }
    var showDeleteDonorDialog by remember { mutableStateOf(false) }

    fun loadData() {
        scope.launch {
            isLoading = true
            val repResult = reportRepository.getReportById(reportId)
            var found = repResult.getOrNull()
            if (found == null) {
                // Fallback إلى قائمة البلاغات
                val all = reportRepository.getAllReports().getOrNull()
                found = all?.find { it.id == reportId }
            }
            report = found

            if (found != null && found.donorId.isNotBlank()) {
                val donorResult = donorRepository.getDonorById(found.donorId)
                matchedDonor = donorResult.getOrNull()
            }
            isLoading = false
        }
    }

    LaunchedEffect(reportId) {
        loadData()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("تفاصيل البلاغ والإجراءات", fontWeight = FontWeight.Bold, color = Color.White) },
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
                    IconButton(onClick = { showDeleteReportDialog = true }) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "حذف البلاغ",
                            tint = Color.White
                        )
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
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.ReportProblem,
                        contentDescription = null,
                        tint = AppColors.Error,
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "لم يتم العثور على هذا البلاغ",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "ربما تم حذف البلاغ أو معالجته مسبقاً",
                        style = MaterialTheme.typography.bodyMedium,
                        color = AppColors.TextSecondary
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(
                        onClick = onNavigateBack,
                        colors = ButtonDefaults.buttonColors(containerColor = AppColors.Primary)
                    ) {
                        Text("العودة للخلف")
                    }
                }
            } else {
                val rep = report!!
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // كرت ملخص البلاغ
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
                                        modifier = Modifier.size(46.dp),
                                        shape = RoundedCornerShape(12.dp),
                                        color = when (rep.priority) {
                                            "critical" -> AppColors.ErrorContainer
                                            "high" -> AppColors.WarningContainer
                                            else -> AppColors.SurfaceVariant
                                        }
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Icon(
                                                imageVector = when (rep.reason) {
                                                    "deceased" -> Icons.Default.Close
                                                    "refuses_to_donate" -> Icons.Default.Block
                                                    "wrong_number", "number_not_working" -> Icons.Default.PhoneDisabled
                                                    else -> Icons.Default.ReportProblem
                                                },
                                                contentDescription = null,
                                                tint = when (rep.priority) {
                                                    "critical" -> AppColors.Error
                                                    "high" -> AppColors.Warning
                                                    else -> AppColors.Primary
                                                },
                                                modifier = Modifier.size(26.dp)
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.width(12.dp))

                                    Column {
                                        Text(
                                            text = rep.reasonText,
                                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = "مستوى الأولوية: ${rep.priorityText}",
                                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                                            color = when (rep.priority) {
                                                "critical" -> AppColors.Error
                                                "high" -> AppColors.Warning
                                                else -> AppColors.TextSecondary
                                            }
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
                                        text = rep.statusText,
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

                            Spacer(modifier = Modifier.height(12.dp))
                            HorizontalDivider(color = AppColors.Divider.copy(alpha = 0.5f))
                            Spacer(modifier = Modifier.height(12.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "تاريخ البلاغ: ${DateUtils.formatIsoToDisplay(rep.createdAt)}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = AppColors.TextSecondary
                                )
                                Text(
                                    text = "الإجراء المقترح: ${rep.suggestedActionText}",
                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                                    color = AppColors.Primary
                                )
                            }
                        }
                    }

                    // كرت بيانات المتبرع المرتبط
                    Text(
                        text = "بيانات المتبرع المبلّغ عنه",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    if (matchedDonor != null) {
                        val donor = matchedDonor!!
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                // رأس المتبرع: الفصيلة والاسم والحالة
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Surface(
                                        modifier = Modifier.size(52.dp),
                                        shape = CircleShape,
                                        color = AppColors.getBloodTypeContainerColor(donor.bloodType)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Text(
                                                text = donor.bloodType,
                                                fontWeight = FontWeight.ExtraBold,
                                                fontSize = 20.sp,
                                                color = AppColors.getBloodTypeColor(donor.bloodType)
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.width(14.dp))

                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = donor.name,
                                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                            Surface(
                                                shape = RoundedCornerShape(4.dp),
                                                color = if (donor.isActive) AppColors.SuccessContainer else AppColors.SurfaceVariant
                                            ) {
                                                Text(
                                                    text = if (donor.isActive) "حساب مفعل" else "حساب معطل",
                                                    fontSize = 12.sp,
                                                    fontWeight = FontWeight.SemiBold,
                                                    color = if (donor.isActive) AppColors.Success else AppColors.TextSecondary,
                                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                                )
                                            }

                                            if (donor.isSuspended) {
                                                Surface(
                                                    shape = RoundedCornerShape(4.dp),
                                                    color = AppColors.WarningContainer
                                                ) {
                                                    Text(
                                                        text = "موقوف مؤقتاً",
                                                        fontSize = 12.sp,
                                                        fontWeight = FontWeight.SemiBold,
                                                        color = AppColors.Warning,
                                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(16.dp))
                                HorizontalDivider(color = AppColors.Divider.copy(alpha = 0.5f))
                                Spacer(modifier = Modifier.height(14.dp))

                                // الموقع
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.LocationOn,
                                        contentDescription = null,
                                        tint = AppColors.Primary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "الموقع: ${donor.displayLocation}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                // أرقام الهواتف التابعة للمتبرع
                                Text(
                                    text = "أرقام هواتف المتبرع:",
                                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                                    color = AppColors.TextSecondary
                                )
                                Spacer(modifier = Modifier.height(8.dp))

                                // الرقم الرئيسي
                                val p1 = donor.phoneNumber
                                if (p1.isNotBlank()) {
                                    PhoneNumberRow(
                                        phone = p1,
                                        label = "الرقم الأساسي",
                                        context = context
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                }

                                // الرقم الثانوي 1
                                val p2 = donor.phoneNumber2
                                if (!p2.isNullOrBlank()) {
                                    PhoneNumberRow(
                                        phone = p2,
                                        label = "رقم هاتف ثانٍ",
                                        context = context
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                }

                                // الرقم الثانوي 2
                                val p3 = donor.phoneNumber3
                                if (!p3.isNullOrBlank()) {
                                    PhoneNumberRow(
                                        phone = p3,
                                        label = "رقم هاتف ثالث",
                                        context = context
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                }

                                Spacer(modifier = Modifier.height(10.dp))
                                HorizontalDivider(color = AppColors.Divider.copy(alpha = 0.5f))
                                Spacer(modifier = Modifier.height(12.dp))

                                // أزرار إدارة المتبرع نفسه
                                Text(
                                    text = "إجراءات مباشرة على سجل المتبرع:",
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                    color = AppColors.TextSecondary
                                )
                                Spacer(modifier = Modifier.height(8.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    // زر تعديل المتبرع
                                    Button(
                                        onClick = { onNavigateToEditDonor(donor.id) },
                                        colors = ButtonDefaults.buttonColors(containerColor = AppColors.Primary),
                                        shape = RoundedCornerShape(10.dp),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("تعديل البيانات", fontSize = 13.sp)
                                    }

                                    // زر تفعيل/تعطيل المتبرع
                                    OutlinedButton(
                                        onClick = {
                                            scope.launch {
                                                donorRepository.toggleDonorStatus(donor.id, !donor.isActive)
                                                val newStatus = if (!donor.isActive) "تفعيل" else "تعطيل"
                                                Toast.makeText(context, "تم $newStatus المتبرع بنجاح", Toast.LENGTH_SHORT).show()
                                                loadData()
                                            }
                                        },
                                        shape = RoundedCornerShape(10.dp),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text(
                                            text = if (donor.isActive) "تعطيل الحساب" else "تفعيل الحساب",
                                            fontSize = 13.sp,
                                            color = if (donor.isActive) AppColors.Error else AppColors.Success
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                // زر حذف المتبرع نهائياً
                                OutlinedButton(
                                    onClick = { showDeleteDonorDialog = true },
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = AppColors.Error),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("حذف المتبرع نهائياً من قاعدة البيانات", fontSize = 13.sp)
                                }
                            }
                        }
                    } else {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = AppColors.SurfaceVariant)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "لم يتم العثور على سجل متبرع مطابق لهذا المعرف في قاعدة البيانات (قد يكون محذوفاً بالفعل).",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = AppColors.TextSecondary
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // قرارات البلاغ
                    Text(
                        text = "القرار بشأن هذا البلاغ",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    if (rep.status == "pending") {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            // قبول البلاغ وتعطيل المتبرع
                            Button(
                                onClick = {
                                    scope.launch {
                                        reportRepository.approveReport(rep.id)
                                        if (matchedDonor != null) {
                                            donorRepository.toggleDonorStatus(matchedDonor!!.id, false)
                                        }
                                        Toast.makeText(context, "تم قبول البلاغ وتعطيل المتبرع بنجاح", Toast.LENGTH_SHORT).show()
                                        onNavigateBack()
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = AppColors.Success),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                            ) {
                                Icon(Icons.Default.Check, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("قبول البلاغ وتعطيل حساب المتبرع", fontWeight = FontWeight.Bold)
                            }

                            // قبول البلاغ فقط
                            OutlinedButton(
                                onClick = {
                                    scope.launch {
                                        reportRepository.approveReport(rep.id)
                                        Toast.makeText(context, "تم قبول البلاغ فقط دون تعطيل المتبرع", Toast.LENGTH_SHORT).show()
                                        onNavigateBack()
                                    }
                                },
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                            ) {
                                Text("قبول البلاغ فقط (مع إبقاء المتبرع مفعل)", fontWeight = FontWeight.SemiBold)
                            }

                            // رفض البلاغ
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
                                    .fillMaxWidth()
                                    .height(48.dp)
                            ) {
                                Icon(Icons.Default.Close, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("رفض هذا البلاغ", fontWeight = FontWeight.Bold)
                            }
                        }
                    } else {
                        // البلاغ معالج مسبقاً: إمكانية إعادة تعيينه أو حذفه
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            OutlinedButton(
                                onClick = {
                                    scope.launch {
                                        reportRepository.resetReportToPending(rep.id)
                                        Toast.makeText(context, "تمت إعادة البلاغ لقيد المراجعة", Toast.LENGTH_SHORT).show()
                                        loadData()
                                    }
                                },
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp)
                            ) {
                                Icon(Icons.Default.LockReset, contentDescription = null)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("إعادة للمراجعة")
                            }

                            Button(
                                onClick = { showDeleteReportDialog = true },
                                colors = ButtonDefaults.buttonColors(containerColor = AppColors.Error),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp)
                            ) {
                                Icon(Icons.Default.Delete, contentDescription = null)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("حذف البلاغ")
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }

    // تأكيد حذف البلاغ
    if (showDeleteReportDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteReportDialog = false },
            title = { Text("حذف البلاغ نهائياً", fontWeight = FontWeight.Bold) },
            text = { Text("هل أنت متأكد من رغبتك في حذف هذا البلاغ من النظام؟") },
            confirmButton = {
                Button(
                    onClick = {
                        scope.launch {
                            reportRepository.deleteReport(reportId)
                            Toast.makeText(context, "تم حذف البلاغ", Toast.LENGTH_SHORT).show()
                            showDeleteReportDialog = false
                            onNavigateBack()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AppColors.Error)
                ) {
                    Text("تأكيد الحذف")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteReportDialog = false }) {
                    Text("إلغاء")
                }
            }
        )
    }

    // تأكيد حذف المتبرع
    if (showDeleteDonorDialog && matchedDonor != null) {
        val donor = matchedDonor!!
        AlertDialog(
            onDismissRequest = { showDeleteDonorDialog = false },
            title = { Text("حذف سجل المتبرع نهائياً", fontWeight = FontWeight.Bold, color = AppColors.Error) },
            text = {
                Text("هل أنت متأكد من حذف المتبرع (${donor.name}) نهائياً من قاعدة البيانات؟ لا يمكن التراجع عن هذا الإجراء.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        scope.launch {
                            donorRepository.deleteDonor(donor.id)
                            Toast.makeText(context, "تم حذف سجل المتبرع نهائياً", Toast.LENGTH_SHORT).show()
                            showDeleteDonorDialog = false
                            loadData()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AppColors.Error)
                ) {
                    Text("حذف المتبرع")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDonorDialog = false }) {
                    Text("إلغاء")
                }
            }
        )
    }
}

@Composable
private fun PhoneNumberRow(
    phone: String,
    label: String,
    context: Context
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = AppColors.SurfaceVariant,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall,
                    color = AppColors.TextSecondary
                )
                Text(
                    text = PhoneUtils.formatDisplayPhone(phone),
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                // نسخ الرقم
                IconButton(
                    onClick = {
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        val clip = ClipData.newPlainText("Phone Number", phone)
                        clipboard.setPrimaryClip(clip)
                        Toast.makeText(context, "تم نسخ الرقم $phone", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = "نسخ",
                        tint = AppColors.TextSecondary,
                        modifier = Modifier.size(18.dp)
                    )
                }

                // واتساب
                IconButton(
                    onClick = { IntentUtils.openWhatsApp(context, phone) },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Chat,
                        contentDescription = "واتساب",
                        tint = AppColors.Success,
                        modifier = Modifier.size(18.dp)
                    )
                }

                // اتصال
                IconButton(
                    onClick = { IntentUtils.makePhoneCall(context, phone) },
                    modifier = Modifier.size(36.dp)
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
}
