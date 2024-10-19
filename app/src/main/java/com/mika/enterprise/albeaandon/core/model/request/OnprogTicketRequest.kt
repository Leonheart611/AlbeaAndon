package com.mika.enterprise.albeaandon.core.model.request

import com.google.gson.annotations.SerializedName

data class OnprogTicketRequest(
    @SerializedName("problemToDoId") val problemToDoId: Int,
    @SerializedName("ticketId") val ticketId: Int
)
