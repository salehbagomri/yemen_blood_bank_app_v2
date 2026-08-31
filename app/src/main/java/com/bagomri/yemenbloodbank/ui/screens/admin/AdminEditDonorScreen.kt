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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Bloodtype
import androidx.compose.material.icons.filled.Cake
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
        governorates = locations.governorates

        val result = donorRepository.getDonorById(donorId)
        result.fold(
            onSuccess = { d ->
                donor = d
                name = d.name
                phone = d.phoneNumber
                phone2 = d.phoneNumber2 ?: ""
                phone3 = d.phoneNumber3 ?: ""
                bloodType = d.bloodType
                governorate = d.governorate
                subDistricts = locations.districtsByGov[d.governorate] ?: emptyList()
                subDistrict = d.subDistrict
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
                        onValueChange = { phone = it },
                        label = AppStrings.phoneNumber,
                        leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = AppColors.Primary) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        CustomTextField(
                            value = phone2,
                            onValueChange = { phone2 = it },
                            label = "هاتف 2",
                            modifier = Modifier.weight(1f)
                        )
                        CustomTextField(
                            value = phone3,
                            onValueChange = { phone3 = it },
                            label = "هاتف 3",
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
                            onValueChange = { age = it },
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
                        onClick = {
                            if (donor != null) {
                                val combinedDistrict = "$governorate - $subDistrict"
                                val updatedDonor = donor!!.copy(
                                    name = name.trim(),
                                    phoneNumber = phone.trim(),
                                    phoneNumber2 = phone2.trim().ifEmpty { null },
                                    phoneNumber3 = phone3.trim().ifEmpty { null },
                                    bloodType = bloodType,
                                    district = combinedDistrict,
                                    rawGovernorate = governorate,
                                    age = age.toIntOrNull() ?: 18,
                                    gender = if (gender == "أنثى") "female" else "male",
                                    isActive = isActive,
                                    notes = notes.trim().ifEmpty { null }
                                )

                                isSaving = true
                                scope.launch {
                                    val res = donorRepository.updateDonor(updatedDonor)
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
