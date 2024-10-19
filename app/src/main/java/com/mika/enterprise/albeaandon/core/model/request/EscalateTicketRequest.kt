package com.mika.enterprise.albeaandon.core.model.request

import com.google.gson.annotations.SerializedName

data class EscalateTicketRequest(
    @SerializedName("ticketId") val ticketId: Int,
    @SerializedName("message") val message: String

)