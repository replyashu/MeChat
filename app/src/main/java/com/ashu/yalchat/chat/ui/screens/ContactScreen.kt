package com.ashu.yalchat.chat.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.ashu.yalchat.chat.ChatViewModel
import com.ashu.yalchat.chat.ui.component.ContactChips

@Composable
fun ContactScreen(navHostController: NavHostController, viewModel: ChatViewModel = hiltViewModel()) {
    val contacts by viewModel.users.collectAsState()

    Column {
        ContactChips(navHostController, contacts)
    }
}