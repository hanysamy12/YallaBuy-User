package com.example.yallabuy_user.data.remote

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await

class FireBaseService(
    private val firebaseAuth: FirebaseAuth
) {
    private var statues: Boolean = true
    suspend fun createUserAccount(email: String, password: String): Boolean {
        return try {
            firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
                if (it.isSuccessful) {
                    statues = true
                    firebaseAuth.currentUser?.sendEmailVerification()
                } else {
                    statues = false
                }

            }
            statues
        } catch (e: Exception) {
            Log.i("TAG", "createUserAccount in firebase service error ${e.message}  ")
            false
        }
    }

    suspend fun loginUser(email: String, password: String): Boolean {
        return try {
            val result = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            val isVerified = result.user?.isEmailVerified ?: false
            Log.i("login", "loginUser fireBase success")
            Log.i("login", "loginUser fireBase validation $isVerified")
            isVerified
        } catch (e: Exception) {
            Log.e("login", "loginUser error: ${e.message}")
            false
        }
    }
}