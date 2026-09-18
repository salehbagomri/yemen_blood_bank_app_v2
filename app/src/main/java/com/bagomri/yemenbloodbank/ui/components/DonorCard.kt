package com.bagomri.yemenbloodbank.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material.icons.filled.Cake
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Info
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
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bagomri.yemenbloodbank.R
import com.bagomri.yemenbloodbank.core.constants.AppColors
import com.bagomri.yemenbloodbank.core.constants.AppStrings
import com.bagomri.yemenbloodbank.core.util.DateUtils
import com.bagomri.yemenbloodbank.core.util.IntentUtils
import com.bagomri.yemenbloodbank.core.util.PhoneUtils
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

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp),
        border = BorderStroke(1.dp, AppColors.Border)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // 1. الصف الأول: مربع الفصيلة + اسم المتبرع (بمحاذاة أفقية في المنتصف) + شارة الحالة
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                BloodTypeBadge(bloodType = donor.bloodType, size = 46)

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = donor.name,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    ),
                    color = AppColors.TextPrimary,
                    maxLines = 1,
                    modifier = Modifier.weight(1f)
                )

                Spacer(modifier = Modifier.width(8.dp))

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

            Spacer(modifier = Modifier.height(10.dp))

            // 2. الصف الثاني: سطر أنيق موحد للمعلومات بالأيقونات فقط (الموقع • العمر • الجنس • آخر تبرع) بدون عناوين مكررة
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // الموقع الجغرافي (محافظة • مديرية بدون أي تكرار)
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = AppColors.Primary,
                    modifier = Modifier.size(15.dp)
                )
                Spacer(modifier = Modifier.width(3.dp))
                Text(
                    text = donor.displayLocation,
                    style = MaterialTheme.typography.bodySmall,
                    color = AppColors.TextSecondary,
                    maxLines = 1
                )

                Text(
                    text = "  •  ",
                    color = AppColors.TextHint,
                    style = MaterialTheme.typography.bodySmall
                )

                // العمر بالأيقونة
                Icon(
                    imageVector = Icons.Default.Cake,
                    contentDescription = null,
                    tint = AppColors.TextSecondary,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(3.dp))
                Text(
                    text = "${donor.age} سنة",
                    style = MaterialTheme.typography.bodySmall,
                    color = AppColors.TextSecondary
                )

                Text(
                    text = "  •  ",
                    color = AppColors.TextHint,
                    style = MaterialTheme.typography.bodySmall
                )

                // الجنس بالأيقونة
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = AppColors.TextSecondary,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(3.dp))
                Text(
                    text = if (donor.gender == "female") AppStrings.female else AppStrings.male,
                    style = MaterialTheme.typography.bodySmall,
                    color = AppColors.TextSecondary
                )

                if (donor.lastDonationDate != null) {
                    Text(
                        text = "  •  ",
                        color = AppColors.TextHint,
                        style = MaterialTheme.typography.bodySmall
                    )
                    Icon(
                        imageVector = Icons.Default.History,
                        contentDescription = null,
                        tint = AppColors.Success,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        text = DateUtils.formatDate(donor.lastDonationDate),
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                        color = AppColors.Success
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // 3. أسطر أرقام التواصل مباشرة بدون كتابة عنوان "أرقام التواصل المسجلة"
            donor.allPhoneNumbers.forEachIndexed { index, phone ->
                val isPrimary = index == 0
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 3.dp),
                    shape = RoundedCornerShape(10.dp),
                    color = AppColors.SurfaceVariant.copy(alpha = 0.55f),
                    border = BorderStroke(0.8.dp, AppColors.Border.copy(alpha = 0.6f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Phone,
                                contentDescription = null,
                                tint = if (isPrimary) AppColors.Primary else AppColors.TextSecondary,
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                                Text(
                                    text = PhoneUtils.formatDisplayPhone(phone),
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    ),
                                    color = AppColors.TextPrimary
                                )
                            }
                            if (donor.allPhoneNumbers.size > 1) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (isPrimary) "(رئيسي)" else "(إضافي $index)",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = AppColors.TextSecondary,
                                    fontSize = 11.sp
                                )
                            }
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            IconButton(
                                onClick = { IntentUtils.dialPhoneNumber(context, phone) },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Call,
                                    contentDescription = AppStrings.call,
                                    tint = AppColors.Primary,
                                    modifier = Modifier.size(17.dp)
                                )
                            }

                            IconButton(
                                onClick = { IntentUtils.openWhatsApp(context, phone) },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_whatsapp),
                                    contentDescription = AppStrings.whatsapp,
                                    tint = Color(0xFF25D366),
                                    modifier = Modifier.size(17.dp)
                                )
                            }
                        }
                    }
                }
            }

            if (!donor.notes.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    color = AppColors.SurfaceVariant.copy(alpha = 0.35f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = AppColors.TextSecondary,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "ملاحظات: ${donor.notes}",
                            style = MaterialTheme.typography.bodySmall,
                            color = AppColors.TextSecondary,
                            fontSize = 12.sp
                        )
                    }
                }
            }

            if (donor.isSuspended && donor.suspendedUntil != null) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "موقوف مؤقتاً حتى: ${DateUtils.formatDate(donor.suspendedUntil)}",
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                    color = AppColors.Warning
                )
            }

            // خيارات إدارية ومستشفيات إن وجدت
            if ((showAdminActions && onDelete != null) || (showHospitalActions && onUpdateDonationDate != null) || onEdit != null || onSuspend != null) {
                Spacer(modifier = Modifier.height(8.dp))
                HorizontalDivider(color = AppColors.Divider, thickness = 1.dp)
                Spacer(modifier = Modifier.height(8.dp))

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

            Spacer(modifier = Modifier.height(10.dp))

            // 4. الأزرار السفلية الأساسية (اتصال فوري + واتساب + زر الإبلاغ)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // زر الاتصال
                Button(
                    onClick = { IntentUtils.dialPhoneNumber(context, donor.phoneNumber) },
                    modifier = Modifier
                        .weight(1f)
                        .height(42.dp),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AppColors.Primary),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 1.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Call,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                        text = AppStrings.call,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        maxLines = 1,
                        softWrap = false,
                        color = Color.White
                    )
                }

                // زر الواتساب
                Button(
                    onClick = { IntentUtils.openWhatsApp(context, donor.phoneNumber) },
                    modifier = Modifier
                        .weight(1f)
                        .height(42.dp),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366)),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 1.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_whatsapp),
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(17.dp)
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                        text = AppStrings.whatsapp,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        maxLines = 1,
                        softWrap = false,
                        color = Color.White
                    )
                }

                if (onReport != null) {
                    Surface(
                        modifier = Modifier.size(42.dp),
                        shape = RoundedCornerShape(12.dp),
                        color = AppColors.SurfaceVariant,
                        border = BorderStroke(1.dp, AppColors.Border)
                    ) {
                        IconButton(onClick = onReport) {
                            Icon(
                                imageVector = Icons.Default.Report,
                                contentDescription = AppStrings.reportDonor,
                                tint = AppColors.Warning,
                                modifier = Modifier.size(19.dp)
                            )
                        }
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
