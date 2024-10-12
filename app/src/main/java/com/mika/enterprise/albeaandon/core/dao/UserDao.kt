package com.mika.enterprise.albeaandon.core.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.mika.enterprise.albeaandon.core.model.User

@Dao
interface UserDao {

    @Query("SELECT * FROM user LIMIT 1")
    suspend fun getUser(): User?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User)

    @Query("DELETE FROM user")
    suspend fun deleteUser()
}