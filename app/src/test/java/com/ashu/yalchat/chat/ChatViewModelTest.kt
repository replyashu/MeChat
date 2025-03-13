package com.ashu.yalchat.chat

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.ashu.yalchat.chat.data.Message
import io.mockk.every
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNotSame
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ChatViewModelTest {
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()
    private val testDispatcher = StandardTestDispatcher()

    private lateinit var chatViewModel: ChatViewModel
    private val chatUseCase: ChatUseCase = mockk(relaxed = true)

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        chatViewModel = ChatViewModel(chatUseCase)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadChatMessage should update messages state`() = runTest {
        // Given
        val senderId = "user1"
        val receiverId = "user2"
        val fakeMessages = listOf(
            Message(id = "1", senderId = senderId, senderName = "sender 1", receiverName = "receiver 1", receiverId = receiverId, message = "Hello!", timestamp = 12345L),
            Message(id = "2", senderId = receiverId, senderName = "sender 1", receiverName = "receiver 1", receiverId = senderId, message = "Hi!", timestamp = 12346L)
        )

        every { chatUseCase.getMessagesBetweenUsers(senderId, receiverId) } returns flowOf(fakeMessages)
        chatViewModel.loadChatMessage(receiverId)

        assertNotSame(fakeMessages, chatViewModel.messages.value)
    }

    @Test
    fun `loadAllLastMessages should update lastChats state`() = runTest {
        // Given
        val fakeChats = listOf(
            Message(id = "1", senderId = "user1", senderName = "sender 1", receiverName = "receiver 1", receiverId = "user2", message = "Last message", timestamp = 12345L)
        )

        every { chatUseCase.getLastChats(any(), any()) } answers {
            secondArg<(List<Message>) -> Unit>().invoke(fakeChats)
        }

        // When
        chatViewModel.loadAllLastMessages()

        // Then
        assertEquals(fakeChats, chatViewModel.lastChats.value)
    }

    @Test
    fun `observeTypingStatus should update isTyping state`() = runTest {
        // Given
        every { chatUseCase.observeTypingStatus(any(), any()) } answers {
            secondArg<(Boolean) -> Unit>().invoke(true)
        }

        // When
        chatViewModel.observeTypingStatus("user2")

        // Then
        assertEquals(true, chatViewModel.isTyping.value)
    }

}