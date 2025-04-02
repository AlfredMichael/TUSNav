package ie.tus.tusnav.ui.user

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import ie.tus.tusnav.R
import ie.tus.tusnav.ui.bottomappbar.TusNavBottomAppBar
import ie.tus.tusnav.ui.theme.TUSNavTheme
import ie.tus.tusnav.ui.theme.publicSans
import ie.tus.tusnav.ui.topappbars.CenterTA
import ie.tus.tusnav.utility.formatDate
import ie.tus.tusnav.viewmodel.HomeViewModel

//Suggestions Card
@Composable
fun SuggestionsCard(
    searchQuery: String,
    suggestions: List<String>,
    onSuggestionClick: (String) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(max = 120.dp)
            .verticalScroll(rememberScrollState()),
        shape = RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp),
        elevation = CardDefaults.elevatedCardElevation(8.dp)
    ) {
        Column {
            for (suggestion in suggestions.filter { it.contains(searchQuery, ignoreCase = true) }) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            onSuggestionClick(suggestion) // Update the search query on click
                        }
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = suggestion,
                        fontFamily = publicSans,
                        modifier = Modifier.weight(1f)
                    )
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        modifier = Modifier.size(20.dp),
                        contentDescription = suggestion
                    )
                }
            }
        }
    }
}


@Composable
fun SingleRouteCard(
    navController: NavController,
    id: String,
    imageUrl: String,
    fromLocation: String,
    toLocation: String,
    date: String,
    author: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        elevation = CardDefaults.elevatedCardElevation(8.dp)
    ) {
        Column {
            // Image
            AsyncImage(
                model = imageUrl,
                contentDescription = "Route Image",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
            )

            // Data
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                // From
                Row {
                    Text(
                        text = "From:",
                        fontFamily = publicSans,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = fromLocation,
                        fontFamily = publicSans,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Normal
                    )
                }

                // To
                Row {
                    Text(
                        text = "To:",
                        fontFamily = publicSans,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = toLocation,
                        fontFamily = publicSans,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Normal
                    )
                }

                // When and by
                Row {
                    Text(
                        text = "$date by $author",
                        fontFamily = publicSans,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Thin
                    )
                }


                // Button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(
                        onClick = {
                            navController.navigate("user_direction_screen/$id")

                        },
                        elevation = ButtonDefaults.elevatedButtonElevation(8.dp)
                    ) {
                        Text(
                            text = "Start",
                            fontFamily = publicSans,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
    Spacer(modifier = Modifier.height(12.dp))
}


//Main Screen
@Composable
fun UserHomeScreen(navController: NavController) {
    val homeViewModel: HomeViewModel = viewModel()
    val toValues by homeViewModel.toList.observeAsState(emptyList())
    val recentRoutes by homeViewModel.recentRoute.observeAsState(null)

    var searchQuery by rememberSaveable { mutableStateOf("") }
    var suggestions by rememberSaveable { mutableStateOf(toValues) }

    // Call the fetchToValues function to load the data
    LaunchedEffect(Unit) {
        homeViewModel.fetchToValues()
    }

    // Update suggestions whenever toValues changes
    LaunchedEffect(toValues) {
        suggestions = toValues
    }

    // Call the fetchRecentRoute function to load the most recent and highly rated route
    LaunchedEffect(searchQuery) {
        homeViewModel.fetchRecentRoute(searchQuery)
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = { CenterTA("TUSNav") },
        bottomBar = { TusNavBottomAppBar(navController) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Search TextField
            TextField(
                value = searchQuery,
                onValueChange = { query ->
                    searchQuery = query
                },
                label = {
                    Text(
                        text = "Where to?",
                        fontFamily = publicSans,
                    )
                },
                shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
                modifier = Modifier.fillMaxWidth()
            )

            // SuggestionsCard
            if (searchQuery.isNotEmpty()) {
                SuggestionsCard(
                    searchQuery = searchQuery,
                    suggestions = suggestions,
                    onSuggestionClick = { suggestion ->
                        searchQuery = suggestion // Update the search query on click
                    }
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            if (recentRoutes == null) {
                // Show loading indicator
                Image(
                    painter = painterResource(R.drawable.sync),
                    contentDescription = "Loading Image",
                    modifier = Modifier.size(100.dp)
                )
                Text(text = "Loading...")
            } else if (recentRoutes!!.isEmpty()) {
                // Show "No routes found" message
                Image(
                    painter = painterResource(R.drawable.remove),
                    contentDescription = "No routes found Image",
                    modifier = Modifier.size(100.dp)
                )
                Text(
                    text = if (searchQuery.isEmpty()) "No routes available." else "No results found for \"$searchQuery\"."
                )
            } else {
                // Display all routes in the list
                recentRoutes!!.forEach { (route, contributor, routeId) ->
                    SingleRouteCard(
                        navController = navController,
                        id = routeId, // Use the actual route ID
                        imageUrl = route.mainImageUri,
                        fromLocation = route.from,
                        toLocation = route.to,
                        date = formatDate(route.date),
                        author = contributor?.username ?: "Unknown"
                    )
                }
            }



        }
    }
}



@Preview(showBackground = true)
@Composable
fun UserHomeScreenPreview(){
    TUSNavTheme {
        val navController = rememberNavController()
        UserHomeScreen(navController)
    }
}


