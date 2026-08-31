package com.bagomri.yemenbloodbank.core.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import com.bagomri.yemenbloodbank.core.constants.AppStrings
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

/**
 * وظائف مساعدة للتفاعل مع التطبيقات الخارجية (الاتصال، واتساب، الروابط)
 */
object IntentUtils {

    /**
     * فتح لوحة الاتصال برقم المتبرع
     */
    fun dialPhoneNumber(context: Context, phoneNumber: String) {
        try {
            val cleanNumber = sanitizePhoneNumber(phoneNumber)
            val intent = Intent(Intent.ACTION_DIAL).apply {
                data = Uri.parse("tel:$cleanNumber")
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "تعذر فتح تطبيق الهاتف", Toast.LENGTH_SHORT).show()
        }
    }

    fun makePhoneCall(context: Context, phoneNumber: String) = dialPhoneNumber(context, phoneNumber)

    /**
     * فتح محادثة WhatsApp مباشرة مع رسالة افتراضية
     */
    fun openWhatsApp(
        context: Context,
        phoneNumber: String,
        message: String = AppStrings.whatsappDefaultMessage
    ) {
        try {
            var cleanPhone = sanitizePhoneNumber(phoneNumber)

            // التأكد من وجود كود الدولة (اليمن 967)
            if (cleanPhone.startsWith("+")) {
                cleanPhone = cleanPhone.substring(1)
            } else if (cleanPhone.startsWith("00")) {
                cleanPhone = cleanPhone.substring(2)
            } else if (!cleanPhone.startsWith("967")) {
                if (cleanPhone.startsWith("0")) {
                    cleanPhone = cleanPhone.substring(1)
                }
                cleanPhone = "967$cleanPhone"
            }

            val encodedMessage = URLEncoder.encode(message, StandardCharsets.UTF_8.toString())
            val uri = Uri.parse("https://api.whatsapp.com/send?phone=$cleanPhone&text=$encodedMessage")

            val intent = Intent(Intent.ACTION_VIEW, uri).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "تعذر فتح تطبيق WhatsApp", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * فتح رابط خارجي في المتصفح
     */
    fun openUrl(context: Context, url: String) {
        try {
            var targetUrl = url.trim()
            if (!targetUrl.startsWith("http://") && !targetUrl.startsWith("https://")) {
                targetUrl = "https://$targetUrl"
            }
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(targetUrl)).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "تعذر فتح الرابط", Toast.LENGTH_SHORT).show()
        }
    }

    private fun sanitizePhoneNumber(phone: String): String {
        return phone.replace(Regex("[\\s\\-\\(\\)]"), "")
    }
}
