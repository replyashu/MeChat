package com.ashu.yalchat.chat.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class LocalTypingStatus(
    @PrimaryKey val userId: String,
    val isTyping: Boolean
)