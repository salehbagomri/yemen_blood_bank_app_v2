package com.bagomri.yemenbloodbank.data.repository

import com.bagomri.yemenbloodbank.core.network.SupabaseProvider
import com.bagomri.yemenbloodbank.data.model.Hospital
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.postgrest.query.Order
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

/**
 * مستودع إدارة المستشفيات
 */
class HospitalRepository(
    private val postgrest: Postgrest = SupabaseProvider.postgrest
) {

    /**
     * الحصول على جميع المستشفيات
     */
    suspend fun getAllHospitals(): Result<List<Hospital>> = withContext(Dispatchers.IO) {
        try {
            val hospitals = postgrest.from("hospitals")
                .select {
                    order("created_at", Order.DESCENDING)
                }.decodeList<Hospital>()
            Result.success(hospitals)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * الحصول على بيانات مستشفى بواسطة المعرّف
     */
    suspend fun getHospitalById(id: String): Result<Hospital?> = withContext(Dispatchers.IO) {
        try {
            val hospital = postgrest.from("hospitals")
                .select {
                    filter { eq("id", id) }
                    limit(1)
                }.decodeSingleOrNull<Hospital>()
            Result.success(hospital)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * تحديث بيانات مستشفى
     */
    suspend fun updateHospital(hospital: Hospital): Result<Hospital> = withContext(Dispatchers.IO) {
        try {
            val updateData = buildJsonObject {
                put("name", hospital.name.trim())
                put("email", hospital.email.trim())
                put("district", hospital.district)
                put("governorate", hospital.governorate)
                put("phone_number", hospital.phoneNumber?.trim()?.ifEmpty { null })
                put("address", hospital.address?.trim()?.ifEmpty { null })
            }

            val updated = postgrest.from("hospitals")
                .update(updateData) {
                    filter { eq("id", hospital.id) }
                    select()
                }.decodeSingle<Hospital>()

            Result.success(updated)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * حذف مستشفى
     */
    suspend fun deleteHospital(id: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            postgrest.from("hospitals").delete {
                filter { eq("id", id) }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * البحث في المستشفيات
     */
    suspend fun searchHospitals(query: String): Result<List<Hospital>> = withContext(Dispatchers.IO) {
        try {
            val hospitals = postgrest.from("hospitals")
                .select {
                    filter {
                        or {
                            ilike("name", "%$query%")
                            ilike("email", "%$query%")
                            ilike("district", "%$query%")
                        }
                    }
                    order("created_at", Order.DESCENDING)
                }.decodeList<Hospital>()
            Result.success(hospitals)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * عدد المستشفيات
     */
    suspend fun getHospitalsCount(): Result<Int> = withContext(Dispatchers.IO) {
        try {
            val rows = postgrest.from("hospitals")
                .select(Columns.list("id"))
                .decodeList<JsonObject>()
            Result.success(rows.size)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
