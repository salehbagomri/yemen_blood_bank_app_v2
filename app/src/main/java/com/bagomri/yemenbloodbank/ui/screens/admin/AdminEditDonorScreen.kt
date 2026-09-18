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
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bloodtype
import androidx.compose.material.icons.filled.Cake
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
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
import androidx.compose.ui.unit.dp
import com.bagomri.yemenbloodbank.core.constants.AppColors
import com.bagomri.yemenbloodbank.core.constants.AppStrings
import com.bagomri.yemenbloodbank.core.util.ErrorHandler
import com.bagomri.yemenbloodbank.data.model.Donor
import com.bagomri.yemenbloodbank.data.repository.DonorRepository
import com.bagomri.yemenbloodbank.data.repository.LocationRepository
import com.bagomri.yemenbloodbank.ui.components.CustomDropdown
import com.bagomri.yemenbloodbank.ui.components.CustomTextField
import com.bagomri.yemenbloodbank.ui.components.LoadingIndicator
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminEditDonorScreen(
    donorId: String,
    onNavigateBack: () -> Unit,
    donorRepository: DonorRepository = DonorRepository(),
    locationRepository: LocationRepository = LocationRepository()
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var isLoading by remember { mutableStateOf(true) }
    var isSaving by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var showConfirmDialog by remember { mutableStateOf(false) }

    var donor by remember { mutableStateOf<Donor?>(null) }
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var phone2 by remember { mutableStateOf("") }
    var phone3 by remember { mutableStateOf("") }
    var bloodType by remember { mutableStateOf("") }
    var governorate by remember { mutableStateOf("") }
    var subDistrict by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("ذكر") }
    var isActive by remember { mutableStateOf(true) }
    var notes by remember { mutableStateOf("") }

    var governorates by remember { mutableStateOf<List<String>>(emptyList()) }
    var subDistricts by remember { mutableStateOf<List<String>>(emptyList()) }

    LaunchedEffect(donorId) {
        val locations = locationRepository.getActiveLocations()
        val result = donorRepository.getDonorById(donorId)
        result.fold(
            onSuccess = { d ->
                donor = d
                name = d.name
                phone = d.phoneNumber.filter { it.isDigit() }.takeLast(9)
                phone2 = d.phoneNumber2?.filter { it.isDigit() }?.takeLast(9) ?: ""
                phone3 = d.phoneNumber3?.filter { it.isDigit() }?.takeLast(9) ?: ""
                bloodType = d.bloodType
                governorate = d.governorate

                governorates = if (d.governorate.isNotBlank() && !locations.governorates.contains(d.governorate)) {
                    listOf(d.governorate) + locations.governorates
                } else {
                    locations.governorates
                }

                val districtList = locations.districtsByGov[d.governorate] ?: emptyList()
                val parts = d.district.split(" - ")
                val currentSub = if (parts.size > 1) parts[1] else ""
                subDistrict = currentSub
                subDistricts = if (currentSub.isNotBlank() && !districtList.contains(currentSub)) {
                    listOf(currentSub) + districtList
                } else {
                    districtList
                }

                age = d.age.toString()
                gender = if (d.gender == "female") "أنثى" else "ذكر"
                isActive = d.isActive
                notes = d.notes ?: ""
                isLoading = false
            },
            onFailure = { error ->
                errorMessage = ErrorHandler.getArabicMessage(error)
                isLoading = false
            }
        )
    }

    val validateAndTriggerSave = {
        when {
            name.isBlank() -> {
                Toast.makeText(context, "يرجى إدخال اسم المتبرع", Toast.LENGTH_SHORT).show()
            }
            phone.length != 9 -> {
                Toast.makeText(context, "يرجى إدخال رقم هاتف صحيح (9 أرقام)", Toast.LENGTH_SHORT).show()
            }
            bloodType.isBlank() -> {
                Toast.makeText(context, "يرجى اختيار فصيلة الدم", Toast.LENGTH_SHORT).show()
            }
            governorate.isBlank() -> {
                Toast.makeText(context, "يرجى اختيار المحافظة", Toast.LENGTH_SHORT).show()
            }
            else -> {
                showConfirmDialog = true
            }
        }
    }

    val executeSave = {
        if (donor != null) {
            val combinedDistrict = if (subDistrict.isNotBlank()) "$governorate - $subDistrict" else governorate
            val updatedDonor = donor!!.copy(
                name = name.trim(),
                phoneNumber = phone.trim(),
                phoneNumber2 = phone2.trim().ifBlank { null },
                phoneNumber3 = phone3.trim().ifBlank { null },
                bloodType = bloodType,
                district = combinedDistrict,
                rawGovernorate = governorate,
                age = age.toIntOrNull() ?: 18,
                gender = if (gender == "أنثى") "female" else "male",
                isActive = isActive,
                notes = notes.trim().ifBlank { null }
            )

            isSaving = true
            scope.launch {
                val res = donorRepository.updateDonor(updatedDonor)
                isSaving = false
                res.fold(
                    onSuccess = {
                        Toast.makeText(context, "تم تحديث بيانات المتبرع بنجاح", Toast.LENGTH_SHORT).show()
                        onNavigateBack()
                    },
                    onFailure = { err ->
                        Toast.makeText(context, "خطأ: ${ErrorHandler.getArabicMessage(err)}", Toast.LENGTH_LONG).show()
                    }
                )
            }
        }
    }

    if (showConfirmDialog) {
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { showConfirmDialog = false },
            title = { Text("تأكيد التعديل", fontWeight = FontWeight.Bold) },
            text = { Text("هل تريد حفظ التعديلات على بيانات ${donor?.name}؟") },
            confirmButton = {
                Button(
                    onClick = {
                        showConfirmDialog = false
                        executeSave()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AppColors.Success)
                ) {
                    Text("حفظ")
                }
            },
            dismissButton = {
                androidx.compose.material3.TextButton(onClick = { showConfirmDialog = false }) {
                    Text(AppStrings.cancel)
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "تعديل بيانات المتبرع",
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
                actions = {
                    if (!isLoading && !isSaving) {
                        IconButton(onClick = { validateAndTriggerSave() }) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "حفظ التعديلات",
                                tint = Color.White
                            )
                        }
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
            if (isLoading || isSaving) {
                LoadingIndicator(message = if (isSaving) "جاري حفظ التعديلات..." else "جاري تحميل بيانات المتبرع...")
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
                        label = AppStrings.donorName,
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = AppColors.Primary) }
                    )

                    CustomTextField(
                        value = phone,
                        onValueChange = { phone = it.filter { c -> c.isDigit() }.take(9) },
                        label = "${AppStrings.phoneNumber} (رئيسي - 9 أرقام)",
                        leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = AppColors.Primary) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        CustomTextField(
                            value = phone2,
                            onValueChange = { phone2 = it.filter { c -> c.isDigit() }.take(9) },
                            label = "هاتف 2 (اختياري)",
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            modifier = Modifier.weight(1f)
                        )
                        CustomTextField(
                            value = phone3,
                            onValueChange = { phone3 = it.filter { c -> c.isDigit() }.take(9) },
                            label = "هاتف 3 (اختياري)",
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            modifier = Modifier.weight(1f)
                        )
                    }

                    CustomDropdown(
                        selectedValue = bloodType.ifEmpty { null },
                        items = AppStrings.bloodTypes,
                        onItemSelected = { bloodType = it },
                        label = AppStrings.bloodType,
                        leadingIcon = { Icon(Icons.Default.Bloodtype, contentDescription = null, tint = AppColors.Primary) }
                    )

                    CustomDropdown(
                        selectedValue = governorate.ifEmpty { null },
                        items = governorates,
                        onItemSelected = { gov ->
                            governorate = gov
                            scope.launch {
                                val locs = locationRepository.getActiveLocations()
                                subDistricts = locs.districtsByGov[gov] ?: emptyList()
                                subDistrict = ""
                            }
                        },
                        label = "المحافظة",
                        leadingIcon = { Icon(Icons.Default.Map, contentDescription = null, tint = AppColors.Primary) }
                    )

                    CustomDropdown(
                        selectedValue = subDistrict.ifEmpty { null },
                        items = subDistricts,
                        onItemSelected = { subDistrict = it },
                        label = AppStrings.subDistrict,
                        leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null, tint = AppColors.Primary) }
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        CustomTextField(
                            value = age,
                            onValueChange = { age = it.filter { c -> c.isDigit() }.take(2) },
                            label = AppStrings.age,
                            leadingIcon = { Icon(Icons.Default.Cake, contentDescription = null, tint = AppColors.Primary) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f)
                        )

                        CustomDropdown(
                            selectedValue = gender,
                            items = listOf("ذكر", "أنثى"),
                            onItemSelected = { gender = it },
                            label = AppStrings.gender,
                            leadingIcon = { Icon(Icons.Default.People, contentDescription = null, tint = AppColors.Primary) },
                            modifier = Modifier.weight(1f)
                        )
                    }

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
                                text = if (isActive) "حساب المتبرع نشط" else "حساب المتبرع معطل",
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

                    CustomTextField(
                        value = notes,
                        onValueChange = { notes = it },
                        label = AppStrings.notes,
                        singleLine = false,
                        maxLines = 3
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Button(
                        onClick = { validateAndTriggerSave() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = AppColors.Success)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = Color.White
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "حفظ التعديلات",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        )
                    }

                    OutlinedButton(
                        onClick = onNavigateBack,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = AppColors.Error),
                        border = BorderStroke(1.dp, AppColors.Error.copy(alpha = 0.5f))
                    ) {
                        Text(
                            text = AppStrings.cancel,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}
