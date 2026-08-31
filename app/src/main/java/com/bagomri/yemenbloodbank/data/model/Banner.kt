package com.bagomri.yemenbloodbank.data.model

import com.bagomri.yemenbloodbank.core.constants.AppConfig
import com.bagomri.yemenbloodbank.core.network.SupabaseProvider
import com.bagomri.yemenbloodbank.core.util.DateUtils
import io.github.jan.supabase.storage.storage
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.util.Date

/**
 * نموذج بيانات البانر الدعائي / الإعلاني
 */
@Serializable
data class Banner(
    val id: String = "",
    val title: String = "",
    val subtitle: String? = null,
    @SerialName("image_path")
    val imagePath: String? = null,
    @SerialName("action_type")
    val actionType: String = "none", // none | internal_route | external_url
    @SerialName("action_value")
    val actionValue: String? = null,
    @SerialName("sort_order")
    val sortOrder: Int = 0,
    @SerialName("is_active")
    val isActive: Boolean = true,
    @SerialName("icon_name")
    val iconName: String? = null,
    @SerialName("bg_gradient")
    val bgGradient: String? = null,
    @SerialName("starts_at")
    val startsAt: String? = null,
    @SerialName("ends_at")
    val endsAt: String? = null,
    @SerialName("created_at")
    val createdAt: String? = null,
    @SerialName("updated_at")
    val updatedAt: String? = null
) {
    /**
     * الحصول على رابط الصورة المباشر من Supabase Storage
     */
    val imageUrl: String
        get() {
            if (imagePath.isNullOrEmpty()) return ""
            return try {
                SupabaseProvider.storage.from(AppConfig.BUCKET_BANNERS).publicUrl(imagePath)
            } catch (e: Exception) {
                ""
            }
        }

    /**
     * هل هذا البانر نصي فقط (بدون صورة خلفية)؟
     */
    val isTextBanner: Boolean
        get() = imagePath.isNullOrEmpty()

    /**
     * التحقق من أن البانر فعال ومدرج ضمن الوقت الحالي
     */
    val isCurrentlyVisible: Boolean
        get() {
            if (!isActive) return false
            val now = Date()

            val startDate = DateUtils.parseIsoDate(startsAt)
            if (startDate != null && now.before(startDate)) return false

            val endDate = DateUtils.parseIsoDate(endsAt)
            if (endDate != null && now.after(endDate)) return false

            return true
        }
}
