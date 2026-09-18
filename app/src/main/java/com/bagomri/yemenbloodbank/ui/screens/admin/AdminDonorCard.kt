package com.bagomri.yemenbloodbank.ui.screens.admin

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Notes
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Cake
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EventBusy
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Notes
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Report
import androidx.compose.material.icons.filled.Update
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bagomri.yemenbloodbank.core.constants.AppColors
import com.bagomri.yemenbloodbank.core.constants.AppStrings
import com.bagomri.yemenbloodbank.core.util.DateUtils
import com.bagomri.yemenbloodbank.core.util.IntentUtils
import com.bagomri.yemenbloodbank.data.model.Donor

/**
 * بطاقة متبرع مخصصة للوحة تحكم الأدمن (مطابقة لنسخة فلاتر الأصلية بكامل الصلاحيات)
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AdminDonorCard(
    donor: Donor,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onSuspend: () -> Unit,
    onCancelSuspension: () -> Unit,
    onUpdateDonationDate: () -> Unit,
    onToggleActive: () -> Unit,
    onViewReports: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var isExpanded by remember { mutableStateOf(false) }
    val rotationAngle by animateFloatAsState(targetValue = if (isExpanded) 180f else 0f, label = "arrow_rotation")

    // تحديد لون الإطار الخارجي بناءً على الحالة
    val borderColor = when {
        !donor.isActive -> AppColors.Error.copy(alpha = 0.4f)
        donor.isSuspended -> AppColors.Warning.copy(alpha = 0.4f)
        else -> AppColors.Success.copy(alpha = 0.3f)
    }

    val bloodColor = AppColors.getBloodTypeColor(donor.bloodType)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 6.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isExpanded) 4.dp else 2.dp),
        border = BorderStroke(1.5.dp, borderColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // الصف العلوي (الرأس) - قابل للضغط للتوسيع أو الطي
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { isExpanded = !isExpanded },
                verticalAlignment = Alignment.CenterVertically
            ) {
                // شارة فصيلة الدم
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .background(
                            color = bloodColor.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(12.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = donor.bloodType,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp,
                            color = bloodColor
                        )
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                // معلومات الاسم ورقم الهاتف
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = donor.name,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = AppColors.TextPrimary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f, fill = false)
                        )

                        Icon(
                            imageVector = Icons.Default.ExpandMore,
                            contentDescription = if (isExpanded) "طي" else "توسيع",
                            tint = AppColors.TextSecondary,
                            modifier = Modifier
                                .size(22.dp)
                                .rotate(rotationAngle)
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Phone,
                            contentDescription = null,
                            tint = AppColors.TextSecondary,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = donor.phoneNumber,
                            style = MaterialTheme.typography.bodySmall,
                            color = AppColors.TextSecondary
                        )
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                // شارة الحالة (معطل > موقوف > متاح)
                when {
                    !donor.isActive -> {
                        AdminStatusBadge(
                            text = "معطل",
                            icon = Icons.Default.Block,
                            color = AppColors.Error
                        )
                    }
                    donor.isSuspended -> {
                        AdminStatusBadge(
                            text = "موقوف",
                            icon = Icons.Default.Pause,
                            color = AppColors.Warning
                        )
                    }
                    else -> {
                        AdminStatusBadge(
                            text = "متاح",
                            icon = Icons.Default.CheckCircle,
                            color = AppColors.Success
                        )
                    }
                }
            }

            // التفاصيل الموسعة
            AnimatedVisibility(visible = isExpanded) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Spacer(modifier = Modifier.height(14.dp))
                    HorizontalDivider(color = AppColors.Divider, thickness = 1.dp)
                    Spacer(modifier = Modifier.height(14.dp))

                    // صفوف التواصل السريع لجميع الأرقام
                    Text(
                        text = "أرقام التواصل:",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = AppColors.TextPrimary
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    AdminPhoneContactRow(
                        label = "الهاتف الأساسي",
                        phoneNumber = donor.phoneNumber,
                        onCall = { IntentUtils.dialPhoneNumber(context, donor.phoneNumber) },
                        onWhatsApp = { IntentUtils.openWhatsApp(context, donor.phoneNumber) }
                    )

                    if (!donor.phoneNumber2.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(6.dp))
                        AdminPhoneContactRow(
                            label = "رقم إضافي 2",
                            phoneNumber = donor.phoneNumber2,
                            onCall = { IntentUtils.dialPhoneNumber(context, donor.phoneNumber2) },
                            onWhatsApp = { IntentUtils.openWhatsApp(context, donor.phoneNumber2) }
                        )
                    }

                    if (!donor.phoneNumber3.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(6.dp))
                        AdminPhoneContactRow(
                            label = "رقم إضافي 3",
                            phoneNumber = donor.phoneNumber3,
                            onCall = { IntentUtils.dialPhoneNumber(context, donor.phoneNumber3) },
                            onWhatsApp = { IntentUtils.openWhatsApp(context, donor.phoneNumber3) }
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    HorizontalDivider(color = AppColors.Divider, thickness = 1.dp)
                    Spacer(modifier = Modifier.height(12.dp))

                    // باقي التفاصيل
                    AdminDetailRow(icon = Icons.Default.LocationOn, label = "المديرية", value = donor.district)
                    Spacer(modifier = Modifier.height(6.dp))
                    AdminDetailRow(
                        icon = Icons.Default.Person,
                        label = "الجنس",
                        value = if (donor.gender == "female") "أنثى" else "ذكر"
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    AdminDetailRow(icon = Icons.Default.Cake, label = "العمر", value = "${donor.age} سنة")

                    if (!donor.lastDonationDate.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(6.dp))
                        AdminDetailRow(
                            icon = Icons.Default.DateRange,
                            label = "آخر تبرع",
                            value = DateUtils.formatDate(donor.lastDonationDate)
                        )
                    }

                    if (donor.isSuspended && !donor.suspendedUntil.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(6.dp))
                        val daysLeft = donor.daysUntilCanDonate
                        val daysStr = if (daysLeft != null && daysLeft > 0) " (متبقي $daysLeft يوم)" else ""
                        AdminDetailRow(
                            icon = Icons.Default.EventBusy,
                            label = "موقوف حتى",
                            value = "${DateUtils.formatDate(donor.suspendedUntil)}$daysStr",
                            valueColor = AppColors.Warning
                        )
                    }

                    if (!donor.notes.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = AppColors.SurfaceVariant,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.Notes,
                                    contentDescription = null,
                                    tint = AppColors.TextSecondary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = donor.notes,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = AppColors.TextPrimary
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    HorizontalDivider(color = AppColors.Divider, thickness = 1.dp)
                    Spacer(modifier = Modifier.height(14.dp))

                    // قسم إجراءات الأدمن
                    Text(
                        text = "إجراءات الأدمن",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = AppColors.Primary
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // إيقاف 6 أشهر أو إلغاء الإيقاف
                        if (!donor.isSuspended) {
                            AdminActionChip(
                                label = "إيقاف 6 أشهر",
                                icon = Icons.Default.Pause,
                                color = AppColors.Warning,
                                onClick = onSuspend
                            )
                        } else {
                            AdminActionChip(
                                label = "إلغاء الإيقاف",
                                icon = Icons.Default.PlayArrow,
                                color = AppColors.Success,
                                onClick = onCancelSuspension
                            )
                        }

                        // تحديث آخر تبرع
                        AdminActionChip(
                            label = "تحديث آخر تبرع",
                            icon = Icons.Default.Update,
                            color = AppColors.Info,
                            onClick = onUpdateDonationDate
                        )

                        // تعديل البيانات
                        AdminActionChip(
                            label = "تعديل البيانات",
                            icon = Icons.Default.Edit,
                            color = AppColors.Primary,
                            onClick = onEdit
                        )

                        // تعطيل / تفعيل الحساب
                        if (donor.isActive) {
                            AdminActionChip(
                                label = "تعطيل الحساب",
                                icon = Icons.Default.Block,
                                color = AppColors.Error,
                                onClick = onToggleActive
                            )
                        } else {
                            AdminActionChip(
                                label = "تفعيل الحساب",
                                icon = Icons.Default.CheckCircle,
                                color = AppColors.Success,
                                onClick = onToggleActive
                            )
                        }

                        // البلاغات
                        AdminActionChip(
                            label = "البلاغات",
                            icon = Icons.Default.Report,
                            color = AppColors.Warning,
                            onClick = onViewReports
                        )

                        // حذف نهائي
                        AdminActionChip(
                            label = "حذف نهائي",
                            icon = Icons.Default.Delete,
                            color = AppColors.Error,
                            onClick = onDelete
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // زر نسخ جميع البيانات
                    OutlinedButton(
                        onClick = {
                            val statusStr = when {
                                !donor.isActive -> "معطل"
                                donor.isSuspended -> "موقوف"
                                else -> "متاح"
                            }
                            val clipboardData = buildString {
                                appendLine("الاسم: ${donor.name}")
                                appendLine("فصيلة الدم: ${donor.bloodType}")
                                appendLine("الهاتف 1: ${donor.phoneNumber}")
                                if (!donor.phoneNumber2.isNullOrBlank()) appendLine("الهاتف 2: ${donor.phoneNumber2}")
                                if (!donor.phoneNumber3.isNullOrBlank()) appendLine("الهاتف 3: ${donor.phoneNumber3}")
                                appendLine("المديرية: ${donor.district}")
                                appendLine("الجنس: ${if (donor.gender == "female") "أنثى" else "ذكر"}")
                                appendLine("العمر: ${donor.age} سنة")
                                appendLine("الحالة: $statusStr")
                                if (!donor.lastDonationDate.isNullOrBlank()) appendLine("آخر تبرع: ${DateUtils.formatDate(donor.lastDonationDate)}")
                                if (!donor.suspendedUntil.isNullOrBlank()) appendLine("موقوف حتى: ${DateUtils.formatDate(donor.suspendedUntil)}")
                                if (!donor.notes.isNullOrBlank()) appendLine("ملاحظات: ${donor.notes}")
                                if (!donor.createdAt.isNullOrBlank()) appendLine("تاريخ الإضافة: ${DateUtils.formatDate(donor.createdAt)}")
                            }

                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            val clip = ClipData.newPlainText("بيانات المتبرع", clipboardData)
                            clipboard.setPrimaryClip(clip)
                            Toast.makeText(context, "تم نسخ جميع البيانات", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, AppColors.Info.copy(alpha = 0.5f))
                    ) {
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = null,
                            tint = AppColors.Info,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "نسخ جميع البيانات",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                            color = AppColors.Info
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AdminStatusBadge(
    text: String,
    icon: ImageVector,
    color: Color
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = color.copy(alpha = 0.1f),
        border = BorderStroke(1.dp, color.copy(alpha = 0.4f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(13.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = color,
                    fontSize = 11.sp
                )
            )
        }
    }
}

@Composable
private fun AdminPhoneContactRow(
    label: String,
    phoneNumber: String,
    onCall: () -> Unit,
    onWhatsApp: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = AppColors.SurfaceVariant,
        border = BorderStroke(1.dp, AppColors.Border),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall,
                    color = AppColors.TextSecondary,
                    fontSize = 11.sp
                )
                Text(
                    text = phoneNumber,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    color = AppColors.TextPrimary
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                // زر اتصال
                Button(
                    onClick = onCall,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AppColors.Success),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp),
                    modifier = Modifier.height(34.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Call,
                        contentDescription = AppStrings.call,
                        tint = Color.White,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "اتصال",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 11.sp
                        )
                    )
                }

                // زر واتساب
                Button(
                    onClick = onWhatsApp,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366)),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp),
                    modifier = Modifier.height(34.dp)
                ) {
                    Text(text = "💬", fontSize = 13.sp)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "واتساب",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 11.sp
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun AdminDetailRow(
    icon: ImageVector,
    label: String,
    value: String,
    valueColor: Color = AppColors.TextPrimary
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = AppColors.TextSecondary,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "$label: ",
            style = MaterialTheme.typography.bodySmall,
            color = AppColors.TextSecondary
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
            color = valueColor
        )
    }
}

@Composable
private fun AdminActionChip(
    label: String,
    icon: ImageVector,
    color: Color,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        color = color.copy(alpha = 0.1f),
        border = BorderStroke(1.dp, color.copy(alpha = 0.35f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(15.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = color,
                    fontSize = 12.sp
                )
            )
        }
    }
}
