package ie.tus.tusnav.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ie.tus.tusnav.room.AppDatabase

// Factory requires the app database which would be provided by the main activity
// Our factory holds the neccessary information on how our viewmodel provider would be able to handle the app database when it is instantiated
class AuthViewModelFactory(private val database: AppDatabase) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        //If the class being requested is  of type AuthViewModel then
        // Create a new instance of the viewmodel and inject the database into it
        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AuthViewModel(database) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

