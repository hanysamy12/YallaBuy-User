package com.example.yallabuy_user.authentication.login

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.yallabuy_user.data.models.customer.CustomerDataResponse
import com.example.yallabuy_user.repo.RepositoryInterface
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class LoginViewModel(
    private val repo: RepositoryInterface
) : ViewModel() {

    private val _validationError = MutableStateFlow<String?>(null)
    val validateError = _validationError.asStateFlow()

    private val _loginUser = MutableStateFlow<Boolean>(false)
    val loginUser = _loginUser

    private val _loginUserError = MutableSharedFlow<String>()
    val loginUserError = _loginUserError

    private val _customerData = MutableStateFlow<CustomerDataResponse?>(null)
    val customerData = _customerData.asStateFlow()

    fun validation(email: String, password: String) {
        viewModelScope.launch {
            when {
                email.isEmpty() -> _validationError.emit("Email can't be empty")
                password.isEmpty() -> _validationError.emit("Password can't be empty")
                !checkForEmailFormatting(email) -> _validationError.emit("Email not in valid format")
                else -> {
                    loginUser(email, password)
                }
            }
        }
    }

    private fun checkForEmailFormatting(email: String): Boolean {
        val emailPattern = Regex("""^[a-zA-Z0-9+_.-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$""")
        return email.matches(emailPattern)
    }

    private fun loginUser(email: String, password: String) {
        viewModelScope.launch {
            try {
                val loginResponse = repo.loginUser(email, password)
                loginResponse.collect {
                    if (it.contains("error")) {
                        _loginUserError.emit(it)
                        _loginUser.emit(false)
                    }
                    if (it.contains("Successfully")) {
                        Log.i("customer", "loginUser email in view model $email  ")
                        repo.getUserDataByEmail(email)
                            .catch {
                                Log.i("customer", "loginUser error in view model ${it.message} ")
                            }
                            .collect { customer ->
                                Log.i("customer", "loginUser id is ${customer.customers[0].id} ")
                                _customerData.emit(customer)
                            }
                        _loginUser.emit(true)
                    }
                }
            } catch (e: Exception) {
                _loginUser.emit(false)
                _loginUserError.emit("${e.message}")
            }
        }
    }
}

