package ie.tus.tusnav.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.database.FirebaseDatabase
import ie.tus.tusnav.classes.Contributor
import ie.tus.tusnav.room.AppDatabase
import kotlinx.coroutines.launch


class ProfileViewModel(private val database: AppDatabase) : ViewModel() {
    private val firebaseDatabase: FirebaseDatabase = FirebaseDatabase.getInstance("https://tusnav-bythedev-default-rtdb.europe-west1.firebasedatabase.app/")
    private val contributorDao = database.contributorDao()

    // LiveData to hold the contributor's data
    val contributorData = MutableLiveData<Contributor?>()
    val ratingMessage = MutableLiveData<String>()
    val updateSuccess = MutableLiveData<Boolean>()
    val updateError = MutableLiveData<String?>()

    // Function to retrieve the contributor's ID from the Room database and fetch details from Firebase
    fun fetchContributorDetails() {
        viewModelScope.launch {
            // Retrieve the single contributor from the Room database
            val contributor = contributorDao.getSingleContributor()
            contributor?.let {
                val userId = it.id
                // Fetch details from Firebase Realtime Database
                fetchDetailsFromFirebase(userId)
            }
        }
    }

    // Function to fetch details from Firebase Realtime Database
    private fun fetchDetailsFromFirebase(userId: String) {
        val userRef = firebaseDatabase.getReference("Contributor").child(userId)
        userRef.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val contributor = task.result.getValue(Contributor::class.java)
                contributorData.postValue(contributor)
                contributor?.let {
                    ratingMessage.postValue(determineRatingMessage(it.ratings))
                }
            } else {
                // Handle error
                contributorData.postValue(null)
            }
        }
    }

    // Function to determine the rating message based on the ratings value
    /*
    * 0-40: "Low Rated Contributor"
    * 41-69: "Mid Rated Contributor"
    * 70-100: "High Rated Contributor
    * If it's higher than 100 we show a different message indicating the user is rated very highly and above 100
    */
    private fun determineRatingMessage(ratings: Int): String {
        return when {
            ratings in 0..40 -> "Low Rated Contributor"
            ratings in 41..69 -> "Mid Rated Contributor"
            ratings in 70..100 -> "High Rated Contributor"
            else -> "You can't game the system, man!"
        }
    }

    // Function to update the status and username in Firebase
    fun updateContributorDetails(userId: String, newUsername: String, newStatus: String) {
        val trimmedUsername = newUsername.trim()
        val trimmedStatus = newStatus.trim()

        if (trimmedUsername.isBlank() || trimmedStatus.isBlank()) {
            updateError.postValue("Username and status cannot be blank or contain only spaces.")
            return
        }

        val userRef = firebaseDatabase.getReference("Contributor").child(userId)
        userRef.child("username").setValue(trimmedUsername)
        userRef.child("status").setValue(trimmedStatus)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    updateSuccess.postValue(true)
                    fetchDetailsFromFirebase(userId) // Refresh the data
                } else {
                    updateError.postValue(task.exception?.message)
                }
            }
    }
}
