package com.example.yallabuy_user.authentication.login

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.yallabuy_user.R
import com.example.yallabuy_user.authentication.registration.OrDivider
import com.example.yallabuy_user.ui.navigation.ScreenRoute
import org.koin.androidx.compose.koinViewModel


@Composable
fun LoginScreen(navController: NavHostController , loginViewModel: LoginViewModel = koinViewModel()) {
    val email = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }
    val validationError = loginViewModel.validateError.collectAsState().value
    val loginUser = loginViewModel.loginUser.collectAsState().value

    val emblemaoneregilarFont = FontFamily(Font(R.font.emblemaoneregular))
    val sigmarRegularFont = FontFamily(Font(R.font.sigmarregular))
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        contentAlignment = Alignment.BottomCenter
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(start = 16.dp, top = 30.dp)
        ) {
        Text(
            text = "Hello",
            color = Color.White,
            fontSize = 25.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = emblemaoneregilarFont,
        )
        Text(
            text = "again",
            color = Color.White,
            fontSize = 25.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = emblemaoneregilarFont,

        )
            }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(500.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(topStart = 15.dp, topEnd = 15.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                LoginTextFeilds(email, password , validationError)
                Spacer(Modifier.height(13.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 5.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        "Don't have an Account ?",
                        textAlign = TextAlign.Start
                    )
                    TextButton(
                        onClick = {
                            navController.navigate(ScreenRoute.Registration)
                        }
                    ) {
                        Text(
                            "Register", fontFamily = sigmarRegularFont, textAlign = TextAlign.End
                        )
                    }
                }
                Spacer(Modifier.height(13.dp))
                Button(
                    onClick = {
                        loginViewModel.validation(email.value , password.value)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(55.dp)
                        .shadow(8.dp, RoundedCornerShape(50))
                        .clip(RoundedCornerShape(50)),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Black,
                        contentColor = Color.White
                    ),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
                ) {
                    Text(
                        text = "Login",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = sigmarRegularFont,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
                OrDivider()
                TextButton(
                    onClick = {
                        navController.navigate(ScreenRoute.Home.route)
                    },
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text("Sign In as A Guest", fontSize = 20.sp, fontFamily = sigmarRegularFont)
                }
            }
        }
        if(loginUser){
            navController.navigate(ScreenRoute.Home.route)
        }
    }
}

@Composable
fun LoginTextFeilds(
    email: MutableState<String>,
    password: MutableState<String>,
    validationError: String?
) {
    Column {
        OutlinedTextField(
            value = email.value,
            onValueChange = {
                email.value = it
            },
            placeholder = { Text("Email@gmail.com") },
            label = { Text("Email") },
            leadingIcon = {
                Icon(imageVector = Icons.Default.Email, contentDescription = "Lock Icon")
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier.fillMaxWidth(),
            // isError = validationError != null
        )
        if (validationError?.contains("Email") == true) {
            Text(
                text = validationError ?: "",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 16.dp , top = 5.dp)
            )
        }
        Spacer(modifier = Modifier.height(10.dp))
        OutlinedTextField(value = password.value, onValueChange = {
            password.value = it
        }, placeholder = { Text("Password") },
            label = { Text("Password") },
            leadingIcon = {
                Icon(imageVector = Icons.Default.Lock, contentDescription = "Lock Icon")
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )
        if (validationError?.contains("Password") == true) {
            Text(
                text = validationError ?: "",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 16.dp , top = 5.dp)
            )
        }
    }
}

