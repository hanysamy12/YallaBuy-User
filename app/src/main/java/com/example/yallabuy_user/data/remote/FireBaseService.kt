package com.example.yallabuy_user.data.remote

import android.util.Log
import com.google.firebase.auth.FirebaseAuth

class FireBaseService(
    private val firebaseAuth: FirebaseAuth
) {
    private  var  statues : String = "error"

    fun createUserAccount(email: String, password: String) : String {
        return try {
            firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
                if (it.isSuccessful) {
                    Log.i("TAG", "createUserAccount created success ")
                    firebaseAuth.currentUser?.sendEmailVerification()?.addOnCompleteListener {
                        if (it.isSuccessful) {
                            Log.i("TAG", "createUserAccount email send  success ")
                            statues = "successful"
                        } else {
                            val exception = it.exception?.message
                            statues = exception ?: "undefined exception "
                        }
                    }
                } else {
                    val exception = it.exception?.message
                    statues = exception ?: "undefined exception "
                }

            }
            statues
        }catch (e : Exception){
            Log.i("TAG", "createUserAccount in firebase service error ${e.message}  ")
            "error"
        }
    }
}