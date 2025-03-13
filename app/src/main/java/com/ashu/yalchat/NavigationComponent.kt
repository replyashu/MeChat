package com.ashu.yalchat

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.ashu.yalchat.chat.ui.screens.ChatListScreen
import com.ashu.yalchat.chat.ui.screens.ChatScreen
import com.ashu.yalchat.chat.ui.screens.ContactScreen
import com.ashu.yalchat.login.AuthViewModel
import com.ashu.yalchat.login.ui.LoginScreen
import com.ashu.yalchat.ui.HomeScreen

@Composable
fun NavigationComponent(navController: NavHostController, authViewModel: AuthViewModel) {
    NavHost(
        navController = navController,
        startDestination = "login"
    ) {
        composable("login") { LoginScreen(navController, authViewModel) }
        composable("home") { HomeScreen(navController, authViewModel) }
        composable("contactList") { ContactScreen(navController) }
        composable("chatList") { ChatListScreen(navController) }
        composable("chat/{receiverId}/{userName}/{receiverName}") { backStackEntry ->
            val receiverId = backStackEntry.arguments?.getString("receiverId") ?: ""
            val userName = backStackEntry.arguments?.getString("userName") ?: ""
            val receiverName = backStackEntry.arguments?.getString("receiverName") ?: ""
            ChatScreen(receiverId, userName, receiverName)
        }
    }
}