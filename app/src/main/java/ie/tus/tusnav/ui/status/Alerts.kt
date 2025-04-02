package ie.tus.tusnav.ui.status

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext


@Composable
fun ErrorToast(message: String) {
    Toast.makeText(LocalContext.current, message, Toast.LENGTH_SHORT).show()
}

@Composable
fun SuccessToast(message: String) {
    Toast.makeText(LocalContext.current, message, Toast.LENGTH_SHORT).show()
}

@Composable
fun LoadingToast() {
    Toast.makeText(LocalContext.current, "Loading...", Toast.LENGTH_LONG).show()
}

