package com.bagomri.yemenbloodbank.ui.screens.reports

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Note
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.RadioButtonChecked
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Report
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bagomri.yemenbloodbank.core.constants.AppColors
import com.bagomri.yemenbloodbank.core.constants.AppStrings
import com.bagomri.yemenbloodbank.ui.components.CustomTextField
import com.bagomri.yemenbloodbank.ui.components.LoadingIndicator

private val REPORT_REASONS = listOf(
    "number_not_working" to "الرقم مفصول أو مغلق",
    "no_response" to "لا يتم الرد على الاتصال",
    "wrong_number" to "الرقم غير صحيح أو لشخص آخر",
    "not_donor" to "الشخص يرفض التبرع أو لم يعد متبرعاً",
    "other" to "سبب آخر"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportDonorScreen(
    donorId: String?,
    donorPhone: String?,
    onNavigateBack: () -> Unit,
    viewModel: ReportDonorViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showSuccessDialog by remember { mutableStateOf(false) }

    LaunchedEffect(donorId, donorPhone) {
        viewModel.setInitialData(donorId, donorPhone)
    }

    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            showSuccessDialog = true
        }
    }

    if (showSuccessDialog) {
        AlertDialog(
            onDismissRequest = {},
            icon = {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = AppColors.Success,
                    modifier = Modifier.size(48.dp)
                )
            },
            title = {
                Text(
                    text = "تم استلام بلاغك بنجاح",
                    fontWeight = FontWeight.Bold,
                    color = AppColors.TextPrimary
                )
            },
            text = {
                Text(
                    text = "شكراً لمساهمتك في الحفاظ على صحة ودقة بيانات المتبرعين لخدمة المحتاجين والمرضى.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = AppColors.TextSecondary
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showSuccessDialog = false
                        onNavigateBack()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AppColors.Primary)
                ) {
                    Text("حسناً", color = Color.White)
                }
            }
        )
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = AppStrings.reportTitle,
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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
        ) {
            if (uiState.isLoading) {
                LoadingIndicator(message = "جاري إرسال البلاغ...")
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // بطاقة تنبيه وتوضيح
                    Card(
                        colors = CardDefaults.cardColors(containerColor = AppColors.WarningContainer),
                        shape = RoundedCornerShape(14.dp),
                        border = BorderStroke(1.dp, AppColors.Warning.copy(alpha = 0.3f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = null,
                                tint = AppColors.Warning,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "يساعدنا إبلاغك عن الأرقام غير الصحيحة أو التي لا ترد في الحفاظ على تحديث البيانات ومساعدة المرضى في الطوارئ.",
                                style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 20.sp),
                                color = AppColors.Warning
                            )
                        }
                    }

                    // رقم الهاتف المبلغ عنه
                    CustomTextField(
                        value = uiState.phoneNumber,
                        onValueChange = { viewModel.onPhoneNumberChange(it) },
                        label = AppStrings.phoneNumber,
                        placeholder = "777123456",
                        enabled = donorId == null,
                        leadingIcon = {
                            Icon(Icons.Default.Phone, contentDescription = null, tint = AppColors.Primary)
                        }
                    )

                    // اختيار سبب البلاغ
                    Text(
                        text = "اختر سبب البلاغ:",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = AppColors.TextPrimary
                    )

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        REPORT_REASONS.forEach { (key, reasonTitle) ->
                            val isSelected = uiState.selectedReason == key

                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { viewModel.onReasonChange(key) },
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isSelected) AppColors.PrimaryContainer else MaterialTheme.colorScheme.surface
                                ),
                                border = BorderStroke(
                                    1.dp,
                                    if (isSelected) AppColors.Primary else AppColors.Border
                                )
                            ) {
                                Row(
                                    modifier = Modifier.padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = if (isSelected) Icons.Default.RadioButtonChecked else Icons.Default.RadioButtonUnchecked,
                                        contentDescription = null,
                                        tint = if (isSelected) AppColors.Primary else AppColors.TextSecondary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text(
                                        text = reasonTitle,
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                        ),
                                        color = if (isSelected) AppColors.Primary else AppColors.TextPrimary
                                    )
                                }
                            }
                        }
                    }

                    // ملاحظات إضافية
                    CustomTextField(
                        value = uiState.notes,
                        onValueChange = { viewModel.onNotesChange(it) },
                        label = "${AppStrings.notes} (${AppStrings.optional})",
                        placeholder = "أدخل أي تفاصيل أخرى تساعد في التحقق من البلاغ...",
                        singleLine = false,
                        maxLines = 3,
                        leadingIcon = {
                            Icon(Icons.AutoMirrored.Filled.Note, contentDescription = null, tint = AppColors.Primary)
                        }
                    )

                    // رسالة الخطأ
                    if (!uiState.errorMessage.isNullOrEmpty()) {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = AppColors.ErrorContainer),
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(1.dp, AppColors.Error.copy(alpha = 0.3f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ErrorOutline,
                                    contentDescription = null,
                                    tint = AppColors.Error
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = uiState.errorMessage!!,
                                    color = AppColors.Error,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // زر الإرسال
                    Button(
                        onClick = { viewModel.submitReport() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = AppColors.Primary),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Report,
                            contentDescription = null,
                            tint = Color.White
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = AppStrings.submit,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}
