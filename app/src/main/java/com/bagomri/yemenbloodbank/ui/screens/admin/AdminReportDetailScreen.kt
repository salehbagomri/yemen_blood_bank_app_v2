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
import androidx.compose.material.icons.automirrored.filled.Notes
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.LockReset
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.PhoneDisabled
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.ReportProblem
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Update
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
    var showSuspendDialog by remember { mutableStateOf(false) }
    var showUpdateDonationDialog by remember { mutableStateOf(false) }

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
                val donor = matchedDonor

                // استخراج الملاحظات سواء من البلاغ أو من سجل المتبرع
                val displayNotes = when {
                    !rep.notes.isNullOrBlank() -> rep.notes
                    donor != null && !donor.notes.isNullOrBlank() -> donor.notes
                    else -> null
                }

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // ==========================================
                    // 1. درجة وأهمية البلاغ (Priority & Impact)
                    // ==========================================
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Surface(
                                        modifier = Modifier.size(42.dp),
                                        shape = RoundedCornerShape(10.dp),
                                        color = when (rep.priority) {
                                            "critical" -> AppColors.ErrorContainer
                                            "high" -> AppColors.WarningContainer
                                            else -> AppColors.SecondaryContainer
                                        }
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Icon(
                                                imageVector = Icons.Default.Security,
                                                contentDescription = null,
                                                tint = when (rep.priority) {
                                                    "critical" -> AppColors.Error
                                                    "high" -> AppColors.Warning
                                                    else -> AppColors.Primary
                                                },
                                                modifier = Modifier.size(24.dp)
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(
                                            text = "درجة وأهمية البلاغ",
                                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = "المستوى: ${rep.priorityText}",
                                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                                            color = when (rep.priority) {
                                                "critical" -> AppColors.Error
                                                "high" -> AppColors.Warning
                                                else -> AppColors.Primary
                                            }
                                        )
                                    }
                                }

                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = when (rep.priority) {
                                        "critical" -> AppColors.Error
                                        "high" -> AppColors.Warning
                                        else -> AppColors.SecondaryContainer
                                    }
                                ) {
                                    Text(
                                        text = rep.priorityText,
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        color = when (rep.priority) {
                                            "critical", "high" -> Color.White
                                            else -> AppColors.Primary
                                        },
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))
                            HorizontalDivider(color = AppColors.Divider.copy(alpha = 0.6f))
                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                text = when (rep.priority) {
                                    "critical" -> "بلاغ عاجل جداً (حرج): يؤثر فورياً على أرواح المرضى المحتاجين ويجب اتخاذ إجراء سريع بتعديل بيانات المتبرع أو استبعاده من الظهور لطالبي الدم."
                                    "high" -> "بلاغ عالي الأهمية: الرقم لا يمكن التواصل معه حالياً، يلزم مراجعة الأرقام البديلة وتحديثها لضمان سرعة الوصول."
                                    "medium" -> "بلاغ متوسط: المتبرع انتقل أو يعاني من مانع تبرع مؤقت، يفضل مراجعة بياناته وتحديثها."
                                    else -> "بلاغ عادي: الرقم مشغول أو لا يرد حالياً، قد يكون ذلك ظرفاً عارضاً."
                                },
                                style = MaterialTheme.typography.bodySmall,
                                color = AppColors.TextSecondary,
                                lineHeight = 20.sp
                            )
                        }
                    }

                    // ==========================================
                    // 2. معلومات وتفاصيل البلاغ (Report Info)
                    // ==========================================
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Surface(
                                        modifier = Modifier.size(42.dp),
                                        shape = RoundedCornerShape(10.dp),
                                        color = AppColors.PrimaryContainer
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Icon(
                                                imageVector = Icons.Default.Info,
                                                contentDescription = null,
                                                tint = AppColors.Primary,
                                                modifier = Modifier.size(24.dp)
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(
                                            text = "معلومات وتفاصيل البلاغ",
                                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = "سبب وتاريخ الإبلاغ",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = AppColors.TextSecondary
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

                            Spacer(modifier = Modifier.height(14.dp))
                            HorizontalDivider(color = AppColors.Divider.copy(alpha = 0.6f))
                            Spacer(modifier = Modifier.height(12.dp))

                            // سبب البلاغ
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = when (rep.reason) {
                                        "deceased" -> Icons.Default.Close
                                        "refuses_to_donate" -> Icons.Default.Block
                                        "wrong_number", "number_not_working" -> Icons.Default.PhoneDisabled
                                        else -> Icons.Default.Warning
                                    },
                                    contentDescription = null,
                                    tint = AppColors.Primary,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "السبب: ${rep.reasonText}",
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // تاريخ البلاغ
                            Text(
                                text = "تاريخ الإرسال: ${DateUtils.formatIsoToDisplay(rep.createdAt)}",
                                style = MaterialTheme.typography.bodySmall,
                                color = AppColors.TextSecondary
                            )

                            // عرض الملاحظات المرفقة مع البلاغ
                            Spacer(modifier = Modifier.height(12.dp))
                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp),
                                color = if (displayNotes != null) AppColors.SurfaceVariant else AppColors.SurfaceVariant.copy(alpha = 0.5f)
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.Notes,
                                        contentDescription = null,
                                        tint = if (displayNotes != null) AppColors.Primary else AppColors.TextSecondary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = "الملاحظات المرفقة مع البلاغ:",
                                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                            color = AppColors.TextPrimary
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = displayNotes ?: "لا توجد ملاحظات إضافية مرفقة مع هذا البلاغ",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = if (displayNotes != null) AppColors.TextPrimary else AppColors.TextSecondary,
                                            lineHeight = 18.sp
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // ==========================================
                    // 3. بيانات المتبرع للرقم المبلغ عنه
                    // ==========================================
                    Card(
                        modifier = Modifier.fillMaxWidth(),
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
                                    modifier = Modifier.size(42.dp),
                                    shape = RoundedCornerShape(10.dp),
                                    color = AppColors.PrimaryContainer
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = Icons.Default.Person,
                                            contentDescription = null,
                                            tint = AppColors.Primary,
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = "بيانات المتبرع للرقم المبلّغ عنه",
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = donor?.name ?: "متبرع غير متوفر بقاعدة البيانات",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = AppColors.TextSecondary
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))
                            HorizontalDivider(color = AppColors.Divider.copy(alpha = 0.6f))
                            Spacer(modifier = Modifier.height(12.dp))

                            if (donor != null) {
                                // رأس بيانات المتبرع: الفصيلة والحالة
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Surface(
                                            modifier = Modifier.size(48.dp),
                                            shape = CircleShape,
                                            color = AppColors.getBloodTypeContainerColor(donor.bloodType)
                                        ) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Text(
                                                    text = donor.bloodType,
                                                    fontWeight = FontWeight.ExtraBold,
                                                    fontSize = 18.sp,
                                                    color = AppColors.getBloodTypeColor(donor.bloodType)
                                                )
                                            }
                                        }
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Column {
                                            Text(
                                                text = donor.name,
                                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                            )
                                            Text(
                                                text = "الموقع: ${donor.displayLocation}",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = AppColors.TextSecondary
                                            )
                                        }
                                    }

                                    // شارة حالة المتبرع
                                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                        Surface(
                                            shape = RoundedCornerShape(6.dp),
                                            color = if (donor.isActive) AppColors.SuccessContainer else AppColors.ErrorContainer
                                        ) {
                                            Text(
                                                text = if (donor.isActive) "حساب مفعل" else "حساب معطل",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (donor.isActive) AppColors.Success else AppColors.Error,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                            )
                                        }

                                        if (donor.isSuspended) {
                                            Surface(
                                                shape = RoundedCornerShape(6.dp),
                                                color = AppColors.WarningContainer
                                            ) {
                                                Text(
                                                    text = "موقوف",
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = AppColors.Warning,
                                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                                )
                                            }
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(14.dp))

                                // أرقام هواتف المتبرع
                                Text(
                                    text = "أرقام هواتف المتبرع:",
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                    color = AppColors.TextSecondary
                                )
                                Spacer(modifier = Modifier.height(6.dp))

                                // الرقم الأساسي
                                val p1 = donor.phoneNumber
                                if (p1.isNotBlank()) {
                                    PhoneNumberRow(phone = p1, label = "الرقم الأساسي", context = context)
                                    Spacer(modifier = Modifier.height(6.dp))
                                }

                                // الرقم الثاني
                                val p2 = donor.phoneNumber2
                                if (!p2.isNullOrBlank()) {
                                    PhoneNumberRow(phone = p2, label = "رقم ثانٍ", context = context)
                                    Spacer(modifier = Modifier.height(6.dp))
                                }

                                // الرقم الثالث
                                val p3 = donor.phoneNumber3
                                if (!p3.isNullOrBlank()) {
                                    PhoneNumberRow(phone = p3, label = "رقم ثالث", context = context)
                                    Spacer(modifier = Modifier.height(6.dp))
                                }

                                // تاريخ آخر تبرع
                                if (!donor.lastDonationDate.isNullOrBlank()) {
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.DateRange, contentDescription = null, tint = AppColors.TextSecondary, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "آخر تبرع: ${DateUtils.formatDate(donor.lastDonationDate)}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = AppColors.TextSecondary
                                        )
                                    }
                                }
                            } else {
                                Text(
                                    text = "لم يتم العثور على سجل متبرع مطابق في قاعدة البيانات (قد يكون محذوفاً بالفعل).",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = AppColors.TextSecondary
                                )
                            }
                        }
                    }

                    // ==========================================
                    // 4. اتخاذ القرار والإجراءات الشاملة
                    // ==========================================
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    modifier = Modifier.size(42.dp),
                                    shape = RoundedCornerShape(10.dp),
                                    color = AppColors.PrimaryContainer
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = Icons.Default.Edit,
                                            contentDescription = null,
                                            tint = AppColors.Primary,
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = "اتخاذ القرار والإجراء",
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "القرارات المباشرة وإدارة المتبرع",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = AppColors.TextSecondary
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))
                            HorizontalDivider(color = AppColors.Divider.copy(alpha = 0.6f))
                            Spacer(modifier = Modifier.height(14.dp))

                            // 1. أزرار معالجة البلاغ (قرارات البلاغ)
                            Text(
                                text = "أولاً: قرار البلاغ",
                                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                                color = AppColors.Primary
                            )
                            Spacer(modifier = Modifier.height(10.dp))

                            if (rep.status == "pending") {
                                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                    // الإجراء 1: قبول وتعديل البيانات (يفتح شاشة تعديل المتبرع)
                                    Button(
                                        onClick = {
                                            scope.launch {
                                                reportRepository.approveReport(rep.id)
                                                Toast.makeText(context, "تم قبول البلاغ، جاري فتح تعديل بيانات المتبرع...", Toast.LENGTH_SHORT).show()
                                                if (donor != null) {
                                                    onNavigateToEditDonor(donor.id)
                                                } else {
                                                    onNavigateBack()
                                                }
                                            }
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = AppColors.Success),
                                        shape = RoundedCornerShape(12.dp),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(48.dp)
                                    ) {
                                        Icon(Icons.Default.Edit, contentDescription = null)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("قبول وتعديل البيانات", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                    }

                                    // الإجراء 2: قبول فقط (بدون إجراء)
                                    OutlinedButton(
                                        onClick = {
                                            scope.launch {
                                                reportRepository.approveReport(rep.id)
                                                Toast.makeText(context, "تم قبول البلاغ بنجاح", Toast.LENGTH_SHORT).show()
                                                onNavigateBack()
                                            }
                                        },
                                        shape = RoundedCornerShape(12.dp),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(48.dp)
                                    ) {
                                        Icon(Icons.Default.Check, contentDescription = null, tint = AppColors.Primary)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("قبول فقط (بدون إجراء)", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    }

                                    // الإجراء 3: رفض البلاغ
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
                                        Text("رفض البلاغ", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                    }
                                }
                            } else {
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
                                            .height(46.dp)
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
                                            .height(46.dp)
                                    ) {
                                        Icon(Icons.Default.Delete, contentDescription = null)
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("حذف البلاغ")
                                    }
                                }
                            }

                            // 2. بقية الإجراءات المباشرة على المتبرع
                            if (donor != null) {
                                Spacer(modifier = Modifier.height(18.dp))
                                HorizontalDivider(color = AppColors.Divider.copy(alpha = 0.6f))
                                Spacer(modifier = Modifier.height(14.dp))

                                Text(
                                    text = "ثانياً: إجراءات مباشرة على المتبرع",
                                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                                    color = AppColors.Primary
                                )
                                Spacer(modifier = Modifier.height(10.dp))

                                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        // زر إيقاف 6 أشهر / إلغاء الإيقاف
                                        Button(
                                            onClick = {
                                                if (donor.isSuspended) {
                                                    scope.launch {
                                                        donorRepository.cancelDonorSuspension(donor.id)
                                                        Toast.makeText(context, "تم إلغاء إيقاف المتبرع", Toast.LENGTH_SHORT).show()
                                                        loadData()
                                                    }
                                                } else {
                                                    showSuspendDialog = true
                                                }
                                            },
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = if (donor.isSuspended) AppColors.Success else AppColors.Warning
                                            ),
                                            shape = RoundedCornerShape(10.dp),
                                            modifier = Modifier
                                                .weight(1f)
                                                .height(44.dp)
                                        ) {
                                            Icon(
                                                imageVector = if (donor.isSuspended) Icons.Default.PlayArrow else Icons.Default.Pause,
                                                contentDescription = null,
                                                modifier = Modifier.size(16.dp)
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = if (donor.isSuspended) "إلغاء الإيقاف" else "إيقاف 6 أشهر",
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }

                                        // زر تحديث آخر تبرع
                                        Button(
                                            onClick = { showUpdateDonationDialog = true },
                                            colors = ButtonDefaults.buttonColors(containerColor = AppColors.Info),
                                            shape = RoundedCornerShape(10.dp),
                                            modifier = Modifier
                                                .weight(1f)
                                                .height(44.dp)
                                        ) {
                                            Icon(Icons.Default.Update, contentDescription = null, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text("تحديث آخر تبرع", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        // زر تعطيل / تفعيل الحساب
                                        OutlinedButton(
                                            onClick = {
                                                scope.launch {
                                                    val newActive = !donor.isActive
                                                    donorRepository.toggleDonorStatus(donor.id, newActive)
                                                    val msg = if (newActive) "تم تفعيل حساب المتبرع" else "تم تعطيل حساب المتبرع"
                                                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                                                    loadData()
                                                }
                                            },
                                            shape = RoundedCornerShape(10.dp),
                                            modifier = Modifier
                                                .weight(1f)
                                                .height(44.dp)
                                        ) {
                                            Icon(
                                                imageVector = if (donor.isActive) Icons.Default.Block else Icons.Default.CheckCircle,
                                                contentDescription = null,
                                                tint = if (donor.isActive) AppColors.Error else AppColors.Success,
                                                modifier = Modifier.size(16.dp)
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = if (donor.isActive) "تعطيل الحساب" else "تفعيل الحساب",
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (donor.isActive) AppColors.Error else AppColors.Success
                                            )
                                        }

                                        // زر تعديل المتبرع في أي وقت
                                        OutlinedButton(
                                            onClick = { onNavigateToEditDonor(donor.id) },
                                            shape = RoundedCornerShape(10.dp),
                                            modifier = Modifier
                                                .weight(1f)
                                                .height(44.dp)
                                        ) {
                                            Icon(Icons.Default.Edit, contentDescription = null, tint = AppColors.Primary, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text("تعديل البيانات", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = AppColors.Primary)
                                        }
                                    }

                                    // زر الحذف النهائي للمتبرع
                                    Button(
                                        onClick = { showDeleteDonorDialog = true },
                                        colors = ButtonDefaults.buttonColors(containerColor = AppColors.Error),
                                        shape = RoundedCornerShape(10.dp),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(44.dp)
                                    ) {
                                        Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("حذف المتبرع نهائياً من قاعدة البيانات", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }

    // تأكيد إيقاف 6 أشهر
    if (showSuspendDialog && matchedDonor != null) {
        val d = matchedDonor!!
        AlertDialog(
            onDismissRequest = { showSuspendDialog = false },
            title = { Text("تأكيد إيقاف المتبرع", fontWeight = FontWeight.Bold) },
            text = { Text("هل تريد إيقاف المتبرع (${d.name}) لمدة 6 أشهر؟ سيتم تعليق ظهوره لطالبي الدم مؤقتاً.") },
            confirmButton = {
                Button(
                    onClick = {
                        scope.launch {
                            donorRepository.suspendDonorFor6Months(d.id)
                            Toast.makeText(context, "تم إيقاف المتبرع لمدة 6 أشهر", Toast.LENGTH_SHORT).show()
                            showSuspendDialog = false
                            loadData()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AppColors.Warning)
                ) {
                    Text("إيقاف 6 أشهر")
                }
            },
            dismissButton = {
                TextButton(onClick = { showSuspendDialog = false }) { Text("إلغاء") }
            }
        )
    }

    // تأكيد تحديث آخر تبرع
    if (showUpdateDonationDialog && matchedDonor != null) {
        val d = matchedDonor!!
        AlertDialog(
            onDismissRequest = { showUpdateDonationDialog = false },
            title = { Text("تحديث تاريخ آخر تبرع", fontWeight = FontWeight.Bold) },
            text = { Text("سيتم تسجيل تبرع جديد للمتبرع (${d.name}) بتاريخ اليوم وإيقافه تلقائياً لمدة 6 أشهر حتى يسترد عافيته. هل تريد المتابعة؟") },
            confirmButton = {
                Button(
                    onClick = {
                        scope.launch {
                            donorRepository.updateDonationDate(d.id)
                            Toast.makeText(context, "تم تحديث تاريخ التبرع بنجاح", Toast.LENGTH_SHORT).show()
                            showUpdateDonationDialog = false
                            loadData()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AppColors.Info)
                ) {
                    Text("تأكيد التحديث")
                }
            },
            dismissButton = {
                TextButton(onClick = { showUpdateDonationDialog = false }) { Text("إلغاء") }
            }
        )
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

    // تأكيد حذف المتبرع نهائياً
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
