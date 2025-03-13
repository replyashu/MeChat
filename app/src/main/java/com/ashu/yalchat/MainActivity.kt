package com.ashu.yalchat

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import com.ashu.yalchat.login.AuthState
import com.ashu.yalchat.login.AuthViewModel
import com.ashu.yalchat.login.DataProvider
import com.ashu.yalchat.login.ui.LoginScreen
import com.ashu.yalchat.ui.theme.YalChatTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LoginActivity()
        }
    }
}

@Composable
fun LoginActivity() {
    val navController = rememberNavController()

    YalChatTheme {

        val authViewModel = hiltViewModel<AuthViewModel>()
        val currentUser = authViewModel.currentUser.collectAsState().value
        DataProvider.updateAuthState(currentUser)
        NavigationComponent(navController, authViewModel)

        if (DataProvider.authState != AuthState.SignedOut) {
            navController.navigate("home")
        } else {
            LoginScreen(navController, authViewModel)
        }
    }
}