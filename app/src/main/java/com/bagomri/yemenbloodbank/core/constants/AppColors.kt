package com.bagomri.yemenbloodbank.core.constants

import androidx.compose.ui.graphics.Color

/**
 * ألوان التطبيق الأساسية
 * التصميم يعتمد على الأحمر الطبي المتطور + الأبيض مع دعم الوضع الليلي
 */
object AppColors {
    // اللون الأساسي - الأحمر الطبي
    val Primary = Color(0xFFE63946)
    val PrimaryDark = Color(0xFFB8262F)
    val PrimaryLight = Color(0xFFFF6B77)
    val PrimaryContainer = Color(0xFFFFEBEE)
    val OnPrimaryContainer = Color(0xFF680008)

    // ألوان ثانوية ولهجات
    val Secondary = Color(0xFF1D3557)
    val SecondaryLight = Color(0xFF457B9D)
    val SecondaryContainer = Color(0xFFD9E2EC)
    val Accent = Color(0xFFA8DADC)

    // الخلفية والبطاقات
    val Background = Color(0xFFF8F9FA)
    val Surface = Color(0xFFFFFFFF)
    val SurfaceVariant = Color(0xFFF1F3F5)
    val CardBackground = Color(0xFFFFFFFF)

    // الوضع الليلي
    val DarkBackground = Color(0xFF121212)
    val DarkSurface = Color(0xFF1E1E1E)
    val DarkSurfaceVariant = Color(0xFF2C2C2C)
    val DarkPrimaryContainer = Color(0xFF4A1015)
    val DarkOnPrimaryContainer = Color(0xFFFFD1D6)

    // النصوص
    val TextPrimary = Color(0xFF212529)
    val TextSecondary = Color(0xFF6C757D)
    val TextHint = Color(0xFFADB5BD)
    val TextOnPrimary = Color(0xFFFFFFFF)

    // الحالات
    val Success = Color(0xFF28A745)
    val SuccessContainer = Color(0xFFE8F5E9)
    val Warning = Color(0xFFFFC107)
    val WarningContainer = Color(0xFFFFF8E1)
    val Error = Color(0xFFDC3545)
    val ErrorContainer = Color(0xFFFFEBEE)
    val Info = Color(0xFF17A2B8)
    val InfoContainer = Color(0xFFE0F7FA)

    // الحدود والفواصل
    val Border = Color(0xFFDEE2E6)
    val Divider = Color(0xFFE9ECEF)

    // فصائل الدم - ألوان مميزة
    val BloodTypeA = Color(0xFFE63946)
    val BloodTypeB = Color(0xFF1D3557)
    val BloodTypeAB = Color(0xFFAB47BC)
    val BloodTypeO = Color(0xFF2E7D32)

    fun forBloodType(bloodType: String): Color {
        return when {
            bloodType.startsWith("A") && !bloodType.startsWith("AB") -> BloodTypeA
            bloodType.startsWith("B") -> BloodTypeB
            bloodType.startsWith("AB") -> BloodTypeAB
            bloodType.startsWith("O") -> BloodTypeO
            else -> Primary
        }
    }

    fun getBloodTypeColor(bloodType: String): Color = forBloodType(bloodType)
}
