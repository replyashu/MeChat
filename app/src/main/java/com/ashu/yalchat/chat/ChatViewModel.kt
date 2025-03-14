package com.ashu.yalchat.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ashu.yalchat.chat.data.User
import com.ashu.yalchat.chat.data.Message
import com.ashu.yalchat.login.DataProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(private val chatUseCase: ChatUseCase): ViewModel() {

    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages = _messages.asStateFlow()
    private val _users = MutableStateFlow<List<User>>(emptyList())
    val users = _users.asStateFlow()
    val currentUser = DataProvider.user
    private var typingJob: Job? = null

    private val _lastChats = MutableStateFlow<List<Message>>(emptyList())
    val lastChats: StateFlow<List<Message>> get() = _lastChats

    fun loadChatMessage(receiverId: String)  {
        viewModelScope.launch {
            val senderId = currentUser?.uid ?: return@launch
            chatUseCase.getMessagesBetweenUsers(senderId, receiverId).collect { msg ->
                _messages.value = msg
            }
        }
    }

    fun loadAllLastMessages() {
        chatUseCase.getLastChats(currentUser?.uid) { chats ->
            _lastChats.value = chats
        }
    }


    private val _isTyping = MutableStateFlow(false)
    val isTyping: StateFlow<Boolean> get() = _isTyping

    init {
        fetchUsers()
        updateTypingStatus(false)
    }


    private fun fetchUsers() {
        viewModelScope.launch {
            chatUseCase.getAllUsers().collect { _users.value = it }
        }
    }

    fun sendMessage(receiverId: String, message: String, receiverName: String?) = viewModelScope.launch {
        currentUser?.let {
            chatUseCase.sendMessage(it.uid, receiverId, message, receiverName, it.displayName)
        }
    }

    fun updateTypingStatus(isTyping: Boolean) {
        var typed = isTyping
        if (isTyping) {
            typingJob?.cancel()
            typingJob = viewModelScope.launch {
                delay(2000)
                typed = true
                _isTyping.value = false
            }
        } else {
            typed = false
        }
        currentUser?.let {
            chatUseCase.updateTypingStatus(it.uid, typed)
        }

    }

    fun observeTypingStatus(userId: String) {
        chatUseCase.observeTypingStatus(userId) { isTyping ->
            _isTyping.value = isTyping
        }
    }
}
