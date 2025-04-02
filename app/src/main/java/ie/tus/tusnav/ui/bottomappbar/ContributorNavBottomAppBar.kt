package ie.tus.tusnav.ui.bottomappbar

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Route
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
fun ContributorNavBottomAppBar(navController: NavController){
    BottomAppBar {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            IconButton(onClick = { navController.navigate("contributor_profile_screen") }, modifier = Modifier.width(120.dp)) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Filled.Person, contentDescription = "Profile Icon")
                    Text(
                        text = "Profile",
                        fontSize = 12.sp,
                        fontFamily = publicSans,
                        fontWeight = FontWeight.Thin,
                    )
                }
            }
            IconButton(onClick = { navController.navigate("contributor_create_route")}, modifier = Modifier.width(120.dp)) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Filled.Route, contentDescription = "Route Icon")
                    Text(
                        text = "Create Route",
                        fontSize = 12.sp,
                        fontFamily = publicSans,
                        fontWeight = FontWeight.Thin,
                    )
                }
            }

        }
    }
}