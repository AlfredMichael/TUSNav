package ie.tus.tusnav.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ie.tus.tusnav.classes.Contributor
import kotlinx.coroutines.flow.Flow


@Dao
interface ContributorDao {
    //Declares our set of methods for interacting with our local room database
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(contributor: Contributor)

    @Query("DELETE FROM contributors")
    suspend fun clearTable()

    @Query("SELECT * FROM contributors WHERE email = :email LIMIT 1")
    fun getContributorByEmail(email: String): Flow<Contributor?>

    @Query("SELECT * FROM contributors LIMIT 1")
    suspend fun getSingleContributor(): Contributor?

    @Query("SELECT COUNT(*) FROM contributors")
    suspend fun getCount(): Int
}





