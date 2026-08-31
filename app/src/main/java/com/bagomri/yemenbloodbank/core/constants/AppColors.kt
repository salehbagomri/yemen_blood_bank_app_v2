package com.bagomri.yemenbloodbank.core.constants

import androidx.compose.ui.graphics.Color

/**
 * نظام الألوان المعتمد لتطبيق بنك دم اليمن (Visual Design System 2.0)
 * ألوان رسمية مريحة للعين، متناسقة، ذات تباين عالٍ ومعتمدة طبياً
 */
object AppColors {
    // 🔴 الألوان الأساسية - الأحمر الطبي النبيل (Medical Crimson)
    val Primary = Color(0xFFDC2626)
    val PrimaryDark = Color(0xFF991B1B)
    val PrimaryLight = Color(0xFFEF4444)
    val PrimaryContainer = Color(0xFFFEE2E2)
    val OnPrimaryContainer = Color(0xFF991B1B)

    // 🔵 ألوان ثانوية ولهجات (Slate & Deep Navy)
    val Secondary = Color(0xFF0F172A)
    val SecondaryLight = Color(0xFF334155)
    val SecondaryContainer = Color(0xFFE2E8F0)
    val Accent = Color(0xFF0284C7)

    // ⚪ ألوان اللوحة والخلفيات (Slate Light Canvas)
    val Background = Color(0xFFF8FAFC)
    val Surface = Color(0xFFFFFFFF)
    val SurfaceVariant = Color(0xFFF1F5F9)
    val CardBackground = Color(0xFFFFFFFF)

    // ⚫ الوضع الليلي (Dark Palette)
    val DarkBackground = Color(0xFF0F172A)
    val DarkSurface = Color(0xFF1E293B)
    val DarkSurfaceVariant = Color(0xFF334155)
    val DarkPrimaryContainer = Color(0xFF450A0A)
    val DarkOnPrimaryContainer = Color(0xFFFEE2E2)

    // ✍️ نصوص الحبر والتباين (Typography Ink - WCAG AAA Compliant)
    val TextPrimary = Color(0xFF0F172A)
    val TextSecondary = Color(0xFF475569)
    val TextHint = Color(0xFF94A3B8)
    val TextOnPrimary = Color(0xFFFFFFFF)

    // 🟢 الحالات والإشعارات (Semantic States)
    val Success = Color(0xFF15803D)
    val SuccessContainer = Color(0xFFDCFCE7)
    val Warning = Color(0xFFD97706)
    val WarningContainer = Color(0xFFFEF3C7)
    val Error = Color(0xFFDC2626)
    val ErrorContainer = Color(0xFFFEE2E2)
    val Info = Color(0xFF0284C7)
    val InfoContainer = Color(0xFFE0F2FE)

    // 🔲 الحدود والفواصل الدقيقة (Micro-Borders & Dividers)
    val Border = Color(0xFFE2E8F0)
    val BorderFocused = Color(0xFFDC2626)
    val Divider = Color(0xFFEDF2F7)

    // 🩸 شارات فصائل الدم - تناسق لوني مميز
    val BloodTypeA = Color(0xFFDC2626)
    val BloodTypeAContainer = Color(0xFFFEE2E2)

    val BloodTypeB = Color(0xFF1D4ED8)
    val BloodTypeBContainer = Color(0xFFEFF6FF)

    val BloodTypeAB = Color(0xFF7E22CE)
    val BloodTypeABContainer = Color(0xFFF3E8FF)

    val BloodTypeO = Color(0xFF15803D)
    val BloodTypeOContainer = Color(0xFFDCFCE7)

    fun getBloodTypeColor(bloodType: String): Color {
        return when {
            bloodType.startsWith("A") && !bloodType.startsWith("AB") -> BloodTypeA
            bloodType.startsWith("B") -> BloodTypeB
            bloodType.startsWith("AB") -> BloodTypeAB
            bloodType.startsWith("O") -> BloodTypeO
            else -> Primary
        }
    }

    fun getBloodTypeContainerColor(bloodType: String): Color {
        return when {
            bloodType.startsWith("A") && !bloodType.startsWith("AB") -> BloodTypeAContainer
            bloodType.startsWith("B") -> BloodTypeBContainer
            bloodType.startsWith("AB") -> BloodTypeABContainer
            bloodType.startsWith("O") -> BloodTypeOContainer
            else -> PrimaryContainer
        }
    }
}
