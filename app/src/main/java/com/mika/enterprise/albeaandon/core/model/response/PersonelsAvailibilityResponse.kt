package com.mika.enterprise.albeaandon.core.model.response

import com.google.gson.annotations.SerializedName


data class PersonnelAvailabilityResponse(
    @SerializedName("data") val data: List<PersonnelData> = listOf(),
    @SerializedName("messages") val messages: List<String>,
    @SerializedName("page") val page: Page = Page(),
    @SerializedName("success") val success: Boolean
)

data class PersonnelData(
    @SerializedName("JumlahTicketOnProgress") val jumlahTicketOnProgress: Int,
    @SerializedName("UserDept") val userDept: String,
    @SerializedName("UserGroup") val userGroup: String,
    @SerializedName("UserName") val userName: String,
    @SerializedName("UserRFID") val userRFID: String,
)



