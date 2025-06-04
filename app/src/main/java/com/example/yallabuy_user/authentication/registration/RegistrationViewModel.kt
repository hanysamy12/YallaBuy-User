package com.example.yallabuy_user.authentication.registration

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.yallabuy_user.repo.RepositoryInterface
import com.example.yallabuy_user.utilities.ApiResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class RegistrationViewModel(
    private val repositoryInterface: RepositoryInterface
) : ViewModel() {

    private val _createAccount: MutableStateFlow<ApiResponse<String>> =
        MutableStateFlow(ApiResponse.Loading)

    val createAccount = _createAccount.asStateFlow()

    private fun createUserAccount(email: String, password: String) {
        viewModelScope.launch {
            try {
                val createAccount = repositoryInterface.createUserAccount(email, password)
                Log.i("TAG", "createUserAccount in view model response is $createAccount ")
                    _createAccount.emit(ApiResponse.Success(createAccount))
            } catch (e: Exception) {
                Log.i("TAG", "createUserAccount error in view model ${e.message} ")
            }
        }
    }


    fun validation(email: String, userName: String, password: String, confirmPassword: String) {
        Log.i("error", "validation $email , $password , $userName , $confirmPassword ")
        viewModelScope.launch {
            if (email.isEmpty()) {
                _createAccount.emit(ApiResponse.Success("Email can't be empty"))
            } else if (userName.isEmpty()) {
                _createAccount.emit(ApiResponse.Success("User name can't be empty"))
            } else if (password.isEmpty()) {
                _createAccount.emit(ApiResponse.Success("password can't be empty"))
            } else if (confirmPassword.isEmpty()) {
                _createAccount.emit(ApiResponse.Success(" confirm Password can't be empty"))
            } else {
                checkEmailAndPassword(password, confirmPassword, email)
            }
        }
    }

    private fun checkEmailAndPassword(password: String, confirmPassword: String, email: String) {
        viewModelScope.launch {
            if (isPasswordEqualConfirmPassword(password, confirmPassword)) {
                if (checkForEmailFormatting(email)) {
                    if (isPasswordStrong(password)) {
                        Log.i("error", "checkEmailAndPassword send create request ")
                        createUserAccount(email, password)
                    }else {
                        _createAccount.emit(ApiResponse.Success("passwords is weak  "))
                    }
                }else {
                    _createAccount.emit(ApiResponse.Success("email not in email formatting "))
                }
            } else {
                _createAccount.emit(ApiResponse.Success("passwords does not match "))
            }
        }
    }

    private fun isPasswordStrong(password: String): Boolean {
        val strongPasswordRegex = Regex("""^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@#$%^&+=!_-]).{8,}$""")
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