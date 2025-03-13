package com.ashu.yalchat.chat.repository

import com.ashu.yalchat.chat.data.Message
import com.ashu.yalchat.chat.data.User
import com.ashu.yalchat.chat.repository.db.MessageDao
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class ChatRepository @Inject constructor(private val firestore: FirebaseFirestore, private val messageDao: MessageDao) {
    private val db = FirebaseFirestore.getInstance()
    private val usersCollection = db.collection("users")


    fun sendMessage(senderId: String, receiverId: String, message: String, receiverName: String?, senderName: String?) {
        if (message.isEmpty() || message.isBlank()) return
        val chatRef = firestore.collection("chats").document()
        val chatMessage = Message(id = chatRef.id, senderId = senderId, receiverId = receiverId, message = message, timestamp = System.currentTimeMillis(), status = "pending", receiverName = receiverName, senderName = senderName, isSynced = false)
        messageDao.insertMessage(chatMessage)

         chatRef.set(chatMessage.copy(status = "sent", isSynced = true))
             .addOnSuccessListener {
                 messageDao.insertMessage(chatMessage.copy(status = "sent", isSynced = true))
                 syncUnsyncedMessages()
             }
    }

    private fun syncUnsyncedMessages() {
        val unsyncedMessages = messageDao.getUnsyncedMessages()
        unsyncedMessages.forEach { message ->
            val chatMessage = hashMapOf(
                "id" to message.id,
                "senderId" to message.senderId,
                "receiverId" to message.receiverId,
                "message" to message.message,
                "senderName" to message.senderName,
                "receiverName" to message.receiverName,
                "timestamp" to message.timestamp,
                "status" to "pending"
            )

            chatMessage["status"] = "sent"
            firestore.collection("chats").add(chatMessage)
                .addOnSuccessListener {
                    messageDao.markMessageAsSynced(message.id)
                }
        }
    }

    fun updateTypingStatus(userId: String, isTyping: Boolean) {
        firestore.collection("typing_status").document(userId).set(mapOf("isTyping" to isTyping))
    }

    fun observeTypingStatus(userId: String, onTypingStatusChanged: (Boolean) -> Unit) {
        firestore.collection("typing_status").document(userId)
            .addSnapshotListener { snapshot, _ ->
                snapshot?.getBoolean("isTyping")?.let(onTypingStatusChanged)
            }
    }

    fun getAllUsers(): Flow<List<User>> = callbackFlow {
        val subscription = usersCollection.addSnapshotListener { snapshot, _ ->
            val users = snapshot?.documents?.mapNotNull { it.toObject(User::class.java) } ?: emptyList()
            trySend(users)
        }
        awaitClose { subscription.remove() }
    }

    fun getMessagesBetweenUsers(senderId: String, receiverId: String): Flow<List<Message>> = callbackFlow {
        // Load from local database first
        val cachedMessages = messageDao.getMessagesBetweenUsers(senderId, receiverId)

        // Listen to Firestore for real-time updates
        firestore.collection("chats")
            .whereIn("senderId", listOf(senderId, receiverId))
            .whereIn("receiverId", listOf(senderId, receiverId))
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, _ ->
                snapshot?.let {
                    val messages = it.documents.map { doc ->
                        Message(
                            id = doc.getString("id") ?: "",
                            senderId = doc.getString("senderId") ?: "",
                            receiverId = doc.getString("receiverId") ?: "",
                            message = doc.getString("message") ?: "",
                            timestamp = doc.getLong("timestamp") ?: 0L,
                            status = doc.getString("status") ?: "read",
                            senderName = doc.getString("senderName") ?: "",
                            receiverName = doc.getString("receiverName") ?: "",
                            isSynced = true
                        )
                    }

                    messages.forEach { msg ->
                        firestore.collection("chats").document(msg.id.toString())
                            .update("status", "read")
                        messageDao.insertMessage(msg)
                    }
                }
            }
        cachedMessages.collect {
            trySend(it)
        }

        awaitClose {
            channel.close()
        }
    }

    fun getLastChats(userId: String?, onChatsReceived: (List<Message>) -> Unit) {
        val currentUserId = userId ?: return
        val lastMessages = messageDao.getLastChats(currentUserId)
        onChatsReceived(lastMessages)

        firestore.collection("chats")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, _ ->
                snapshot?.let {
                    val messages = it.documents.map { doc ->
                        Message(
                            id = doc.getString("id") ?: "",
                            senderId = doc.getString("senderId") ?: "",
                            receiverId = doc.getString("receiverId") ?: "",
                            message = doc.getString("message") ?: "",
                            timestamp = doc.getLong("timestamp") ?: 0L,
                            status = doc.getString("status") ?: "delivered",
                            senderName = doc.getString("senderName") ?: "",
                            receiverName = doc.getString("receiverName") ?: "",
                            isSynced = true
                        )
                    }
                    messages.forEach { msg -> messageDao.insertMessage(msg) }
                    onChatsReceived(messageDao.getLastChats(currentUserId))
                }
            }
    }
}
