package com.mika.enterprise.albeaandon.core.model.response

import com.google.gson.annotations.SerializedName

data class ProblemGroupResponse(
    @SerializedName("data") val problemGroup: List<ProblemGeneralResponse> = listOf(),
    @SerializedName("messages") val messages: List<String> = listOf(),
    @SerializedName("page") val page: Page = Page(),
    @SerializedName("success") val success: Boolean
)


