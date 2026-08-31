package com.bagomri.yemenbloodbank.core.constants

/**
 * إعدادات وثوابت التطبيق والاتصال بـ Supabase
 */
object AppConfig {
    const val SUPABASE_URL = "https://wdvsjpdrlvydoohvvhtx.supabase.co"
    const val SUPABASE_ANON_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6IndkdnNqcGRybHZ5ZG9vaHZ2aHR4Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3Nzk5MDg2MTEsImV4cCI6MjA5NTQ4NDYxMX0.AFT-aJBoQECUE1f1vFSHooxWebsUgJaXL7BrChm0v_g"

    const val DONATION_INTERVAL_DAYS = 180L // 6 months
    const val DEFAULT_COUNTRY_CODE = "+967"
    const val BUCKET_BANNERS = "banners"
}
