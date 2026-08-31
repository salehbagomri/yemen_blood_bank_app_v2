package com.bagomri.yemenbloodbank.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Report
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bagomri.yemenbloodbank.core.constants.AppColors
import com.bagomri.yemenbloodbank.core.constants.AppStrings
import com.bagomri.yemenbloodbank.core.util.DateUtils
import com.bagomri.yemenbloodbank.core.util.IntentUtils
import com.bagomri.yemenbloodbank.data.model.Donor

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DonorCard(
    donor: Donor,
    modifier: Modifier = Modifier,
    showAdminActions: Boolean = false,
    showHospitalActions: Boolean = false,
    onEdit: (() -> Unit)? = null,
    onDelete: (() -> Unit)? = null,
    onSuspend: (() -> Unit)? = null,
    onUpdateDonationDate: (() -> Unit)? = null,
    onReport: (() -> Unit)? = null
) {
    val context = LocalContext.current
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp)
            .clickable { expanded = !expanded },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp),
        border = BorderStroke(1.dp, AppColors.Border)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // الصف العلوي: شارة الفصيلة + الاسم + المحافظة والمديرية + حالة المتبرع
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                BloodTypeBadge(bloodType = donor.bloodType, size = 48)

                Spacer(modifier = Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = donor.name,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        ),
                        color = AppColors.TextPrimary
                    )

                    Spacer(modifier = Modifier.height(3.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = AppColors.Primary,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (donor.subDistrict.isNullOrEmpty()) donor.district else "${donor.district} • ${donor.subDistrict}",
                            style = MaterialTheme.typography.bodySmall,
                            color = AppColors.TextSecondary
                        )
                    }
                }

                // شارة الحالة
                if (donor.isSuspended) {
                    StatusPillBadge(
                        text = "موقوف",
                        bgColor = AppColors.WarningContainer,
                        textColor = AppColors.Warning
                    )
                } else if (donor.canDonateNow) {
                    StatusPillBadge(
                        text = "متاح للتبرع",
                        bgColor = AppColors.SuccessContainer,
                        textColor = AppColors.Success
                    )
                } else {
                    StatusPillBadge(
                        text = "غير متاح",
                        bgColor = AppColors.ErrorContainer,
                        textColor = AppColors.Error
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // شريط المعلومات الخفيفة: العمر، الجنس، آخر تبرع
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                MetaInfoItem(label = "العمر", value = "${donor.age} سنة")
                MetaInfoItem(
                    label = "الجنس",
                    value = if (donor.gender == "female") AppStrings.female else AppStrings.male
                )
                if (donor.lastDonationDate != null) {
                    MetaInfoItem(
                        label = "آخر تبرع",
                        value = DateUtils.formatDate(donor.lastDonationDate)
                    )
                }
            }

            // التفاصيل الموسعة
            AnimatedVisibility(visible = expanded) {
                Column(modifier = Modifier.padding(top = 14.dp)) {
                    HorizontalDivider(color = AppColors.Divider, thickness = 1.dp)

                    Spacer(modifier = Modifier.height(10.dp))

                    // أرقام الهواتف المتعددة
                    Text(
                        text = "أرقام التواصل المسجلة:",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = AppColors.TextSecondary
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    donor.allPhoneNumbers.forEach { phone ->
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 3.dp),
                            shape = RoundedCornerShape(10.dp),
                            color = AppColors.SurfaceVariant
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Phone,
                                        contentDescription = null,
                                        tint = AppColors.TextSecondary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = phone,
                                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                        color = AppColors.TextPrimary
                                    )
                                }

                                Row {
                                    IconButton(
                                        onClick = { IntentUtils.dialPhoneNumber(context, phone) },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Call,
                                            contentDescription = AppStrings.call,
                                            tint = AppColors.Primary,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }

                                    IconButton(
                                        onClick = { IntentUtils.openWhatsApp(context, phone) },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Text(text = "💬", fontSize = 16.sp)
                                    }
                                }
                            }
                        }
                    }

                    if (!donor.notes.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "ملاحظات: ${donor.notes}",
                            style = MaterialTheme.typography.bodySmall,
                            color = AppColors.TextSecondary
                        )
                    }

                    if (donor.isSuspended && donor.suspendedUntil != null) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "موقوف مؤقتاً حتى: ${DateUtils.formatDate(donor.suspendedUntil)}",
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                            color = AppColors.Warning
                        )
                    }

                    // أزرار الإجراءات الإدارية والمستشفيات
                    if (showAdminActions || showHospitalActions) {
                        Spacer(modifier = Modifier.height(12.dp))
                        HorizontalDivider(color = AppColors.Divider, thickness = 1.dp)
                        Spacer(modifier = Modifier.height(10.dp))

                        FlowRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            if (onEdit != null) {
                                OutlinedButton(
                                    onClick = onEdit,
                                    shape = RoundedCornerShape(10.dp),
                                    border = BorderStroke(1.dp, AppColors.Border)
                                ) {
                                    Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(AppStrings.edit, style = MaterialTheme.typography.labelSmall)
                                }
                            }

                            if (onUpdateDonationDate != null) {
                                Button(
                                    onClick = onUpdateDonationDate,
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = AppColors.Secondary)
                                ) {
                                    Icon(Icons.Default.Event, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(AppStrings.updateLastDonation, style = MaterialTheme.typography.labelSmall, color = Color.White)
                                }
                            }

                            if (onSuspend != null && !donor.isSuspended) {
                                OutlinedButton(
                                    onClick = onSuspend,
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = AppColors.Warning),
                                    border = BorderStroke(1.dp, AppColors.Warning.copy(alpha = 0.5f))
                                ) {
                                    Icon(Icons.Default.Block, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(AppStrings.suspendFor6Months, style = MaterialTheme.typography.labelSmall)
                                }
                            }

                            if (showAdminActions && onDelete != null) {
                                OutlinedButton(
                                    onClick = onDelete,
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = AppColors.Error),
                                    border = BorderStroke(1.dp, AppColors.Error.copy(alpha = 0.5f))
                                ) {
                                    Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(AppStrings.delete, style = MaterialTheme.typography.labelSmall)
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // الأزرار السفلية الأساسية (اتصال فوري + واتساب + زر الإبلاغ والتفاصيل)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // زر الاتصال
                Button(
                    onClick = { IntentUtils.dialPhoneNumber(context, donor.phoneNumber) },
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AppColors.Primary),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 1.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Call,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(17.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = AppStrings.call, fontWeight = FontWeight.Bold, color = Color.White)
                }

                // زر الواتساب
                Button(
                    onClick = { IntentUtils.openWhatsApp(context, donor.phoneNumber) },
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366)),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 1.dp)
                ) {
                    Text(text = "💬", fontSize = 16.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = AppStrings.whatsapp, fontWeight = FontWeight.Bold, color = Color.White)
                }

                if (onReport != null) {
                    Surface(
                        modifier = Modifier.size(44.dp),
                        shape = RoundedCornerShape(12.dp),
                        color = AppColors.SurfaceVariant,
                        border = BorderStroke(1.dp, AppColors.Border)
                    ) {
                        IconButton(onClick = onReport) {
                            Icon(
                                imageVector = Icons.Default.Report,
                                contentDescription = AppStrings.reportDonor,
                                tint = AppColors.Warning,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }

                Surface(
                    modifier = Modifier.size(44.dp),
                    shape = RoundedCornerShape(12.dp),
                    color = AppColors.SurfaceVariant,
                    border = BorderStroke(1.dp, AppColors.Border)
                ) {
                    IconButton(onClick = { expanded = !expanded }) {
                        Icon(
                            imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            contentDescription = null,
                            tint = AppColors.TextSecondary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun StatusPillBadge(text: String, bgColor: Color, textColor: Color) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = bgColor
    ) {
        Text(
            text = text,
            color = textColor,
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp)
        )
    }
}

@Composable
private fun MetaInfoItem(label: String, value: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = "$label: ",
            style = MaterialTheme.typography.bodySmall,
            color = AppColors.TextSecondary
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
            color = AppColors.TextPrimary
        )
    }
}
