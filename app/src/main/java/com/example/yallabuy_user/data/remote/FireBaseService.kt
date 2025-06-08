package com.example.yallabuy_user.data.remote

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.tasks.await

class FireBaseService(
    private val firebaseAuth: FirebaseAuth
) {
    private var statues: String = ""
    suspend fun createUserAccount(email: String, password: String): Flow<String> {
       return  try {
           firebaseAuth.createUserWithEmailAndPassword(email, password).await()
           try {
               firebaseAuth.currentUser?.sendEmailVerification()?.await()
               Log.i("createUser", "Account and verification email sent")
               flowOf("Account created successfully. Verification email sent.")
           } catch (e: Exception) {
               Log.i("createUser", "Verification email failed: ${e.message}")
               flowOf("Account created error, but failed to send verification email: ${e.message}")
           }
        } catch (e: Exception) {
            Log.i("createUser", "createUserAccount in firebase service error ${e.message}  ")
            statues = "error ${e.message}"
           flowOf(statues)
        }
    }

    suspend fun loginUser(email: String, password: String): String {
        return try {
            val result = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            val isVerified = result.user?.isEmailVerified ?: false
            Log.i("login", "loginUser fireBase success")
            Log.i("login", "loginUser fireBase validation $isVerified")
            if(isVerified){
                "login Successfully"
            }else {
                "error account not verified"
            }
        } catch (e: Exception) {
            Log.e("login", "loginUser error: ${e.message}")
            "error ${e.message}"
        }
    }
}