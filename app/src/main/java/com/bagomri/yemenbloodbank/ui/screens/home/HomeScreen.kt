package com.bagomri.yemenbloodbank.ui.screens.home

import android.content.Context
import android.content.Intent
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.PrivacyTip
import androidx.compose.material.icons.filled.Report
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bagomri.yemenbloodbank.core.constants.AppColors
import com.bagomri.yemenbloodbank.core.constants.AppStrings
import com.bagomri.yemenbloodbank.core.util.IntentUtils
import com.bagomri.yemenbloodbank.ui.components.BannerSlider

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToSearch: () -> Unit,
    onNavigateToAddDonor: () -> Unit,
    onNavigateToAwareness: () -> Unit,
    onNavigateToReport: () -> Unit,
    onNavigateToLogin: () -> Unit,
    onNavigateToAbout: () -> Unit,
    onNavigateToContact: () -> Unit,
    onNavigateByRoute: (String) -> Unit,
    viewModel: HomeViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    var menuExpanded by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = AppStrings.appName,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                },
                actions = {
                    // زر دخول الإدارة
                    IconButton(onClick = onNavigateToLogin) {
                        Icon(
                            imageVector = Icons.Default.AdminPanelSettings,
                            contentDescription = "دخول الإدارة",
                            tint = Color.White,
                            modifier = Modifier.size(26.dp)
                        )
                    }

                    // قائمة المزيد
                    IconButton(onClick = { menuExpanded = true }) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "المزيد",
                            tint = Color.White
                        )
                    }

                    DropdownMenu(
                        expanded = menuExpanded,
                        onDismissRequest = { menuExpanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("حول التطبيق") },
                            leadingIcon = { Icon(Icons.Default.Info, contentDescription = null, tint = AppColors.Primary) },
                            onClick = {
                                menuExpanded = false
                                onNavigateToAbout()
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("تواصل معنا") },
                            leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = AppColors.Primary) },
                            onClick = {
                                menuExpanded = false
                                onNavigateToContact()
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("شارك التطبيق") },
                            leadingIcon = { Icon(Icons.Default.Share, contentDescription = null, tint = AppColors.Success) },
                            onClick = {
                                menuExpanded = false
                                shareApp(context)
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("سياسة الخصوصية") },
                            leadingIcon = { Icon(Icons.Default.PrivacyTip, contentDescription = null, tint = AppColors.Info) },
                            onClick = {
                                menuExpanded = false
                                IntentUtils.openUrl(context, "https://salehbagomri.github.io/yemen-blood-bank-privacy/")
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("شروط الاستخدام") },
                            leadingIcon = { Icon(Icons.Default.Description, contentDescription = null, tint = AppColors.Info) },
                            onClick = {
                                menuExpanded = false
                                IntentUtils.openUrl(context, "https://salehbagomri.github.io/yemen-blood-bank-privacy/terms.html")
                            }
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = AppColors.Primary
                )
            )
        }
    ) { paddingValues ->
        PullToRefreshBox(
            isRefreshing = uiState.isRefreshing,
            onRefresh = { viewModel.refresh() },
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                // سلايدر البانرات التفاعلي
                BannerSlider(
                    banners = uiState.banners,
                    onNavigate = onNavigateByRoute
                )

                Spacer(modifier = Modifier.height(20.dp))

                // الأزرار الرئيسية
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // 1. زر البحث عن متبرعين
                    MainHeroActionButton(
                        icon = Icons.Default.Search,
                        title = AppStrings.searchForDonors,
                        subtitle = "ابحث عن متبرعين حسب الفصيلة والمحافظة",
                        gradient = listOf(AppColors.Primary, AppColors.PrimaryDark),
                        onClick = onNavigateToSearch
                    )

                    // 2. زر إضافة متبرع
                    MainHeroActionButton(
                        icon = Icons.Default.PersonAdd,
                        title = AppStrings.addDonor,
                        subtitle = "أضف نفسك أو شخصاً آخر كمتبرع",
                        gradient = listOf(Color(0xFF2E7D32), Color(0xFF43A047)),
                        onClick = onNavigateToAddDonor
                    )

                    // 3. أزرار التوعية والإبلاغ
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        SecondaryActionCard(
                            icon = Icons.Default.School,
                            title = AppStrings.awareness,
                            iconColor = AppColors.Info,
                            modifier = Modifier.weight(1f),
                            onClick = onNavigateToAwareness
                        )

                        SecondaryActionCard(
                            icon = Icons.Default.Report,
                            title = AppStrings.reportDonor,
                            iconColor = AppColors.Warning,
                            modifier = Modifier.weight(1f),
                            onClick = onNavigateToReport
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // تذييل الصفحة (Footer)
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Surface(
                            modifier = Modifier
                                .size(width = 40.dp, height = 2.dp),
                            color = AppColors.Divider,
                            shape = RoundedCornerShape(1.dp)
                        ) {}

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "صُنع بحب",
                                style = MaterialTheme.typography.bodySmall,
                                color = AppColors.TextSecondary
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(
                                imageVector = Icons.Default.Favorite,
                                contentDescription = null,
                                tint = Color.Red,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "لأهالي اليمن",
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                                color = AppColors.TextSecondary
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MainHeroActionButton(
    icon: ImageVector,
    title: String,
    subtitle: String,
    gradient: List<Color>,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.horizontalGradient(gradient))
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    modifier = Modifier.size(52.dp),
                    shape = RoundedCornerShape(14.dp),
                    color = Color.White.copy(alpha = 0.2f)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(30.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        ),
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                }

                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                    contentDescription = null,
                    tint = Color.White.copy(alpha = 0.8f),
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
private fun SecondaryActionCard(
    icon: ImageVector,
    title: String,
    iconColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp),
        border = BorderStroke(1.dp, AppColors.Border)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 18.dp, horizontal = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                modifier = Modifier.size(46.dp),
                shape = RoundedCornerShape(12.dp),
                color = iconColor.copy(alpha = 0.12f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = iconColor,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )
        }
    }
}

private fun shareApp(context: Context) {
    val shareText = """
        🩸 بنك دم اليمن - تطبيق ينقذ الأرواح!
        
        التطبيق يساعد على:
        • البحث السريع عن متبرعين بالدم
        • ربط المتبرعين مع المحتاجين
        • نشر الوعي حول أهمية التبرع
        
        📥 حمّل التطبيق الآن:
        https://play.google.com/store/apps/details?id=com.bagomri.yemenbloodbank
        
        💙 معاً ننقذ الأرواح في اليمن
    """.trimIndent()

    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, shareText)
    }
    context.startActivity(Intent.createChooser(intent, "مشاركة التطبيق"))
}
