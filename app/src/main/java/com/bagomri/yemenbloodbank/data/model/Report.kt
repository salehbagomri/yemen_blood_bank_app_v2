package com.bagomri.yemenbloodbank.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * نموذج بيانات البلاغات عن الأرقام غير الصالحة
 */
@Serializable
data class Report(
    val id: String = "",
    @SerialName("donor_id")
    val donorId: String = "",
    @SerialName("donor_phone_number")
    val donorPhoneNumber: String = "",
    val reason: String = "",
    val notes: String? = null,
    val status: String = "pending", // pending, approved, rejected
    @SerialName("reviewed_by")
    val reviewedBy: String? = null,
    @SerialName("reviewed_at")
    val reviewedAt: String? = null,
    @SerialName("created_at")
    val createdAt: String? = null
) {
    val isPending: Boolean get() = status == "pending"
    val isApproved: Boolean get() = status == "approved"
    val isRejected: Boolean get() = status == "rejected"

    val reasonText: String
        get() = when (reason) {
            "number_not_working" -> "الرقم لا يعمل"
            "wrong_number" -> "رقم خاطئ"
            "refuses_to_donate" -> "يرفض التبرع"
            "number_busy" -> "الرقم مشغول دائماً"
            "no_answer" -> "لا يرد على الاتصال"
            "deceased" -> "متوفى"
            "moved_away" -> "انتقل إلى منطقة أخرى"
            "health_issues" -> "مشاكل صحية"
            "other" -> "سبب آخر"
            else -> reason
        }

    val statusText: String
        get() = when (status) {
            "pending" -> "قيد المراجعة"
            "approved" -> "تم القبول"
            "rejected" -> "تم الرفض"
            else -> status
        }

    val priority: String
        get() = when (reason) {
            "deceased" -> "critical"
            "wrong_number", "number_not_working" -> "high"
            "moved_away", "health_issues" -> "medium"
            else -> "low"
        }

    val priorityText: String
        get() = when (priority) {
            "critical" -> "حرج"
            "high" -> "عالي"
            "medium" -> "متوسط"
            "low" -> "منخفض"
            else -> priority
        }

    val suggestedAction: String
        get() = when (reason) {
            "deceased" -> "delete"
            "wrong_number", "number_not_working", "moved_away", "health_issues" -> "edit"
            else -> "note"
        }

    val suggestedActionText: String
        get() = when (suggestedAction) {
            "delete" -> "حذف نهائي"
            "edit" -> "تعديل البيانات"
            "note" -> "إضافة ملاحظة"
            else -> suggestedAction
        }
}
