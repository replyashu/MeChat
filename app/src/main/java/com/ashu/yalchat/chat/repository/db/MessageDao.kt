package com.ashu.yalchat.chat.repository.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ashu.yalchat.chat.data.Message
import kotlinx.coroutines.flow.Flow

@Dao
interface MessageDao {
    @Query("SELECT * FROM Message WHERE receiverId = :receiverId ORDER BY timestamp DESC")
    fun getMessages(receiverId: String): List<Message>

    @Query("SELECT * FROM Message WHERE isSynced = 0")
    fun getUnsyncedMessages(): List<Message>

    @Query("SELECT * FROM Message ORDER BY timestamp ASC")
    fun getAllMessages(): List<Message>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertMessage(message: Message)

    @Query("UPDATE Message SET isSynced = 1 WHERE id = :messageId")
    fun markMessageAsSynced(messageId: String)

    @Query("UPDATE Message SET status = :status WHERE id = :messageId")
    fun setStatusOfMessages(messageId: String, status: String)

    @Query("""
        SELECT * FROM Message 
        WHERE (senderId = :senderId AND receiverId = :receiverId) 
           OR (senderId = :receiverId AND receiverId = :senderId)
        ORDER BY timestamp ASC
    """)
    fun getMessagesBetweenUsers(senderId: String, receiverId: String): Flow<List<Message>>

    @Query("""
        SELECT * FROM Message 
        WHERE id IN (
            SELECT MAX(id) FROM Message 
            WHERE senderId = :currentUserId OR receiverId = :currentUserId
            GROUP BY CASE 
                WHEN senderId < receiverId THEN senderId || receiverId
                ELSE receiverId || senderId 
            END
        )
        ORDER BY timestamp DESC
    """)
    fun getLastChats(currentUserId: String): List<Message>
}
