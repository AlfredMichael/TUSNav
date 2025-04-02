package ie.tus.tusnav.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import ie.tus.tusnav.classes.Contributor
import ie.tus.tusnav.room.AppDatabase
import kotlinx.coroutines.launch


class AuthViewModel(private val database: AppDatabase) : ViewModel() {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firebaseDatabase: FirebaseDatabase = FirebaseDatabase.getInstance("https://tusnav-bythedev-default-rtdb.europe-west1.firebasedatabase.app/")
    private val contributorDao = database.contributorDao()

    // LiveData to hold the error message
    val errorMessage = MutableLiveData<String?>()

    // LiveData to hold the success message
    val successMessage = MutableLiveData<String?>()

    // LiveData to hold the loading state
    val isLoading = MutableLiveData<Boolean>()

    // LiveData to hold the user's id
    val userId = MutableLiveData<String>()

    // LiveData to indicate login success
    val loginSuccess = MutableLiveData<Boolean>()

    // LiveData to indicate register success
    val registerSuccess = MutableLiveData<Boolean>()

    // LiveData to hold the contributor's data
    val contributorData = MutableLiveData<Contributor>()

    fun registerUser(email: String, password: String) {
        //Validating the email and password
        val lowerCaseEmail = email.lowercase()
        if (lowerCaseEmail.isEmpty() || password.isEmpty()) {
            errorMessage.postValue("Email and password must not be empty.")
            return
        }

        if (!lowerCaseEmail.endsWith(".tus.ie")) {
            errorMessage.postValue("Only TUS students are allowed to be contributors.")
            return
        }

        //Set other values
        val username = "Unknown"
        val status = "activated"
        val contributions = 0
        val ratings = 0

        isLoading.postValue(true)
        viewModelScope.launch {
            //Creating a new Firebase user with the email and password.
            auth.createUserWithEmailAndPassword(lowerCaseEmail, password)
                .addOnCompleteListener { task ->
                    isLoading.postValue(false)
                    if (task.isSuccessful) {
                        val firebaseUser = auth.currentUser
                        firebaseUser?.let {
                            //Saving the user's data in Firebase Realtime Database under the Contributor node.
                            val userId = it.uid
                            val contributor = Contributor(userId, lowerCaseEmail, username, status, password, contributions, ratings)
                            val userRef = firebaseDatabase.getReference("Contributor").child(userId)
                            userRef.setValue(contributor)
                                .addOnCompleteListener { dbTask ->
                                    if (dbTask.isSuccessful) {
                                        this@AuthViewModel.userId.postValue(userId)
                                        successMessage.postValue("You have been registered successfully!")

                                        // Clearing existing then saving the user's data locally in the Room database for future use
                                        viewModelScope.launch {
                                            contributorDao.clearTable()
                                            Log.d("AuthViewModel", "Inserting Contributor into Room during registration: $contributor")
                                            contributorDao.insert(contributor)
                                            val count = contributorDao.getCount()
                                            Log.d("AuthViewModel", "Room database record count after registration: $count")
                                        }

                                        registerSuccess.postValue(true)
                                    } else {
                                        errorMessage.postValue(dbTask.exception?.message)
                                        registerSuccess.postValue(false)
                                    }
                                }
                        }
                    } else {
                        errorMessage.postValue(task.exception?.message)
                        registerSuccess.postValue(false)
                    }
                }
        }
    }

    fun loginUser(email: String, password: String) {
        //Validating the email and password

        val lowerCaseEmail = email.lowercase()
        if (lowerCaseEmail.isEmpty() || password.isEmpty()) {
            errorMessage.postValue("Email and password must not be empty.")
            return
        }

        isLoading.postValue(true)
        viewModelScope.launch {
            //Signing in the user using Firebase Authentication
            auth.signInWithEmailAndPassword(lowerCaseEmail, password)
                .addOnCompleteListener { task ->
                    isLoading.postValue(false)
                    if (task.isSuccessful) {
                        val firebaseUser = auth.currentUser
                        firebaseUser?.let {
                            val userId = it.uid
                            this@AuthViewModel.userId.postValue(userId)
                            val userRef = firebaseDatabase.getReference("Contributor").child(userId)
                            Log.d("AuthViewModel", "A")
                            userRef.get().addOnCompleteListener { dbTask ->
                                if (dbTask.isSuccessful) {
                                    val contributor = dbTask.result.getValue(Contributor::class.java)
                                    Log.d("AuthViewModel", "B")
                                    contributor?.let {
                                        val updatedContributor = it.copy(password = password)
                                        contributorData.postValue(updatedContributor)
                                        Log.d("AuthViewModel", "c")

                                        // Save contributor data to Room database
                                        viewModelScope.launch {
                                            contributorDao.clearTable()
                                            Log.d("AuthViewModel", "D")
                                            contributorDao.insert(updatedContributor)
                                            val count = contributorDao.getCount()
                                            Log.d("AuthViewModel", "Room database record count after login: $count")

                                            // Ensure navigation only happens after database operations are complete
                                            if (count > 0) {
                                                successMessage.postValue("Login successful!")
                                                loginSuccess.postValue(true)
                                            } else {
                                                errorMessage.postValue("Failed to insert contributor into local database.")
                                                loginSuccess.postValue(false)
                                            }
                                        }

                                    } ?: run {
                                        errorMessage.postValue("Failed to retrieve user data.")
                                        loginSuccess.postValue(false)
                                    }
                                } else {
                                    errorMessage.postValue(dbTask.exception?.message)
                                    loginSuccess.postValue(false)

                                    Log.d("AuthViewModel", "eRROR IN INSERT")
                                }
                            }
                        }
                    } else {
                        errorMessage.postValue(task.exception?.message)
                        loginSuccess.postValue(false)
                    }
                }
        }
    }

    fun autoLogin() {
        viewModelScope.launch {
            val contributor = contributorDao.getSingleContributor() // Fetch the single user from Room
            contributor?.let {
                val email = it.email
                val password = it.password
                Log.d("AuthViewModel", "Attempting auto-login with email: $email")
                loginUser(email, password) // Delegate to the existing loginUser method
            } ?: run {
                errorMessage.postValue("No saved credentials found. Please log in manually.")
            }
        }
    }

    // Fetch contributor by email
    fun fetchContributorByEmail(email: String) {
        viewModelScope.launch {
            contributorDao.getContributorByEmail(email).collect { contributor ->
                contributor?.let {
                    contributorData.postValue(it)
                } ?: run {
                    errorMessage.postValue("Contributor not found.")
                }
            }
        }
    }

    // Reset toast messages
    fun resetMessages() {
        errorMessage.postValue(null)
        successMessage.postValue(null)
    }

    // Logout function
    fun logout() {
        viewModelScope.launch {
            // Clear all data from Room database
            contributorDao.clearTable()
            // Log out from Firebase Auth
            auth.signOut()
            // Clear LiveData
            userId.postValue("")
            contributorData.postValue(Contributor())
            successMessage.postValue("Logged out successfully.")
        }
    }
}


