package ie.tus.tusnav.ui.contributor

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import ie.tus.tusnav.R
import ie.tus.tusnav.ui.bottomappbar.ContributorNavBottomAppBar
import ie.tus.tusnav.room.AppDatabase
import ie.tus.tusnav.ui.status.ErrorToast
import ie.tus.tusnav.ui.status.SuccessToast
import ie.tus.tusnav.ui.theme.publicSans
import ie.tus.tusnav.ui.topappbars.LeftTACR
import ie.tus.tusnav.viewmodel.ProfileViewModel
import ie.tus.tusnav.viewmodel.ProfileViewModelFactory


@Composable
fun ContributorProfileScreen(navController: NavController, database: AppDatabase) {
    val viewModel: ProfileViewModel = viewModel(factory = ProfileViewModelFactory(database))

    var username by rememberSaveable { mutableStateOf("") }
    var status by rememberSaveable { mutableStateOf("") }

    // Observe the contributor data and rating message
    val contributor by viewModel.contributorData.observeAsState()
    val ratingMessage by viewModel.ratingMessage.observeAsState()
    val updateSuccess by viewModel.updateSuccess.observeAsState()
    val updateError by viewModel.updateError.observeAsState()

    // Fetch contributor details when the composable is first composed
    LaunchedEffect(Unit) {
        viewModel.fetchContributorDetails()
    }

    // Initialize state variables when contributor data is loaded
    LaunchedEffect(contributor) {
        contributor?.let {
            username = it.username
            status = it.status
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = { LeftTACR(navController, "Profile", database) },
        bottomBar = { ContributorNavBottomAppBar(navController) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(R.drawable.profile),
                contentDescription = "Contributors Image",
                modifier = Modifier.size(100.dp).clip(CircleShape)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = contributor?.email ?: "Loading...",
                    fontFamily = publicSans,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                )

                Text(
                    text = ratingMessage ?: "Loading...",
                    fontFamily = publicSans,
                    fontWeight = FontWeight.ExtraLight,
                    fontSize = 14.sp,
                )

                Text(
                    text = "${contributor?.contributions ?: 0} Contributions",
                    fontFamily = publicSans,
                    fontWeight = FontWeight.Thin,
                    fontSize = 12.sp,
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Column {
                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it },
                    textStyle = TextStyle.Default.copy(fontFamily = publicSans, fontSize = 13.sp, fontWeight = FontWeight.ExtraLight),
                    label = {
                        Text(
                            text = "Please enter your username!!",
                            fontFamily = publicSans,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.ExtraLight,
                        )
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(18.dp))

                OutlinedTextField(
                    value = status,
                    onValueChange = { status = it },
                    textStyle = TextStyle.Default.copy(fontFamily = publicSans, fontSize = 13.sp, fontWeight = FontWeight.ExtraLight),
                    label = {
                        Text(
                            text = "Type “deactivated” in here to deactivate your account",
                            fontFamily = publicSans,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.ExtraLight,
                        )
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(22.dp))

            Button(
                onClick = {
                    contributor?.id?.let { userId ->
                        viewModel.updateContributorDetails(userId, username, status)
                    }
                },
                modifier = Modifier.width(160.dp),
                elevation = ButtonDefaults.elevatedButtonElevation(8.dp)
            ) {
                Text(
                    text = "Save",
                    fontFamily = publicSans,
                    fontSize = 16.sp,
                )
            }

            updateError?.let {
                ErrorToast(it)
            }

            updateSuccess?.let {
                if (it) {
                    SuccessToast("Update successful!")
                }
            }
        }
    }
}

