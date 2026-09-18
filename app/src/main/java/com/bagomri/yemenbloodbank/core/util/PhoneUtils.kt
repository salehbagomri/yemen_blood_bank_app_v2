package com.bagomri.yemenbloodbank.core.util

/**
 * أدوات معالجة وتنسيق أرقام الهواتف لعرض موحد ومحلي بدون مفاتيح دولية
 */
object PhoneUtils {

    /**
     * إزالة المفتاح الدولي (+967، 00967، 967) وأي أصفار بادئة،
     * وعرض الرقم بتنسيق محلي أنيق ومريح للقراءة (مثال: 770 727 055)
     * مع تضمين علامات التوجيه LTR لمنع انعكاس الكتل في الواجهات العربية.
     */
    fun formatDisplayPhone(phone: String?): String {
        if (phone.isNullOrBlank()) return ""

        var digits = phone.filter { it.isDigit() }

        // إزالة مفتاح اليمن 00967 أو 967
        if (digits.startsWith("00967")) {
            digits = digits.substring(5)
        } else if (digits.startsWith("967") && digits.length > 9) {
            digits = digits.substring(3)
        }

        // إزالة الصفر في البداية إن وجد (مثل 0770727055 -> 770727055)
        if (digits.startsWith("0") && digits.length == 10) {
            digits = digits.substring(1)
        }

        // تنسيق الرقم المكون من 9 أرقام إلى 3 كتل مع Left-to-Right Embedding لمنع انقلاب الكتل
        return if (digits.length == 9) {
            "\u202A${digits.substring(0, 3)} ${digits.substring(3, 6)} ${digits.substring(6)}\u202C"
        } else {
            "\u202A$digits\u202C"
        }
    }

    /**
     * الحصول على الرقم الخام بدون كود دولي وبدون مسافات
     */
    fun cleanLocalPhone(phone: String?): String {
        if (phone.isNullOrBlank()) return ""
        var digits = phone.filter { it.isDigit() }
        if (digits.startsWith("00967")) {
            digits = digits.substring(5)
        } else if (digits.startsWith("967") && digits.length > 9) {
            digits = digits.substring(3)
        }
        if (digits.startsWith("0") && digits.length == 10) {
            digits = digits.substring(1)
        }
        return digits
    }
}
