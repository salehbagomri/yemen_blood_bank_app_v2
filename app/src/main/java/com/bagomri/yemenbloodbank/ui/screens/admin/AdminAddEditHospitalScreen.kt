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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.unit.dp
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
    var phone by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var isActive by remember { mutableStateOf(true) }

    var governorates by remember { mutableStateOf<List<String>>(emptyList()) }

    LaunchedEffect(Unit) {
        val locations = locationRepository.getActiveLocations()
        governorates = locations.governorates
        if (governorates.isNotEmpty()) {
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
                LoadingIndicator(message = "جاري إنشاء حساب المستشفى...")
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
                        placeholder = "مثال: مستشفى الثورة العام",
                        leadingIcon = { Icon(Icons.Default.LocalHospital, contentDescription = null, tint = AppColors.Primary) }
                    )

                    CustomTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = AppStrings.email,
                        placeholder = "hospital@example.com",
                        leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = AppColors.Primary) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                    )

                    CustomTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = AppStrings.password,
                        placeholder = "••••••••",
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = AppColors.Primary) },
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
                    )

                    CustomDropdown(
                        selectedValue = governorate.ifEmpty { null },
                        items = governorates,
                        onItemSelected = { governorate = it },
                        label = "المحافظة",
                        placeholder = "اختر محافظة المستشفى",
                        leadingIcon = { Icon(Icons.Default.Map, contentDescription = null, tint = AppColors.Primary) }
                    )

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
                                colors = SwitchDefaults.colors(checkedThumbColor = AppColors.Success)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Button(
                        onClick = {
                            if (name.isBlank() || email.isBlank() || governorate.isBlank()) {
                                Toast.makeText(context, "يرجى ملء جميع الحقول المطلوبة", Toast.LENGTH_SHORT).show()
                                return@Button
                            }

                            isSaving = true
                            scope.launch {
                                val hospital = Hospital(
                                    name = name.trim(),
                                    email = email.trim(),
                                    rawGovernorate = governorate,
                                    phoneNumber = phone.trim().ifEmpty { null },
                                    address = address.trim().ifEmpty { null },
                                    isActive = isActive
                                )
                                val res = hospitalRepository.addHospital(hospital)
                                isSaving = false
                                res.fold(
                                    onSuccess = {
                                        Toast.makeText(context, "تمت إضافة المستشفى بنجاح", Toast.LENGTH_SHORT).show()
                                        onNavigateBack()
                                    },
                                    onFailure = { err ->
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
                            text = "إضافة المستشفى",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = Color.White)
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
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
    var phone by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var isActive by remember { mutableStateOf(true) }

    var governorates by remember { mutableStateOf<List<String>>(emptyList()) }

    LaunchedEffect(hospitalId) {
        val locations = locationRepository.getActiveLocations()
        governorates = locations.governorates

        val result = hospitalRepository.getHospitalById(hospitalId)
        result.fold(
            onSuccess = { h ->
                if (h != null) {
                    existingHospital = h
                    name = h.name
                    email = h.email
                    governorate = h.governorate
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
                        onItemSelected = { governorate = it },
                        label = "المحافظة",
                        leadingIcon = { Icon(Icons.Default.Map, contentDescription = null, tint = AppColors.Primary) }
                    )

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
                                colors = SwitchDefaults.colors(checkedThumbColor = AppColors.Success)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Button(
                        onClick = {
                            if (existingHospital != null) {
                                val updated = existingHospital!!.copy(
                                    name = name.trim(),
                                    email = email.trim(),
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
