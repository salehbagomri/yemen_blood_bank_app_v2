package com.bagomri.yemenbloodbank.core.util

import io.github.jan.supabase.exceptions.HttpRequestException
import io.github.jan.supabase.exceptions.RestException

/**
 * معالج الأخطاء المركزي لتحويل الاستثناءات لرسائل عربية مفهومة
 */
object ErrorHandler {

    fun getArabicMessage(throwable: Throwable?): String {
        if (throwable == null) return "حدث خطأ غير متوقع"

        val rawMessage = when (throwable) {
            is RestException -> "${throwable.description ?: ""} ${throwable.error} ${throwable.message ?: ""}".trim()
            else -> throwable.message ?: ""
        }

        return when {
            // أخطاء الاتصال بالإنترنت
            throwable is HttpRequestException ||
            rawMessage.contains("ConnectException", ignoreCase = true) ||
            rawMessage.contains("UnknownHostException", ignoreCase = true) ||
            rawMessage.contains("SocketTimeoutException", ignoreCase = true) ||
            rawMessage.contains("Network is unreachable", ignoreCase = true) -> {
                "تعذر الاتصال بالخادم. يرجى التحقق من اتصالك بالإنترنت"
            }

            // أخطاء المصادقة والحسابات
            rawMessage.contains("Invalid login credentials", ignoreCase = true) ||
            rawMessage.contains("invalid_credentials", ignoreCase = true) -> {
                "البريد الإلكتروني أو كلمة المرور غير صحيحة"
            }

            rawMessage.contains("Email not confirmed", ignoreCase = true) -> {
                "يرجى تأكيد البريد الإلكتروني أولاً"
            }

            rawMessage.contains("User already registered", ignoreCase = true) ||
            rawMessage.contains("already registered", ignoreCase = true) -> {
                "هذا البريد الإلكتروني مسجل مسبقاً"
            }

            rawMessage.contains("duplicate key", ignoreCase = true) ||
            rawMessage.contains("23505", ignoreCase = true) -> {
                "رقم الهاتف مسجل مسبقاً"
            }

            rawMessage.contains("Password should be at least", ignoreCase = true) -> {
                "كلمة المرور يجب أن لا تقل عن 6 خانات"
            }

            rawMessage.contains("JWT expired", ignoreCase = true) ||
            rawMessage.contains("invalid_grant", ignoreCase = true) -> {
                "انتهت صلاحية الجلسة، يرجى تسجيل الدخول مجدداً"
            }

            rawMessage.contains("Rate limit exceeded", ignoreCase = true) ||
            rawMessage.contains("429", ignoreCase = true) -> {
                "تم تجاوز الحد المسموح من الطلبات، يرجى المحاولة بعد قليل"
            }

            // قيود قاعدة البيانات
            rawMessage.contains("violates check constraint", ignoreCase = true) -> {
                "البيانات المدخلة غير مقبولة أو أن سبب البلاغ المحدد غير صالح"
            }

            rawMessage.contains("violates foreign key constraint", ignoreCase = true) -> {
                "المتبرع المبلغ عنه غير موجود في قاعدة البيانات"
            }

            rawMessage.contains("invalid input syntax for type uuid", ignoreCase = true) -> {
                "معرف المتبرع غير صالح"
            }

            // حظر الحساب أو غير مصرح
            rawMessage.contains("Unauthorized", ignoreCase = true) ||
            rawMessage.contains("Permission denied", ignoreCase = true) ||
            rawMessage.contains("Row level security", ignoreCase = true) ||
            rawMessage.contains("row-level security", ignoreCase = true) -> {
                "ليس لديك الصلاحية لتنفيذ هذا الإجراء"
            }

            rawMessage.isNotEmpty() && rawMessage.startsWith("فشل") -> rawMessage
            rawMessage.isNotEmpty() && rawMessage.startsWith("لا يمكن") -> rawMessage

            else -> "حدث خطأ أثناء معالجة الطلب، يرجى المحاولة مرة أخرى"
        }
    }
}
