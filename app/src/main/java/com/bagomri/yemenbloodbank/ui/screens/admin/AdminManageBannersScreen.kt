package com.bagomri.yemenbloodbank.ui.screens.admin

import android.widget.Toast
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.bagomri.yemenbloodbank.core.constants.AppColors
import com.bagomri.yemenbloodbank.core.constants.AppStrings
import com.bagomri.yemenbloodbank.core.util.ErrorHandler
import com.bagomri.yemenbloodbank.data.model.Banner
import com.bagomri.yemenbloodbank.data.repository.BannerRepository
import com.bagomri.yemenbloodbank.ui.components.CustomDropdown
import com.bagomri.yemenbloodbank.ui.components.CustomTextField
import com.bagomri.yemenbloodbank.ui.components.EmptyState
import com.bagomri.yemenbloodbank.ui.components.ErrorDisplay
import com.bagomri.yemenbloodbank.ui.components.LoadingIndicator
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminManageBannersScreen(
    onNavigateBack: () -> Unit,
    viewModel: AdminDashboardViewModel = viewModel(),
    bannerRepository: BannerRepository = BannerRepository()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var showBottomSheet by remember { mutableStateOf(false) }
    var editingBanner by remember { mutableStateOf<Banner?>(null) }

    var bannerTitle by remember { mutableStateOf("") }
    var bannerSubtitle by remember { mutableStateOf("") }
    var bannerActionValue by remember { mutableStateOf("") }
    var bannerGradientType by remember { mutableStateOf("green") }
    var isSaving by remember { mutableStateOf(false) }

    val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    fun openForm(banner: Banner? = null) {
        editingBanner = banner
        if (banner != null) {
            bannerTitle = banner.title
            bannerSubtitle = banner.subtitle ?: ""
            bannerActionValue = banner.actionValue ?: ""
            bannerGradientType = banner.bgGradient ?: "green"
        } else {
            bannerTitle = ""
            bannerSubtitle = ""
            bannerActionValue = ""
            bannerGradientType = "green"
        }
        showBottomSheet = true
    }

    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet = false },
            sheetState = bottomSheetState
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = if (editingBanner == null) "إضافة بانر جديد" else "تعديل البانر",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )

                CustomTextField(
                    value = bannerTitle,
                    onValueChange = { bannerTitle = it },
                    label = "عنوان البانر",
                    placeholder = "مثال: قطرة دم تنقذ حياة"
                )

                CustomTextField(
                    value = bannerSubtitle,
                    onValueChange = { bannerSubtitle = it },
                    label = "النص الفرعي / الوصف",
                    placeholder = "تفاصيل توعوية أو توجيهية...",
                    singleLine = false,
                    maxLines = 2
                )

                CustomDropdown(
                    selectedValue = bannerGradientType,
                    items = listOf("green", "red", "blue", "purple", "orange"),
                    onItemSelected = { bannerGradientType = it },
                    label = "لون خلفية التدرج (green, red, blue, purple, orange)"
                )

                CustomTextField(
                    value = bannerActionValue,
                    onValueChange = { bannerActionValue = it },
                    label = "رابط أو مسار الشاشة (اختياري)",
                    placeholder = "/donor/search أو /awareness أو رابط خارجي"
                )

                Spacer(modifier = Modifier.height(10.dp))

                Button(
                    onClick = {
                        if (bannerTitle.isBlank()) {
                            Toast.makeText(context, "يرجى إدخال عنوان البانر", Toast.LENGTH_SHORT).show()
                            return@Button
                        }

                        isSaving = true
                        scope.launch {
                            val newBanner = if (editingBanner != null) {
                                editingBanner!!.copy(
                                    title = bannerTitle.trim(),
                                    subtitle = bannerSubtitle.trim().ifEmpty { null },
                                    actionValue = bannerActionValue.trim().ifEmpty { null },
                                    actionType = if (bannerActionValue.isNotBlank()) "internal_route" else "none",
                                    bgGradient = bannerGradientType
                                )
                            } else {
                                Banner(
                                    title = bannerTitle.trim(),
                                    subtitle = bannerSubtitle.trim().ifEmpty { null },
                                    actionValue = bannerActionValue.trim().ifEmpty { null },
                                    actionType = if (bannerActionValue.isNotBlank()) "internal_route" else "none",
                                    bgGradient = bannerGradientType,
                                    sortOrder = uiState.banners.size + 1
                                )
                            }

                            val res = if (editingBanner != null) {
                                bannerRepository.updateBanner(newBanner)
                            } else {
                                bannerRepository.createBanner(newBanner)
                            }

                            isSaving = false
                            showBottomSheet = false
                            res.fold(
                                onSuccess = {
                                    Toast.makeText(context, "تم حفظ البانر بنجاح", Toast.LENGTH_SHORT).show()
                                    viewModel.refresh()
                                },
                                onFailure = { err ->
                                    Toast.makeText(context, ErrorHandler.getArabicMessage(err), Toast.LENGTH_LONG).show()
                                }
                            )
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AppColors.Primary)
                ) {
                    Text(
                        text = if (editingBanner == null) "إضافة البانر" else "حفظ التعديلات",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = Color.White)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("إدارة البانرات والشرائح", fontWeight = FontWeight.Bold, color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = AppStrings.back, tint = Color.White)
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.refresh() }) {
                        Icon(Icons.Default.Refresh, contentDescription = AppStrings.refresh, tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = AppColors.Primary)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { openForm() },
                containerColor = AppColors.Primary,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "إضافة بانر")
            }
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
            if (uiState.isLoading && uiState.banners.isEmpty()) {
                LoadingIndicator(message = "جاري تحميل البانرات...")
            } else if (uiState.errorMessage != null) {
                ErrorDisplay(message = uiState.errorMessage!!, onRetry = { viewModel.loadData() })
            } else if (uiState.banners.isEmpty()) {
                EmptyState(
                    title = "لا توجد بانرات",
                    message = "أضف بانرات نصية أو صورية لتظهر في الصفحة الرئيسية للتطبيق",
                    icon = Icons.Default.PhotoLibrary,
                    actionButtonText = "إضافة بانر الآن",
                    onActionClick = { openForm() }
                )
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = AppColors.InfoContainer),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Info, contentDescription = null, tint = AppColors.Info)
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = "تظهر البانرات النشطة في سلايدر الشاشة الرئيسية. يمكنك إعادة الترتيب وتغيير حالة الظهور.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = AppColors.Info
                                )
                            }
                        }
                    }

                    itemsIndexed(uiState.banners, key = { _, b -> b.id }) { index, banner ->
                        BannerAdminCard(
                            banner = banner,
                            canMoveUp = index > 0,
                            canMoveDown = index < uiState.banners.size - 1,
                            onMoveUp = {
                                val list = uiState.banners.toMutableList()
                                val temp = list[index]
                                list[index] = list[index - 1]
                                list[index - 1] = temp
                                val ids = list.map { it.id }
                                scope.launch {
                                    bannerRepository.reorderBanners(ids)
                                    viewModel.refresh()
                                }
                            },
                            onMoveDown = {
                                val list = uiState.banners.toMutableList()
                                val temp = list[index]
                                list[index] = list[index + 1]
                                list[index + 1] = temp
                                val ids = list.map { it.id }
                                scope.launch {
                                    bannerRepository.reorderBanners(ids)
                                    viewModel.refresh()
                                }
                            },
                            onToggleStatus = {
                                viewModel.toggleBannerStatus(banner) {
                                    Toast.makeText(context, "تم تحديث حالة البانر", Toast.LENGTH_SHORT).show()
                                }
                            },
                            onEdit = { openForm(banner) },
                            onDelete = {
                                viewModel.deleteBanner(banner.id) {
                                    Toast.makeText(context, "تم حذف البانر", Toast.LENGTH_SHORT).show()
                                }
                            }
                        )
                    }

                    item {
                        Spacer(modifier = Modifier.height(80.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun BannerAdminCard(
    banner: Banner,
    canMoveUp: Boolean,
    canMoveDown: Boolean,
    onMoveUp: () -> Unit,
    onMoveDown: () -> Unit,
    onToggleStatus: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            if (banner.imageUrl.isNotEmpty()) {
                AsyncImage(
                    model = banner.imageUrl,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .clip(RoundedCornerShape(10.dp)),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.height(10.dp))
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = banner.title,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    if (!banner.subtitle.isNullOrEmpty()) {
                        Text(
                            text = banner.subtitle!!,
                            style = MaterialTheme.typography.bodySmall,
                            color = AppColors.TextSecondary
                        )
                    }
                }

                Switch(
                    checked = banner.isActive,
                    onCheckedChange = { onToggleStatus() },
                    colors = SwitchDefaults.colors(checkedThumbColor = AppColors.Success)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row {
                    IconButton(onClick = onMoveUp, enabled = canMoveUp, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.ArrowUpward, contentDescription = "للأعلى", tint = if (canMoveUp) AppColors.Primary else AppColors.TextHint)
                    }
                    IconButton(onClick = onMoveDown, enabled = canMoveDown, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.ArrowDownward, contentDescription = "للأسفل", tint = if (canMoveDown) AppColors.Primary else AppColors.TextHint)
                    }
                }

                Row {
                    TextButton(onClick = onEdit) {
                        Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("تعديل")
                    }

                    TextButton(
                        onClick = onDelete,
                        colors = ButtonDefaults.textButtonColors(contentColor = AppColors.Error)
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("حذف")
                    }
                }
            }
        }
    }
}
