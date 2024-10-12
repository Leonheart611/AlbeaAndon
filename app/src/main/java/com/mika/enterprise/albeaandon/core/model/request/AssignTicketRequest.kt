package com.mika.enterprise.albeaandon.core.model.request

import com.google.gson.annotations.SerializedName

data class AssignTicketRequest(
    @SerializedName("username") val username: String,
    @SerializedName("ticketId") val ticketId: Int
)
