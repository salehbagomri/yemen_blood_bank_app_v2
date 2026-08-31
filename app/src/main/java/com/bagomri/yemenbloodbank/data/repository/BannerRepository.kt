package com.bagomri.yemenbloodbank.data.repository

import com.bagomri.yemenbloodbank.core.constants.AppConfig
import com.bagomri.yemenbloodbank.core.network.SupabaseProvider
import com.bagomri.yemenbloodbank.data.model.Banner
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.query.Order
import io.github.jan.supabase.storage.Storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

/**
 * مستودع إدارة البانرات الإعلانية والتوعوية
 */
class BannerRepository(
    private val postgrest: Postgrest = SupabaseProvider.postgrest,
    private val storage: Storage = SupabaseProvider.storage
) {

    /**
     * الحصول على البانرات النشطة المتاحة حالياً
     */
    suspend fun getActiveBanners(): Result<List<Banner>> = withContext(Dispatchers.IO) {
        try {
            val banners = postgrest.from("banners")
                .select {
                    filter { eq("is_active", true) }
                    order("sort_order", Order.ASCENDING)
                }.decodeList<Banner>()

            val active = banners.filter { it.isCurrentlyVisible }
            Result.success(active)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * الحصول على جميع البانرات للأدمن
     */
    suspend fun getAllBanners(): Result<List<Banner>> = withContext(Dispatchers.IO) {
        try {
            val banners = postgrest.from("banners")
                .select {
                    order("sort_order", Order.ASCENDING)
                }.decodeList<Banner>()
            Result.success(banners)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * رفع صورة بانر إلى التخزين السحابي
     */
    suspend fun uploadBannerImage(fileName: String, fileBytes: ByteArray): Result<String> = withContext(Dispatchers.IO) {
        try {
            val uniqueName = "banner_${System.currentTimeMillis()}_$fileName"
            storage.from(AppConfig.BUCKET_BANNERS).upload(uniqueName, fileBytes) {
                upsert = true
            }
            Result.success(uniqueName)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * حذف صورة بانر
     */
    suspend fun deleteBannerImage(imagePath: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            storage.from(AppConfig.BUCKET_BANNERS).delete(imagePath)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * إنشاء بانر جديد
     */
    suspend fun createBanner(banner: Banner): Result<Banner> = withContext(Dispatchers.IO) {
        try {
            val insertData = buildJsonObject {
                put("title", banner.title.trim())
                put("subtitle", banner.subtitle?.trim()?.ifEmpty { null })
                put("image_path", banner.imagePath?.ifEmpty { null })
                put("action_type", banner.actionType)
                put("action_value", banner.actionValue?.trim()?.ifEmpty { null })
                put("sort_order", banner.sortOrder)
                put("is_active", banner.isActive)
                put("icon_name", banner.iconName?.ifEmpty { null })
                put("bg_gradient", banner.bgGradient?.ifEmpty { null })
                put("starts_at", banner.startsAt)
                put("ends_at", banner.endsAt)
            }

            val created = postgrest.from("banners")
                .insert(insertData) {
                    select()
                }.decodeSingle<Banner>()

            Result.success(created)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * تحديث بيانات بانر
     */
    suspend fun updateBanner(banner: Banner): Result<Banner> = withContext(Dispatchers.IO) {
        try {
            val updateData = buildJsonObject {
                put("title", banner.title.trim())
                put("subtitle", banner.subtitle?.trim()?.ifEmpty { null })
                put("image_path", banner.imagePath?.ifEmpty { null })
                put("action_type", banner.actionType)
                put("action_value", banner.actionValue?.trim()?.ifEmpty { null })
                put("sort_order", banner.sortOrder)
                put("is_active", banner.isActive)
                put("icon_name", banner.iconName?.ifEmpty { null })
                put("bg_gradient", banner.bgGradient?.ifEmpty { null })
                put("starts_at", banner.startsAt)
                put("ends_at", banner.endsAt)
            }

            val updated = postgrest.from("banners")
                .update(updateData) {
                    filter { eq("id", banner.id) }
                    select()
                }.decodeSingle<Banner>()

            Result.success(updated)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * تفعيل أو تعطيل بانر
     */
    suspend fun toggleBannerStatus(id: String, isActive: Boolean): Result<Banner> = withContext(Dispatchers.IO) {
        try {
            val updated = postgrest.from("banners")
                .update(buildJsonObject { put("is_active", isActive) }) {
                    filter { eq("id", id) }
                    select()
                }.decodeSingle<Banner>()
            Result.success(updated)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * حذف بانر
     */
    suspend fun deleteBanner(id: String, imagePath: String? = null): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            postgrest.from("banners").delete {
                filter { eq("id", id) }
            }
            if (!imagePath.isNullOrEmpty()) {
                try {
                    deleteBannerImage(imagePath)
                } catch (_: Exception) {}
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * إعادة ترتيب البانرات دفعة واحدة
     */
    suspend fun reorderBanners(bannerIds: List<String>): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val params = buildJsonObject {
                put("p_ids", JsonArray(bannerIds.map { JsonPrimitive(it) }))
            }
            postgrest.rpc("reorder_banners", params)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
