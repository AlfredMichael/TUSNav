package ie.tus.tusnav.utility

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import android.app.Activity


@Composable
fun RequestPermissions(content: @Composable () -> Unit) {
    //State variables to track whether the permissions have been granted or not
    var cameraPermissionGranted by remember { mutableStateOf(false) }
    var locationPermissionGranted by remember { mutableStateOf(false) }
    var notificationPermissionGranted by remember { mutableStateOf(false) }

    //Gets the current activity context, so we can exit the app if the permissions are denied
    val context = LocalContext.current as Activity

    //Create launcher for permission requests, after completing either update the state variables or exit the application entirely
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            cameraPermissionGranted = true
        } else {
            // If permission is denied, exit the app
            context.finish()
        }
    }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            locationPermissionGranted = true
        } else {
            // If permission is denied, exit the app
            context.finish()
        }
    }

    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            notificationPermissionGranted = true
        } else {
            // If permission is denied, exit the app
            context.finish()
        }
    }

    //Request permission on first composition
    LaunchedEffect(Unit) {
        cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
    }

    //Chained location permission after camera permission has been granted
    LaunchedEffect(cameraPermissionGranted) {
        if (cameraPermissionGranted) {
            locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    //Chained notification permission after location permission has been granted
    LaunchedEffect(locationPermissionGranted) {
        if (locationPermissionGranted) {
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    //Display the content only is all the three permissions have been granted
    if (cameraPermissionGranted && locationPermissionGranted && notificationPermissionGranted) {
        content()
    }
}
