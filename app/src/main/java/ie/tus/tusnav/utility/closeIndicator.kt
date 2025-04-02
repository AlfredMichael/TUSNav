package ie.tus.tusnav.utility

import kotlin.math.*

// Source: https://youtu.be/nsVsdHeTXIE?si=XVImlYrQgwu8i6SV
// Earth Volumetric mean radius: https://nssdc.gsfc.nasa.gov/planetary/factsheet/earthfact.html
fun closeIndicator(
    startLat: Double,
    startLng: Double,
    destinationLat: Double,
    destinationLng: Double,
    range: Double = 100.0 // Default range in meters : 100 meters is about the length of a football field
): Boolean {
    val earthRadius = 6371000.0 // Radius of the Earth in meters

    val dLat = Math.toRadians(destinationLat - startLat)
    val dLng = Math.toRadians(destinationLng - startLng)

    val a = sin(dLat / 2) * sin(dLat / 2) +
            cos(Math.toRadians(startLat)) * cos(Math.toRadians(destinationLat)) *
            sin(dLng / 2) * sin(dLng / 2)

    val c = 2 * atan2(sqrt(a), sqrt(1 - a))

    val distance = earthRadius * c

    return distance <= range
}
