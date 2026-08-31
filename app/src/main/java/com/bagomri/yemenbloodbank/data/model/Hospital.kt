package com.bagomri.yemenbloodbank.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * نموذج بيانات المستشفى
 */
@Serializable
data class Hospital(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val district: String = "",
    @SerialName("governorate")
    val rawGovernorate: String? = null,
    @SerialName("phone_number")
    val phoneNumber: String? = null,
    val address: String? = null,
    @SerialName("is_active")
    val isActive: Boolean = true,
    @SerialName("created_at")
    val createdAt: String? = null,
    @SerialName("updated_at")
    val updatedAt: String? = null
) {
    /**
     * المحافظة - تشتق دفاعياً من district إذا لم تكن موجودة
     */
    val governorate: String
        get() = if (!rawGovernorate.isNullOrEmpty()) {
            rawGovernorate
        } else {
            district.substringBefore(" - ")
        }

    val subDistrict: String
        get() = if (district.contains(" - ")) {
            district.substringAfter(" - ")
        } else {
            district
        }
}
