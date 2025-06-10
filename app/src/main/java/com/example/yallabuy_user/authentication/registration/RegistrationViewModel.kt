package com.example.yallabuy_user.authentication.registration

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.yallabuy_user.repo.RepositoryInterface
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class RegistrationViewModel(
    private val repositoryInterface: RepositoryInterface
) : ViewModel() {

    private val _createAccount =
        MutableSharedFlow<String>()
    val createAccount = _createAccount

    private val _errorInCreatingAccount = MutableSharedFlow<String>()
    val errorInCreatingAccount = _errorInCreatingAccount

    private val _validationError = MutableStateFlow<String?>(null)
    val validationError = _validationError.asStateFlow()

    private fun createUserAccount(email: String,userName: String, password: String) {
        viewModelScope.launch {
            coroutineScope {
                try {
                    val createAccount = repositoryInterface.createUserAccount(email, password)
                    createAccount.collect{
                        if(it.contains("error")) {
                            _errorInCreatingAccount.emit(it)
                            Log.i("createUser", "error is $createAccount ")
                        }
                        if(it.contains("successfully")) {
                            Log.i("createUser", "in view model success ")
                            createUserOnShopify(email, password, userName)
                            _createAccount.emit(it)
                        }
                    }
                } catch (e: Exception) {
                    Log.i("TAG", "createUserAccount error in view model ${e.message} ")
                }
            }
        }
    }

    private fun createUserOnShopify(email: String, password: String, userName: String){
        viewModelScope.launch {
            try {
               val response =  repositoryInterface.createUserOnShopify(email , password , userName)
                Log.i("shopify", "createUserOnShopify response in view model is success")
            }catch (e : Exception){
                Log.i("TAG", "createUserOnShopify error is ${e.message} ")
            }
        }
    }

    fun validation(email: String, userName: String, password: String, confirmPassword: String) {
        viewModelScope.launch {
            when {
                email.isEmpty() -> _validationError.emit("Email can't be empty")
                userName.isEmpty() -> _validationError.emit("User name can't be empty")
                password.isEmpty() -> _validationError.emit("Password can't be empty")
                confirmPassword.isEmpty() -> _validationError.emit("Confirm Password can't be empty")
                !checkForEmailFormatting(email) -> _validationError.emit("Email not in valid format")
                !isPasswordStrong(password) -> _validationError.emit("Password is weak")
                !isPasswordEqualConfirmPassword(password, confirmPassword) -> _validationError.emit(
                    "Passwords do not match"
                )

                else -> {
                    _validationError.emit(null)
                    createUserAccount(email , userName, password)
                }
            }
        }
    }


    private fun isPasswordStrong(password: String): Boolean {
        val strongPasswordRegex =
            Regex("""^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@#$%^&+=!_-]).{8,}$""")
        return strongPasswordRegex.matches(password)
    }

    private fun checkForEmailFormatting(email: String): Boolean {
        val emailPattern = Regex("""^[a-zA-Z0-9+_.-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$""")
        return email.matches(emailPattern)
    }

    private fun isPasswordEqualConfirmPassword(password: String, confirmPassword: String): Boolean {
        return password == confirmPassword
    }

}