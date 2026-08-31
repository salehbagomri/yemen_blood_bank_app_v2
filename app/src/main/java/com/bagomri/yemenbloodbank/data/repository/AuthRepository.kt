package com.bagomri.yemenbloodbank.data.repository

import com.bagomri.yemenbloodbank.core.network.SupabaseProvider
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.auth.status.SessionStatus
import io.github.jan.supabase.auth.user.UserInfo
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonPrimitive

/**
 * مستودع إدارة المصادقة والمستخدمين
 */
class AuthRepository(
    private val auth: Auth = SupabaseProvider.auth
) {

    val sessionStatus: Flow<SessionStatus> get() = auth.sessionStatus

    val currentUser: UserInfo? get() = auth.currentUserOrNull()

    val currentUserId: String? get() = currentUser?.id

    val isLoggedIn: Boolean get() = currentUser != null

    /**
     * تسجيل الدخول بالبريد الإلكتروني وكلمة المرور
     */
    suspend fun signIn(email: String, password: String): Result<UserInfo> = withContext(Dispatchers.IO) {
        try {
            auth.signInWith(Email) {
                this.email = email.trim()
                this.password = password
            }
            val user = currentUser ?: throw Exception("تعذر استرداد بيانات المستخدم بعد تسجيل الدخول")
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * تسجيل الخروج
     */
    suspend fun signOut(): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            auth.signOut()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * التحقق من نوع المستخدم الحالي (أدمن أو مستشفى)
     */
    suspend fun getUserType(): Result<String?> = withContext(Dispatchers.IO) {
        if (!isLoggedIn) return@withContext Result.success(null)

        try {
            // محاولة التحقق عبر RPCs أولاً
            try {
                val isAdmin = SupabaseProvider.postgrest.rpc("is_admin").decodeAs<Boolean>()
                if (isAdmin) return@withContext Result.success("admin")
            } catch (_: Exception) {}

            try {
                val isHospital = SupabaseProvider.postgrest.rpc("is_hospital").decodeAs<Boolean>()
                if (isHospital) return@withContext Result.success("hospital")
            } catch (_: Exception) {}

            // fallback: استعلام الجداول المباشرة
            val uid = currentUserId ?: return@withContext Result.success(null)

            val adminRows = SupabaseProvider.postgrest.from("admins")
                .select(Columns.list("id")) {
                    filter { eq("id", uid) }
                    limit(1)
                }.decodeList<JsonObject>()

            if (adminRows.isNotEmpty()) {
                return@withContext Result.success("admin")
            }

            val hospitalRows = SupabaseProvider.postgrest.from("hospitals")
                .select(Columns.list("id")) {
                    filter { eq("id", uid) }
                    limit(1)
                }.decodeList<JsonObject>()

            if (hospitalRows.isNotEmpty()) {
                return@withContext Result.success("hospital")
            }

            Result.success(null)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * الحصول على المحافظة الخاصة بحساب المستشفى الحالي (للحوكمة الجغرافية)
     */
    suspend fun getCurrentHospitalGovernorate(): Result<String?> = withContext(Dispatchers.IO) {
        val uid = currentUserId ?: return@withContext Result.success(null)
        try {
            val response = SupabaseProvider.postgrest.from("hospitals")
                .select(Columns.list("governorate", "district")) {
                    filter { eq("id", uid) }
                    limit(1)
                }.decodeSingleOrNull<JsonObject>()

            if (response == null) return@withContext Result.success(null)

            val gov = response["governorate"]?.jsonPrimitive?.content
            if (!gov.isNullOrEmpty()) {
                return@withContext Result.success(gov)
            }

            val district = response["district"]?.jsonPrimitive?.content
            if (!district.isNullOrEmpty()) {
                return@withContext Result.success(district.substringBefore(" - "))
            }

            Result.success(null)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
