package com.undabot.izzy.models

/**
 * Used to identify resources in DataPool
 */
data class ResourceID(
    val id: String?,
    val type: String,
    val method: String? = null,
    val `temp-id`: String? = null,
)
