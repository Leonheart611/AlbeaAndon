package com.mika.enterprise.albeaandon.core.model.response

import com.google.gson.annotations.SerializedName
import com.mika.enterprise.albeaandon.core.model.User

data class LoginResponse(
    @SerializedName("data") val data: Data,
    @SerializedName("messages") val messages: List<String>,
    @SerializedName("success") val success: Boolean
)

data class Data(
    @SerializedName("Active") val active: Boolean,
    @SerializedName("role_id") val roleId: Int,
    @SerializedName("token") val token: String,
    @SerializedName("UserDept") val userDept: String?,
    @SerializedName("UserGroup") val userGroup: String?,
    @SerializedName("UserLoc") val userLoc: String,
    @SerializedName("UserName") val userName: String,
    @SerializedName("UserRFID") val userRFID: String,
    @SerializedName("UserTitle") val userTitle: String,
    @SerializedName("UserUAP") val userUAP: String
)

fun Data.toUser(): User {
    return User(
        userRfid = userRFID,
        username = userName,
        userDept = userDept.orEmpty(),
        active = active,
        userGrup = userGroup ?: "",
        userLocation = userLoc
    )
}


