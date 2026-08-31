package com.bagomri.yemenbloodbank.data.repository

import com.bagomri.yemenbloodbank.core.network.SupabaseProvider
import com.bagomri.yemenbloodbank.data.model.Statistics
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.postgrest.query.Order
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonPrimitive
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

/**
 * مستودع إحصائيات النظام
 */
class StatisticsRepository(
    private val postgrest: Postgrest = SupabaseProvider.postgrest
) {

    private val isoDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US).apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }

    /**
     * الحصول على الإحصائيات العامة
     */
    suspend fun getStatistics(): Result<Statistics> = withContext(Dispatchers.IO) {
        try {
            try {
                val stats = postgrest.rpc("get_statistics").decodeAs<Statistics>()
                return@withContext Result.success(stats)
            } catch (_: Exception) {
                // في حالة عدم توفر الدالة المخصصة نستخدم التجميع الخادمي
                return@withContext getSimpleStatistics()
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * حساب الإحصائيات عبر دوال التجميع الخادمي
     */
    suspend fun getSimpleStatistics(): Result<Statistics> = withContext(Dispatchers.IO) {
        try {
            // إجمالي المتبرعين النشطين
            val totalRows = postgrest.from("donors")
                .select(Columns.list("id")) {
                    filter { eq("is_active", true) }
                }.decodeList<JsonObject>()
            val totalDonors = totalRows.size

            // أكثر فصيلة متوفرة
            val bloodTypeRows = postgrest.rpc("get_bloodtype_stats").decodeList<JsonObject>()
            val bloodMap = mutableMapOf<String, Int>()
            bloodTypeRows.forEach { row ->
                val type = row["blood_type"]?.jsonPrimitive?.content ?: ""
                val count = row["cnt"]?.jsonPrimitive?.intOrNull ?: 0
                if (type.isNotEmpty()) bloodMap[type] = count
            }

            var mostCommonBloodType: String? = null
            var mostCommonBloodTypeCount = 0
            if (bloodMap.isNotEmpty()) {
                val max = bloodMap.maxByOrNull { it.value }
                mostCommonBloodType = max?.key
                mostCommonBloodTypeCount = max?.value ?: 0
            }

            // أكثر مديرية نشاطاً
            val districtRows = postgrest.rpc("get_district_stats").decodeList<JsonObject>()
            val districtMap = mutableMapOf<String, Int>()
            districtRows.forEach { row ->
                val dist = row["district"]?.jsonPrimitive?.content ?: ""
                val count = row["cnt"]?.jsonPrimitive?.intOrNull ?: 0
                if (dist.isNotEmpty()) districtMap[dist] = count
            }

            var mostActiveDistrict: String? = null
            var mostActiveDistrictCount = 0
            if (districtMap.isNotEmpty()) {
                val max = districtMap.maxByOrNull { it.value }
                mostActiveDistrict = max?.key
                mostActiveDistrictCount = max?.value ?: 0
            }

            // أحدث متبرع
            val latestDonorRows = postgrest.from("donors")
                .select(Columns.list("name", "created_at")) {
                    filter { eq("is_active", true) }
                    order("created_at", Order.DESCENDING)
                    limit(1)
                }.decodeList<JsonObject>()

            var latestDonorName: String? = null
            var latestDonorDate: String? = null
            if (latestDonorRows.isNotEmpty()) {
                val row = latestDonorRows.first()
                latestDonorName = row["name"]?.jsonPrimitive?.content
                latestDonorDate = row["created_at"]?.jsonPrimitive?.content
            }

            val stats = Statistics(
                totalDonors = totalDonors,
                mostCommonBloodType = mostCommonBloodType,
                mostCommonBloodTypeCount = mostCommonBloodTypeCount,
                mostActiveDistrict = mostActiveDistrict,
                mostActiveDistrictCount = mostActiveDistrictCount,
                latestDonorName = latestDonorName,
                latestDonorDate = latestDonorDate,
                bloodTypeDistribution = bloodMap,
                districtDistribution = districtMap,
                lastUpdated = isoDateFormat.format(Date())
            )

            Result.success(stats)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
