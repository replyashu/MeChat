package com.ashu.yalchat.login.api

import com.ashu.yalchat.login.AuthStateResponse
import com.ashu.yalchat.login.DeleteAccountResponse
import com.ashu.yalchat.login.FirebaseSignInResponse
import com.ashu.yalchat.login.OneTapSignInResponse
import com.ashu.yalchat.login.SignOutResponse
import com.google.android.gms.auth.api.identity.SignInCredential
import kotlinx.coroutines.CoroutineScope

interface AuthApiService {

    fun getAuthState(viewModelScope: CoroutineScope): AuthStateResponse

    suspend fun verifyGoogleSignIn(): Boolean

    suspend fun signInAnonymously(): FirebaseSignInResponse

    suspend fun onTapSignIn(): OneTapSignInResponse

    suspend fun signInWithGoogle(credential: SignInCredential): FirebaseSignInResponse

    suspend fun signOut(): SignOutResponse

    suspend fun authorizeGoogleSignIn(): String?

    suspend fun deleteUserAccount(googleIdToken: String?): DeleteAccountResponse

    fun checkNeedsReAuth(): Boolean
}