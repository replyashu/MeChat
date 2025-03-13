package com.ashu.yalchat.db

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.ashu.yalchat.chat.data.Message
import com.ashu.yalchat.chat.repository.db.AppDatabase
import com.ashu.yalchat.chat.repository.db.MessageDao
import kotlinx.coroutines.runBlocking
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MessageDaoTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var database: AppDatabase
    private lateinit var messageDao: MessageDao

    @Before
    fun setup() {
        // Use an in-memory database for testing (data is not persisted)
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).allowMainThreadQueries().build()

        messageDao = database.messageDao()
    }

    @After
    fun teardown() {
        database.close()
    }

    @Test
    fun insertMessage_andRetrieveByReceiverId() = run {
        val message = Message(
            id = "1",
            senderId = "user1",
            senderName = "sender 1",
            receiverName = "receiver 1",
            receiverId = "user2",
            message = "Hello!",
            timestamp = System.currentTimeMillis(),
            status = "sent",
            isSynced = false
        )

        messageDao.insertMessage(message)
        val messages = messageDao.getMessages("user2")

        assert(messages.isNotEmpty())
        assertThat(messages[0].message, true)
    }

    @Test
    fun insertMessage_andGetByReceiverId() = runBlocking {
        val message = Message(
            id = "1",
            senderId = "user1",
            senderName = "sender 1",
            receiverName = "receiver 1",
            receiverId = "user2",
            message = "Hello, World!",
            timestamp = System.currentTimeMillis(),
            status = "sent",
            isSynced = false
        )

        messageDao.insertMessage(message)
        val messages = messageDao.getMessages("user2")

        Assert.assertEquals(1, messages.size)
        Assert.assertEquals("Hello, World!", messages[0].message)
    }

    @Test
    fun insertMessage_andGetUnsyncedMessages() = runBlocking {
        val message1 = Message("2", "user1","sender 1", "receiver 1", "user2", "First Message", System.currentTimeMillis(), "sent", false)
        val message2 = Message("3", "user2", "sender 2", "receiver 2",  "user1", "Second Message", System.currentTimeMillis(), "sent", true)

        messageDao.insertMessage(message1)
        messageDao.insertMessage(message2)

        val unsyncedMessages = messageDao.getUnsyncedMessages()
        Assert.assertEquals(1, unsyncedMessages.size)
        Assert.assertEquals("First Message", unsyncedMessages[0].message)
    }

    @Test
    fun markMessageAsSynced_updatesCorrectly() = runBlocking {
        val message = Message("4", "user1", "sender 1", "receiver 1","user2", "Test Sync", System.currentTimeMillis(), "sent", false)
        messageDao.insertMessage(message)

        messageDao.markMessageAsSynced("4")
        val unsyncedMessages = messageDao.getUnsyncedMessages()

        Assert.assertTrue(unsyncedMessages.isEmpty())
    }

}