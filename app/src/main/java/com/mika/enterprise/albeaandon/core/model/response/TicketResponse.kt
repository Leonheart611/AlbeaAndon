package com.mika.enterprise.albeaandon.core.model.response


import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class TicketResponse(
    @SerializedName("data") val `data`: List<TicketData> = listOf(),
    @SerializedName("messages") val messages: List<String>,
    @SerializedName("page") val page: Page = Page(),
    @SerializedName("success") val success: Boolean
)


data class TicketData(
    @SerializedName("ActionPlan") val actionPlan: String?,
    @SerializedName("ActualFinish") val actualFinish: String?,
    @SerializedName("AssignDate") val assignDate: String?,
    @SerializedName("AssignTo") val assignTo: String?,
    @SerializedName("barcode") val barcode: String,
    @SerializedName("CloseBy") val closeBy: String?,
    @SerializedName("MchLoc") val mchLoc: String,
    @SerializedName("MchNumber") val mchNumber: String,
    @SerializedName("OnProgDate") val onProgDate: String?,
    @SerializedName("Problem") val problem: String?,
    @SerializedName("rfid") val rfid: String,
    @SerializedName("TicketID") val ticketID: Int,
    @SerializedName("TicketStatus") val ticketStatus: String,
    @SerializedName("UAP") val uAP: String,
    @SerializedName("TicketDate") val ticketDate: String
) : Serializable