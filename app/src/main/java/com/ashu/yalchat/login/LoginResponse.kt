package com.ashu.yalchat.login

import com.google.android.gms.auth.api.identity.BeginSignInResult
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.StateFlow

typealias OneTapSignInResponse = LoginResponse<BeginSignInResult>
typealias FirebaseSignInResponse = LoginResponse<AuthResult>
typealias SignOutResponse = LoginResponse<Boolean>
typealias DeleteAccountResponse = LoginResponse<Boolean>
typealias AuthStateResponse = StateFlow<FirebaseUser?>

sealed class LoginResponse<out T> {
    data object Loading: LoginResponse<Nothing>()
    data class Success<out T>(val data: T?): LoginResponse<T>()
    data class Failure(val e: Exception): LoginResponse<Nothing>()
}

