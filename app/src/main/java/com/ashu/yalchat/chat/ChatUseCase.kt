package com.ashu.yalchat.chat

import com.ashu.yalchat.chat.data.Message
import com.ashu.yalchat.chat.repository.ChatRepository
import javax.inject.Inject

class ChatUseCase @Inject constructor(private val repository: ChatRepository) {

    fun sendMessage(senderId: String, receiverId: String, message: String, receiverName: String?, senderName: String?)
        = repository.sendMessage(senderId, receiverId, message, receiverId, senderName)

    fun updateTypingStatus(userId: String, isTyping: Boolean) = repository.updateTypingStatus(userId, isTyping)

    fun observeTypingStatus(userId: String, onTypingStatusChanged: (Boolean) -> Unit) =
        repository.observeTypingStatus(userId, onTypingStatusChanged)

    fun getAllUsers() = repository.getAllUsers()

    fun getMessagesBetweenUsers(senderId: String, receiverId: String) = repository.getMessagesBetweenUsers(senderId, receiverId)

    fun getLastChats(userId: String?, onChatsReceived: (List<Message>) -> Unit) = repository.getLastChats(userId, onChatsReceived)
}