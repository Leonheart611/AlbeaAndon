package com.mika.enterprise.albeaandon.core.repository

import android.content.SharedPreferences
import com.mika.enterprise.albeaandon.core.domain.API
import com.mika.enterprise.albeaandon.core.model.request.AssignTicketRequest
import com.mika.enterprise.albeaandon.core.model.request.CloseTicketRequest
import com.mika.enterprise.albeaandon.core.model.request.EscalateTicketMechanicRequest
import com.mika.enterprise.albeaandon.core.model.request.LoginRequest
import com.mika.enterprise.albeaandon.core.model.request.NotifyTicketRequest
import com.mika.enterprise.albeaandon.core.model.request.OnprogTicketRequest
import com.mika.enterprise.albeaandon.core.model.response.AssignTicketResponse
import com.mika.enterprise.albeaandon.core.model.response.LoginResponse
import com.mika.enterprise.albeaandon.core.model.response.PersonnelAvailabilityResponse
import com.mika.enterprise.albeaandon.core.model.response.ProblemGroupResponse
import com.mika.enterprise.albeaandon.core.model.response.ProblemTodo
import com.mika.enterprise.albeaandon.core.model.response.TicketGeneralResponse
import com.mika.enterprise.albeaandon.core.model.response.TicketResponse
import com.mika.enterprise.albeaandon.core.model.response.toUser
import com.mika.enterprise.albeaandon.core.util.Constant.USER_NIK
import com.mika.enterprise.albeaandon.core.util.Constant.USER_TOKEN
import com.mika.enterprise.albeaandon.core.util.ResultResponse
import com.mika.enterprise.albeaandon.core.util.handleGenericError
import com.mika.enterprise.albeaandon.core.util.handleNotFoundError
import com.mika.enterprise.albeaandon.core.util.handleUnauthorizedError
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

interface NetworkRepository {
    suspend fun login(username: String, password: String): Flow<ResultResponse<LoginResponse>>
    suspend fun getTickets(
        status: String,
        page: Int,
        assignTo: String? = null
    ): Flow<ResultResponse<TicketResponse>>

    suspend fun getTicketDetail(ticketId: Int): Flow<ResultResponse<TicketResponse>>
    suspend fun getPersonnelsAvailability(
        userGroup: String,
        userDept: String
    ): Flow<ResultResponse<PersonnelAvailabilityResponse>>

    suspend fun postAssignTicket(
        username: String,
        ticketId: Int
    ): Flow<ResultResponse<AssignTicketResponse>>

    suspend fun getProblemGroup(isEscalated: Int = 0): Flow<ResultResponse<ProblemGroupResponse>>
    suspend fun getProblem(
        problemGroupId: Int,
        isEscalated: Int = 0
    ): Flow<ResultResponse<ProblemGroupResponse>>

    suspend fun getTodoProblem(
        problemId: Int,
        isEscalated: Int = 0
    ): Flow<ResultResponse<ProblemTodo>>

    suspend fun postOnProgressTicket(
        ticketId: Int,
        todoId: Int
    ): Flow<ResultResponse<TicketGeneralResponse>>

    suspend fun postEscalateTicket(
        ticketId: Int,
        message: String
    ): Flow<ResultResponse<TicketGeneralResponse>>

    suspend fun postCloseTicket(
        ticketId: Int,
        problemId: Int
    ): Flow<ResultResponse<TicketGeneralResponse>>

    suspend fun postNotifyTicket(
        ticketId: Int,
        isHelp: Int,
        isDone: Int
    ): Flow<ResultResponse<TicketGeneralResponse>>

    suspend fun logout()
}

