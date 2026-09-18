package com.bagomri.yemenbloodbank.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bagomri.yemenbloodbank.core.constants.AppColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomDropdown(
    selectedValue: String?,
    items: List<String>,
    onItemSelected: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    placeholder: String = "اختر...",
    enabled: Boolean = true,
    isError: Boolean = false,
    errorMessage: String? = null,
    onClear: (() -> Unit)? = null,
    leadingIcon: @Composable (() -> Unit)? = null
) {
    var expanded by remember { mutableStateOf(false) }
    val rotation by animateFloatAsState(
        targetValue = if (expanded) 180f else 0f,
        label = "dropdownChevron"
    )

    Column(modifier = modifier.fillMaxWidth()) {
        ExposedDropdownMenuBox(
            expanded = expanded && enabled,
            onExpandedChange = { if (enabled) expanded = !expanded },
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = selectedValue ?: "",
                onValueChange = {},
                readOnly = true,
                label = {
                    Text(
                        text = label,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp
                    )
                },
                placeholder = {
                    Text(
                        text = placeholder,
                        color = AppColors.TextHint,
                        fontSize = 14.sp
                    )
                },
                trailingIcon = {
                    if (enabled) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (!selectedValue.isNullOrEmpty() && onClear != null) {
                                IconButton(
                                    onClick = onClear,
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Clear,
                                        contentDescription = "مسح",
                                        tint = AppColors.TextSecondary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                            Icon(
                                imageVector = Icons.Default.KeyboardArrowDown,
                                contentDescription = null,
                                tint = if (expanded) AppColors.Primary else AppColors.TextSecondary,
                                modifier = Modifier
                                    .size(22.dp)
                                    .rotate(rotation)
                                    .padding(end = 4.dp)
                            )
                        }
                    }
                },
                leadingIcon = leadingIcon,
                enabled = enabled,
                isError = isError,
                shape = RoundedCornerShape(14.dp),
                textStyle = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Medium,
                    fontSize = 15.sp,
                    color = if (enabled) AppColors.TextPrimary else AppColors.TextSecondary
                ),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = AppColors.SurfaceVariant.copy(alpha = 0.5f),
                    disabledContainerColor = AppColors.SurfaceVariant.copy(alpha = 0.25f),
                    focusedBorderColor = AppColors.Primary,
                    unfocusedBorderColor = AppColors.Border,
                    focusedLabelColor = AppColors.Primary,
                    unfocusedLabelColor = AppColors.TextSecondary,
                    disabledBorderColor = AppColors.Border.copy(alpha = 0.5f),
                    disabledLabelColor = AppColors.TextSecondary.copy(alpha = 0.6f),
                    disabledTextColor = AppColors.TextPrimary.copy(alpha = 0.6f)
                ),
                modifier = Modifier
                    .menuAnchor(androidx.compose.material3.ExposedDropdownMenuAnchorType.PrimaryNotEditable, enabled)
                    .fillMaxWidth()
            )

            ExposedDropdownMenu(
                expanded = expanded && enabled,
                onDismissRequest = { expanded = false },
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.surface)
                    .clip(RoundedCornerShape(14.dp))
            ) {
                items.forEach { item ->
                    val isItemSelected = item == selectedValue
                    DropdownMenuItem(
                        text = {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = item,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = if (isItemSelected) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isItemSelected) AppColors.Primary else AppColors.TextPrimary
                                    ),
                                    modifier = Modifier.weight(1f)
                                )
                                if (isItemSelected) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        tint = AppColors.Primary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        },
                        modifier = Modifier.background(
                            if (isItemSelected) AppColors.Primary.copy(alpha = 0.08f) else Color.Transparent
                        ),
                        onClick = {
                            onItemSelected(item)
                            expanded = false
                        }
                    )
                }
            }
        }

        if (isError && !errorMessage.isNullOrEmpty()) {
            Text(
                text = errorMessage,
                color = AppColors.Error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 12.dp, top = 4.dp)
            )
        }
    }
}
