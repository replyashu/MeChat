package com.ashu.yalchat.login

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.auth.api.identity.SignInCredential
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repository: AuthRepository,
    val oneTapClient: SignInClient
): ViewModel() {

    val currentUser = getAuthState()

    init {
        getAuthState()
        CoroutineScope(Dispatchers.IO).launch {
            repository.verifyGoogleSignIn()
        }
    }

    private fun getAuthState() = repository.getAuthState(viewModelScope)

    fun oneTapSignIn() = CoroutineScope(Dispatchers.IO).launch {
        DataProvider.oneTapSignInResponse = LoginResponse.Loading
        DataProvider.oneTapSignInResponse = repository.onTapSignIn()
    }

    fun signInWithGoogle(credentials: SignInCredential) = CoroutineScope(Dispatchers.IO).launch {
        DataProvider.googleSignInResponse = LoginResponse.Loading
        DataProvider.googleSignInResponse = repository.signInWithGoogle(credentials)
    }

    fun signOut() = CoroutineScope(Dispatchers.IO).launch {
        DataProvider.signOutResponse = LoginResponse.Loading
        DataProvider.signOutResponse = repository.signOut()
    }

    fun checkNeedsReAuth() = CoroutineScope(Dispatchers.IO).launch {
        if (repository.checkNeedsReAuth()) {
            val idToken = repository.authorizeGoogleSignIn()
            if (idToken != null) {
                deleteAccount(idToken)
            }
            else {
                oneTapSignIn()
                Log.i("AuthViewModel:deleteAccount","OneTapSignIn")
            }
        } else {
            deleteAccount(null)
        }
    }

    fun deleteAccount(googleIdToken: String?) = CoroutineScope(Dispatchers.IO).launch {
        DataProvider.deleteAccountResponse = LoginResponse.Loading
        DataProvider.deleteAccountResponse = repository.deleteUserAccount(googleIdToken)
    }
}