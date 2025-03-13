package com.ashu.yalchat.ui

import android.app.Activity
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavHostController
import com.ashu.yalchat.chat.ui.screens.ChatListScreen
import com.ashu.yalchat.chat.ui.screens.ContactScreen
import com.ashu.yalchat.login.AuthState
import com.ashu.yalchat.login.DataProvider
import com.ashu.yalchat.login.ui.component.OneTapSignIn
import com.ashu.yalchat.login.ui.LoginScreen
import com.ashu.yalchat.ui.theme.YalChatTheme
import com.ashu.yalchat.login.AuthViewModel
import com.google.android.gms.auth.api.identity.BeginSignInResult
import com.google.android.gms.common.api.ApiException


@Composable
fun HomeScreen(navController: NavHostController,
    authViewModel: AuthViewModel
) {
    val openLoginDialog = remember { mutableStateOf(false) }
    val openDeleteAccountAlertDialog = remember { mutableStateOf(false) }
    val authState = DataProvider.authState

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            try {
                val credential = authViewModel.oneTapClient.getSignInCredentialFromIntent(result.data)
                authViewModel.deleteAccount(credential.googleIdToken)
            }
            catch (e: ApiException) {
                Log.e("HomeScreen:Launcher","Re-auth error: $e")
            }
        }
    }

    fun launch(signInResult: BeginSignInResult) {
        val intent = IntentSenderRequest.Builder(signInResult.pendingIntent.intentSender).build()
        launcher.launch(intent)
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.primary
    ) { paddingValues ->
        Column (
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .fillMaxSize()
                .wrapContentSize(Alignment.TopCenter),
            Arrangement.spacedBy(8.dp),
            Alignment.CenterHorizontally
        ) {
            Card(
                modifier = Modifier.padding(16.dp),
                shape = RoundedCornerShape(10.dp),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 0.dp,
                    pressedElevation = 0.dp
                ),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.tertiary
                )
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalAlignment = Alignment.Start,
                ) {
                    if (authState == AuthState.SignedIn) {
                        Text(
                            DataProvider.user?.displayName ?: "Name Placeholder",
                            fontWeight = FontWeight.Bold
                        )
//                        Text(DataProvider.user?.email ?: "Email Placeholder")
                    }
                    else {
                        Text(
                            "Sign-in to view data!"
                        )
                    }
                }
            }

            Row {
                Button(
                    onClick = {
                        if (authState != AuthState.SignedIn)
                            openLoginDialog.value = true
                        else
                            authViewModel.signOut()
                    },
                    modifier = Modifier
                        .size(width = 160.dp, height = 50.dp)
                        .padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.tertiary,
                    )
                ) {
                    Text(
                        text = if (authState != AuthState.SignedIn) "Login" else "Sign out",
                        modifier = Modifier.padding(6.dp),
                        color = MaterialTheme.colorScheme.inversePrimary
                    )
                }

                Button(
                    onClick = {
                        // Show message to the user before deleting account
                        openDeleteAccountAlertDialog.value = true
                    },
                    modifier = Modifier
                        .size(width = 200.dp, height = 50.dp)
                        .padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.tertiary,
                    )
                ) {
                    Text(
                        text = "Deactivate",
                        modifier = Modifier.padding(6.dp),
                        color = Color.Red
                    )
                }
            }
            Column(
                modifier = Modifier
                    .padding(16.dp),
                verticalArrangement = Arrangement.SpaceEvenly,) {
                ContactScreen(navController)
                ChatListScreen(navController)
            }

        }

        BackHandler {

        }

        AnimatedVisibility(visible = openLoginDialog.value) {
            Dialog(
                onDismissRequest = { openLoginDialog.value = false },
                properties = DialogProperties(
                    usePlatformDefaultWidth = false // experimental
                )
            ) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    LoginScreen(navController, authViewModel, openLoginDialog)
                }
            }
        }

        AnimatedVisibility(visible = openDeleteAccountAlertDialog.value) {
            AlertDialog(
                onDismissRequest = {
                    openDeleteAccountAlertDialog.value = false
                },
                title = { Text("Delete Account") },
                text = {
                    Text("Deleting account is permanent. Are you sure you want to delete your account?")
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            authViewModel.checkNeedsReAuth()
                            openDeleteAccountAlertDialog.value = false
                        }
                    ) {
                        Text("Yes, Delete", color = Color.Red)
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            openDeleteAccountAlertDialog.value = false
                        }
                    ) {
                        Text("Dismiss")
                    }
                }
            )
        }
    }

    OneTapSignIn (
        launch = {
            launch(it)
        }
    )
}


@Preview
@Composable
fun HomeScreenPreview() {
    YalChatTheme   {
//        HomeScreen(hiltViewModel)
    }
}