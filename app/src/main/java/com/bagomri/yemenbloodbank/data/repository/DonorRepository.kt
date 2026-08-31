package com.bagomri.yemenbloodbank.data.repository

import com.bagomri.yemenbloodbank.core.network.SupabaseProvider
import com.bagomri.yemenbloodbank.data.model.Donor
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.postgrest.query.Order
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

/**
 * مستودع إدارة المتبرعين واستعلامات قاعدة البيانات
 */
class DonorRepository(
    private val postgrest: Postgrest = SupabaseProvider.postgrest
) {

    private val isoDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US).apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }

    /**
     * البحث عن متبرعين عبر الدالة الخادمية search_donors
     */
    suspend fun searchDonors(
        bloodType: String? = null,
        governorate: String? = null,
        district: String? = null,
        availableOnly: Boolean = true
    ): Result<List<Donor>> = withContext(Dispatchers.IO) {
        try {
            val params = buildJsonObject {
                put("p_blood_type", bloodType?.ifEmpty { null })
                put("p_district", district?.ifEmpty { null })
                put("p_available_only", availableOnly)
                put("p_governorate", governorate?.ifEmpty { null })
            }

            val donors = postgrest.rpc("search_donors", params).decodeList<Donor>()
            Result.success(donors)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * الحصول على متبرع بواسطة المعرّف
     */
    suspend fun getDonorById(id: String): Result<Donor?> = withContext(Dispatchers.IO) {
        try {
            val donor = postgrest.from("donors")
                .select {
                    filter { eq("id", id) }
                    limit(1)
                }.decodeSingleOrNull<Donor>()
            Result.success(donor)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * إضافة متبرع جديد
     */
    suspend fun addDonor(donor: Donor): Result<Donor> = withContext(Dispatchers.IO) {
        try {
            val currentUserId = SupabaseProvider.auth.currentUserOrNull()?.id

            val insertData = buildJsonObject {
                put("name", donor.name.trim())
                put("phone_number", donor.phoneNumber.trim())
                donor.phoneNumber2?.let { if (it.isNotBlank()) put("phone_number_2", it.trim()) }
                donor.phoneNumber3?.let { if (it.isNotBlank()) put("phone_number_3", it.trim()) }
                put("blood_type", donor.bloodType)
                put("district", donor.district)
                put("governorate", donor.governorate)
                put("age", donor.age)
                put("gender", donor.gender)
                donor.notes?.let { if (it.isNotBlank()) put("notes", it.trim()) }
                if (currentUserId != null) {
                    put("added_by", currentUserId)
                }
            }

            val created = postgrest.from("donors")
                .insert(insertData) {
                    select()
                }.decodeSingle<Donor>()

            Result.success(created)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * تحديث بيانات متبرع
     */
    suspend fun updateDonor(donor: Donor): Result<Donor> = withContext(Dispatchers.IO) {
        try {
            val updateData = buildJsonObject {
                put("name", donor.name.trim())
                put("phone_number", donor.phoneNumber.trim())
                put("phone_number_2", donor.phoneNumber2?.trim()?.ifEmpty { null })
                put("phone_number_3", donor.phoneNumber3?.trim()?.ifEmpty { null })
                put("blood_type", donor.bloodType)
                put("district", donor.district)
                put("governorate", donor.governorate)
                put("age", donor.age)
                put("gender", donor.gender)
                put("notes", donor.notes?.trim()?.ifEmpty { null })
                put("is_available", donor.isAvailable)
                put("last_donation_date", donor.lastDonationDate)
                put("suspended_until", donor.suspendedUntil)
                put("is_active", donor.isActive)
            }

            val updated = postgrest.from("donors")
                .update(updateData) {
                    filter { eq("id", donor.id) }
                    select()
                }.decodeSingle<Donor>()

            Result.success(updated)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * البحث عن متبرع برقم الهاتف (معالجة وتنظيف الأرقام)
     */
    suspend fun findDonorByPhone(phoneNumber: String): Result<Donor?> = withContext(Dispatchers.IO) {
        try {
            var cleanPhone = phoneNumber.trim().replace(Regex("[\\s\\-\\(\\)]"), "")
            if (cleanPhone.startsWith("+967")) {
                cleanPhone = cleanPhone.substring(4)
            } else if (cleanPhone.startsWith("967")) {
                cleanPhone = cleanPhone.substring(3)
            } else if (cleanPhone.startsWith("00967")) {
                cleanPhone = cleanPhone.substring(5)
            }

            val response = postgrest.from("donors")
                .select {
                    filter {
                        or {
                            eq("phone_number", cleanPhone)
                            eq("phone_number", "+967$cleanPhone")
                            eq("phone_number_2", cleanPhone)
                            eq("phone_number_2", "+967$cleanPhone")
                            eq("phone_number_3", cleanPhone)
                            eq("phone_number_3", "+967$cleanPhone")
                        }
                    }
                    order("created_at", Order.DESCENDING)
                    limit(1)
                }.decodeList<Donor>()

            if (response.isNotEmpty()) {
                return@withContext Result.success(response.first())
            }

            // محاولة بدون صفر البداية إذا كان موجوداً
            if (cleanPhone.startsWith("0")) {
                val withoutZero = cleanPhone.substring(1)
                val fallbackResponse = postgrest.from("donors")
                    .select {
                        filter {
                            or {
                                eq("phone_number", withoutZero)
                                eq("phone_number", "+967$withoutZero")
                                eq("phone_number_2", withoutZero)
                                eq("phone_number_2", "+967$withoutZero")
                                eq("phone_number_3", withoutZero)
                                eq("phone_number_3", "+967$withoutZero")
                            }
                        }
                        order("created_at", Order.DESCENDING)
                        limit(1)
                    }.decodeList<Donor>()

                if (fallbackResponse.isNotEmpty()) {
                    return@withContext Result.success(fallbackResponse.first())
                }
            }

            Result.success(null)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * حذف متبرع
     */
    suspend fun deleteDonor(id: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            postgrest.from("donors").delete {
                filter { eq("id", id) }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * إيقاف متبرع لمدة 6 أشهر
     */
    suspend fun suspendDonorFor6Months(id: String): Result<Donor> = withContext(Dispatchers.IO) {
        try {
            val params = buildJsonObject { put("p_donor_id", id) }
            val suspended = postgrest.rpc("suspend_donor_by_hospital", params).decodeAs<Donor>()
            Result.success(suspended)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * تحديث تاريخ آخر تبرع لمتبرع
     */
    suspend fun updateDonorDonationDate(
        donorId: String,
        lastDonationDate: String,
        suspendedUntil: String?
    ): Result<Donor> = withContext(Dispatchers.IO) {
        try {
            val params = buildJsonObject {
                put("p_donor_id", donorId)
                put("p_last_donation_date", lastDonationDate)
                put("p_suspended_until", suspendedUntil)
            }
            val updated = postgrest.rpc("update_donor_donation_date", params).decodeAs<Donor>()
            Result.success(updated)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * الحصول على المتبرعين الموقوفين
     */
    suspend fun getSuspendedDonors(governorate: String? = null): Result<List<Donor>> = withContext(Dispatchers.IO) {
        try {
            val now = isoDateFormat.format(Date())
            val donors = postgrest.from("donors")
                .select {
                    filter {
                        gt("suspended_until", now)
                        if (!governorate.isNullOrEmpty()) {
                            eq("governorate", governorate)
                        }
                    }
                    order("suspended_until", Order.ASCENDING)
                }.decodeList<Donor>()
            Result.success(donors)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * الحصول على عدد المتبرعين المعطلين
     */
    suspend fun getInactiveDonorsCount(governorate: String? = null): Result<Int> = withContext(Dispatchers.IO) {
        try {
            val rows = postgrest.from("donors")
                .select(Columns.list("id")) {
                    filter {
                        eq("is_active", false)
                        if (!governorate.isNullOrEmpty()) {
                            eq("governorate", governorate)
                        }
                    }
                }.decodeList<JsonObject>()
            Result.success(rows.size)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * الحصول على جميع المتبرعين (مع Pagination)
     */
    suspend fun getAllDonors(limit: Int? = null, offset: Int? = null): Result<List<Donor>> = withContext(Dispatchers.IO) {
        try {
            val donors = postgrest.from("donors")
                .select {
                    order("created_at", Order.DESCENDING)
                    if (limit != null && offset != null) {
                        range(offset.toLong(), (offset + limit - 1).toLong())
                    } else if (limit != null) {
                        limit(limit.toLong())
                    }
                }.decodeList<Donor>()
            Result.success(donors)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * الحصول على متبرعي محافظة معينة (للوحة المستشفى)
     */
    suspend fun getDonorsByGovernorate(governorate: String, limit: Int? = null): Result<List<Donor>> = withContext(Dispatchers.IO) {
        try {
            val donors = postgrest.from("donors")
                .select {
                    filter { eq("governorate", governorate) }
                    order("created_at", Order.DESCENDING)
                    if (limit != null) limit(limit.toLong())
                }.decodeList<Donor>()
            Result.success(donors)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * البحث بالاسم أو رقم الهاتف
     */
    suspend fun searchByNameOrPhone(query: String, governorate: String? = null): Result<List<Donor>> = withContext(Dispatchers.IO) {
        try {
            val donors = postgrest.from("donors")
                .select {
                    filter {
                        or {
                            ilike("name", "%$query%")
                            ilike("phone_number", "%$query%")
                            ilike("phone_number_2", "%$query%")
                            ilike("phone_number_3", "%$query%")
                        }
                        eq("is_active", true)
                        if (!governorate.isNullOrEmpty()) {
                            eq("governorate", governorate)
                        }
                    }
                    order("name", Order.ASCENDING)
                    limit(50)
                }.decodeList<Donor>()
            Result.success(donors)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * الحصول على إحصائيات المحافظات عبر RPC
     */
    suspend fun getGovernorateStats(governorate: String? = null): Result<List<JsonObject>> = withContext(Dispatchers.IO) {
        try {
            val params = buildJsonObject {
                put("p_governorate", governorate)
            }
            val list = postgrest.rpc("get_governorate_stats", params).decodeList<JsonObject>()
            Result.success(list)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * إحصائيات فصائل الدم
     */
    suspend fun getDonorCountByBloodType(): Result<Map<String, Int>> = withContext(Dispatchers.IO) {
        try {
            val rows = postgrest.rpc("get_bloodtype_stats").decodeList<JsonObject>()
            val map = mutableMapOf<String, Int>()
            rows.forEach { row ->
                val type = row["blood_type"]?.jsonPrimitive?.content ?: ""
                val count = row["cnt"]?.jsonPrimitive?.intOrNull ?: 0
                if (type.isNotEmpty()) map[type] = count
            }
            Result.success(map)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * إحصائيات المديريات
     */
    suspend fun getDonorCountByDistrict(): Result<Map<String, Int>> = withContext(Dispatchers.IO) {
        try {
            val rows = postgrest.rpc("get_district_stats").decodeList<JsonObject>()
            val map = mutableMapOf<String, Int>()
            rows.forEach { row ->
                val dist = row["district"]?.jsonPrimitive?.content ?: ""
                val count = row["cnt"]?.jsonPrimitive?.intOrNull ?: 0
                if (dist.isNotEmpty()) map[dist] = count
            }
            Result.success(map)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * عدد المتبرعين المتاحين للتبرع الآن
     */
    suspend fun getAvailableDonorsCount(governorate: String? = null): Result<Int> = withContext(Dispatchers.IO) {
        try {
            val now = isoDateFormat.format(Date())
            val rows = postgrest.from("donors")
                .select(Columns.list("id", "suspended_until")) {
                    filter {
                        eq("is_active", true)
                        if (!governorate.isNullOrEmpty()) {
                            eq("governorate", governorate)
                        }
                    }
                }.decodeList<JsonObject>()

            val availableCount = rows.count { row ->
                val suspendedUntil = row["suspended_until"]?.jsonPrimitive?.contentOrNull
                suspendedUntil == null || suspendedUntil < now
            }
            Result.success(availableCount)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * عدد المتبرعين الجدد هذا الشهر
     */
    suspend fun getNewDonorsThisMonth(governorate: String? = null): Result<Int> = withContext(Dispatchers.IO) {
        try {
            val cal = Calendar.getInstance().apply {
                set(Calendar.DAY_OF_MONTH, 1)
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
            }
            val startOfMonth = isoDateFormat.format(cal.time)

            val rows = postgrest.from("donors")
                .select(Columns.list("id")) {
                    filter {
                        gte("created_at", startOfMonth)
                        if (!governorate.isNullOrEmpty()) {
                            eq("governorate", governorate)
                        }
                    }
                }.decodeList<JsonObject>()
            Result.success(rows.size)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * آخر المتبرعين المضافين
     */
    suspend fun getRecentDonors(limit: Int = 5, governorate: String? = null): Result<List<Donor>> = withContext(Dispatchers.IO) {
        try {
            val donors = postgrest.from("donors")
                .select {
                    filter {
                        if (!governorate.isNullOrEmpty()) {
                            eq("governorate", governorate)
                        }
                    }
                    order("created_at", Order.DESCENDING)
                    limit(limit.toLong())
                }.decodeList<Donor>()
            Result.success(donors)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * آخر التبرعات
     */
    suspend fun getRecentDonations(limit: Int = 5, governorate: String? = null): Result<List<Donor>> = withContext(Dispatchers.IO) {
        try {
            val donors = postgrest.from("donors")
                .select {
                    filter {
                        gt("last_donation_date", "1970-01-01")
                        if (!governorate.isNullOrEmpty()) {
                            eq("governorate", governorate)
                        }
                    }
                    order("last_donation_date", Order.DESCENDING)
                    limit(limit.toLong())
                }.decodeList<Donor>()
            Result.success(donors)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
