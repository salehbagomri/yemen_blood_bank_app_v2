package com.bagomri.yemenbloodbank.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.KeyboardArrowDown
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
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
    initiallyExpanded: Boolean = false,
    showAdminActions: Boolean = false,
    showHospitalActions: Boolean = false,
    onEdit: (() -> Unit)? = null,
    onDelete: (() -> Unit)? = null,
    onSuspend: (() -> Unit)? = null,
    onUpdateDonationDate: (() -> Unit)? = null,
    onReport: (() -> Unit)? = null
) {
    val context = LocalContext.current
    var isExpanded by remember { mutableStateOf(initiallyExpanded) }
    val rotationAngle by animateFloatAsState(
        targetValue = if (isExpanded) 180f else 0f,
        animationSpec = tween(durationMillis = 220),
        label = "chevron_rotation"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isExpanded) 2.5.dp else 1.dp),
        border = BorderStroke(
            width = if (isExpanded) 1.5.dp else 1.dp,
            color = if (isExpanded) AppColors.Primary else AppColors.Border
        )
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // 1. الجزء المطوي المرئي دائماً (قابل للنقر للتوسيع / الطي)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { isExpanded = !isExpanded }
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // شارة فصيلة الدم بحجم مدروس (46dp)
                BloodTypeBadge(bloodType = donor.bloodType, size = 46)

                Spacer(modifier = Modifier.width(10.dp))

                // عمود معلومات المتبرع (الاسم + سطر الأيقونات الموحد بدون تكرار)
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = donor.name,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        ),
                        color = AppColors.TextPrimary,
                        maxLines = 1
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    // سطر تفاصيل المتبرع بالأيقونات فقط: موقع • عمر • جنس
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = AppColors.Primary,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(
                            text = donor.displayLocation,
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                            color = AppColors.TextSecondary,
                            maxLines = 1
                        )

                        Text(
                            text = " • ",
                            color = AppColors.TextHint,
                            fontSize = 11.sp
                        )

                        Icon(
                            imageVector = Icons.Default.Cake,
                            contentDescription = null,
                            tint = AppColors.TextSecondary,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(
                            text = "${donor.age} سنة",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                            color = AppColors.TextSecondary
                        )

                        Text(
                            text = " • ",
                            color = AppColors.TextHint,
                            fontSize = 11.sp
                        )

                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = AppColors.TextSecondary,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(
                            text = if (donor.gender == "female") AppStrings.female else AppStrings.male,
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                            color = AppColors.TextSecondary
                        )
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                // الجهة اليسرى: شارة الحالة + سهم التوسع الدوار
                Column(
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.Center
                ) {
                    if (donor.isSuspended) {
                        StatusPillBadge(
                            text = "موقوف",
                            bgColor = AppColors.WarningContainer,
                            textColor = AppColors.Warning
                        )
                    } else if (donor.canDonateNow) {
                        StatusPillBadge(
                            text = "متاح",
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

                    Spacer(modifier = Modifier.height(4.dp))

                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = if (isExpanded) "طي" else "توسيع",
                        tint = AppColors.Primary,
                        modifier = Modifier
                            .size(22.dp)
                            .rotate(rotationAngle)
                    )
                }
            }

            // 2. المحتوى الموسع مع انتقال متحرك سلس
            AnimatedVisibility(
                visible = isExpanded,
                enter = expandVertically(animationSpec = tween(220)) + fadeIn(animationSpec = tween(220)),
                exit = shrinkVertically(animationSpec = tween(180)) + fadeOut(animationSpec = tween(180))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 12.dp, end = 12.dp, bottom = 12.dp)
                ) {
                    HorizontalDivider(color = AppColors.Divider, thickness = 1.dp)
                    Spacer(modifier = Modifier.height(10.dp))

                    // أرقام الهواتف مع أزرار الاتصال والواتساب
                    donor.allPhoneNumbers.forEachIndexed { index, phone ->
                        val label = when (index) {
                            0 -> "رقم الهاتف الأساسي"
                            1 -> "رقم إضافي 2"
                            2 -> "رقم إضافي 3"
                            else -> "رقم إضافي ${index + 1}"
                        }
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 3.dp),
                            shape = RoundedCornerShape(10.dp),
                            color = AppColors.SurfaceVariant.copy(alpha = 0.5f),
                            border = BorderStroke(0.8.dp, AppColors.Border.copy(alpha = 0.7f))
                        ) {
                            Column(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Phone,
                                        contentDescription = null,
                                        tint = if (index == 0) AppColors.Primary else AppColors.TextSecondary,
                                        modifier = Modifier.size(13.dp)
                                    )
                                    Spacer(modifier = Modifier.width(5.dp))
                                    Text(
                                        text = label,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Medium,
                                            fontSize = 11.sp
                                        ),
                                        color = AppColors.TextSecondary
                                    )
                                }

                                Spacer(modifier = Modifier.height(5.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // عرض الرقم بالإنجليزية لتجنب انعكاس الأرقام
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

                                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        // زر الاتصال المباشر
                                        Button(
                                            onClick = { IntentUtils.dialPhoneNumber(context, phone) },
                                            modifier = Modifier.height(32.dp),
                                            shape = RoundedCornerShape(8.dp),
                                            colors = ButtonDefaults.buttonColors(containerColor = AppColors.Success),
                                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 0.dp),
                                            elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Call,
                                                contentDescription = null,
                                                tint = Color.White,
                                                modifier = Modifier.size(13.dp)
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(
                                                text = AppStrings.call,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White
                                            )
                                        }

                                        // زر الواتساب المباشر
                                        Button(
                                            onClick = { IntentUtils.openWhatsApp(context, phone) },
                                            modifier = Modifier.height(32.dp),
                                            shape = RoundedCornerShape(8.dp),
                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366)),
                                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 0.dp),
                                            elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
                                        ) {
                                            Icon(
                                                painter = painterResource(id = R.drawable.ic_whatsapp),
                                                contentDescription = null,
                                                tint = Color.White,
                                                modifier = Modifier.size(14.dp)
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(
                                                text = AppStrings.whatsapp,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // معلومات إضافية إن وجدت
                    if (donor.lastDonationDate != null) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.History,
                                contentDescription = null,
                                tint = AppColors.Success,
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "آخر تبرع: ${DateUtils.formatDate(donor.lastDonationDate)}",
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                                color = AppColors.Success
                            )
                        }
                    }

                    if (donor.isSuspended && donor.suspendedUntil != null) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Block,
                                contentDescription = null,
                                tint = AppColors.Warning,
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "موقوف مؤقتاً حتى: ${DateUtils.formatDate(donor.suspendedUntil)}",
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                                color = AppColors.Warning
                            )
                        }
                    }

                    if (!donor.notes.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(6.dp))
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

                    // زر الإبلاغ عن الرقم إن كان متاحاً
                    if (onReport != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedButton(
                            onClick = onReport,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(36.dp),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = AppColors.Warning),
                            border = BorderStroke(1.dp, AppColors.Warning.copy(alpha = 0.45f)),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 0.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Report,
                                contentDescription = null,
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = AppStrings.reportDonor,
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
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
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
        )
    }
}
