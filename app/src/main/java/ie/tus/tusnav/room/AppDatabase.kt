package ie.tus.tusnav.room

import androidx.room.Database
import androidx.room.RoomDatabase
import ie.tus.tusnav.classes.Contributor

@Database(entities = [Contributor::class], version = 2) //Defines the room database with the contributor entity
abstract class AppDatabase : RoomDatabase() { //Actual database entry point
    abstract fun contributorDao(): ContributorDao //Instance of out contributors dao
}
