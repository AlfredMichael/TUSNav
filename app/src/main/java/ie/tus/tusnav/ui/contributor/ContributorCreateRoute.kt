package ie.tus.tusnav.ui.contributor

import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Fort
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import ie.tus.tusnav.R
import ie.tus.tusnav.classes.Element
import ie.tus.tusnav.classes.Route
import ie.tus.tusnav.classes.RouteElement
import ie.tus.tusnav.room.AppDatabase
import ie.tus.tusnav.ui.status.LoadingOverlay
import ie.tus.tusnav.ui.theme.publicSans
import ie.tus.tusnav.ui.topappbars.LeftTA
import ie.tus.tusnav.utility.rememberLocationUpdates
import ie.tus.tusnav.utility.saveBitmapToUri
import ie.tus.tusnav.viewmodel.ProfileViewModel
import ie.tus.tusnav.viewmodel.ProfileViewModelFactory
import ie.tus.tusnav.viewmodel.RouteViewModel

@Composable
fun ContributorCreateRoute(navController: NavController, database: AppDatabase) {
    val viewModel: ProfileViewModel = viewModel(factory = ProfileViewModelFactory(database))
    val routeViewModel: RouteViewModel = viewModel()

    var from by rememberSaveable { mutableStateOf("") }
    var to by rememberSaveable { mutableStateOf("") }

    // Change mainImageUri to store the URI as a string
    var mainImageUriString by rememberSaveable { mutableStateOf<String?>(null) }
    var elements by remember { mutableStateOf(listOf<Element>()) }
    val context = LocalContext.current

    // Observe the contributor data
    val contributor by viewModel.contributorData.observeAsState()
    val isLoading by routeViewModel.isLoading.observeAsState(false)
    val successMessage by routeViewModel.successMessage.observeAsState()
    val errorMessage by routeViewModel.errorMessage.observeAsState()
    // Fetch contributor details when the composable is first composed
    LaunchedEffect(Unit) {
        viewModel.fetchContributorDetails()
    }

    // Observe real-time location updates
    val currentLocation by rememberLocationUpdates()

    // Launcher for capturing a picture for the main image
    val mainImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap: Bitmap? ->
        if (bitmap != null) {
            mainImageUriString = saveBitmapToUri(context, bitmap).toString() // Store URI as a string
            Log.d("ContributorCreateRoute", "Main building image updated: $mainImageUriString")
        } else {
            Log.d("ContributorCreateRoute", "Failed to capture main building image")
        }
    }

    // Launcher for capturing images for individual elements
    val elementImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap: Bitmap? ->
        bitmap?.let {
            val uri = saveBitmapToUri(context, it)
            elements = elements.toMutableList().apply {
                val index = indexOfFirst { it is Element.Image && (it as Element.Image).uri == null }
                if (index != -1) {
                    // Convert the saved URI string back to a Uri object
                    set(index, Element.Image(uri))  // Pass Uri directly, not uri.toString()
                    Log.d("ContributorCreateRoute", "Image for element at index $index set: $uri")
                }
            }
        } ?: Log.d("ContributorCreateRoute", "Failed to capture element image")
    }


    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = { LeftTA(navController,"Create Route") },
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
            /*Text(
               text = "Contributor ID: ${contributor?.id ?: "Loading..."}",
           )
           // Display the current location
          Text(
               text = currentLocation?.let {
                   "Current Location: Lat=${it.latitude}, Lng=${it.longitude}"
               } ?: "Fetching location..."
           )*/
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp,end = 16.dp, bottom = 16.dp, top = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center

            ) {
                Image(
                    painter = if (currentLocation == null) painterResource(R.drawable.sync) else painterResource(R.drawable.accept),
                    contentDescription = if (currentLocation == null) "Sync Image" else "Location Image",
                    modifier = Modifier.size(25.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = currentLocation?.let {
                        "Lat=${it.latitude}, Lng=${it.longitude}"
                    } ?: "Fetching location...",
                    fontFamily = publicSans,
                    fontWeight = FontWeight.Thin,
                    fontSize = 12.sp
                )
            }



            // From Text Field
            OutlinedTextField(
                value = from,
                onValueChange = { from = it },
                textStyle = TextStyle.Default.copy(fontFamily = publicSans, fontSize = 13.sp, fontWeight = FontWeight.ExtraLight),
                label = { Text("Name of the current building", fontFamily = publicSans, fontSize = 13.sp) },
                trailingIcon = {
                    IconButton(onClick = { /* */ }) {
                        Icon(imageVector = Icons.Default.Fort, contentDescription = null)
                    }
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(18.dp))

            // To Text Field
            OutlinedTextField(
                value = to,
                onValueChange = { to = it },
                textStyle = TextStyle.Default.copy(fontFamily = publicSans, fontSize = 13.sp, fontWeight = FontWeight.ExtraLight),
                label = { Text("Name of the destination room, space or building", fontFamily = publicSans, fontSize = 13.sp) },
                trailingIcon = {
                    IconButton(onClick = { /* */ }) {
                        Icon(imageVector = Icons.Default.Flag, contentDescription = null)
                    }
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(18.dp))

            // Main Building Image
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.Start
            ) {
                Text("Main Building Image", fontFamily = publicSans, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                Spacer(modifier = Modifier.height(12.dp))


                Image(
                    painter = rememberAsyncImagePainter(model = mainImageUriString?.let { Uri.parse(it) } ?: R.drawable.defaultimage),
                    contentDescription = "Image of your current location",
                    modifier = Modifier
                        .width(240.dp)
                        .height(150.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .clickable {
                            mainImageLauncher.launch(null)
                            Log.d("ContributorCreateRoute", "Main image capture initiated")
                        },
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.height(14.dp))
                HorizontalDivider(thickness = 2.dp)
                Spacer(modifier = Modifier.height(10.dp))
            }

            // Divider Text
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Text("Create Route", fontFamily = publicSans, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }

            // Display our Dynamic input field and images
            elements.forEachIndexed { index, element ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    when (element) {
                        is Element.TextField -> {
                            OutlinedTextField(
                                value = element.text,
                                onValueChange = { newText ->
                                    elements = elements.toMutableList().apply {
                                        set(index, Element.TextField(newText))
                                    }
                                },
                                textStyle = TextStyle.Default.copy(fontFamily = publicSans, fontSize = 13.sp, fontWeight = FontWeight.ExtraLight),
                                label = { Text("Input Field ${index + 1}", fontFamily = publicSans, fontSize = 13.sp) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                        is Element.Image -> {
                            Image(
                                painter = rememberAsyncImagePainter(model = element.uri?.toString() ?: R.drawable.defaultimage),
                                contentDescription = "Landmark Image",
                                modifier = Modifier
                                    .width(240.dp)
                                    .height(150.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .clickable {
                                        elementImageLauncher.launch(null)
                                        Log.d("ContributorCreateRoute", "Element image capture initiated for index $index")
                                    },
                                contentScale = ContentScale.Crop
                            )
                        }
                    }
                    IconButton(onClick = {
                        elements = elements.toMutableList().apply {
                            removeAt(index)
                        }
                        Log.d("ContributorCreateRoute", "Removed element at index $index. Remaining elements: ${elements.size}")
                    }) {
                        Icon(Icons.Default.Close, contentDescription = "Delete")
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Add Elements Buttons
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(
                    onClick = {
                        val route = Route(
                            creatorId = contributor?.id ?: "",
                            from = from,
                            fromLat = currentLocation?.latitude.toString(),
                            fromLong = currentLocation?.longitude.toString(),
                            to = to,
                            mainImageUri = mainImageUriString ?: "",
                            elements = elements.map {
                                when (it) {
                                    is Element.TextField -> RouteElement(
                                        type = "text",
                                        text = it.text
                                    )
                                    is Element.Image -> RouteElement(
                                        type = "image",
                                        imageUri = it.uri.toString()
                                    )
                                }
                            }
                        )
                        routeViewModel.uploadRoute(route)
                    },
                    modifier = Modifier.width(130.dp),
                    elevation = ButtonDefaults.elevatedButtonElevation(8.dp)
                ) {
                    Text("Save", fontFamily = publicSans, fontSize = 14.sp)
                }
                Spacer(modifier = Modifier.height(14.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Button(
                        onClick = {
                            elements = elements + Element.TextField("")
                            Log.d("ContributorCreateRoute", "Added new Element.TextField. Total elements: ${elements.size}")
                        },
                        modifier = Modifier.width(160.dp),
                        elevation = ButtonDefaults.elevatedButtonElevation(8.dp)
                    ) {
                        Text("Add Routes Text", fontFamily = publicSans, fontSize = 12.sp)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Button(
                        onClick = {
                            elements = elements + Element.Image()
                            Log.d("ContributorCreateRoute", "Added new Element.Image. Total elements: ${elements.size}")
                        },
                        modifier = Modifier.width(160.dp),
                        elevation = ButtonDefaults.elevatedButtonElevation(8.dp)
                    ) {
                        Text("Add Landmark Image", fontFamily = publicSans, fontSize = 12.sp)
                    }
                }
            }

            /*if (isLoading) { Text(text = "Uploading route...") }
            successMessage?.let { Text(text = it) }*/
            if (isLoading || successMessage != null) {
                LoadingOverlay(navController, isLoading = isLoading, successMessage = successMessage) {
                    routeViewModel.isLoading.value = false
                    routeViewModel.successMessage.value = null
                }
            }
        }
    }
}
