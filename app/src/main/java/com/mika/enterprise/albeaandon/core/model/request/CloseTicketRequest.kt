package com.mika.enterprise.albeaandon.core.model.request

import com.google.gson.annotations.SerializedName

data class CloseTicketRequest(
    @SerializedName("ticketId") val ticketId: Int,
    @SerializedName("problemToDoId") val problemToDoId: Int
)