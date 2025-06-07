package com.example.yallabuy_user.data.remote

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await

class FireBaseService(
    private val firebaseAuth: FirebaseAuth
) {
    private var statues: String = ""
    suspend fun createUserAccount(email: String, password: String): String {
       return  try {
            firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
                if (it.isSuccessful) {
                    val validation = firebaseAuth.currentUser?.sendEmailVerification()
                    if (validation?.isSuccessful == true) {
                       statues =  "Account created successfully"
                    } else {
                     statues =  "error ${validation?.exception?.message ?: "Unknown Error"}"
                    }
                } else {
                  statues =   " error ${it.exception?.message}"
                }
            }.await()
            statues
        } catch (e: Exception) {
            Log.i("TAG", "createUserAccount in firebase service error ${e.message}  ")
            statues = "error ${e.message}"
           statues
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