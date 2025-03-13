package com.ashu.yalchat.login

import com.ashu.yalchat.login.api.AuthApiServiceImpl
import com.google.android.gms.auth.api.identity.SignInCredential
import kotlinx.coroutines.CoroutineScope
import javax.inject.Inject

class AuthRepository @Inject constructor(private val authApiService: AuthApiServiceImpl) {

    fun getAuthState(viewModelScope: CoroutineScope) = authApiService.getAuthState(viewModelScope)

    suspend fun verifyGoogleSignIn() = authApiService.verifyGoogleSignIn()

    suspend fun signInAnonymously() = authApiService.signInAnonymously()

    suspend fun onTapSignIn() = authApiService.onTapSignIn()

    suspend fun signInWithGoogle(credential: SignInCredential) = authApiService.signInWithGoogle(credential)

    suspend fun signOut() = authApiService.signOut()

    suspend fun authorizeGoogleSignIn() = authApiService.authorizeGoogleSignIn()

    suspend fun deleteUserAccount(googleIdToken: String?) = authApiService.deleteUserAccount(googleIdToken)

    fun checkNeedsReAuth() = authApiService.checkNeedsReAuth()
}