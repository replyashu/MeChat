package com.ashu.yalchat.chat.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Message(
    @PrimaryKey val id: String = "",
    val senderId: String,
    val senderName: String? = "",
    val receiverName: String? = "",
    val receiverId: String,
    val message: String,
    val timestamp: Long,
    val status: String = "pending",
    val isSynced: Boolean = false
)