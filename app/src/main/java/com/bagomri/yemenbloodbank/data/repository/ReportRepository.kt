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

    /**
     * إرسال بلاغ جديد عن متبرع
     */
    suspend fun addReport(
        donorId: String,
        donorPhoneNumber: String,
        reason: String,
        notes: String?
    ): Result<Report> = withContext(Dispatchers.IO) {
        try {
            val insertData = buildJsonObject {
                put("donor_id", donorId)
                put("donor_phone_number", donorPhoneNumber.trim())
                put("reason", reason)
                notes?.let { if (it.isNotBlank()) put("notes", it.trim()) }
            }

            val created = postgrest.from("reports")
                .insert(insertData) {
                    select()
                }.decodeSingle<Report>()

            Result.success(created)
        } catch (e: Exception) {
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
            Result.failure(e)
        }
    }

    /**
     * قبول بلاغ
     */
    suspend fun approveReport(reportId: String): Result<Report> = withContext(Dispatchers.IO) {
        try {
            val currentUserId = SupabaseProvider.auth.currentUserOrNull()?.id
            val now = isoDateFormat.format(Date())

            val updateData = buildJsonObject {
                put("status", "approved")
                currentUserId?.let { put("reviewed_by", it) }
                put("reviewed_at", now)
            }

            val updated = postgrest.from("reports")
                .update(updateData) {
                    filter { eq("id", reportId) }
                    select()
                }.decodeSingle<Report>()

            Result.success(updated)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * رفض بلاغ
     */
    suspend fun rejectReport(reportId: String): Result<Report> = withContext(Dispatchers.IO) {
        try {
            val currentUserId = SupabaseProvider.auth.currentUserOrNull()?.id
            val now = isoDateFormat.format(Date())

            val updateData = buildJsonObject {
                put("status", "rejected")
                currentUserId?.let { put("reviewed_by", it) }
                put("reviewed_at", now)
            }

            val updated = postgrest.from("reports")
                .update(updateData) {
                    filter { eq("id", reportId) }
                    select()
                }.decodeSingle<Report>()

            Result.success(updated)
        } catch (e: Exception) {
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
