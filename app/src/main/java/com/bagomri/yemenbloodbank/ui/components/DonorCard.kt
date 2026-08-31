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
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.material.icons.filled.Report
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
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
            .padding(vertical = 6.dp)
            .clickable { expanded = !expanded },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = BorderStroke(1.dp, AppColors.Divider)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // الصف العلوي: شارة الفصيلة + الاسم + المحافظة + زر التوسيع
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                BloodTypeBadge(bloodType = donor.bloodType, size = 48)

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = donor.name,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = AppColors.Primary,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = donor.district,
                            style = MaterialTheme.typography.bodySmall,
                            color = AppColors.TextSecondary
                        )
                    }
                }

                // حالة الأهلية للتبرع
                if (donor.isSuspended) {
                    StatusBadge(
                        text = "موقوف",
                        bgColor = AppColors.ErrorContainer,
                        textColor = AppColors.Error
                    )
                } else if (donor.canDonateNow) {
                    StatusBadge(
                        text = "متاح",
                        bgColor = AppColors.SuccessContainer,
                        textColor = AppColors.Success
                    )
                } else {
                    StatusBadge(
                        text = "غير متاح",
                        bgColor = AppColors.WarningContainer,
                        textColor = AppColors.Warning
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // معلومات إضافية سريعة: العمر، الجنس
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                InfoTag(label = "العمر", value = "${donor.age} سنة")
                InfoTag(
                    label = "الجنس",
                    value = if (donor.gender == "female") AppStrings.female else AppStrings.male
                )
                if (donor.lastDonationDate != null) {
                    InfoTag(
                        label = "آخر تبرع",
                        value = DateUtils.formatDate(donor.lastDonationDate)
                    )
                }
            }

            // التفاصيل الموسعة
            AnimatedVisibility(visible = expanded) {
                Column(modifier = Modifier.padding(top = 12.dp)) {
                    Divider(color = AppColors.Divider, thickness = 1.dp)

                    Spacer(modifier = Modifier.height(8.dp))

                    // عرض أرقام الهواتف المتعددة إن وجدت
                    Text(
                        text = "أرقام الهواتف:",
                        style = MaterialTheme.typography.labelMedium,
                        color = AppColors.TextSecondary
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    donor.allPhoneNumbers.forEach { phone ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = phone,
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
                            )

                            Row {
                                IconButton(
                                    onClick = { IntentUtils.dialPhoneNumber(context, phone) },
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Call,
                                        contentDescription = AppStrings.call,
                                        tint = AppColors.Primary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }

                                IconButton(
                                    onClick = { IntentUtils.openWhatsApp(context, phone) },
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Text(
                                        text = "💬",
                                        fontSize = 18.sp
                                    )
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
                            text = "موقوف حتى: ${DateUtils.formatDate(donor.suspendedUntil)}",
                            style = MaterialTheme.typography.bodySmall,
                            color = AppColors.Error
                        )
                    }

                    // أزرار الإجراءات الإدارية والمستشفيات
                    if (showAdminActions || showHospitalActions) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Divider(color = AppColors.Divider, thickness = 1.dp)
                        Spacer(modifier = Modifier.height(8.dp))

                        FlowRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            if (onEdit != null) {
                                OutlinedButton(
                                    onClick = onEdit,
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(AppStrings.edit, style = MaterialTheme.typography.labelSmall)
                                }
                            }

                            if (onUpdateDonationDate != null) {
                                Button(
                                    onClick = onUpdateDonationDate,
                                    shape = RoundedCornerShape(8.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = AppColors.Secondary)
                                ) {
                                    Icon(Icons.Default.Event, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(AppStrings.updateLastDonation, style = MaterialTheme.typography.labelSmall)
                                }
                            }

                            if (onSuspend != null && !donor.isSuspended) {
                                OutlinedButton(
                                    onClick = onSuspend,
                                    shape = RoundedCornerShape(8.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = AppColors.Warning)
                                ) {
                                    Icon(Icons.Default.Block, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(AppStrings.suspendFor6Months, style = MaterialTheme.typography.labelSmall)
                                }
                            }

                            if (showAdminActions && onDelete != null) {
                                OutlinedButton(
                                    onClick = onDelete,
                                    shape = RoundedCornerShape(8.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = AppColors.Error)
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

            Spacer(modifier = Modifier.height(12.dp))

            // الأزرار السفلية الافتراضية (اتصال سريع + واتساب + إبلاغ)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = { IntentUtils.dialPhoneNumber(context, donor.phoneNumber) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AppColors.Primary)
                ) {
                    Icon(
                        imageVector = Icons.Default.Call,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = AppStrings.call, fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = { IntentUtils.openWhatsApp(context, donor.phoneNumber) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AppColors.Success)
                ) {
                    Text(text = "💬", fontSize = 16.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = AppStrings.whatsapp, fontWeight = FontWeight.Bold)
                }

                if (onReport != null) {
                    IconButton(
                        onClick = onReport,
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Report,
                            contentDescription = AppStrings.reportDonor,
                            tint = AppColors.TextSecondary
                        )
                    }
                }

                IconButton(
                    onClick = { expanded = !expanded },
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = null,
                        tint = AppColors.TextSecondary
                    )
                }
            }
        }
    }
}

@Composable
private fun StatusBadge(text: String, bgColor: Color, textColor: Color) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = bgColor
    ) {
        Text(
            text = text,
            color = textColor,
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

@Composable
private fun InfoTag(label: String, value: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = "$label: ",
            style = MaterialTheme.typography.bodySmall,
            color = AppColors.TextSecondary
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
