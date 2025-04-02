package ie.tus.tusnav.ui.contributor

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import ie.tus.tusnav.ui.bottomappbar.TusNavBottomAppBar
import ie.tus.tusnav.room.AppDatabase
import ie.tus.tusnav.ui.status.ErrorToast
import ie.tus.tusnav.ui.status.LoadingToast
import ie.tus.tusnav.ui.status.SuccessToast
import ie.tus.tusnav.ui.theme.publicSans
import ie.tus.tusnav.ui.topappbars.CenterTA
import ie.tus.tusnav.viewmodel.AuthViewModel
import ie.tus.tusnav.viewmodel.AuthViewModelFactory



@Composable
fun ContributorLoginScreen(navController: NavController, database: AppDatabase) {
    val viewModel: AuthViewModel = viewModel(factory = AuthViewModelFactory(database))
    val context = LocalContext.current

    val errorMessage by viewModel.errorMessage.observeAsState()
    val successMessage by viewModel.successMessage.observeAsState()
    val isLoading by viewModel.isLoading.observeAsState(false)
    val userId by viewModel.userId.observeAsState()
    val loginSuccess by viewModel.loginSuccess.observeAsState()
    val registerSuccess by viewModel.registerSuccess.observeAsState()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = { CenterTA("Contribute") },
        bottomBar = { TusNavBottomAppBar(navController) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(52.dp))

            var email by rememberSaveable { mutableStateOf("") }
            var password by rememberSaveable { mutableStateOf("") }
            var passwordVisible by rememberSaveable { mutableStateOf(false) }

            Text(
                text = "Want to Contribute to TUS Nav?",
                fontFamily = publicSans,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
            )
            Text(
                text = "Please register using your TUS email only!",
                fontFamily = publicSans,
                fontWeight = FontWeight.ExtraLight,
                fontSize = 15.sp,
            )

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = {
                    Text(
                        text = "Email",
                        fontFamily = publicSans,
                        fontWeight = FontWeight.ExtraLight,
                    )
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = {
                    Text(
                        text = "Password",
                        fontFamily = publicSans,
                        fontWeight = FontWeight.ExtraLight,
                    )
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    val image = if (passwordVisible)
                        Icons.Filled.Visibility
                    else Icons.Filled.VisibilityOff

                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(imageVector = image, contentDescription = null)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(32.dp))

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(
                    onClick = {
                        viewModel.registerUser(email, password)
                    },
                    modifier = Modifier.width(200.dp),
                    elevation = ButtonDefaults.elevatedButtonElevation(8.dp)
                ) {
                    Text(
                        text = "Register",
                        fontFamily = publicSans,
                        fontSize = 16.sp,
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        viewModel.loginUser(email, password)
                    },
                    modifier = Modifier.width(160.dp),
                    elevation = ButtonDefaults.elevatedButtonElevation(8.dp)
                ) {
                    Text(
                        text = "Login",
                        fontFamily = publicSans,
                        fontSize = 16.sp,
                    )
                }
            }
        }
    }

    // Auto-login attempt on screen load
    LaunchedEffect(Unit) {
        viewModel.autoLogin()
    }

    if (isLoading) {
        LoadingToast()
    }

    errorMessage?.let {
        ErrorToast(it)
        viewModel.resetMessages()
    }

    successMessage?.let {
        SuccessToast(it)
        viewModel.resetMessages()
    }

    registerSuccess?.let {
        if (it) {
            userId?.let { id ->
                Log.d("ContributorRegisterScreen", "Navigating to profile screen with user ID: $id")
                navController.navigate("contributor_profile_screen")
            }
            viewModel.registerSuccess.postValue(false)
        }
    }

    LaunchedEffect(loginSuccess) {
        if (loginSuccess == true) {
            Log.d("ContributorLoginScreen", "Navigating to profile screen")
            navController.navigate("contributor_profile_screen") {
                popUpTo("contributor_login_screen") { inclusive = true }
            }
        }
    }




    LaunchedEffect(Unit) {
        userId?.let {
            Log.d("AuthViewModel", "User ID: $it")
        }
    }

}

