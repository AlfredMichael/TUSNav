package ie.tus.tusnav.viewmodel

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.*
import ie.tus.tusnav.classes.Contributor
import ie.tus.tusnav.classes.Route

class DirectionsViewModel : ViewModel() {
    private val firebaseDatabase: FirebaseDatabase = FirebaseDatabase.getInstance("https://tusnav-bythedev-default-rtdb.europe-west1.firebasedatabase.app/")
    private val routesRef: DatabaseReference = firebaseDatabase.getReference("routes")
    private val contributorsRef: DatabaseReference = firebaseDatabase.getReference("Contributor")
    private val ipRef: DatabaseReference = firebaseDatabase.getReference("ip")

    // LiveData to hold the route details
    val routeDetails = MutableLiveData<Pair<Route, Contributor?>>()

    // Function to fetch route details by route ID
    fun fetchRouteDetails(routeId: String) {
        routesRef.child(routeId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val route = snapshot.getValue(Route::class.java)
                if (route != null) {
                    contributorsRef.child(route.creatorId).addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(contributorSnapshot: DataSnapshot) {
                            val contributor = contributorSnapshot.getValue(Contributor::class.java)
                            routeDetails.postValue(Pair(route, contributor))
                        }

                        override fun onCancelled(error: DatabaseError) {
                            // Later
                        }
                    })
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Later
            }
        })
    }

    // Function to update contributor rating and store IP address with route ID
    fun updateRatingAndStoreIp(contributorId: String, routeId: String, rating: Int, ipAddress: String) {
        // Sanitize the IP address by replacing periods with underscores
        val sanitizedIpAddress = ipAddress.replace(".", "_")
        val ipRouteKey = sanitizedIpAddress+"_"+routeId

        ipRef.child(ipRouteKey).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (!snapshot.exists()) {
                    // IP address and route ID combination does not exist, proceed with rating update
                    contributorsRef.child(contributorId).addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(contributorSnapshot: DataSnapshot) {
                            val contributor = contributorSnapshot.getValue(Contributor::class.java)
                            if (contributor != null) {
                                val updatedRating = contributor.ratings + rating
                                contributorsRef.child(contributorId).child("ratings").setValue(updatedRating)
                                ipRef.child(ipRouteKey).setValue(true) // Store IP address and route ID combination
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            // Later
                        }
                    })
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Later
            }
        })
    }
}
