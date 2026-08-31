package com.bagomri.yemenbloodbank.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * نموذج بيانات مدير النظام (الأدمن)
 */
@Serializable
data class Admin(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    @SerialName("is_active")
    val isActive: Boolean = true,
    @SerialName("created_at")
    val createdAt: String? = null,
    @SerialName("updated_at")
    val updatedAt: String? = null
)
