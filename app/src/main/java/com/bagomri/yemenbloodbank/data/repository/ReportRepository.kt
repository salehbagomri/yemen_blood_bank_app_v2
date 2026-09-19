package com.bagomri.yemenbloodbank.data.repository

import com.bagomri.yemenbloodbank.core.network.SupabaseProvider
import com.bagomri.yemenbloodbank.data.model.Report
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.postgrest.query.Order
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

/**
 * مستودع إدارة بلاغات الأرقام غير الصالحة
 */
class ReportRepository(
    private val postgrest: Postgrest = SupabaseProvider.postgrest
) {

    private val isoDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US).apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }

@kotlinx.serialization.Serializable
data class ReportInsertDto(
    @kotlinx.serialization.SerialName("donor_id")
    val donorId: String,
    val reason: String
)

    /**
     * إرسال بلاغ جديد عن متبرع
     * جدول reports في Supabase يحتوي فقط على: id, donor_id, reason, status, created_at
     */
    suspend fun addReport(
        donorId: String,
        donorPhoneNumber: String = "",
        reason: String,
        notes: String? = null
    ): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val dto = ReportInsertDto(
                donorId = donorId,
                reason = reason
            )

            // إرسال البلاغ بدون select() لتفادي رفض سياسة RLS لغير الأدمن
            postgrest.from("reports").insert(dto)

            Result.success(Unit)
        } catch (e: Exception) {
            android.util.Log.e("ReportRepository", "Error adding report: ${e.message}", e)
            Result.failure(e)
        }
    }

    /**
     * الحصول على جميع البلاغات (للأدمن)
     */
    suspend fun getAllReports(status: String? = null, limit: Int? = null): Result<List<Report>> = withContext(Dispatchers.IO) {
        try {
            val reports = postgrest.from("reports")
                .select {
                    filter {
                        if (!status.isNullOrEmpty()) {
                            eq("status", status)
                        }
                    }
                    order("created_at", Order.DESCENDING)
                    if (limit != null) limit(limit.toLong())
                }.decodeList<Report>()
            Result.success(reports)
        } catch (e: Exception) {
            android.util.Log.e("ReportRepository", "Error getting reports: ${e.message}", e)
            Result.failure(e)
        }
    }

    /**
     * الحصول على بلاغ محدد بالمعرف
     */
    suspend fun getReportById(reportId: String): Result<Report?> = withContext(Dispatchers.IO) {
        try {
            val report = postgrest.from("reports")
                .select {
                    filter { eq("id", reportId) }
                    limit(1)
                }.decodeSingleOrNull<Report>()
            Result.success(report)
        } catch (e: Exception) {
            android.util.Log.e("ReportRepository", "Error getting report by id: ${e.message}", e)
            Result.failure(e)
        }
    }

    /**
     * قبول بلاغ
     * تنبيه: جدول reports يحتوي فقط على (id, donor_id, reason, status, created_at)
     */
    suspend fun approveReport(reportId: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val updateData = buildJsonObject {
                put("status", "approved")
            }

            postgrest.from("reports")
                .update(updateData) {
                    filter { eq("id", reportId) }
                }

            Result.success(Unit)
        } catch (e: Exception) {
            android.util.Log.e("ReportRepository", "Error approving report: ${e.message}", e)
            Result.failure(e)
        }
    }

    /**
     * رفض بلاغ
     */
    suspend fun rejectReport(reportId: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val updateData = buildJsonObject {
                put("status", "rejected")
            }

            postgrest.from("reports")
                .update(updateData) {
                    filter { eq("id", reportId) }
                }

            Result.success(Unit)
        } catch (e: Exception) {
            android.util.Log.e("ReportRepository", "Error rejecting report: ${e.message}", e)
            Result.failure(e)
        }
    }

    /**
     * إعادة البلاغ لحالة قيد المراجعة
     */
    suspend fun resetReportToPending(reportId: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val updateData = buildJsonObject {
                put("status", "pending")
            }

            postgrest.from("reports")
                .update(updateData) {
                    filter { eq("id", reportId) }
                }

            Result.success(Unit)
        } catch (e: Exception) {
            android.util.Log.e("ReportRepository", "Error resetting report: ${e.message}", e)
            Result.failure(e)
        }
    }

    /**
     * حذف بلاغ
     */
    suspend fun deleteReport(reportId: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            postgrest.from("reports").delete {
                filter { eq("id", reportId) }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            android.util.Log.e("ReportRepository", "Error deleting report: ${e.message}", e)
            Result.failure(e)
        }
    }

    /**
     * عدد البلاغات المعلقة
     */
    suspend fun getPendingReportsCount(): Result<Int> = withContext(Dispatchers.IO) {
        try {
            val rows = postgrest.from("reports")
                .select(Columns.list("id")) {
                    filter { eq("status", "pending") }
                }.decodeList<JsonObject>()
            Result.success(rows.size)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
