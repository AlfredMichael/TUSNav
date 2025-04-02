package ie.tus.tusnav.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ie.tus.tusnav.room.AppDatabase

// Factory requires the app database which would be provided by the main activity
// Our factory holds the neccessary information on how our viewmodel provider would be able to handle the app database when it is instantiated
class ProfileViewModelFactory(private val database: AppDatabase) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        //If the class being requested is  of type ProfileViewModel then
        // Create a new instance of the viewmodel and inject the database into it
        if (modelClass.isAssignableFrom(ProfileViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ProfileViewModel(database) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
