package net.oneorder.izzygraffiti.models

import com.google.gson.annotations.SerializedName
import com.undabot.izzy.annotations.Type
import com.undabot.izzy.models.IzzyResource

@Type("brands")
class BrandResponse(
    id: String? = null,
    val name: String? = null,
    val position: Int? = null,
    @SerializedName("original_url")
    val originalUrl: String? = null
) : IzzyResource(id = id)