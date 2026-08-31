package com.bagomri.yemenbloodbank.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * نماذج المناطق الجغرافية (المحافظات والمديريات)
 */
@Serializable
data class Governorate(
    val name: String = "",
    @SerialName("is_active")
    val isActive: Boolean = true,
    @SerialName("sort_order")
    val sortOrder: Int = 0
)

@Serializable
data class District(
    val id: String = "",
    val governorate: String = "",
    val name: String = "",
    @SerialName("is_active")
    val isActive: Boolean = true
)

/**
 * حزمة المناطق المفعّلة المستخدمة في القوائم المنسدلة
 */
@Serializable
data class LocationData(
    val governorates: List<String> = emptyList(),
    val districtsByGov: Map<String, List<String>> = emptyMap()
) {
    val isEmpty: Boolean get() = governorates.isEmpty()
}
