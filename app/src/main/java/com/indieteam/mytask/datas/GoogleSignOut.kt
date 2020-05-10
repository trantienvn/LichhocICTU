package com.indieteam.mytask.datas

import android.content.Context
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions

class GoogleSignOut(val context: Context) {

    fun signOut() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .build()
        val mGoogleSignInClient = GoogleSignIn.getClient(context, gso)
        mGoogleSignInClient.signOut()
    }
}