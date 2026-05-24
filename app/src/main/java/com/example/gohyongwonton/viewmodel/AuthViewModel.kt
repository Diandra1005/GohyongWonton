package com.example.gohyongwonton.viewmodel

import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gohyongwonton.data.repository.AuthRepository
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class AuthState {
    object Loading         : AuthState()
    object Unauthenticated : AuthState()
    data class Authenticated(val user: FirebaseUser) : AuthState()
    data class Error(val message: String)            : AuthState()
}

class AuthViewModel(
    private val authRepo: AuthRepository
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Loading)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    val isLoggedIn: Boolean      get() = authRepo.isLoggedIn
    val currentUserId: String?   get() = authRepo.currentUserId
    val currentUserName: String? get() = authRepo.currentUserName
    val currentUserEmail: String? get() = authRepo.currentUserEmail  // ← tambahan

    init {
        val user = authRepo.currentUser
        _authState.value = if (user != null) AuthState.Authenticated(user)
        else AuthState.Unauthenticated
    }

    fun handleGoogleSignInResult(data: Intent?) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val account: GoogleSignInAccount = GoogleSignIn
                    .getSignedInAccountFromIntent(data)
                    .getResult(ApiException::class.java)
                val idToken = account.idToken ?: throw Exception("ID Token kosong")
                val user = authRepo.signInWithGoogle(idToken)
                _authState.value = if (user != null) AuthState.Authenticated(user)
                else AuthState.Error("Login gagal, coba lagi")
            } catch (e: ApiException) {
                _authState.value = AuthState.Error("Google Sign-In gagal: ${e.statusCode}")
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "Terjadi kesalahan")
            }
        }
    }

    fun signOut() {
        viewModelScope.launch {
            authRepo.signOut()
            _authState.value = AuthState.Unauthenticated
        }
    }

    fun getSignInIntent(): Intent = authRepo.getGoogleSignInIntent()
}