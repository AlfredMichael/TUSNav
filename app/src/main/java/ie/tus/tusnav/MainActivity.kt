package ie.tus.tusnav

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.room.Room
import ie.tus.tusnav.room.AppDatabase
import ie.tus.tusnav.ui.contributor.ContributorCreateRoute
import ie.tus.tusnav.ui.contributor.ContributorLoginScreen
import ie.tus.tusnav.ui.contributor.ContributorProfileScreen
import ie.tus.tusnav.ui.theme.TUSNavTheme
import ie.tus.tusnav.ui.user.UserContactScreen
import ie.tus.tusnav.ui.user.UserDirectionScreen
import ie.tus.tusnav.ui.user.UserHomeScreen
import ie.tus.tusnav.utility.RequestPermissions

class MainActivity : ComponentActivity() {
    private lateinit var database: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Initialize Room database
        database = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "my-database"
        ).build()

        setContent {
            TUSNavTheme {
                PermissionHandler {
                    NavGraph(database)
                }
            }
        }
    }
}


@Composable
fun PermissionHandler(content: @Composable () -> Unit) {
    RequestPermissions {
        content()
    }
}


@Composable
fun NavGraph(database: AppDatabase, startDestination: String = "user_home_screen") {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = startDestination) {
        composable("contributor_login_screen") { ContributorLoginScreen(navController, database) }
        composable("contributor_profile_screen") { ContributorProfileScreen(navController, database) }
        composable("contributor_create_route") { ContributorCreateRoute(navController, database) }
        composable("user_home_screen") { UserHomeScreen(navController) }
        composable("user_direction_screen/{id}") { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id") ?: "dummy_id"
            UserDirectionScreen(navController, id)
        }
        composable("user_contact_screen") { UserContactScreen(navController) }
    }
}


