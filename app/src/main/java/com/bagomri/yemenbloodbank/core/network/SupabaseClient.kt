package com.bagomri.yemenbloodbank.core.network

import com.bagomri.yemenbloodbank.core.constants.AppConfig
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.realtime.Realtime
import io.github.jan.supabase.realtime.realtime
import io.github.jan.supabase.storage.Storage
import io.github.jan.supabase.storage.storage
import io.ktor.client.engine.okhttp.OkHttp
import kotlin.time.Duration.Companion.seconds

/**
 * مدير عميل Supabase المركزي للتطبيق
 */
object SupabaseProvider {

    val client: SupabaseClient by lazy {
        createSupabaseClient(
            supabaseUrl = AppConfig.SUPABASE_URL,
            supabaseKey = AppConfig.SUPABASE_ANON_KEY
        ) {
            httpEngine = OkHttp.create {
                config {
                    connectTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
                    readTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
                    writeTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
                }
            }

            install(Postgrest) {
                defaultSchema = "public"
            }

            install(Auth) {
                alwaysAutoRefresh = true
            }

            install(Realtime)

            install(Storage) {
                transferTimeout = 60.seconds
            }
        }
    }

    val postgrest: Postgrest get() = client.postgrest
    val auth: Auth get() = client.auth
    val realtime: Realtime get() = client.realtime
    val storage: Storage get() = client.storage
}
