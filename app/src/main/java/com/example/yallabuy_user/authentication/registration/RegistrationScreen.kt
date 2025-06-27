package com.example.yallabuy_user.authentication.registration

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Divider
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.zIndex
import androidx.navigation.NavHostController
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.yallabuy_user.R
import com.example.yallabuy_user.home.ProgressShow
import com.example.yallabuy_user.ui.navigation.ScreenRoute
import org.koin.androidx.compose.koinViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistrationScreen(
    navControl: NavHostController,
    registrationViewModel: RegistrationViewModel = koinViewModel(),
    setTopBar: (@Composable () -> Unit) -> Unit
) {

    var createAccount = registrationViewModel.createAccount
    val validationError = registrationViewModel.validationError.collectAsState().value
    val createAccountError = registrationViewModel.errorInCreatingAccount
    val isAccountCreated = remember { mutableStateOf(false) }
    var createAccountErrorText = remember { mutableStateOf("") }
    var showDialog = remember { mutableStateOf(false) }
    val showProgressBar = remember { mutableStateOf(false) }
    val email = remember { mutableStateOf("") }
    val userName = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }
    val confirmPassword = remember { mutableStateOf("") }

    val emblemaoneregilarFont = FontFamily(Font(R.font.emblemaoneregular))
    val sigmarRegularFont = FontFamily(Font(R.font.sigmarregular))

    LaunchedEffect(Unit) {
        setTopBar {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Sign Up",
                        color = Color.White,
                        fontFamily = FontFamily(Font(R.font.caprasimo_regular)),
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color(0xFF3B9A94)
                ),
                navigationIcon = {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_app),
                        contentDescription = "App Icon",
                        tint = Color.Unspecified, // Optional: set tint if needed
                        modifier = Modifier.padding(start = 12.dp)
                    )
                }
            )
        }
        createAccount.collect {
            if (it.isNotEmpty()) {
                showDialog.value = true
                showProgressBar.value = false
                isAccountCreated.value = true
                email.value = ""
                userName.value = ""
                password.value = ""
                confirmPassword.value = ""
            }
        }
    }
    LaunchedEffect(Unit) {
        createAccountError.collect {
            if (it.isNotEmpty()) {
                showDialog.value = true
                showProgressBar.value = false
                email.value = ""
                userName.value = ""
                password.value = ""
                confirmPassword.value = ""
                createAccountErrorText.value = it
                isAccountCreated.value = false
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.BottomCenter
    ) {
        Text(
            text = "Let's Start",
            color =  Color(0xFF3B9A94),
            fontSize = 25.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = emblemaoneregilarFont,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(start = 16.dp, top = 30.dp)
        )
        Image(
            painter = painterResource(R.drawable.sign_up_logo),
            contentDescription = "logo",
            alignment = Alignment.TopCenter,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .offset(y = (-490).dp , x = (100).dp) // Push upward from bottom
                .zIndex(1f)
                .size(250.dp)
        )

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(580.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(topStart = 15.dp, topEnd = 15.dp) ,
            border = BorderStroke(2.dp ,  Color(0xFF3B9A94))
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Spacer(Modifier.height(5.dp))
                RegistrationTextFeilds(email, userName, password, confirmPassword, validationError)
                Spacer(Modifier.height(13.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 5.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        "Already have an Account ?",
                        textAlign = TextAlign.Start ,
                        color = Color.Black
                    )
                    TextButton(
                        onClick = {
                            navControl.navigate(ScreenRoute.Login.route)
                        }
                    ) {
                        Text(
                            "Login", fontFamily = sigmarRegularFont, textAlign = TextAlign.End ,
                            color =  Color(0xFF3B9A94)
                        )
                    }
                }
                Spacer(Modifier.height(13.dp))
                Button(

                    onClick = {
                        showProgressBar.value = true
                        registrationViewModel.validation(
                            email.value,
                            userName.value,
                            password.value,
                            confirmPassword.value
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(55.dp)
                        .shadow(8.dp, RoundedCornerShape(50))
                        .clip(RoundedCornerShape(50)),
                    colors = ButtonDefaults.buttonColors(
                        containerColor =  Color(0xFF3B9A94),
                        contentColor = Color.White
                    ),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
                ) {
                    Text(
                        text = "Register",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = sigmarRegularFont,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
                Spacer(Modifier.height(7.dp))
                OrDivider()
                Spacer(Modifier.height(5.dp))
                TextButton(
                    onClick = {
                        navControl.navigate(ScreenRoute.Home.route)
                    },
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text("Sign Up as A Guest", fontSize = 18.sp, fontFamily = sigmarRegularFont
                    , color =  Color(0xFF3B9A94))
                }
            }
        }
        if(validationError != null){
            showProgressBar.value = false
        }
        if (showProgressBar.value) {
            ProgressShow()
        }
        if (showDialog.value) {
            SuccessRegistrationAlert(isAccountCreated, onConfirmation = {
                showDialog.value = false
            }, onDismissRequest = {
                showDialog.value = false
            },
                createAccountErrorText)
        }
    }
}

@Composable
fun RegistrationTextFeilds(
    email: MutableState<String>,
    userName: MutableState<String>,
    password: MutableState<String>,
    confirmPassword: MutableState<String>,
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
            isError = validationError?.contains("Email") ?: false
        )
        if (validationError?.contains("Email") == true) {
            Text(
                text = validationError ,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 16.dp, top = 5.dp)
            )
        }
        Spacer(modifier = Modifier.height(10.dp))
        OutlinedTextField(value = userName.value, onValueChange = {
            userName.value = it
        }, placeholder = { Text("UserName") },
            label = { Text("UserName") },
            leadingIcon = {
                Icon(imageVector = Icons.Default.Person, contentDescription = "Lock Icon")
            },
            modifier = Modifier.fillMaxWidth() ,
            isError = validationError?.contains("User name") ?: false
        )
        if (validationError?.contains("User name") == true) {
            Text(
                text = validationError ,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 16.dp, top = 5.dp)
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
            modifier = Modifier.fillMaxWidth() ,
            isError = validationError?.contains("Password") ?: false
        )
        if (validationError?.contains("Password") == true) {
            Text(
                text = validationError ?: "",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 16.dp, top = 5.dp)
            )
        }
        Spacer(modifier = Modifier.height(10.dp))
        OutlinedTextField(value = confirmPassword.value, onValueChange = {
            confirmPassword.value = it
        }, placeholder = { Text("Confirm Password") },
            label = { Text("Confirm Password") },
            leadingIcon = {
                Icon(imageVector = Icons.Default.Lock, contentDescription = "Lock Icon")
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth() ,
            isError = validationError?.contains("Confirm") ?: false
        )
        if (validationError?.contains("Confirm") == true) {
            Text(
                text = validationError ?: "",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 16.dp, top = 5.dp)
            )
        }
    }
}

