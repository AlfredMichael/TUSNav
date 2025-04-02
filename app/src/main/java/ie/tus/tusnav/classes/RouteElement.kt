package ie.tus.tusnav.classes


data class RouteElement(
    val type: String = "", // "text" or "image"
    val text: String? = null,
    val imageUri: String? = null
)
