package ie.tus.tusnav.utility

import android.content.Context
import android.util.Log
import ie.tus.tusnav.R
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException


fun fetchOutdoorDirections(
    context: Context,
    originLat: Double,
    originLng: Double,
    destinationLat: Double,
    destinationLng: Double,
    callback: (List<String>) -> Unit
) {
    val apiKey = context.getString(R.string.google_maps_api_key)
    val url = "https://maps.googleapis.com/maps/api/directions/json" +
            "?origin=$originLat,$originLng" +
            "&destination=$destinationLat,$destinationLng" +
            "&mode=transit&avoid=highways&key=$apiKey"

    Log.d("MapScreen", "Generated URL: $url")

    // Use OkHttp for making the HTTP request
    val client = OkHttpClient()

    val request = Request.Builder()
        .url(url)
        .build()

    client.newCall(request).enqueue(object : Callback {
        override fun onFailure(call: Call, e: IOException) {
            Log.e("DirectionsAPI", "Error fetching directions: ${e.message}")
            callback(emptyList()) // Return an empty list on failure
        }

        override fun onResponse(call: Call, response: Response) {
            if (response.isSuccessful) {
                response.body?.string()?.let { jsonResponse ->
                    Log.d("DirectionsAPI", "Response: $jsonResponse")

                    // Parse JSON and extract instructions
                    val instructions = parseDirectionsResponse(jsonResponse)
                    callback(instructions) // Return the instructions
                } ?: callback(emptyList())
            } else {
                Log.e("DirectionsAPI", "API call failed: ${response.message}")
                callback(emptyList())
            }
        }
    })
}

// Helper function to parse the JSON response and extract html_instructions
private fun parseDirectionsResponse(jsonResponse: String): List<String> {
    val instructions = mutableListOf<String>()

    try {
        val jsonObject = JSONObject(jsonResponse)
        val routes = jsonObject.getJSONArray("routes")

        for (i in 0 until routes.length()) {
            val route = routes.getJSONObject(i)
            val legs = route.getJSONArray("legs")

            for (j in 0 until legs.length()) {
                val leg = legs.getJSONObject(j)
                val steps = leg.getJSONArray("steps")

                for (k in 0 until steps.length()) {
                    val step = steps.getJSONObject(k)
                    val htmlInstruction = step.optString("html_instructions", "")
                    if (htmlInstruction.isNotEmpty()) {
                        val cleanedInstruction = cleanHtmlTags(htmlInstruction)
                        instructions.add(cleanedInstruction)
                    }
                }
            }
        }
    } catch (e: JSONException) {
        Log.e("DirectionsAPI", "Error parsing JSON: ${e.message}")
    }

    return instructions
}

// Helper function to remove HTML tags from the string
private fun cleanHtmlTags(html: String): String {
    return html.replace(Regex("<[^>]*>"), "").replace("&nbsp;", " ")
}

