package com.bagomri.yemenbloodbank.core.util

import io.github.jan.supabase.exceptions.HttpRequestException
import io.github.jan.supabase.exceptions.RestException

/**
 * معالج الأخطاء المركزي لتحويل الاستثناءات لرسائل عربية مفهومة
 */
object ErrorHandler {

    fun getArabicMessage(throwable: Throwable?): String {
        if (throwable == null) return "حدث خطأ غير متوقع"

        val message = throwable.message ?: ""

        return when {
            // أخطاء الاتصال بالإنترنت
            throwable is HttpRequestException ||
            message.contains("ConnectException", ignoreCase = true) ||
            message.contains("UnknownHostException", ignoreCase = true) ||
            message.contains("SocketTimeoutException", ignoreCase = true) ||
            message.contains("Network is unreachable", ignoreCase = true) -> {
                "تعذر الاتصال بالخادم. يرجى التحقق من اتصالك بالإنترنت"
            }

            // أخطاء المصادقة والحسابات
            message.contains("Invalid login credentials", ignoreCase = true) ||
            message.contains("invalid_credentials", ignoreCase = true) -> {
                "البريد الإلكتروني أو كلمة المرور غير صحيحة"
            }

            message.contains("Email not confirmed", ignoreCase = true) -> {
                "يرجى تأكيد البريد الإلكتروني أولاً"
            }

            message.contains("User already registered", ignoreCase = true) ||
            message.contains("already registered", ignoreCase = true) -> {
                "هذا البريد الإلكتروني مسجل مسبقاً"
            }

            message.contains("duplicate key", ignoreCase = true) ||
            message.contains("23505", ignoreCase = true) -> {
                "رقم الهاتف مسجل مسبقاً"
            }

            message.contains("Password should be at least", ignoreCase = true) -> {
                "كلمة المرور يجب أن لا تقل عن 6 خانات"
            }

            message.contains("JWT expired", ignoreCase = true) ||
            message.contains("invalid_grant", ignoreCase = true) -> {
                "انتهت صلاحية الجلسة، يرجى تسجيل الدخول مجدداً"
            }

            message.contains("Rate limit exceeded", ignoreCase = true) ||
            message.contains("429", ignoreCase = true) -> {
                "تم تجاوز الحد المسموح من الطلبات، يرجى المحاولة بعد قليل"
            }

            // حظر الحساب أو غير مصرح
            message.contains("Unauthorized", ignoreCase = true) ||
            message.contains("Permission denied", ignoreCase = true) ||
            message.contains("Row level security", ignoreCase = true) -> {
                "ليس لديك الصلاحية لتنفيذ هذا الإجراء"
            }

            message.isNotEmpty() && message.startsWith("فشل") -> message
            message.isNotEmpty() && message.startsWith("لا يمكن") -> message

            else -> "حدث خطأ أثناء معالجة الطلب، يرجى المحاولة مرة أخرى"
        }
    }
}
