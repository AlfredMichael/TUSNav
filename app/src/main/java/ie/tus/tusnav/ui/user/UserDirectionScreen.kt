package ie.tus.tusnav.ui.user

import android.util.Log
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import ie.tus.tusnav.ui.notification.showNotification
import ie.tus.tusnav.ui.theme.TUSNavTheme
import ie.tus.tusnav.ui.theme.publicSans
import ie.tus.tusnav.ui.topappbars.LeftTA
import ie.tus.tusnav.utility.closeIndicator
import ie.tus.tusnav.utility.fetchOutdoorDirections
import ie.tus.tusnav.utility.getIPAddress
import ie.tus.tusnav.utility.rememberLocationUpdates
import ie.tus.tusnav.viewmodel.DirectionsViewModel


@Composable
fun OutdoorGoogleMap(
    lat1: Double,
    lng1: Double,
    lat2: Double,
    lng2: Double,
    zoom: Float,
    modifier: Modifier = Modifier
) {
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(lat1, lng1), zoom)
    }
    Log.d("OutdoorGoogleMap", "lat1: $lat1")
    Log.d("OutdoorGoogleMap", "lng1: $lng1")
    Log.d("OutdoorGoogleMap", "lat2: $lat2")
    Log.d("OutdoorGoogleMap", "lng2: $lng2")

    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(300.dp), //fixed height for the map
        shape = RoundedCornerShape(16.dp), //rounded corners
        elevation = CardDefaults.elevatedCardElevation(8.dp)
    ) {
        GoogleMap(
            cameraPositionState = cameraPositionState,
            modifier = Modifier.fillMaxSize()
        ) {
            Marker(
                state = rememberMarkerState(position = LatLng(lat1, lng1)),
                title = "Your current location!!",
                icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE) // current location icon
            )
            Marker(
                state = rememberMarkerState(position = LatLng(lat2, lng2)),
                title = "Destination",
                icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED) // destination icon
            )
        }
    }
}


@Composable
fun TextInstructions(instructions:String) {
    Spacer(
        Modifier.height(6.dp)
    )
    Row(
        modifier = Modifier
            .padding(8.dp)
            .background(Color(0xFFA39461), shape = RoundedCornerShape(8.dp))
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            text = instructions,
            fontFamily = publicSans,
            fontWeight = FontWeight.Normal,
            fontSize = 13.sp,
            color = Color.White
        )
    }
    Spacer(
        Modifier.height(6.dp)
    )
}

@Composable
fun ImageLandmarks(imageURLs: String) {
    Spacer(
        Modifier.height(6.dp)
    )
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start
    ) {
        AsyncImage(
            model = imageURLs,
            contentDescription = "Landmark Image",
            contentScale = ContentScale.FillBounds,
            modifier = Modifier
                .height(130.dp)
                .width(200.dp)
                .clip(RoundedCornerShape(16.dp))
        )
    }
    Spacer(
        Modifier.height(6.dp)
    )
}





@Composable
fun UserDirectionScreen(navController: NavController, id: String) {
    val directionsViewModel: DirectionsViewModel = viewModel()
    val routeDetails by directionsViewModel.routeDetails.observeAsState(null)
    var sliderPosition by rememberSaveable { mutableFloatStateOf(0f) }
    val context = LocalContext.current
    var outdoorInstructions by rememberSaveable { mutableStateOf(listOf<String>()) }
    var isCloseToDestination by rememberSaveable { mutableStateOf(false) }

    val currentLocation by rememberLocationUpdates()

    // Fetch route details when the composable is first composed
    LaunchedEffect(id) {
        directionsViewModel.fetchRouteDetails(id)
    }

    routeDetails?.let { (route, contributor) ->
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = { LeftTA(navController, "${contributor?.username}'s Route") }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
            ) {
                Column {

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Current Location >> ${route.to}",
                            fontFamily = publicSans,
                            fontWeight = FontWeight.Thin,
                            fontSize = 16.sp
                        )
                    }

                    // Outdoor Navigation Part
                    Column(
                        modifier = Modifier.padding(
                            start = 16.dp,
                            end = 16.dp
                        )
                    ) {
                        Text(
                            text = "Outdoor Navigation: Main Building",
                            fontFamily = publicSans,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                        // Image of Building
                        AsyncImage(
                            model = route.mainImageUri,
                            contentDescription = "Main Building Image",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .height(130.dp)
                                .clip(RoundedCornerShape(16.dp))
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        // Google Maps
                        currentLocation?.let {
                            OutdoorGoogleMap(
                                lat1 = it.latitude,
                                lng1 = it.longitude,
                                lat2 = route.fromLat.toDouble(),
                                lng2 = route.fromLong.toDouble(),
                                zoom = 5f,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp)
                            )

                            // Fetch outdoor directions
                            LaunchedEffect(it.latitude, it.longitude, route.fromLat, route.fromLong) {
                                fetchOutdoorDirections(
                                    context = context,
                                    originLat = it.latitude,
                                    originLng = it.longitude,
                                    destinationLat = route.fromLat.toDouble(),
                                    destinationLng = route.fromLong.toDouble()
                                ) { instructions ->
                                    outdoorInstructions = instructions
                                }
                            }

                            // Check if close to destination
                            isCloseToDestination = closeIndicator(
                                startLat = it.latitude,
                                startLng = it.longitude,
                                destinationLat = route.fromLat.toDouble(),
                                destinationLng = route.fromLong.toDouble()
                            )


                        }
                        // Display outdoor instructions
                        outdoorInstructions.forEach { instruction ->
                            TextInstructions(instruction)
                        }

                        // Show notification if close to destination
                        if (isCloseToDestination) {
                            showNotification(
                                context = context,
                                title = "Destination Alert",
                                message = "You are close to your destination!"
                            )
                        }


                    }

                    // Indoor Navigation Part
                    Column(
                        modifier = Modifier.padding(start = 12.dp, end = 12.dp)
                    ) {
                        Text(
                            text = "Indoor Navigation",
                            fontFamily = publicSans,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                        // Display route elements
                        route.elements.forEach { element ->
                            when (element.type) {
                                "text" -> TextInstructions(element.text ?: "")
                                "image" -> ImageLandmarks(element.imageUri ?: "")
                            }
                        }
                    }

                    // Ratings
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 17.dp, end = 17.dp)
                    ) {
                        Text(
                            text = "Please rate ${contributor?.username}'s route",
                            fontFamily = publicSans,
                            fontWeight = FontWeight.Thin,
                            fontSize = 13.sp
                        )
                        //Update the contributors rating
                        // Store the users ipaddress and id so they don't rate again from the same device
                        Slider(
                            value = sliderPosition,
                            onValueChange = { sliderPosition = it },
                            steps = 9,
                            valueRange = 0f..10f,
                            onValueChangeFinished = {
                                val ipAddress = getIPAddress()
                                directionsViewModel.updateRatingAndStoreIp(route.creatorId, id, sliderPosition.toInt(), ipAddress)
                            }

                        )
                        Box(
                            modifier = Modifier
                                .background(Color(0xFFA39461), shape = CircleShape)
                                .size(40.dp)
                                .align(Alignment.CenterHorizontally)
                        ) {
                            Text(
                                text = sliderPosition.toInt().toString(),
                                fontFamily = publicSans,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                modifier = Modifier.align(Alignment.Center)
                            )
                        }
                    }
                }
            }
        }
    }
}



@Preview(showBackground = true)
@Composable
fun UserDirectionScreenPreview(){
    TUSNavTheme {
        val id ="ddibdi"
        val navController = rememberNavController()
        UserDirectionScreen(navController,id)
    }
}