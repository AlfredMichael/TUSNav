package ie.tus.tusnav.utility

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File

//Takes the bitmap of the current captured image, and store it in the cache as a png file
fun saveBitmapToUri(context: Context, bitmap: Bitmap): Uri {
    // Generate a unique filename based on the current timestamp (Solution to Unique Instances of Image)
    val fileName = "route_image_${System.currentTimeMillis()}.png"
    val file = File(context.cacheDir, fileName)

    file.outputStream().use {
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
    }

    //Get and return the uri pointing to the saved png file
    return FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
}
