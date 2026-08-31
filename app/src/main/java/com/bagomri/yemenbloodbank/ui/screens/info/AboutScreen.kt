package com.bagomri.yemenbloodbank.ui.screens.info

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.PrivacyTip
import androidx.compose.material.icons.filled.VolunteerActivism
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bagomri.yemenbloodbank.R
import com.bagomri.yemenbloodbank.core.constants.AppColors
import com.bagomri.yemenbloodbank.core.constants.AppStrings
import com.bagomri.yemenbloodbank.core.util.IntentUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    var devExpanded by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "حول التطبيق",
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
        ) {
            // Header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            listOf(AppColors.Primary, AppColors.PrimaryDark)
                        )
                    )
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_logo_white),
                        contentDescription = null,
                        modifier = Modifier.size(100.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = AppStrings.appName,
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = AppStrings.appNameEnglish,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.85f)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    val versionName = com.bagomri.yemenbloodbank.BuildConfig.VERSION_NAME
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = Color.White.copy(alpha = 0.2f)
                    ) {
                        Text(
                            text = "الإصدار $versionName (Native Jetpack Compose)",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                        )
                    }
                }
            }

            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                AboutCard(
                    title = "عن التطبيق",
                    content = "بنك دم اليمن هو تطبيق خيري تطوعي يهدف إلى تسهيل عملية البحث عن متبرعين بالدم وربط المحتاجين للدم بالمتبرعين في جميع محافظات ومديريات الجمهورية اليمنية الـ 22 بدون أي وسيط أو تكلفة."
                )

                AboutCard(
                    title = "رؤيتنا ورسالتنا",
                    content = "المساهمة في إنقاذ الأرواح وتوفير فصائل الدم المختلفة في أسرع وقت ممكن عبر منصة رقمية حديثة، موثوقة ومتاحة للجميع مجاناً على مدار الساعة."
                )

                AboutCard(
                    title = "مميزات التطبيق",
                    content = "• قاعدة بيانات واسعة ومحدثة للمتبرعين في كافة المحافظات\n• بحث سريع وفوري حسب فصيلة الدم والموقع الجغرافي\n• اتصال وتواصل مباشر عبر الهاتف وواتساب\n• خصوصية وأمان عالي للمتبرعين\n• لوحات إدارة مخصصة للمستشفيات والمراكز الطبية"
                )

                // بطاقة المطور القابلة للتوسيع
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp),
                    border = BorderStroke(1.dp, AppColors.Border)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { devExpanded = !devExpanded },
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                modifier = Modifier.size(42.dp),
                                shape = CircleShape,
                                color = AppColors.PrimaryContainer
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.Code,
                                        contentDescription = null,
                                        tint = AppColors.Primary,
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "فريق التطوير والتقنية",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = AppColors.TextPrimary
                                )
                                Text(
                                    text = "معلومات المطور والتواصل",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = AppColors.TextSecondary
                                )
                            }

                            Icon(
                                imageVector = if (devExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                contentDescription = null,
                                tint = AppColors.TextSecondary
                            )
                        }

                        AnimatedVisibility(visible = devExpanded) {
                            Column(modifier = Modifier.padding(top = 16.dp)) {
                                Text(
                                    text = "تطوير وتصميم",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = AppColors.TextSecondary
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "صالح باقمري (Saleh Bagomri)",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = AppColors.TextPrimary
                                )

                                Spacer(modifier = Modifier.height(14.dp))
                                HorizontalDivider(color = AppColors.Divider, thickness = 1.dp)
                                Spacer(modifier = Modifier.height(12.dp))

                                ContactRowItem(
                                    icon = Icons.Default.Email,
                                    label = "s.bagomri@gmail.com",
                                    onClick = { IntentUtils.openUrl(context, "mailto:s.bagomri@gmail.com") }
                                )
                                ContactRowItem(
                                    icon = Icons.AutoMirrored.Filled.Chat,
                                    label = "+967 770 727 055",
                                    subtitle = "تواصل عبر واتساب",
                                    iconColor = AppColors.Success,
                                    onClick = { IntentUtils.openWhatsApp(context, "967770727055") }
                                )
                                ContactRowItem(
                                    icon = Icons.Default.Language,
                                    label = "www.bagomri.com",
                                    onClick = { IntentUtils.openUrl(context, "https://www.bagomri.com") }
                                )
                                ContactRowItem(
                                    icon = Icons.Default.LocationOn,
                                    label = "حضرموت، اليمن",
                                    onClick = null
                                )

                                Spacer(modifier = Modifier.height(12.dp))
                                HorizontalDivider(color = AppColors.Divider, thickness = 1.dp)
                                Spacer(modifier = Modifier.height(12.dp))

                                Text(
                                    text = "بنك دم اليمن 2026 © جميع الحقوق محفوظة",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = AppColors.TextSecondary,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    }
                }

                // روابط قانونية
                LegalLinkCard(
                    title = "سياسة الخصوصية",
                    icon = Icons.Default.PrivacyTip,
                    onClick = { IntentUtils.openUrl(context, "https://salehbagomri.github.io/yemen-blood-bank-privacy/") }
                )

                LegalLinkCard(
                    title = "شروط الاستخدام",
                    icon = Icons.Default.Description,
                    onClick = { IntentUtils.openUrl(context, "https://salehbagomri.github.io/yemen-blood-bank-privacy/terms.html") }
                )

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun AboutCard(title: String, content: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp),
        border = BorderStroke(1.dp, AppColors.Border)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = AppColors.TextPrimary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = content,
                style = MaterialTheme.typography.bodyMedium.copy(
                    lineHeight = 24.sp,
                    color = AppColors.TextSecondary
                )
            )
        }
    }
}

@Composable
private fun ContactRowItem(
    icon: ImageVector,
    label: String,
    subtitle: String? = null,
    iconColor: Color = AppColors.Primary,
    onClick: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = onClick != null) { onClick?.invoke() }
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            modifier = Modifier.size(36.dp),
            shape = CircleShape,
            color = iconColor.copy(alpha = 0.12f)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                color = AppColors.TextPrimary
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = AppColors.TextSecondary
                )
            }
        }

        if (onClick != null) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                contentDescription = null,
                tint = AppColors.TextHint,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

@Composable
private fun LegalLinkCard(
    title: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        border = BorderStroke(1.dp, AppColors.Border)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    modifier = Modifier.size(36.dp),
                    shape = CircleShape,
                    color = AppColors.Info.copy(alpha = 0.12f)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = AppColors.Info,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                    color = AppColors.TextPrimary
                )
            }

            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                contentDescription = null,
                tint = AppColors.TextHint,
                modifier = Modifier.size(14.dp)
            )
        }
    }
}
