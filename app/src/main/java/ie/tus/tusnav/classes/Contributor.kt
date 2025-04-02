package ie.tus.tusnav.classes

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "contributors") //Marks this data class as room's database table
data class Contributor(
    @PrimaryKey val id: String, //Our primary key in the room's database table
    //Rest of the data field
    val email: String,
    val username: String = "Unknown",
    val status: String = "activated",
    val password: String,
    val contributions: Int = 0,
    val ratings: Int = 0
) {
    // No-argument constructor for Firebase to serialize and deserialize our contributors data class
    constructor() : this("", "", "Unknown", "activated", "", 0,0)
}


