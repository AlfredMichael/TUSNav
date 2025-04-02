package ie.tus.tusnav.viewmodel

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.tasks.Tasks
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.MutableData
import com.google.firebase.database.Transaction
import com.google.firebase.storage.FirebaseStorage
import ie.tus.tusnav.classes.Route
import ie.tus.tusnav.classes.RouteElement
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap


class RouteViewModel : ViewModel() {
    private val firebaseDatabase: FirebaseDatabase = FirebaseDatabase.getInstance("https://tusnav-bythedev-default-rtdb.europe-west1.firebasedatabase.app/")
    private val firebaseStorage: FirebaseStorage = FirebaseStorage.getInstance()
    private val contributorsRef: DatabaseReference = firebaseDatabase.getReference("Contributor")

    // LiveData to hold the loading state
    val isLoading = MutableLiveData<Boolean>()

    // LiveData to hold the success message
    val successMessage = MutableLiveData<String?>()

    // LiveData to hold the error message
    val errorMessage = MutableLiveData<String?>()

    // Function to upload route to Firebase
    fun uploadRoute(route: Route) {
        // Validate all empty or null fields
        if (route.creatorId.isBlank() || route.from.isBlank() || route.fromLat.isBlank() || route.fromLong.isBlank() || route.to.isBlank() || route.mainImageUri.isBlank()) {
            errorMessage.postValue("All fields must be filled.")
            return
        }

        isLoading.postValue(true)

        // Upload main image to Firebase Storage
        val mainImageRef = firebaseStorage.reference.child("route_images/${UUID.randomUUID()}.jpg")
        val mainImageUri = Uri.parse(route.mainImageUri)
        mainImageRef.putFile(mainImageUri)
            .addOnSuccessListener { taskSnapshot ->
                mainImageRef.downloadUrl.addOnSuccessListener { uri ->
                    val updatedRoute = route.copy(mainImageUri = uri.toString())

                    // Upload elements images to Firebase Storage concurrently
                    val updatedElements = ConcurrentHashMap<Int, RouteElement>()
                    val uploadTasks = route.elements.mapIndexed { index, element ->
                        if (element.type == "image" && !element.imageUri.isNullOrEmpty()) {
                            val imageRef = firebaseStorage.reference.child("route_images/${UUID.randomUUID()}.jpg")
                            val imageUri = Uri.parse(element.imageUri)
                            imageRef.putFile(imageUri).continueWithTask { task ->
                                if (!task.isSuccessful) {
                                    task.exception?.let { throw it }
                                }
                                imageRef.downloadUrl
                            }.addOnSuccessListener { downloadUri ->
                                updatedElements[index] = RouteElement(
                                    type = "image",
                                    imageUri = downloadUri.toString()
                                )
                            }
                        } else {
                            updatedElements[index] = element // For "text" elements or already uploaded images
                            null
                        }
                    }.filterNotNull()

                    Tasks.whenAllComplete(uploadTasks).addOnCompleteListener {
                        val finalElements = (0 until route.elements.size).map { updatedElements[it]!! }
                        val finalRoute = updatedRoute.copy(elements = finalElements)

                        // Upload route to Firebase Realtime Database
                        val routeRef = firebaseDatabase.getReference("routes").push()
                        routeRef.setValue(finalRoute)
                            .addOnCompleteListener { dbTask ->
                                isLoading.postValue(false)
                                if (dbTask.isSuccessful) {
                                    successMessage.postValue("Route uploaded successfully!")
                                    incrementContributions(route.creatorId)
                                } else {
                                    errorMessage.postValue(dbTask.exception?.message)
                                }
                            }
                    }
                }
            }
            .addOnFailureListener { exception ->
                isLoading.postValue(false)
                errorMessage.postValue(exception.message)
            }
    }

    // Function to increment contributions in the Contributor node
    fun incrementContributions(contributorId: String) {
        contributorsRef.child(contributorId).child("contributions").runTransaction(object : Transaction.Handler {
            override fun doTransaction(currentData: MutableData): Transaction.Result {
                val currentContributions = currentData.getValue(Int::class.java) ?: 0
                currentData.value = currentContributions + 1
                return Transaction.success(currentData)
            }

            override fun onComplete(error: DatabaseError?, committed: Boolean, currentData: DataSnapshot?) {
                if (error != null) {
                    errorMessage.postValue("Failed to increment contributions: ${error.message}")
                }
            }
        })
    }

}
