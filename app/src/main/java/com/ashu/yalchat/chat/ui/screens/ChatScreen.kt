package com.ashu.yalchat.chat.ui.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.graphics.Color.Companion.Gray
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ashu.yalchat.chat.ChatViewModel
import com.ashu.yalchat.login.DataProvider

@Composable
fun ChatScreen(receiverId: String, userName: String, receiverName: String, viewModel: ChatViewModel = hiltViewModel()) {
    var message by remember { mutableStateOf("") }
    val messages by viewModel.messages.collectAsState()
    val isTyping by viewModel.isTyping.collectAsState()

    LaunchedEffect(receiverId) {
        viewModel.loadChatMessage(receiverId)
        viewModel.observeTypingStatus(receiverId)
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.Top, horizontalAlignment = Alignment.CenterHorizontally
    ) {
        var name = DataProvider.user?.displayName

        Log.d("namaaa", name + "  " + receiverName)
        if (name.equals(receiverName.trim())) name = "Me"
        Text("Chat with $receiverName", style = MaterialTheme.typography.bodyMedium)
        LazyColumn(modifier = Modifier
            .weight(1f)
            .fillMaxWidth()
            .wrapContentHeight()) {
            items(messages.sortedBy { it.timestamp }) { msg ->
                Text(modifier = Modifier.align(if (name == receiverName) Alignment.Start else Alignment.End), text = "${name}: ${msg.message} (${msg.status})")
            }
        }
        if (isTyping) {
            Text("$receiverName is typing...")
        }
        OutlinedTextField(
            textStyle = LocalTextStyle.current.copy(color = Black),
            value = message,
            onValueChange = {
                message = it
                viewModel.updateTypingStatus(it.isNotEmpty()) },
            label = { Text(fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Gray, text = "Enter message") },
            modifier = Modifier.fillMaxWidth().background(Color.LightGray)
        )
        Button(
            onClick = {
                viewModel.sendMessage(receiverId, message, receiverName.trim())
                message = ""
            },
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
        ) {
            Text("Send")
        }
    }
}

