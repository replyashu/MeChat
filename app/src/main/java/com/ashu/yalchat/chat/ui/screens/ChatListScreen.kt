package com.ashu.yalchat.chat.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.ashu.yalchat.chat.ChatViewModel
import com.ashu.yalchat.login.DataProvider

@Composable
fun ChatListScreen(navHostController: NavHostController, viewModel: ChatViewModel = hiltViewModel()) {

    val messages by viewModel.lastChats.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadAllLastMessages()
    }

    Column(modifier = Modifier.fillMaxSize().padding(8.dp)) {
        Text("All Messages", style = MaterialTheme.typography.bodyMedium)
        Column {
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(messages.sortedByDescending { it.timestamp }) { msg ->
                    var name = DataProvider.user?.displayName
                    Card(modifier = Modifier.padding(6.dp), onClick = {
                        navHostController.navigate("chat/${msg.senderId}/${name?.trim()}/${msg.receiverName?.trim()}")
                    }) {
                        Row {
                            Text(
                                modifier = Modifier
                                    .padding(16.dp)
                                    .align(Alignment.CenterVertically)
                                    .drawBehind {
                                        drawCircle(
                                            color = Color.Red,
                                            radius = this.size.minDimension
                                        )
                                    },
                                text = if (name.isNullOrEmpty()) "Y" else  name.toUpperCase().trim().substring(0, 1)
                            )
                            Column(modifier = Modifier.fillMaxWidth()) {
                                Text(fontSize = 16.sp, fontStyle = FontStyle.Italic, modifier = Modifier.padding(top = 8.dp), text = msg.receiverName + "")
                                Text(fontSize = 14.sp, modifier = Modifier.padding(top = 8.dp), text = msg.message)
                            }

                        }
                    }
                }
            }
        }

    }
}
