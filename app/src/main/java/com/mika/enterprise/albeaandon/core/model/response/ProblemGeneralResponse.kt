package com.mika.enterprise.albeaandon.core.model.response

import com.google.gson.annotations.SerializedName

data class ProblemGeneralResponse(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String
)