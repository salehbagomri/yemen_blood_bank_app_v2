package com.bagomri.yemenbloodbank.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * نموذج بيانات الإحصائيات العامة للنظام
 */
@Serializable
data class Statistics(
    @SerialName("total_donors")
    val totalDonors: Int = 0,
    @SerialName("most_common_blood_type")
    val mostCommonBloodType: String? = null,
    @SerialName("most_common_blood_type_count")
    val mostCommonBloodTypeCount: Int = 0,
    @SerialName("most_active_district")
    val mostActiveDistrict: String? = null,
    @SerialName("most_active_district_count")
    val mostActiveDistrictCount: Int = 0,
    @SerialName("latest_donor_name")
    val latestDonorName: String? = null,
    @SerialName("latest_donor_date")
    val latestDonorDate: String? = null,
    @SerialName("blood_type_distribution")
    val bloodTypeDistribution: Map<String, Int> = emptyMap(),
    @SerialName("district_distribution")
    val districtDistribution: Map<String, Int> = emptyMap(),
    @SerialName("last_updated")
    val lastUpdated: String? = null
) {
    /**
     * توزيع المتبرعين مجمَّعاً حسب المحافظة (يطوي مفاتيح "المحافظة - المديرية")
     */
    val governorateDistribution: Map<String, Int>
        get() {
            val result = mutableMapOf<String, Int>()
            districtDistribution.forEach { (key, count) ->
                val gov = key.substringBefore(" - ")
                result[gov] = (result[gov] ?: 0) + count
            }
            return result
        }
}

/**
 * نموذج إحصائيات لوحة المستشفى
 */
data class DashboardStatistics(
    val totalDonors: Int = 0,
    val availableDonors: Int = 0,
    val suspendedDonors: Int = 0,
    val inactiveDonors: Int = 0,
    val newDonorsThisMonth: Int = 0,
    val mostCommonBloodType: String? = null,
    val mostCommonBloodTypeCount: Int = 0,
    val coveredDistrictsCount: Int = 0,
    val bloodTypeDistribution: Map<String, Int> = emptyMap(),
    val districtDistribution: Map<String, Int> = emptyMap(),
    val recentDonors: List<Donor> = emptyList(),
    val recentDonations: List<Donor> = emptyList(),
    val lastUpdated: String? = null
)
