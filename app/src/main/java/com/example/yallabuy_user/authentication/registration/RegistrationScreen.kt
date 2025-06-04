package com.example.yallabuy_user.authentication.registration

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.yallabuy_user.ui.navigation.ScreenRoute


@Composable
fun RegistrationScreen(navControl: NavHostController) {

    val email = remember { mutableStateOf("") }
    val userName = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }
    val confirmPassword = remember { mutableStateOf("") }

    OutlinedCard(
        modifier = Modifier.fillMaxWidth()
            .height(600.dp)
            .padding(50.dp)
    )  {
        Column(  modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally ){

            Text("Registration" , fontSize = 25.sp , fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(5.dp))
            OutlinedTextField(value = email.value, onValueChange = {
                email.value = it
            }, placeholder = { Text("Email@gmail.com") },
                label = { Text("Email") },
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Email, contentDescription = "Lock Icon")
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email) ,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(10.dp))
            OutlinedTextField(value = userName.value, onValueChange = {
                userName.value = it
            }, placeholder = { Text("UserName") },
                label = { Text("UserName") },
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Person, contentDescription = "Lock Icon")
                } ,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(10.dp))
            OutlinedTextField(value = password.value, onValueChange = {
                password.value = it
            }, placeholder = { Text("Password") },
                label = { Text("Password") },
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Lock, contentDescription = "Lock Icon")
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password) ,
                visualTransformation = PasswordVisualTransformation() ,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(10.dp))
            OutlinedTextField(value = confirmPassword.value, onValueChange = {
                confirmPassword.value = it
            }, placeholder = { Text("Confirm Password") },
                label = { Text("Confirm Password") },
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Lock, contentDescription = "Lock Icon")
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password) ,
                visualTransformation = PasswordVisualTransformation() ,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(13.dp))
            Button(
                onClick = {

                } ,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Registration" , fontSize = 15.sp , fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.height(10.dp))

            Button(
                onClick = {
                    navControl.navigate(ScreenRoute.Home.route)
                } ,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Guest", fontSize = 20.sp , fontWeight = FontWeight.Bold)
            }
        }
    }
}