class NetworkRepositoryImpl @Inject constructor(
    private val api: API,
    private val sharedPreferences: SharedPreferences,
    private val userRepository: UserRepository
) : NetworkRepository {

    override suspend fun login(
        username: String,
        password: String
    ): Flow<ResultResponse<LoginResponse>> =
        flow {
            try {
                val response = api.login(LoginRequest(username, password))
                when (response.code()) {
                    200 -> {
                        response.body()?.let {
                            sharedPreferences.edit().putString(USER_TOKEN, it.data.token).apply()
                            sharedPreferences.edit().putString(USER_NIK, it.data.userRFID).apply()
                            userRepository.insertUser(it.data.toUser())
                            emit(ResultResponse.Success(it))
                        }
                    }

                    401 -> emit(handleUnauthorizedError(response))
                    404 -> emit(handleNotFoundError(response))
                    else -> emit(handleGenericError(response))
                }
            } catch (e: Exception) {
                emit(handleGenericError(e))
            }
        }

    override suspend fun getTickets(
        status: String,
        page: Int,
        assignTo: String?
    ): Flow<ResultResponse<TicketResponse>> =
        flow {
            try {
                val response =
                    api.getTickets(status = status, page = page, limit = 20, assignTo = assignTo)
                when (response.code()) {
                    in listOf(200, 202) -> response.body()?.let { emit(ResultResponse.Success(it)) }
                    401, 403 -> emit(handleUnauthorizedError(response))
                    else -> emit(handleGenericError(response))
                }
            } catch (e: Exception) {
                emit(handleGenericError(e))
            }
        }

    override suspend fun getTicketDetail(ticketId: Int): Flow<ResultResponse<TicketResponse>> =
        flow {
            try {
                val response = api.getTicketDetail(ticketId)
                when (response.code()) {
                    200 -> response.body()?.let { emit(ResultResponse.Success(it)) }
                    401 -> emit(handleUnauthorizedError(response))
                    404 -> emit(handleNotFoundError(response))
                    else -> emit(handleGenericError(response))
                }
            } catch (e: Exception) {
                emit(handleGenericError(e))
            }
        }

    override suspend fun getPersonnelsAvailability(
        userGroup: String,
        userDept: String
    ): Flow<ResultResponse<PersonnelAvailabilityResponse>> =
        flow {
            try {
                val response =
                    api.getPersonnelsAvailability(userGroup = userGroup, userDept = userDept)
                when (response.code()) {
                    200 -> response.body()?.let { emit(ResultResponse.Success(it)) }
                    401 -> emit(handleUnauthorizedError(response))
                    404 -> emit(handleNotFoundError(response))
                    else -> emit(handleGenericError(response))
                }
            } catch (e: Exception) {
                emit(handleGenericError(e))
            }
        }

    override suspend fun postAssignTicket(
        username: String,
        ticketId: Int
    ): Flow<ResultResponse<AssignTicketResponse>> = flow {
        try {
            val response =
                api.postAssignTicket(AssignTicketRequest(username = username, ticketId = ticketId))
            when (response.code()) {
                200 -> response.body()?.let { emit(ResultResponse.Success(it)) }
                401 -> emit(handleUnauthorizedError(response))
                404 -> emit(handleNotFoundError(response))
                else -> emit(handleGenericError(response))
            }
        } catch (e: Exception) {
            emit(handleGenericError(e))
        }
    }


    override suspend fun getProblemGroup(isEscalated: Int): Flow<ResultResponse<ProblemGroupResponse>> =
        flow {
            try {
                val response = api.getProblemGroup(isEscalated = isEscalated)
                when (response.code()) {
                    200 -> response.body()?.let { emit(ResultResponse.Success(it)) }
                    401 -> emit(handleUnauthorizedError(response))
                    404 -> emit(handleNotFoundError(response))
                    else -> emit(handleGenericError(response))
                }
            } catch (e: Exception) {
                emit(handleGenericError(e))
            }

        }

    override suspend fun getProblem(
        problemGroupId: Int,
        isEscalated: Int
    ): Flow<ResultResponse<ProblemGroupResponse>> = flow {
        try {
            val response =
                api.getProblem(problemGroupId = problemGroupId, isEscalated = isEscalated)
            when (response.code()) {
                200 -> response.body()?.let { emit(ResultResponse.Success(it)) }
                401 -> emit(handleUnauthorizedError(response))
                404 -> emit(handleNotFoundError(response))
                else -> emit(handleGenericError(response))
            }
        } catch (e: Exception) {
            emit(handleGenericError(e))
        }
    }

    override suspend fun getTodoProblem(
        problemId: Int,
        isEscalated: Int
    ): Flow<ResultResponse<ProblemTodo>> = flow {
        val response = api.getTodoProblem(problemId, isEscalated)
        when (response.code()) {
            200 -> response.body()?.let { emit(ResultResponse.Success(it)) }
            401 -> emit(handleUnauthorizedError(response))
            404 -> emit(handleNotFoundError(response))
            else -> emit(handleGenericError(response))
        }
    }

    override suspend fun postOnProgressTicket(
        ticketId: Int,
        todoId: Int
    ): Flow<ResultResponse<TicketGeneralResponse>> = flow {
        val response = api.postOnProgressTicket(
            OnprogTicketRequest(
                problemToDoId = todoId,
                ticketId = ticketId
            )
        )
        when (response.code()) {
            200 -> response.body()?.let { emit(ResultResponse.Success(it)) }
            401 -> emit(handleUnauthorizedError(response))
            404 -> emit(handleNotFoundError(response))
            else -> emit(handleGenericError(response))
        }
    }

    override suspend fun postEscalateTicket(
        ticketId: Int,
        message: String
    ): Flow<ResultResponse<TicketGeneralResponse>> = flow {
        val response = api.postEscalateTicket(EscalateTicketMechanicRequest(ticketId, message))
        when (response.code()) {
            200 -> response.body()?.let { emit(ResultResponse.Success(it)) }
            401 -> emit(handleUnauthorizedError(response))
            404 -> emit(handleNotFoundError(response))
            else -> emit(handleGenericError(response))
        }
    }

    override suspend fun postCloseTicket(
        ticketId: Int,
        problemId: Int
    ): Flow<ResultResponse<TicketGeneralResponse>> =
        flow {
            try {
                val response = api.postCloseTicket(CloseTicketRequest(ticketId, problemId))
                when (response.code()) {
                    200 -> response.body()?.let { emit(ResultResponse.Success(it)) }
                    401 -> emit(handleUnauthorizedError(response))
                    else -> emit(handleGenericError(response))
                }
            } catch (e: Exception) {
                emit(handleGenericError(e))
            }

        }

    override suspend fun postNotifyTicket(
        ticketId: Int,
        isHelp: Int,
        isDone: Int
    ): Flow<ResultResponse<TicketGeneralResponse>> = flow {
        val response = api.postNotifyTicket(NotifyTicketRequest(ticketId, isHelp, isDone))
        when (response.code()) {
            200 -> response.body()?.let { emit(ResultResponse.Success(it)) }
            401 -> emit(handleUnauthorizedError(response))
            404 -> emit(handleNotFoundError(response))
            else -> emit(handleGenericError(response))
        }
    }

    override suspend fun logout() {
        sharedPreferences.edit().clear().apply()
        userRepository.deleteUser()
    }
}