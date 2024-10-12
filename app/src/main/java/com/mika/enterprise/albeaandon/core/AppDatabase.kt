package com.mika.enterprise.albeaandon.core

import androidx.room.Database
import androidx.room.RoomDatabase
import com.mika.enterprise.albeaandon.core.dao.UserDao
import com.mika.enterprise.albeaandon.core.model.User

@Database(entities = [User::class], version = 1, exportSchema = false)
abstract class AppDatabase: RoomDatabase() {
    abstract fun userDao(): UserDao
}