package ie.tus.tusnav.ui.user

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import ie.tus.tusnav.R
import ie.tus.tusnav.ui.bottomappbar.TusNavBottomAppBar
import ie.tus.tusnav.ui.theme.TUSNavTheme
import ie.tus.tusnav.ui.theme.publicSans
import ie.tus.tusnav.ui.topappbars.CenterTA

@Composable
fun UserContactScreen(navController: NavController) {
    val context = LocalContext.current
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = { CenterTA("Contact") },
        bottomBar = { TusNavBottomAppBar(navController) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // Image on top of the card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(372.dp)
                    .padding(start=18.dp)
            ) {
                Image(
                    painter = painterResource(R.drawable.contactme),
                    contentDescription = "Profile Image",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }

            Spacer(modifier = Modifier.height(11.dp))

            // Card with contact details and buttons
            Card(
                shape = RoundedCornerShape(topStart = 25.dp, topEnd = 25.dp),
                elevation = CardDefaults.elevatedCardElevation(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .padding(top = 32.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxSize()
                ) {
                    Column(
                        modifier = Modifier.padding(
                            start = 12.dp,
                            end = 12.dp,
                            top = 17.dp
                        )
                    ) {
                        Row {
                            Text(
                                text = "Name: ",
                                fontFamily = publicSans,
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp,
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Michael Alfred",
                                fontFamily = publicSans,
                                fontWeight = FontWeight.Thin,
                                fontSize = 16.sp,
                            )
                        }

                        Row {
                            Text(
                                text = "Year: ",
                                fontFamily = publicSans,
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp,
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "4th year",
                                fontFamily = publicSans,
                                fontWeight = FontWeight.Thin,
                                fontSize = 16.sp,
                            )
                        }

                        Row {
                            Text(
                                text = "Graduating Set: ",
                                fontFamily = publicSans,
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp,
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "2025",
                                fontFamily = publicSans,
                                fontWeight = FontWeight.Thin,
                                fontSize = 16.sp,
                            )
                        }

                        Row {
                            Text(
                                text = "Email: ",
                                fontFamily = publicSans,
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp,
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "success_fred@yahoo.com",
                                fontFamily = publicSans,
                                fontWeight = FontWeight.Thin,
                                fontSize = 16.sp,
                            )
                        }

                        Row {
                            Text(
                                text = "Phone: ",
                                fontFamily = publicSans,
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp,
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "+353874864779",
                                fontFamily = publicSans,
                                fontWeight = FontWeight.Thin,
                                fontSize = 16.sp,
                            )
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Button(
                                onClick = {
                                    val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
                                        data = Uri.parse("mailto:success_fred@yahoo.com")
                                    }
                                    context.startActivity(emailIntent)
                                },
                                elevation = ButtonDefaults.elevatedButtonElevation(8.dp)
                            ) {
                                Text(
                                    text = "Send an Email?",
                                    fontFamily = publicSans,
                                    fontSize = 16.sp,
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Button(
                                onClick = {
                                    val dialIntent = Intent(Intent.ACTION_DIAL).apply {
                                        data = Uri.parse("tel:+353874864779")
                                    }
                                    context.startActivity(dialIntent)
                                },
                                elevation = ButtonDefaults.elevatedButtonElevation(8.dp)
                            ) {
                                Text(
                                    text = "Call Alfred?",
                                    fontFamily = publicSans,
                                    fontSize = 16.sp,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun UserContactScreenPreview(){
    TUSNavTheme {
        val navController = rememberNavController()
        UserContactScreen(navController)
    }
}