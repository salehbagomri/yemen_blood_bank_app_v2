package com.bagomri.yemenbloodbank.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bloodtype
import androidx.compose.material3.Icon
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
    size: Int = 46
) {
    val badgeColor = AppColors.getBloodTypeColor(bloodType)
    val containerColor = AppColors.getBloodTypeContainerColor(bloodType)

    Surface(
        modifier = modifier.size(size.dp),
        shape = RoundedCornerShape(12.dp),
        color = containerColor,
        border = BorderStroke(1.dp, badgeColor.copy(alpha = 0.25f)),
        shadowElevation = 0.dp
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = bloodType,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = if (size > 40) 17.sp else 13.sp,
                    letterSpacing = 0.5.sp
                ),
                color = badgeColor
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
    val typeColor = AppColors.getBloodTypeColor(bloodType)
    val containerColor = AppColors.getBloodTypeContainerColor(bloodType)

    val bgColor by animateColorAsState(
        targetValue = if (isSelected) typeColor else containerColor.copy(alpha = 0.6f),
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
        shape = RoundedCornerShape(12.dp),
        color = bgColor,
        border = BorderStroke(
            width = if (isSelected) 0.dp else 1.dp,
            color = if (isSelected) Color.Transparent else typeColor.copy(alpha = 0.3f)
        ),
        shadowElevation = if (isSelected) 3.dp else 0.dp
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 9.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Bloodtype,
                contentDescription = null,
                tint = textColor,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = bloodType,
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                color = textColor
            )
        }
    }
}
