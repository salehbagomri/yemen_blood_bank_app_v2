package com.bagomri.yemenbloodbank.data.model

import com.bagomri.yemenbloodbank.core.util.DateUtils
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.util.Date

/**
 * نموذج بيانات المتبرع
 */
@Serializable
data class Donor(
    val id: String = "",
    val name: String = "",
    @SerialName("phone_number")
    val phoneNumber: String = "",
    @SerialName("phone_number_2")
    val phoneNumber2: String? = null,
    @SerialName("phone_number_3")
    val phoneNumber3: String? = null,
    @SerialName("blood_type")
    val bloodType: String = "",
    val district: String = "",
    @SerialName("governorate")
    val rawGovernorate: String? = null,
    val age: Int = 18,
    val gender: String = "male", // male or female
    val notes: String? = null,
    @SerialName("is_available")
    val isAvailable: Boolean = true,
    @SerialName("last_donation_date")
    val lastDonationDate: String? = null,
    @SerialName("suspended_until")
    val suspendedUntil: String? = null,
    @SerialName("created_at")
    val createdAt: String? = null,
    @SerialName("updated_at")
    val updatedAt: String? = null,
    @SerialName("added_by")
    val addedBy: String? = null,
    @SerialName("is_active")
    val isActive: Boolean = true
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

    /**
     * المديرية بدون اسم المحافظة
     */
    val subDistrict: String
        get() = if (district.contains(" - ")) {
            district.substringAfter(" - ")
        } else {
            district
        }

    /**
     * قائمة بجميع أرقام الهواتف غير الفارغة
     */
    val allPhoneNumbers: List<String>
        get() {
            val list = mutableListOf<String>()
            if (phoneNumber.isNotEmpty()) list.add(phoneNumber)
            if (!phoneNumber2.isNullOrEmpty()) list.add(phoneNumber2)
            if (!phoneNumber3.isNullOrEmpty()) list.add(phoneNumber3)
            return list
        }

    /**
     * هل المتبرع موقوف حالياً؟
     */
    val isSuspended: Boolean
        get() {
            val suspendedDate = DateUtils.parseIsoDate(suspendedUntil) ?: return false
            return suspendedDate.after(Date())
        }

    /**
     * هل يمكن للمتبرع التبرع الآن؟
     */
    val canDonateNow: Boolean
        get() {
            if (!isAvailable || !isActive) return false
            if (isSuspended) return false

            val lastDate = DateUtils.parseIsoDate(lastDonationDate)
            if (lastDate != null) {
                val sixMonthsAgo = Date(System.currentTimeMillis() - 180L * 24 * 60 * 60 * 1000)
                return lastDate.before(sixMonthsAgo)
            }
            return true
        }

    /**
     * عدد الأيام المتبقية حتى إمكانية التبرع
     */
    val daysUntilCanDonate: Int?
        get() {
            if (canDonateNow) return 0

            val suspendedDate = DateUtils.parseIsoDate(suspendedUntil)
            if (isSuspended && suspendedDate != null) {
                val diff = DateUtils.getDaysDifference(suspendedDate)
                return if (diff > 0) diff.toInt() else null
            }

            val lastDate = DateUtils.parseIsoDate(lastDonationDate)
            if (lastDate != null) {
                val sixMonthsFromLast = Date(lastDate.time + 180L * 24 * 60 * 60 * 1000)
                if (Date().before(sixMonthsFromLast)) {
                    val diff = DateUtils.getDaysDifference(sixMonthsFromLast)
                    return if (diff > 0) diff.toInt() else null
                }
            }

            return null
        }
}
