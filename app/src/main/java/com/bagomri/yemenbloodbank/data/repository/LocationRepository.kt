package com.bagomri.yemenbloodbank.data.repository

import com.bagomri.yemenbloodbank.core.constants.AppStrings
import com.bagomri.yemenbloodbank.core.network.SupabaseProvider
import com.bagomri.yemenbloodbank.data.model.District
import com.bagomri.yemenbloodbank.data.model.Governorate
import com.bagomri.yemenbloodbank.data.model.LocationData
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.query.Order
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put

/**
 * مستودع إدارة المناطق والمحافظات والمديريات
 */
class LocationRepository(
    private val postgrest: Postgrest = SupabaseProvider.postgrest
) {

    /**
     * الحصول على المناطق المفعّلة مع fallback ذكي على الثوابت المحلية
     */
    suspend fun getActiveLocations(): LocationData = withContext(Dispatchers.IO) {
        try {
            val govs = postgrest.from("governorates")
                .select {
                    filter { eq("is_active", true) }
                    order("sort_order", Order.ASCENDING)
                }.decodeList<JsonObject>()

            val dists = postgrest.from("districts")
                .select {
                    filter { eq("is_active", true) }
                    order("name", Order.ASCENDING)
                }.decodeList<JsonObject>()

            val govNames = govs.mapNotNull { it["name"]?.jsonPrimitive?.content }
            val map = mutableMapOf<String, MutableList<String>>()

            dists.forEach { d ->
                val g = d["governorate"]?.jsonPrimitive?.content ?: return@forEach
                val n = d["name"]?.jsonPrimitive?.content ?: return@forEach
                map.getOrPut(g) { mutableListOf() }.add(n)
            }

            if (govNames.isNotEmpty()) {
                LocationData(governorates = govNames, districtsByGov = map)
            } else {
                fallbackLocalLocations()
            }
        } catch (e: Exception) {
            fallbackLocalLocations()
        }
    }

    private fun fallbackLocalLocations(): LocationData {
        return LocationData(
            governorates = AppStrings.districts,
            districtsByGov = AppStrings.governorateDistricts
        )
    }

    /**
     * جميع المحافظات (للأدمن)
     */
    suspend fun getAllGovernorates(): Result<List<Governorate>> = withContext(Dispatchers.IO) {
        try {
            val list = postgrest.from("governorates")
                .select {
                    order("sort_order", Order.ASCENDING)
                }.decodeList<Governorate>()
            Result.success(list)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * مديريات محافظة معينة
     */
    suspend fun getDistrictsOf(governorate: String): Result<List<District>> = withContext(Dispatchers.IO) {
        try {
            val list = postgrest.from("districts")
                .select {
                    filter { eq("governorate", governorate) }
                    order("name", Order.ASCENDING)
                }.decodeList<District>()
            Result.success(list)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * تفعيل أو تعطيل محافظة
     */
    suspend fun setGovernorateActive(name: String, isActive: Boolean): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            postgrest.from("governorates")
                .update(buildJsonObject { put("is_active", isActive) }) {
                    filter { eq("name", name) }
                }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * تفعيل أو تعطيل مديرية
     */
    suspend fun setDistrictActive(id: String, isActive: Boolean): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            postgrest.from("districts")
                .update(buildJsonObject { put("is_active", isActive) }) {
                    filter { eq("id", id) }
                }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * إضافة مديرية جديدة
     */
    suspend fun addDistrict(governorate: String, name: String): Result<District> = withContext(Dispatchers.IO) {
        try {
            val insertData = buildJsonObject {
                put("governorate", governorate)
                put("name", name.trim())
            }
            val created = postgrest.from("districts")
                .insert(insertData) {
                    select()
                }.decodeSingle<District>()
            Result.success(created)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * التحقق إن كانت المديرية مستخدمة في سجلات المتبرعين
     */
    suspend fun isDistrictInUse(governorate: String, name: String): Boolean = withContext(Dispatchers.IO) {
        try {
            val params = buildJsonObject {
                put("p_governorate", governorate)
                put("p_name", name)
            }
            postgrest.rpc("district_in_use", params).decodeAs<Boolean>()
        } catch (e: Exception) {
            false
        }
    }

    /**
     * تعديل اسم مديرية
     */
    suspend fun updateDistrict(id: String, governorate: String, oldName: String, newName: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            if (isDistrictInUse(governorate, oldName)) {
                return@withContext Result.failure(Exception("لا يمكن تعديل المديرية: توجد سجلات متبرعين مرتبطة بها"))
            }
            postgrest.from("districts")
                .update(buildJsonObject { put("name", newName.trim()) }) {
                    filter { eq("id", id) }
                }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * حذف مديرية
     */
    suspend fun deleteDistrict(id: String, governorate: String, name: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            if (isDistrictInUse(governorate, name)) {
                return@withContext Result.failure(Exception("لا يمكن حذف المديرية: توجد سجلات متبرعين مرتبطة بها"))
            }
            postgrest.from("districts").delete {
                filter { eq("id", id) }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
