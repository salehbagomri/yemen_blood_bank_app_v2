package com.bagomri.yemenbloodbank.ui.screens.admin

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bagomri.yemenbloodbank.core.constants.AppColors
import com.bagomri.yemenbloodbank.core.constants.AppStrings
import com.bagomri.yemenbloodbank.core.util.ErrorHandler
import com.bagomri.yemenbloodbank.data.model.Hospital
import com.bagomri.yemenbloodbank.data.repository.HospitalRepository
import com.bagomri.yemenbloodbank.data.repository.LocationRepository
import com.bagomri.yemenbloodbank.ui.components.CustomDropdown
import com.bagomri.yemenbloodbank.ui.components.CustomTextField
import com.bagomri.yemenbloodbank.ui.components.LoadingIndicator
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminAddHospitalScreen(
    onNavigateBack: () -> Unit,
    hospitalRepository: HospitalRepository = HospitalRepository(),
    locationRepository: LocationRepository = LocationRepository()
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var isSaving by remember { mutableStateOf(false) }
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var governorate by remember { mutableStateOf("") }
    var subDistrict by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var isActive by remember { mutableStateOf(true) }

    var showSuccessDialog by remember { mutableStateOf(false) }
    var createdHospitalInfo by remember { mutableStateOf<Hospital?>(null) }
    var createdPassword by remember { mutableStateOf("") }

    var governorates by remember { mutableStateOf<List<String>>(emptyList()) }
    var districtsByGov by remember { mutableStateOf<Map<String, List<String>>>(emptyMap()) }

    LaunchedEffect(Unit) {
        val locations = locationRepository.getActiveLocations()
        governorates = locations.governorates
        districtsByGov = locations.districtsByGov
        if (governorates.isNotEmpty() && governorate.isEmpty()) {
            governorate = governorates.first()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("إضافة مستشفى جديد", fontWeight = FontWeight.Bold, color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = AppStrings.back, tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = AppColors.Primary)
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
        ) {
            if (isSaving) {
                LoadingIndicator(message = "جاري إنشاء حساب المستشفى واعتماده...")
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    CustomTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = "اسم المستشفى / المركز الطبي *",
                        placeholder = "مثال: مستشفى الثورة العام",
                        leadingIcon = { Icon(Icons.Default.LocalHospital, contentDescription = null, tint = AppColors.Primary) }
                    )

                    CustomTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = "${AppStrings.email} *",
                        placeholder = "hospital@example.com",
                        leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = AppColors.Primary) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                    )

                    CustomTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = "${AppStrings.password} (6 خانات على الأقل) *",
                        placeholder = "••••••••",
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = AppColors.Primary) },
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
                    )

                    CustomDropdown(
                        selectedValue = governorate.ifEmpty { null },
                        items = governorates,
                        onItemSelected = {
                            governorate = it
                            subDistrict = ""
                        },
                        label = "المحافظة *",
                        placeholder = "اختر محافظة المستشفى",
                        leadingIcon = { Icon(Icons.Default.Map, contentDescription = null, tint = AppColors.Primary) }
                    )

                    val currentDistricts = districtsByGov[governorate] ?: emptyList()
                    if (currentDistricts.isNotEmpty()) {
                        CustomDropdown(
                            selectedValue = subDistrict.ifEmpty { null },
                            items = currentDistricts,
                            onItemSelected = { subDistrict = it },
                            label = "المديرية (اختياري)",
                            placeholder = "اختر المديرية التابع لها المستشفى",
                            leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null, tint = AppColors.Primary) }
                        )
                    }

                    CustomTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        label = "رقم هاتف الطوارئ / الاستقبال",
                        placeholder = "01234567 أو 777123456",
                        leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = AppColors.Primary) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
                    )

                    CustomTextField(
                        value = address,
                        onValueChange = { address = it },
                        label = "العنوان بالتفصيل",
                        placeholder = "الشارع - الحي - معلم قريب",
                        leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null, tint = AppColors.Primary) }
                    )

                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (isActive) "حساب المستشفى مفعل" else "حساب المستشفى معطل",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                color = if (isActive) AppColors.Success else AppColors.Error
                            )
                            Switch(
                                checked = isActive,
                                onCheckedChange = { isActive = it },
                                thumbContent = if (isActive) {
                                    {
                                        Icon(
                                            imageVector = Icons.Filled.Check,
                                            contentDescription = null,
                                            modifier = Modifier.size(androidx.compose.material3.SwitchDefaults.IconSize),
                                            tint = AppColors.Success
                                        )
                                    }
                                } else null,
                                colors = androidx.compose.material3.SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = AppColors.Success,
                                    checkedBorderColor = Color.Transparent,
                                    uncheckedThumbColor = Color.White,
                                    uncheckedTrackColor = Color(0xFFE2E8F0),
                                    uncheckedBorderColor = Color(0xFFCBD5E1)
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Button(
                        onClick = {
                            if (name.isBlank() || email.isBlank() || password.isBlank() || governorate.isBlank()) {
                                Toast.makeText(context, "يرجى ملء جميع الحقول الإلزامية (بما فيها كلمة المرور)", Toast.LENGTH_SHORT).show()
                                return@Button
                            }

                            if (password.length < 6) {
                                Toast.makeText(context, "كلمة المرور يجب أن لا تقل عن 6 أحرف أو أرقام", Toast.LENGTH_SHORT).show()
                                return@Button
                            }

                            isSaving = true
                            scope.launch {
                                val fullDistrict = if (subDistrict.isNotBlank()) "$governorate - $subDistrict" else governorate
                                val hospital = Hospital(
                                    name = name.trim(),
                                    email = email.trim(),
                                    district = fullDistrict,
                                    rawGovernorate = governorate,
                                    phoneNumber = phone.trim().ifEmpty { null },
                                    address = address.trim().ifEmpty { null },
                                    isActive = isActive
                                )
                                val res = hospitalRepository.addHospital(hospital, password)
                                isSaving = false
                                res.fold(
                                    onSuccess = { h ->
                                        createdHospitalInfo = h
                                        createdPassword = password
                                        showSuccessDialog = true
                                    },
                                    onFailure = { err ->
                                        Log.e("AdminAddHospital", "Error creating hospital account", err)
                                        Toast.makeText(context, ErrorHandler.getArabicMessage(err), Toast.LENGTH_LONG).show()
                                    }
                                )
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = AppColors.Success)
                    ) {
                        Text(
                            text = "إضافة واعتماد المستشفى",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = Color.White)
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }

    // نافذة نجاح إنشاء الحساب وعرض بيانات الاعتماد للمدير
    if (showSuccessDialog && createdHospitalInfo != null) {
        val hName = createdHospitalInfo!!.name
        val hEmail = createdHospitalInfo!!.email
        val hPass = createdPassword
        val credentialsText = "بيانات دخول لوحة تحكم بنك دم اليمن للمستشفى:\nاسم المستشفى: $hName\nالبريد الإلكتروني: $hEmail\nكلمة المرور: $hPass"

        AlertDialog(
            onDismissRequest = { /* يمنع الإغلاق بالضغط خارج النافذة لضمان حفظ البيانات */ },
            icon = {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = AppColors.Success,
                    modifier = Modifier.size(46.dp)
                )
            },
            title = {
                Text(
                    text = "تم إنشاء الحساب بنجاح!",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "تم تسجيل مستشفى \"$hName\" وربط حسابه بقاعدة البيانات بنجاح.",
                        style = MaterialTheme.typography.bodyMedium
                    )

                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = "بيانات تسجيل الدخول للوحة التحكم:",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = AppColors.Primary
                                )
                            )
                            Text(
                                text = "البريد: $hEmail",
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold)
                            )
                            Text(
                                text = "كلمة المرور: $hPass",
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold)
                            )
                        }
                    }

                    Text(
                        text = "يمكنك نسخ هذه البيانات مباشرة وإرسالها لإدارة المستشفى للدخول إلى حسابهم وإدارة المتبرعين.",
                        style = MaterialTheme.typography.bodySmall,
                        color = AppColors.TextSecondary
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        val clip = ClipData.newPlainText("Hospital Credentials", credentialsText)
                        clipboard.setPrimaryClip(clip)
                        Toast.makeText(context, "تم نسخ بيانات الدخول إلى الحافظة بنجاح", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AppColors.Primary)
                ) {
                    Icon(imageVector = Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("نسخ البيانات")
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = {
                        showSuccessDialog = false
                        onNavigateBack()
                    }
                ) {
                    Text("العودة للقائمة")
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminEditHospitalScreen(
    hospitalId: String,
    onNavigateBack: () -> Unit,
    hospitalRepository: HospitalRepository = HospitalRepository(),
    locationRepository: LocationRepository = LocationRepository()
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var isLoading by remember { mutableStateOf(true) }
    var isSaving by remember { mutableStateOf(false) }

    var existingHospital by remember { mutableStateOf<Hospital?>(null) }
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var governorate by remember { mutableStateOf("") }
    var subDistrict by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var isActive by remember { mutableStateOf(true) }

    var governorates by remember { mutableStateOf<List<String>>(emptyList()) }
    var districtsByGov by remember { mutableStateOf<Map<String, List<String>>>(emptyMap()) }

    LaunchedEffect(hospitalId) {
        val locations = locationRepository.getActiveLocations()
        governorates = locations.governorates
        districtsByGov = locations.districtsByGov

        val result = hospitalRepository.getHospitalById(hospitalId)
        result.fold(
            onSuccess = { h ->
                if (h != null) {
                    existingHospital = h
                    name = h.name
                    email = h.email
                    governorate = h.governorate
                    subDistrict = if (h.district.contains(" - ")) h.district.substringAfter(" - ") else ""
                    phone = h.phoneNumber ?: ""
                    address = h.address ?: ""
                    isActive = h.isActive
                }
                isLoading = false
            },
            onFailure = {
                isLoading = false
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("تعديل بيانات المستشفى", fontWeight = FontWeight.Bold, color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = AppStrings.back, tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = AppColors.Primary)
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
        ) {
            if (isLoading || isSaving) {
                LoadingIndicator(message = if (isSaving) "جاري حفظ التعديلات..." else "جاري تحميل بيانات المستشفى...")
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    CustomTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = "اسم المستشفى / المركز الطبي",
                        leadingIcon = { Icon(Icons.Default.LocalHospital, contentDescription = null, tint = AppColors.Primary) }
                    )

                    CustomTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = AppStrings.email,
                        leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = AppColors.Primary) }
                    )

                    CustomDropdown(
                        selectedValue = governorate.ifEmpty { null },
                        items = governorates,
                        onItemSelected = {
                            governorate = it
                            subDistrict = ""
                        },
                        label = "المحافظة",
                        leadingIcon = { Icon(Icons.Default.Map, contentDescription = null, tint = AppColors.Primary) }
                    )

                    val currentDistricts = districtsByGov[governorate] ?: emptyList()
                    if (currentDistricts.isNotEmpty()) {
                        CustomDropdown(
                            selectedValue = subDistrict.ifEmpty { null },
                            items = currentDistricts,
                            onItemSelected = { subDistrict = it },
                            label = "المديرية (اختياري)",
                            leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null, tint = AppColors.Primary) }
                        )
                    }

                    CustomTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        label = "رقم هاتف الطوارئ / الاستقبال",
                        leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = AppColors.Primary) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
                    )

                    CustomTextField(
                        value = address,
                        onValueChange = { address = it },
                        label = "العنوان بالتفصيل",
                        leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null, tint = AppColors.Primary) }
                    )

                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (isActive) "حساب المستشفى مفعل" else "حساب المستشفى معطل",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                color = if (isActive) AppColors.Success else AppColors.Error
                            )
                            Switch(
                                checked = isActive,
                                onCheckedChange = { isActive = it },
                                thumbContent = if (isActive) {
                                    {
                                        Icon(
                                            imageVector = Icons.Filled.Check,
                                            contentDescription = null,
                                            modifier = Modifier.size(androidx.compose.material3.SwitchDefaults.IconSize),
                                            tint = AppColors.Success
                                        )
                                    }
                                } else null,
                                colors = androidx.compose.material3.SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = AppColors.Success,
                                    checkedBorderColor = Color.Transparent,
                                    uncheckedThumbColor = Color.White,
                                    uncheckedTrackColor = Color(0xFFE2E8F0),
                                    uncheckedBorderColor = Color(0xFFCBD5E1)
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Button(
                        onClick = {
                            if (existingHospital != null) {
                                val fullDistrict = if (subDistrict.isNotBlank()) "$governorate - $subDistrict" else governorate
                                val updated = existingHospital!!.copy(
                                    name = name.trim(),
                                    email = email.trim(),
                                    district = fullDistrict,
                                    rawGovernorate = governorate,
                                    phoneNumber = phone.trim().ifEmpty { null },
                                    address = address.trim().ifEmpty { null },
                                    isActive = isActive
                                )

                                isSaving = true
                                scope.launch {
                                    val res = hospitalRepository.updateHospital(updated)
                                    isSaving = false
                                    res.fold(
                                        onSuccess = {
                                            Toast.makeText(context, "تم حفظ التعديلات بنجاح", Toast.LENGTH_SHORT).show()
                                            onNavigateBack()
                                        },
                                        onFailure = { err ->
                                            Log.e("AdminEditHospital", "Error updating hospital", err)
                                            Toast.makeText(context, ErrorHandler.getArabicMessage(err), Toast.LENGTH_LONG).show()
                                        }
                                    )
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = AppColors.Primary)
                    ) {
                        Text(
                            text = AppStrings.save,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = Color.White)
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}
