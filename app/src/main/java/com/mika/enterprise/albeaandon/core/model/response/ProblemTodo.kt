package com.mika.enterprise.albeaandon.core.model.response

import com.google.gson.annotations.SerializedName

data class ProblemTodo(
    @SerializedName("data") val todoProblem: List<TodoProblemData> = listOf(),
    @SerializedName("messages") val messages: List<String> = listOf(),
    @SerializedName("page") val page: Page = Page(),
    @SerializedName("success") val success: Boolean
)

data class TodoProblemData(
    @SerializedName("dept_pic") val deptPic: String,
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String
)

fun List<TodoProblemData>.toGeneralProblem(): List<ProblemGeneralResponse> {
    return this.map {
        ProblemGeneralResponse(
            id = it.id,
            name = it.name
        )
    }
}