@Composable
fun SuccessRegistrationAlert(
    isAccountCreated: MutableState<Boolean>,
    onConfirmation: () -> Unit,
    onDismissRequest: () -> Unit,
    createAccountErrorText: MutableState<String>
) {

    val title = remember { mutableStateOf("") }
    val description = remember { mutableStateOf("") }
    val successComposition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.successanimation))
    val failComposition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.failanimation))

    val successProgress by animateLottieCompositionAsState(
        composition = successComposition,
        iterations = LottieConstants.IterateForever
    )

    val failProgress by animateLottieCompositionAsState(
        composition = failComposition,
        iterations = LottieConstants.IterateForever
    )

    if (isAccountCreated.value) {
        title.value = "Success"
        description.value = "Please Check your email and login "
    } else {
        title.value = "Fail"
        description.value = createAccountErrorText.value
    }

    val icon: @Composable () -> Unit = {
        if (isAccountCreated.value) {
            LottieAnimation(
                composition = successComposition,
                progress = { successProgress },
                modifier = Modifier.size(100.dp)
            )
        } else {
            LottieAnimation(
                composition = failComposition,
                progress = { failProgress },
                modifier = Modifier.size(100.dp)
            )
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Alert Dialog Box
        AlertDialog(
            // set dismiss request
            onDismissRequest = {
                onDismissRequest()
            },
            // configure confirm button
            confirmButton = {
                Button(
                    onClick = {
                        onConfirmation()
                    }, colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Black,
                        contentColor = Color.White
                    )
                ) {
                    Text("Confirm")
                }
            },
            // set icon
            icon = icon,
            // set title text
            title = {
                Text(text = title.value, color = Color.Black)
            },
            // set description text
            text = {
                Text(text = description.value, color = Color.DarkGray)
            },
            // set padding for contents inside the box
            modifier = Modifier.padding(16.dp),
            // define box shape
            shape = RoundedCornerShape(16.dp),
            // set box background color
            containerColor = Color.White,
            // set icon color
            iconContentColor = Color.Red,
            // set title text color
            titleContentColor = Color.Black,
            // set text color
            textContentColor = Color.DarkGray,
            // set elevation
            tonalElevation = 8.dp,
            // set properties
            properties = DialogProperties(
                dismissOnBackPress = true,
                dismissOnClickOutside = false
            )
        )
    }
}

@Composable
fun OrDivider(
    modifier: Modifier = Modifier,
    text: String = "or"
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp)
    ) {
        Divider(
            modifier = Modifier
                .weight(1f)
                .height(1.dp),
            color = Color.Black
        )
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 8.dp),
            color = Color.Black,
            fontSize = 14.sp
        )
        Divider(
            modifier = Modifier
                .weight(1f)
                .height(1.dp),
            color = Color.Black
        )
    }
}

