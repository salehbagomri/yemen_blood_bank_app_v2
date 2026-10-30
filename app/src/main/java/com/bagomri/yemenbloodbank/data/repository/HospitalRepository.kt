package com.bagomri.yemenbloodbank.data.repository

import com.bagomri.yemenbloodbank.core.constants.AppConfig
import com.bagomri.yemenbloodbank.core.network.SupabaseProvider
import com.bagomri.yemenbloodbank.data.model.Hospital
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.postgrest.query.Order
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.JsonNull
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
     * إضافة مستشفى جديد
     * 1. إنشاء حساب المستخدم في Supabase Auth باستخدام عميل مصادقة مستقل حتى لا يتم تسجيل خروج الأدمن
     * 2. استدعاء الدالة الخادمية المصرحة add_hospital_bypassing_rls لربط الحساب وحفظ بيانات المستشفى
     */
    suspend fun addHospital(hospital: Hospital, password: String): Result<Hospital> = withContext(Dispatchers.IO) {
        var tempClient: io.github.jan.supabase.SupabaseClient? = null
        try {
            val adminId = SupabaseProvider.auth.currentUserOrNull()?.id
                ?: throw Exception("يجب تسجيل الدخول كمدير نظام أولاً")

            // 1. إنشاء عميل Supabase مستقل لعملية التسجيل لحماية جلسة الأدمن الحالية
            tempClient = io.github.jan.supabase.createSupabaseClient(
                supabaseUrl = com.bagomri.yemenbloodbank.core.constants.AppConfig.SUPABASE_URL,
                supabaseKey = com.bagomri.yemenbloodbank.core.constants.AppConfig.SUPABASE_ANON_KEY
            ) {
                install(io.github.jan.supabase.auth.Auth)
            }

            val hospitalEmail = hospital.email.trim()
            val hospitalName = hospital.name.trim()

            // تسجيل الحساب الجديد في Auth
            val userResponse = tempClient.auth.signUpWith(io.github.jan.supabase.auth.providers.builtin.Email) {
                this.email = hospitalEmail
                this.password = password
                data = buildJsonObject {
                    put("role", "hospital")
                    put("name", hospitalName)
                }
            }

            val userId = userResponse?.id ?: tempClient.auth.currentUserOrNull()?.id
                ?: throw Exception("فشل إنشاء حساب المستخدم. يرجى التحقق من البريد الإلكتروني وكلمة المرور")

            // 2. استدعاء دالة قاعدة البيانات add_hospital_bypassing_rls
            val districtValue = if (hospital.district.isNotBlank()) {
                hospital.district
            } else {
                hospital.governorate
            }

            val rpcParams = buildJsonObject {
                put("p_admin_id", adminId)
                put("p_hospital_id", userId)
                put("p_name", hospitalName)
                put("p_email", hospitalEmail)
                put("p_district", districtValue)
                if (!hospital.phoneNumber.isNullOrBlank()) {
                    put("p_phone_number", hospital.phoneNumber.trim())
                } else {
                    put("p_phone_number", kotlinx.serialization.json.JsonNull)
                }
                if (!hospital.address.isNullOrBlank()) {
                    put("p_address", hospital.address.trim())
                } else {
                    put("p_address", kotlinx.serialization.json.JsonNull)
                }
            }

            postgrest.rpc("add_hospital_bypassing_rls", rpcParams)

            // في حال رغب الأدمن بتعطيل الحساب منذ البداية
            if (!hospital.isActive) {
                toggleHospitalStatus(userId, false)
            }

            val created = getHospitalById(userId).getOrNull() ?: hospital.copy(id = userId)
            Result.success(created)
        } catch (e: Exception) {
            Result.failure(e)
        } finally {
            try {
                tempClient?.close()
            } catch (_: Exception) {}
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
                put("is_active", hospital.isActive)
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
     * تفعيل أو تعطيل مستشفى
     */
    suspend fun toggleHospitalStatus(id: String, isActive: Boolean): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val updateData = buildJsonObject { put("is_active", isActive) }
            postgrest.from("hospitals").update(updateData) {
                filter { eq("id", id) }
            }
            Result.success(Unit)
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
