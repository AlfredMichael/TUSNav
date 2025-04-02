package ie.tus.tusnav.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.FirebaseDatabase
import androidx.lifecycle.viewModelScope
import com.google.firebase.database.*
import ie.tus.tusnav.classes.Contributor
import ie.tus.tusnav.classes.Route
import kotlinx.coroutines.launch


class HomeViewModel : ViewModel() {
    private val firebaseDatabase: FirebaseDatabase = FirebaseDatabase.getInstance("https://tusnav-bythedev-default-rtdb.europe-west1.firebasedatabase.app/")
    private val routesRef: DatabaseReference = firebaseDatabase.getReference("routes")
    private val contributorsRef: DatabaseReference = firebaseDatabase.getReference("Contributor")

    // LiveData to hold the list of "to" values
    val toList = MutableLiveData<List<String>>()

    // LiveData to hold the most recent route
    val recentRoute = MutableLiveData<List<Triple<Route, Contributor?, String>>?>()

    // Function to fetch all "to" values from the routes
    fun fetchToValues() {
        viewModelScope.launch {
            routesRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val toValues = mutableListOf<String>()
                    for (routeSnapshot in snapshot.children) {
                        val toValue = routeSnapshot.child("to").getValue(String::class.java)
                        if (toValue != null) {
                            toValues.add(toValue)
                        }
                    }
                    toList.postValue(toValues)
                }

                override fun onCancelled(error: DatabaseError) {
                    //Later
                }
            })
        }
    }

    // Function to fetch the most recent and highly rated route
    fun fetchRecentRoute(searchQuery: String? = null) {
        viewModelScope.launch {
            routesRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val routes = mutableListOf<Triple<Route, Contributor?, String>>() // Include route ID
                    val contributorsMap = mutableMapOf<String, Contributor>()

                    // Fetch all contributors
                    contributorsRef.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(contributorsSnapshot: DataSnapshot) {
                            for (contributorSnapshot in contributorsSnapshot.children) {
                                val contributor = contributorSnapshot.getValue(Contributor::class.java)
                                if (contributor != null) {
                                    contributorsMap[contributor.id] = contributor
                                }
                            }

                            // Filter and process routes
                            for (routeSnapshot in snapshot.children) {
                                val route = routeSnapshot.getValue(Route::class.java)
                                if (route != null) {
                                    if (searchQuery == null || route.to.contains(searchQuery, ignoreCase = true)) {
                                        val contributor = contributorsMap[route.creatorId]
                                        val routeId = routeSnapshot.key ?: ""
                                        routes.add(Triple(route, contributor, routeId)) // Add route ID
                                    }
                                }
                            }

                            // Sort routes by contributor's ratings (descending) and date (ascending)
                            routes.sortWith(
                                compareByDescending<Triple<Route, Contributor?, String>> { it.second?.ratings ?: 0 }
                                    .thenBy { it.first.date }
                            )

                            // Update LiveData with sorted routes or an empty list
                            recentRoute.postValue(routes)
                        }

                        override fun onCancelled(error: DatabaseError) {
                            //Later
                        }
                    })
                }

                override fun onCancelled(error: DatabaseError) {
                    //Later
                }
            })
        }
    }

}
