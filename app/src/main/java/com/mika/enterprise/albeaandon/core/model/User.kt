package com.mika.enterprise.albeaandon.core.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user")
data class User(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userRfid: String,
    val username: String,
    val userDept: String,
    val active: Boolean,
    val userGrup: String,
    val userLocation: String
)