package com.mika.enterprise.albeaandon.core.repository

import com.mika.enterprise.albeaandon.core.dao.UserDao
import com.mika.enterprise.albeaandon.core.model.User
import javax.inject.Inject

interface UserRepository {
    suspend fun login(): User?
    suspend fun insertUser(user: User)
    suspend fun deleteUser()
}

class UserRepositoryImpl @Inject constructor(private val userDao: UserDao) : UserRepository {
    override suspend fun login(): User? {
        return userDao.getUser()
    }

    override suspend fun insertUser(user: User) {
        userDao.insertUser(user)
    }

    override suspend fun deleteUser() {
        userDao.deleteUser()
    }

}