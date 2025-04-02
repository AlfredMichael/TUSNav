package ie.tus.tusnav.classes

import java.util.Calendar

data class Route(
    val creatorId: String = "",
    val from: String = "",
    val fromLat: String = "",
    val fromLong: String = "",
    val to: String = "",
    val mainImageUri: String = "",
    val elements: List<RouteElement> = emptyList(),
    val date: Long = Calendar.getInstance().timeInMillis
)



