package com.bagomri.yemenbloodbank.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bagomri.yemenbloodbank.core.constants.AppColors

@Composable
fun BloodTypeBadge(
    bloodType: String,
    modifier: Modifier = Modifier,
    size: Int = 44
) {
    val bg = AppColors.forBloodType(bloodType)
    Surface(
        modifier = modifier.size(size.dp),
        shape = RoundedCornerShape(12.dp),
        color = bg,
        shadowElevation = 2.dp
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = bloodType,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = if (size > 40) 16.sp else 13.sp
                ),
                color = Color.White
            )
        }
    }
}

@Composable
fun BloodTypeSelectorChip(
    bloodType: String,
    isSelected: Boolean,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val typeColor = AppColors.forBloodType(bloodType)
    val bgColor by animateColorAsState(
        targetValue = if (isSelected) typeColor else Color.Transparent,
        label = "chipBg"
    )
    val textColor by animateColorAsState(
        targetValue = if (isSelected) Color.White else typeColor,
        label = "chipText"
    )

    Surface(
        modifier = modifier
            .padding(4.dp)
            .clickable { onSelect(bloodType) },
        shape = RoundedCornerShape(10.dp),
        color = bgColor,
        border = BorderStroke(
            width = if (isSelected) 0.dp else 1.5.dp,
            color = typeColor
        ),
        shadowElevation = if (isSelected) 3.dp else 0.dp
    ) {
        Box(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = bloodType,
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                color = textColor
            )
        }
    }
}
