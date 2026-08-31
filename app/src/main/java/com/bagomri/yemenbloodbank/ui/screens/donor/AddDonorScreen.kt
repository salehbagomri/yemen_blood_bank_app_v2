package com.bagomri.yemenbloodbank.ui.screens.donor

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Bloodtype
import androidx.compose.material.icons.filled.Cake
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Note
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.PhoneIphone
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bagomri.yemenbloodbank.core.constants.AppColors
import com.bagomri.yemenbloodbank.core.constants.AppStrings
import com.bagomri.yemenbloodbank.ui.components.CustomDropdown
import com.bagomri.yemenbloodbank.ui.components.CustomTextField
import com.bagomri.yemenbloodbank.ui.components.LoadingIndicator

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddDonorScreen(
    onNavigateBack: () -> Unit,
    viewModel: AddDonorViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    var showSuccessDialog by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            showSuccessDialog = true
        }
    }

    if (showSuccessDialog) {
        AlertDialog(
            onDismissRequest = {
                showSuccessDialog = false
                onNavigateBack()
            },
            icon = {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = AppColors.Success,
                    modifier = Modifier.size(48.dp)
                )
            },
            title = {
                Text(
                    text = "تمت الإضافة بنجاح",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium
                )
            },
            text = {
                Text(
                    text = AppStrings.donorAddedSuccessfully + "، وأصبح متاحاً للبحث فوراً في بنك الدم.",
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showSuccessDialog = false
                        onNavigateBack()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AppColors.Primary)
                ) {
                    Text("حسناً")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = AppStrings.addDonor,
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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
        ) {
            if (uiState.isLoading) {
                LoadingIndicator(message = "جاري إضافة المتبرع...")
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // بطاقة معلومات توضيحية
                    Card(
                        colors = CardDefaults.cardColors(containerColor = AppColors.InfoContainer),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = null,
                                tint = AppColors.Info,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "املأ جميع البيانات المطلوبة. سيظهر المتبرع فوراً في نتائج البحث لجميع طالبي الدم.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = AppColors.Info
                            )
                        }
                    }

                    // 1. الاسم
                    CustomTextField(
                        value = uiState.name,
                        onValueChange = {
                            viewModel.onNameChange(it)
                            viewModel.clearError()
                        },
                        label = AppStrings.donorName,
                        placeholder = "أدخل الاسم الكامل للمتبرع",
                        leadingIcon = {
                            Icon(Icons.Default.Person, contentDescription = null, tint = AppColors.Primary)
                        }
                    )

                    // 2. رقم الهاتف الرئيسي
                    CustomTextField(
                        value = uiState.phoneNumber,
                        onValueChange = {
                            viewModel.onPhoneChange(it)
                            viewModel.clearError()
                        },
                        label = "${AppStrings.phoneNumber} (رئيسي)",
                        placeholder = "777123456",
                        helperText = "أدخل 9 أرقام تبدأ بـ 7 (مثال: 771234567)",
                        leadingIcon = {
                            Icon(Icons.Default.Phone, contentDescription = null, tint = AppColors.Primary)
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
                    )

                    // 3. أرقام إضافية اختيارية
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        CustomTextField(
                            value = uiState.phoneNumber2,
                            onValueChange = { viewModel.onPhone2Change(it) },
                            label = "رقم إضافي 1 (اختياري)",
                            placeholder = "712345678",
                            leadingIcon = {
                                Icon(Icons.Default.PhoneAndroid, contentDescription = null, tint = AppColors.TextSecondary)
                            },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            modifier = Modifier.weight(1f)
                        )

                        CustomTextField(
                            value = uiState.phoneNumber3,
                            onValueChange = { viewModel.onPhone3Change(it) },
                            label = "رقم إضافي 2 (اختياري)",
                            placeholder = "732345678",
                            leadingIcon = {
                                Icon(Icons.Default.PhoneIphone, contentDescription = null, tint = AppColors.TextSecondary)
                            },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            modifier = Modifier.weight(1f)
                        )
                    }

                    // 4. فصيلة الدم
                    CustomDropdown(
                        selectedValue = uiState.bloodType.ifEmpty { null },
                        items = AppStrings.bloodTypes,
                        onItemSelected = { viewModel.onBloodTypeChange(it) },
                        label = AppStrings.bloodType,
                        placeholder = AppStrings.selectBloodType,
                        leadingIcon = {
                            Icon(Icons.Default.Bloodtype, contentDescription = null, tint = AppColors.Primary)
                        }
                    )

                    // 5. المحافظة
                    CustomDropdown(
                        selectedValue = uiState.governorate.ifEmpty { null },
                        items = uiState.locationData.governorates,
                        onItemSelected = { viewModel.onGovernorateChange(it) },
                        label = if (uiState.isGovernorateLocked) "المحافظة (محافظة مستشفاك)" else AppStrings.district,
                        placeholder = "اختر المحافظة",
                        enabled = !uiState.isGovernorateLocked,
                        leadingIcon = {
                            Icon(Icons.Default.Map, contentDescription = null, tint = AppColors.Primary)
                        }
                    )

                    // 6. المديرية
                    CustomDropdown(
                        selectedValue = uiState.subDistrict.ifEmpty { null },
                        items = uiState.subDistricts,
                        onItemSelected = { viewModel.onSubDistrictChange(it) },
                        label = AppStrings.subDistrict,
                        placeholder = if (uiState.governorate.isEmpty()) "اختر المحافظة أولاً" else "اختر المديرية",
                        enabled = uiState.subDistricts.isNotEmpty(),
                        leadingIcon = {
                            Icon(Icons.Default.LocationOn, contentDescription = null, tint = AppColors.Primary)
                        }
                    )

                    // 7. العمر والجنس
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        CustomTextField(
                            value = uiState.age,
                            onValueChange = { viewModel.onAgeChange(it) },
                            label = AppStrings.age,
                            placeholder = "25",
                            leadingIcon = {
                                Icon(Icons.Default.Cake, contentDescription = null, tint = AppColors.Primary)
                            },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f)
                        )

                        CustomDropdown(
                            selectedValue = uiState.gender,
                            items = listOf("ذكر", "أنثى"),
                            onItemSelected = { viewModel.onGenderChange(it) },
                            label = AppStrings.gender,
                            leadingIcon = {
                                Icon(Icons.Default.People, contentDescription = null, tint = AppColors.Primary)
                            },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    // 8. ملاحظات اختيارية
                    CustomTextField(
                        value = uiState.notes,
                        onValueChange = { viewModel.onNotesChange(it) },
                        label = "${AppStrings.notes} (${AppStrings.optional})",
                        placeholder = "أي ملاحظات صحية أو تفاصيل إضافية...",
                        singleLine = false,
                        maxLines = 3,
                        leadingIcon = {
                            Icon(Icons.Default.Note, contentDescription = null, tint = AppColors.TextSecondary)
                        }
                    )

                    // رسالة الخطأ
                    if (!uiState.errorMessage.isNullOrEmpty()) {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = AppColors.ErrorContainer),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.ErrorOutline, contentDescription = null, tint = AppColors.Error)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = uiState.errorMessage!!,
                                    color = AppColors.Error,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // زر الإرسال
                    Button(
                        onClick = { viewModel.submitDonor() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = AppColors.Success)
                    ) {
                        Text(
                            text = AppStrings.save,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}
