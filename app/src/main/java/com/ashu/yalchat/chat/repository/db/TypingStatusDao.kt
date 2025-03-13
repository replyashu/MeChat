package com.ashu.yalchat.chat.repository.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ashu.yalchat.chat.data.LocalTypingStatus

@Dao
interface TypingStatusDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertTypingStatus(status: LocalTypingStatus)

    @Query("SELECT * FROM LocalTypingStatus WHERE userId = :userId")
    fun getTypingStatus(userId: String): LocalTypingStatus?
}