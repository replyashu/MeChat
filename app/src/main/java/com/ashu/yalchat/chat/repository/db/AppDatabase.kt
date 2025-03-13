package com.ashu.yalchat.chat.repository.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.ashu.yalchat.chat.data.LocalTypingStatus
import com.ashu.yalchat.chat.data.Message

@Database(entities = [Message::class, LocalTypingStatus::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun messageDao(): MessageDao
}