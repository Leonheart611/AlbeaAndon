package com.mika.enterprise.albeaandon.core.model.request

import com.google.gson.annotations.SerializedName

data class NotifyTicketRequest(
    @SerializedName("ticketId") val ticketId: Int,
    @SerializedName("is_help") val isHelp: Int,
    @SerializedName("is_done") val isDone: Int
)
