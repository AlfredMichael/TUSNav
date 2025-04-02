package ie.tus.tusnav.ui.bottomappbar

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Contacts
import androidx.compose.material.icons.filled.Cookie
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import ie.tus.tusnav.ui.theme.publicSans

@Composable
fun TusNavBottomAppBar(navController: NavController) {
    BottomAppBar {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            IconButton(onClick = { navController.navigate("user_home_screen") }, modifier = Modifier.width(120.dp)) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Filled.Home, contentDescription = "Home Icon")
                    Text(
                        text = "Home",
                        fontSize = 12.sp,
                        fontFamily = publicSans,
                        fontWeight = FontWeight.Thin,
                    )
                }
            }
            IconButton(onClick = { navController.navigate("contributor_login_screen")}, modifier = Modifier.width(120.dp)) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Filled.Cookie, contentDescription = "Cookie Icon")
                    Text(
                        text = "Contribute",
                        fontSize = 12.sp,
                        fontFamily = publicSans,
                        fontWeight = FontWeight.Thin,
                    )
                }
            }

            IconButton(onClick = { navController.navigate("user_contact_screen")}, modifier = Modifier.width(120.dp)) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                    Icon(Icons.Default.Contacts, contentDescription = "Contacts Icon")
                    Text(
                        text = "Contact",
                        fontSize = 12.sp,
                        fontFamily = publicSans,
                        fontWeight = FontWeight.Thin,
                        )
                }
            }

        }
    }
}

