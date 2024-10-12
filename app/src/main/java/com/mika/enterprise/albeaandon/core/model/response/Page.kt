package com.mika.enterprise.albeaandon.core.model.response

import com.google.gson.annotations.SerializedName

data class Page(
    @SerializedName("current") val current: Int = 0,
    @SerializedName("limit") val limit: Int = 0,
    @SerializedName("total") val total: Int = 0
)
