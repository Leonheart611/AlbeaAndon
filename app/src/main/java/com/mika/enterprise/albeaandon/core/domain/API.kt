package com.mika.enterprise.albeaandon.core.domain

import com.mika.enterprise.albeaandon.core.model.request.AssignTicketRequest
import com.mika.enterprise.albeaandon.core.model.request.LoginRequest
import com.mika.enterprise.albeaandon.core.model.response.AssignTicketResponse
import com.mika.enterprise.albeaandon.core.model.response.LoginResponse
import com.mika.enterprise.albeaandon.core.model.response.PersonnelAvailabilityResponse
import com.mika.enterprise.albeaandon.core.model.response.ProblemGroupResponse
import com.mika.enterprise.albeaandon.core.model.response.ProblemTodo
import com.mika.enterprise.albeaandon.core.model.response.TicketResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Query

interface API {

    @Headers("Content-Type: application/json")
    @POST("/auth/login")
    suspend fun login(@Body param: LoginRequest): Response<LoginResponse>

    @GET("/api/tickets")
    suspend fun getTickets(
        @Query("status") status: String,
        @Query("page") page: Int,
        @Query("limit") limit: Int
    ): Response<TicketResponse>

    @GET("/api/tickets/personnels")
    suspend fun getPersonnelsAvailability(
        @Query("group") userGroup: String,
        @Query("dept") userDept: String,
        @Query("uap") uap: String = "BASIC",
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20
    ): Response<PersonnelAvailabilityResponse>

    @Headers("Content-Type: application/json")
    @POST("/api/tickets/assign")
    suspend fun postAssignTicket(
        @Body param: AssignTicketRequest
    ): Response<AssignTicketResponse>

    @GET("/api/problems/problem_group")
    suspend fun getProblemGroup(
        @Query("is_escalated") isEscalated: Int,
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 100
    ): Response<ProblemGroupResponse>

    @GET("/api/problems/problem")
    suspend fun getProblem(
        @Query("problem_group_id") problemGroupId: Int,
        @Query("is_escalated") isEscalated: Int,
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 100
    ): Response<ProblemGroupResponse>

    @GET("/api/problems/todo")
    suspend fun getTodoProblem(
        @Query("problem_id") problemId: Int,
        @Query("is_escalated") isEscalated: Int,
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 100
    ): Response<ProblemTodo>

}