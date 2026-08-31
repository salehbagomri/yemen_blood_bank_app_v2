package com.bagomri.yemenbloodbank.core.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import com.bagomri.yemenbloodbank.core.constants.AppColors

private val LightColorScheme = lightColorScheme(
    primary = AppColors.Primary,
    onPrimary = AppColors.TextOnPrimary,
    primaryContainer = AppColors.PrimaryContainer,
    onPrimaryContainer = AppColors.OnPrimaryContainer,
    secondary = AppColors.Secondary,
    onSecondary = AppColors.TextOnPrimary,
    secondaryContainer = AppColors.SecondaryContainer,
    onSecondaryContainer = AppColors.Secondary,
    background = AppColors.Background,
    onBackground = AppColors.TextPrimary,
    surface = AppColors.Surface,
    onSurface = AppColors.TextPrimary,
    surfaceVariant = AppColors.SurfaceVariant,
    onSurfaceVariant = AppColors.TextSecondary,
    error = AppColors.Error,
    onError = AppColors.TextOnPrimary,
    errorContainer = AppColors.ErrorContainer,
    onErrorContainer = AppColors.Error,
    outline = AppColors.Border,
    outlineVariant = AppColors.Divider
)

private val DarkColorScheme = darkColorScheme(
    primary = AppColors.PrimaryLight,
    onPrimary = AppColors.DarkBackground,
    primaryContainer = AppColors.DarkPrimaryContainer,
    onPrimaryContainer = AppColors.DarkOnPrimaryContainer,
    secondary = AppColors.Accent,
    onSecondary = AppColors.DarkBackground,
    secondaryContainer = AppColors.Secondary,
    onSecondaryContainer = AppColors.Accent,
    background = AppColors.DarkBackground,
    onBackground = AppColors.Surface,
    surface = AppColors.DarkSurface,
    onSurface = AppColors.Surface,
    surfaceVariant = AppColors.DarkSurfaceVariant,
    onSurfaceVariant = AppColors.TextHint,
    error = AppColors.Error,
    onError = AppColors.TextOnPrimary,
    errorContainer = AppColors.DarkPrimaryContainer,
    onErrorContainer = AppColors.PrimaryLight,
    outline = AppColors.DarkSurfaceVariant,
    outlineVariant = AppColors.DarkSurfaceVariant
)

@Composable
fun YemenBloodBankTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = AppTypography,
            shapes = AppShapes,
            content = content
        )
    }
}
