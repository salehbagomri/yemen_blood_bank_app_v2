package com.bagomri.yemenbloodbank.ui.components

import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.collectIsDraggedAsState
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
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.SubcomposeAsyncImage
import com.bagomri.yemenbloodbank.core.constants.AppColors
import com.bagomri.yemenbloodbank.core.util.IntentUtils
import com.bagomri.yemenbloodbank.data.model.Banner
import kotlinx.coroutines.delay

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun BannerSlider(
    banners: List<Banner>,
    modifier: Modifier = Modifier,
    onNavigate: ((String) -> Unit)? = null
) {
    val context = LocalContext.current
    val effectiveBanners = if (banners.isNotEmpty()) banners else defaultFallbackBanners()

    val pagerState = rememberPagerState(pageCount = { effectiveBanners.size })
    val isDragged by pagerState.interactionSource.collectIsDraggedAsState()

    // تشغيل تلقائي كل 4 ثوانٍ يتوقف عند لمس الشاشة
    LaunchedEffect(isDragged, effectiveBanners.size) {
        if (!isDragged && effectiveBanners.size > 1) {
            while (true) {
                delay(4000)
                val nextPage = (pagerState.currentPage + 1) % effectiveBanners.size
                pagerState.animateScrollToPage(
                    page = nextPage,
                    animationSpec = tween(600)
                )
            }
        }
    }

    Column(modifier = modifier.fillMaxWidth()) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .height(170.dp)
        ) { page ->
            val banner = effectiveBanners[page]
            BannerItemCard(
                banner = banner,
                onClick = {
                    when (banner.actionType) {
                        "internal_route" -> banner.actionValue?.let { onNavigate?.invoke(it) }
                        "external_url" -> banner.actionValue?.let { IntentUtils.openUrl(context, it) }
                    }
                }
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // مؤشرات التنقل (Expanding Dots)
        if (effectiveBanners.size > 1) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(effectiveBanners.size) { index ->
                    val isSelected = pagerState.currentPage == index
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 3.dp)
                            .size(
                                width = if (isSelected) 20.dp else 6.dp,
                                height = 6.dp
                            )
                            .clip(RoundedCornerShape(3.dp))
                            .background(
                                if (isSelected) AppColors.Primary else AppColors.Border
                            )
                    )
                }
            }
        }
    }
}

@Composable
private fun BannerItemCard(
    banner: Banner,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clickable(enabled = banner.actionType != "none", onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            if (!banner.imageUrl.isNullOrEmpty()) {
                SubcomposeAsyncImage(
                    model = banner.imageUrl,
                    contentDescription = banner.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize(),
                    loading = {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(AppColors.SurfaceVariant),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = AppColors.Primary
                            )
                        }
                    },
                    error = {
                        TextBannerContent(banner = banner)
                    }
                )
            } else {
                TextBannerContent(banner = banner)
            }
        }
    }
}

@Composable
private fun TextBannerContent(banner: Banner) {
    val gradient = getGradient(banner.bgGradient)
    val icon = getBannerIcon(banner.iconName)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.horizontalGradient(gradient))
            .padding(20.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = banner.title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    ),
                    color = Color.White
                )

                if (!banner.subtitle.isNullOrEmpty()) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = banner.subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                }
            }

            if (icon != null) {
                Spacer(modifier = Modifier.width(12.dp))
                Surface(
                    modifier = Modifier.size(52.dp),
                    shape = CircleShape,
                    color = Color.White.copy(alpha = 0.2f)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
            }
        }
    }
}

private fun getGradient(type: String?): List<Color> {
    return when (type) {
        "green" -> listOf(Color(0xFF1B5E20), Color(0xFF43A047))
        "orange" -> listOf(Color(0xFFE65100), Color(0xFFFB8C00))
        "blue" -> listOf(Color(0xFF0D47A1), Color(0xFF1976D2))
        "crimson" -> listOf(Color(0xFF880E4F), Color(0xFFC2185B))
        else -> listOf(Color(0xFFB71C1C), Color(0xFFE63946)) // Red default
    }
}

private fun getBannerIcon(iconName: String?): ImageVector? {
    return when (iconName) {
        "favorite" -> Icons.Default.Favorite
        "timer" -> Icons.Default.Timer
        "hospital" -> Icons.Default.LocalHospital
        "info" -> Icons.Default.Info
        else -> Icons.Default.Favorite
    }
}

private fun defaultFallbackBanners(): List<Banner> {
    return listOf(
        Banner(
            id = "f1",
            title = "تبرعك ينقذ حياة إنسان",
            subtitle = "قطرة دم واحدة قد تصنع فارقاً كبيراً في حياة مريض",
            iconName = "favorite",
            bgGradient = "red",
            actionType = "internal_route",
            actionValue = "/donor/search"
        ),
        Banner(
            id = "f2",
            title = "شروط التبرع بالدم",
            subtitle = "أن يكون العمر بين 17 و 70 سنة وبصحة جيدة",
            iconName = "info",
            bgGradient = "blue",
            actionType = "internal_route",
            actionValue = "/awareness"
        ),
        Banner(
            id = "f3",
            title = "المدة بين التبرعات",
            subtitle = "يجب أن يفصل بين كل تبرع 6 أشهر على الأقل",
            iconName = "timer",
            bgGradient = "green",
            actionType = "internal_route",
            actionValue = "/awareness"
        )
    )
}
