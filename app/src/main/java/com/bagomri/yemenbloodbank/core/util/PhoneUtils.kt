package com.bagomri.yemenbloodbank.core.util

/**
 * أدوات معالجة وتنسيق أرقام الهواتف لعرض موحد ومحلي بدون مفاتيح دولية
 */
object PhoneUtils {

    /**
     * تحويل الأرقام العربية والفارسية إلى أرقام إنجليزية قياسية مع تجاهل أي رموز غير رقمية
     */
    private fun toAsciiDigits(input: String?): String {
        if (input.isNullOrBlank()) return ""
        val sb = StringBuilder(input.length)
        for (ch in input) {
            when (ch) {
                in '0'..'9' -> sb.append(ch)
                in '\u0660'..'\u0669' -> sb.append((ch - '\u0660' + '0'.code).toChar()) // Arabic-Indic ٠-٩
                in '\u06F0'..'\u06F9' -> sb.append((ch - '\u06F0' + '0'.code).toChar()) // Eastern Arabic / Persian ۰-۹
            }
        }
        return sb.toString()
    }

    /**
     * الحصول على الرقم المحلي النقي بدون كود دولي وبدون أصفار زائدة
     * يتعامل مع كافة صيغ الإدخال (+967، 00967، 967، 0967، 07...)
     */
    fun cleanLocalPhone(phone: String?): String {
        if (phone.isNullOrBlank()) return ""
        var digits = toAsciiDigits(phone)

        // إزالة الأصفار التي تسبق المفتاح الدولي إن وجدت (مثل 00967 أو 0967)
        while (digits.startsWith("0") && digits.length > 9 && (digits.startsWith("00967") || digits.startsWith("0967"))) {
            digits = digits.substring(1)
        }

        // إزالة المفتاح الدولي لليمن 967
        if (digits.startsWith("967") && digits.length > 9) {
            digits = digits.substring(3)
        }

        // إزالة أي صفر بادئ في الرقم المحلي (مثل 0770123456 -> 770123456)
        while (digits.startsWith("0") && digits.length > 9) {
            digits = digits.substring(1)
        }

        return digits
    }

    /**
     * إزالة المفتاح الدولي وعرض الرقم بتنسيق محلي أنيق ومريح للقراءة (مثال: 770 727 055)
     * مع تضمين علامات التوجيه LTR لمنع انعكاس الكتل في الواجهات العربية.
     */
    fun formatDisplayPhone(phone: String?): String {
        val clean = cleanLocalPhone(phone)
        if (clean.isBlank()) return ""

        return if (clean.length == 9) {
            "\u202A${clean.substring(0, 3)} ${clean.substring(3, 6)} ${clean.substring(6)}\u202C"
        } else {
            "\u202A$clean\u202C"
        }
    }
}
