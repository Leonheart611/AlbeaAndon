package com.mika.enterprise.albeaandon.core.repository

import android.content.SharedPreferences
import com.mika.enterprise.albeaandon.core.domain.API
import com.mika.enterprise.albeaandon.core.model.request.LoginRequest
import com.mika.enterprise.albeaandon.core.model.response.LoginResponse
import com.mika.enterprise.albeaandon.core.model.response.PersonnelAvailabilityResponse
import com.mika.enterprise.albeaandon.core.model.response.TicketResponse
import com.mika.enterprise.albeaandon.core.model.response.toUser
import com.mika.enterprise.albeaandon.core.util.Constant.USER_TOKEN
import com.mika.enterprise.albeaandon.core.util.ErrorResponse
import com.mika.enterprise.albeaandon.core.util.ResultResponse
import com.mika.enterprise.albeaandon.core.util.toErrorResponseValue
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

interface NetworkRepository {
    suspend fun login(username: String, password: String): Flow<ResultResponse<LoginResponse>>
    suspend fun getTickets(status: String, page: Int): Flow<ResultResponse<TicketResponse>>
    suspend fun getPersonnelsAvailability(userGroup: String): Flow<ResultResponse<PersonnelAvailabilityResponse>>
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
            val response = api.login(LoginRequest(username, password))
            when (response.code()) {
                200 -> {
                    response.body()?.let {
                        sharedPreferences.edit().putString(USER_TOKEN, it.data.token).apply()
                        userRepository.insertUser(it.data.toUser())
                        emit(ResultResponse.Success(it))
                    }
                }

                401 -> {
                    emit(
                        ResultResponse.UnAuthorized(
                            ErrorResponse(
                                code = response.code(),
                                message = response.errorBody()
                                    .toErrorResponseValue().messages?.firstOrNull().orEmpty()
                            )
                        )
                    )
                }

                404 -> {
                    response.body()?.let {
                        emit(ResultResponse.Success(it))
                    } ?: run {
                        emit(
                            ResultResponse.Error(
                                Exception(
                                    "Error Login -- ${response.code()} -- ${
                                        response.errorBody()?.string()
                                    }"
                                ),
                                ErrorResponse(code = response.code(), message = response.message())
                            )
                        )
                    }
                }
            }
        }

    override suspend fun getTickets(
        status: String,
        page: Int
    ): Flow<ResultResponse<TicketResponse>> =
        flow {
            val response = api.getTickets(status = status, page, 20)
            when (response.code()) {
                200 -> response.body()?.let { emit(ResultResponse.Success(it)) }
                401 -> {
                    emit(
                        ResultResponse.UnAuthorized(
                            ErrorResponse(
                                code = response.code(),
                                message = response.errorBody()
                                    .toErrorResponseValue().messages?.firstOrNull().orEmpty()
                            )
                        )
                    )
                }

                404 -> {
                    response.errorBody().toErrorResponseValue().let {
                        emit(
                            ResultResponse.Success(
                                TicketResponse(
                                    messages = it.messages.orEmpty(),
                                    success = it.success ?: false,
                                )
                            )
                        )
                    }
                }
            }
        }


    override suspend fun getPersonnelsAvailability(userGroup: String): Flow<ResultResponse<PersonnelAvailabilityResponse>> =
        flow {
            val response = api.getPersonnelsAvailability(userGroup = userGroup)
            when (response.code()) {
                200 -> response.body()?.let { emit(ResultResponse.Success(it)) }
                401 -> {
                    emit(
                        ResultResponse.UnAuthorized(
                            ErrorResponse(
                                code = response.code(),
                                message = response.errorBody()
                                    .toErrorResponseValue().messages?.firstOrNull().orEmpty()
                            )
                        )
                    )
                }

                404 -> {
                    response.errorBody().toErrorResponseValue().let {
                        emit(
                            ResultResponse.Success(
                                PersonnelAvailabilityResponse(
                                    messages = it.messages.orEmpty(),
                                    success = it.success ?: false,
                                )
                            )
                        )
                    }
                }
            }
        }

    override suspend fun logout() {
        sharedPreferences.edit().clear().apply()
        userRepository.deleteUser()
    }
}