package com.mika.enterprise.albeaandon.core.model.response

import com.google.gson.annotations.SerializedName

data class AssignTicketResponse(
    @SerializedName("messages") val messages: List<String>,
    @SerializedName("success") val success: Boolean
)
