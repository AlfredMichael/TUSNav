package ie.tus.tusnav.classes

import android.net.Uri

sealed class Element {
    data class TextField(var text: String) : Element()
    data class Image(var uri: Uri? = null) : Element()
}
