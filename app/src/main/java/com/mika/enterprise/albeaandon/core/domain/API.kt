package com.mika.enterprise.albeaandon.core.domain

import com.mika.enterprise.albeaandon.core.model.request.AssignTicketRequest
import com.mika.enterprise.albeaandon.core.model.request.LoginRequest
import com.mika.enterprise.albeaandon.core.model.response.AssignTicketResponse
import com.mika.enterprise.albeaandon.core.model.response.LoginResponse
import com.mika.enterprise.albeaandon.core.model.response.PersonnelAvailabilityResponse
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
        @Query("uap") uap: String = "BASIC",
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20
    ): Response<PersonnelAvailabilityResponse>

    @POST("/api/tickets/assign")
    suspend fun postAssignTicket(
        @Body param: AssignTicketRequest
    ): Response<AssignTicketResponse>

}