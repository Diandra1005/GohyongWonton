package com.example.gohyongwonton.data.repository

import android.content.Context
import android.content.Intent
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.tasks.await

class AuthRepository(context: Context) {

    private val auth = FirebaseAuth.getInstance()

    private val googleSignInClient: GoogleSignInClient = GoogleSignIn.getClient(
        context,
        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            // ↓↓ GANTI dengan Web Client ID dari Firebase Console
            // Authentication → Sign-in method → Google → Web client ID
            .requestIdToken("649591786782-ethlhpqci464jngnqck6l31cj8mt0sn7.apps.googleusercontent.com")
            .requestEmail()
            .requestProfile()
            .build()
    )

    val currentUser: FirebaseUser? get() = auth.currentUser
    val isLoggedIn: Boolean get()        = auth.currentUser != null
    val currentUserId: String? get()     = auth.currentUser?.uid
    val currentUserName: String? get()   = auth.currentUser?.displayName
    val currentUserEmail: String? get()  = auth.currentUser?.email

    fun getGoogleSignInIntent(): Intent = googleSignInClient.signInIntent

    suspend fun signInWithGoogle(idToken: String): FirebaseUser? {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        return auth.signInWithCredential(credential).await().user
    }

    suspend fun signOut() {
        googleSignInClient.signOut().await()
        auth.signOut()
    }
}