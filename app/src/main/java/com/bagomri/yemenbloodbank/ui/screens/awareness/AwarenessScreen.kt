package com.bagomri.yemenbloodbank.ui.screens.awareness

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Bloodtype
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Schedule
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bagomri.yemenbloodbank.core.constants.AppColors
import com.bagomri.yemenbloodbank.core.constants.AppStrings
import com.bagomri.yemenbloodbank.ui.components.BloodTypeBadge

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AwarenessScreen(
    onNavigateBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = AppStrings.awarenessTitle,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
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
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // بطاقة المقدمة
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(
                                listOf(AppColors.Primary, AppColors.PrimaryDark)
                            )
                        )
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Bloodtype,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(56.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "التبرع بالدم ينقذ الأرواح",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            ),
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "كل قطرة دم تتبرع بها يمكن أن تنقذ حياة 3 أشخاص",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = Color.White.copy(alpha = 0.9f)
                            ),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            // أقسام التوعية
            AwarenessSectionCard(
                title = AppStrings.importanceOfDonation,
                icon = Icons.Default.Favorite,
                iconColor = AppColors.Primary,
                items = listOf(
                    "التبرع بالدم يساعد في إنقاذ حياة المرضى والمصابين في الحالات الطارئة",
                    "كل وحدة دم يمكن أن تنقذ حياة ثلاثة أشخاص",
                    "التبرع آمن تماماً ولا يضر بصحة المتبرع السليم",
                    "عملية التبرع تستغرق 10-15 دقيقة فقط",
                    "التبرع المنتظم يحفز نخاع العظم لإنتاج خلايا دم جديدة ويقلل نسبة الحديد الزائد"
                )
            )

            AwarenessSectionCard(
                title = AppStrings.whoCanDonate,
                icon = Icons.Default.CheckCircle,
                iconColor = AppColors.Success,
                items = listOf(
                    "أن يكون العمر بين 17 و 70 سنة",
                    "أن يكون الوزن أكثر من 50 كيلوجرام",
                    "أن يكون بصحة جيدة وخالياً من الأمراض المعدية والمزمنة",
                    "أن يكون ضغط الدم ومستوى الهيموجلوبين في الحدود الطبيعية",
                    "أن لا يكون قد تبرع بالدم خلال الـ 6 أشهر الماضية"
                )
            )

            AwarenessSectionCard(
                title = AppStrings.beforeDonation,
                icon = Icons.Default.Info,
                iconColor = AppColors.Info,
                items = listOf(
                    "احصل على قسط كافٍ من النوم والراحة ليلة التبرع",
                    "تناول وجبة صحية خفيفة قبل التبرع بساعتين",
                    "اشرب كمية كافية من الماء والسوائل",
                    "تجنب الأطعمة الدسمة والمشروبات المنبهة قبل التبرع",
                    "أخبر الفريق الطبي عن أي أدوية تتناولها"
                )
            )

            AwarenessSectionCard(
                title = AppStrings.afterDonation,
                icon = Icons.Default.Lightbulb,
                iconColor = AppColors.Warning,
                items = listOf(
                    "استرح لمدة 10-15 دقيقة بعد التبرع وتناول عصيراً أو وجبة خفيفة",
                    "اشرب كمية وافرة من السوائل لتعويض حجم الدم",
                    "تجنب المجهود البدني الشاق وحمل الأثقال لمدة 24 ساعة",
                    "إذا شعرت بدوار، اجلس فوراً وارفع قدميك للأعلى",
                    "حافظ على الضمادة على موضع الإبرة لعدة ساعات"
                )
            )

            AwarenessSectionCard(
                title = AppStrings.prohibitedCases,
                icon = Icons.Default.Block,
                iconColor = AppColors.Error,
                items = listOf(
                    "مرضى القلب والكبد والكلى والأورام",
                    "مرضى السكري المعتمد على الأنسولين أو غير المنضبط",
                    "الحوامل والمرضعات حتى سنة بعد الولادة",
                    "من لديه حمى أو نزلة برد أو عدوى نشطة",
                    "من تناول المضادات الحيوية مؤخراً أو أجرى عملية جراحية حديثة"
                )
            )

            AwarenessSectionCard(
                title = AppStrings.donationInterval,
                icon = Icons.Default.Schedule,
                iconColor = AppColors.Primary,
                items = listOf(
                    "يجب أن تمر 6 أشهر (180 يوماً) على الأقل بين كل تبرع وآخر",
                    "هذه المدة تضمن تعويض مخزون الحديد وخلايا الدم بشكل كامل",
                    "التطبيق يحسب هذه المدة تلقائياً ويحدد موعد إتاحة المتبرع"
                )
            )

            // جدول فصائل الدم
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            modifier = Modifier.size(36.dp),
                            shape = CircleShape,
                            color = AppColors.PrimaryContainer
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Bloodtype,
                                    contentDescription = null,
                                    tint = AppColors.Primary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "فصائل الدم وتوافقها",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        AppStrings.bloodTypes.forEach { type ->
                            BloodTypeBadge(bloodType = type, size = 48)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "💡 فصيلة O- هي المعطي العام لجميع الفصائل في الطوارئ، بينما AB+ هي المستقبل العام.",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = AppColors.TextSecondary,
                            lineHeight = 20.sp
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun AwarenessSectionCard(
    title: String,
    icon: ImageVector,
    iconColor: Color,
    items: List<String>
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
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
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            items.forEach { point ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 10.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Surface(
                        modifier = Modifier
                            .padding(top = 7.dp)
                            .size(6.dp),
                        shape = CircleShape,
                        color = iconColor
                    ) {}
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = point,
                        style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 22.sp),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}
