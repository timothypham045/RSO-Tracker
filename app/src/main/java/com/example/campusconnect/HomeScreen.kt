
package com.example.campusconnect

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.platform.LocalContext

data class RSOEvent(
    val name: String,
    val organization: String,
    val date: String,
    val time: String,
    val location: String,
    val category: String
)

@Composable
fun HomeScreen(userName: String, onLogout: () -> Unit) {

    var selectedEvent by remember {
        mutableStateOf<RSOEvent?>(null)
    }

    var showCreateEvent by remember {
        mutableStateOf(false)
    }

    var showOrganizations by remember {
        mutableStateOf(false)
    }

    var selectedPoll by remember {
        mutableStateOf<MeetingPoll?>(null)
    }

    val context = LocalContext.current

    val savedEvents = remember(context) {
        mutableStateListOf<RSOEvent>().apply {
            addAll(EventStorage.loadBookmarks(context))
        }
    }

    val events = remember(context) {
        mutableStateListOf<RSOEvent>().apply {

            val storedEvents = EventStorage.loadEvents(context)

            if (storedEvents != null) {
                addAll(storedEvents)
            } else {
                addAll(
                    listOf(
                        RSOEvent(
                            "ALMA General Meeting",
                            "ALMA",
                            "November 18, 2026",
                            "5:00 PM",
                            "Student Union",
                            "Multicultural"
                        ),
                        RSOEvent(
                            "SHPE Networking Night",
                            "SHPE",
                            "November 19, 2026",
                            "6:00 PM",
                            "Engineering Hall",
                            "Professional"
                        ),
                        RSOEvent(
                            "Campus Art Workshop",
                            "Art Club",
                            "November 20, 2026",
                            "4:00 PM",
                            "Fine Arts Center",
                            "Art"
                        )
                    )
                )
            }
        }
    }


    val categories = listOf(
        "All",
        "Professional",
        "Multicultural",
        "Social",
        "Academic",
        "Art"
    )

    var selectedCategory by remember { mutableStateOf("All") }

    var searchQuery by rememberSaveable {
        mutableStateOf("")
    }


    val filteredEvents = events.filter { event ->

        val matchesCategory =
            selectedCategory == "All" ||
                    event.category == selectedCategory

        val matchesSearch =
            event.name.contains(searchQuery, ignoreCase = true) ||
                    event.organization.contains(searchQuery, ignoreCase = true) ||
                    event.location.contains(searchQuery, ignoreCase = true) ||
                    event.category.contains(searchQuery, ignoreCase = true)

        matchesCategory && matchesSearch
    }

    val currentPoll = selectedPoll

    if (currentPoll != null) {
        MeetingSchedulerScreen(
            poll = currentPoll,
            onBack = {
                selectedPoll = null
            }
        )
        return
    }

    if (showOrganizations) {
        MyOrganizationsScreen(
            onBack = {
                showOrganizations = false
            },
            onPollSelected = { poll ->
                selectedPoll = poll
            }
        )
        return
    }

    if (showCreateEvent) {
        CreateEventScreen(
            onCreateEvent = { newEvent ->
                events.add(0, newEvent)

                // Save the updated event list
                EventStorage.saveEvents(
                    context,
                    events.toList()
                )

                showCreateEvent = false
                selectedCategory = "All"
                searchQuery = ""
            },
            onBack = {
                showCreateEvent = false
            }
        )
        return
    }

    if (selectedEvent != null) {

        val event = selectedEvent!!

        EventDetailsScreen(
            event = event,

            isSaved = savedEvents.contains(event),

            onSaveClick = {
                if (savedEvents.contains(event)) {
                    savedEvents.remove(event)
                } else {
                    savedEvents.add(event)
                }

                // Save bookmarks after every change
                EventStorage.saveBookmarks(
                    context,
                    savedEvents.toList()
                )
            },


            onBack = {
                selectedEvent = null
            }
        )

        return
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        item {
            Text(
                text = "RSO Tracker",
                style = MaterialTheme.typography.headlineLarge
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Displays a personalized greeting for the logged-in user
            Text(
                text = "Welcome, $userName!",
                style = MaterialTheme.typography.titleMedium
            )

            Text(
                text = "Discover what's happening on campus!",
                style = MaterialTheme.typography.bodyLarge
            )


            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Search Events") },
                placeholder = { Text("Search by event or RSO...") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { showCreateEvent = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("+ Create New Event")
            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedButton(
                onClick = {
                    showOrganizations = true
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("My Organizations & Meetings")
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Allows the user to log out
            OutlinedButton(
                onClick = onLogout,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Log Out")
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Explore Categories",
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(modifier = Modifier.height(12.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(categories.size) { index ->
                    val category = categories[index]

                    FilterChip(
                        selected = selectedCategory == category,
                        onClick = { selectedCategory = category },
                        label = { Text(category) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Upcoming Events",
                style = MaterialTheme.typography.titleLarge
            )
        }

        if (filteredEvents.isEmpty()) {
            item {
                Text(
                    text = "No events found. Try another search or category.",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        items(filteredEvents) { event ->
            EventCard(
                event = event,
                onClick = { selectedEvent = event }
            )
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "My Saved Events",
                style = MaterialTheme.typography.titleLarge
            )
        }

        if (savedEvents.isEmpty()) {
            item {
                Text(
                    text = "You haven't saved any events yet.",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        } else {
            items(savedEvents.toList()) { event ->
                EventCard(
                    event = event,
                    onClick = { selectedEvent = event }
                )
            }
        }
    }
}

@Composable
fun EventCard(
    event: RSOEvent,
    onClick: () -> Unit
) {

    ElevatedCard(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {

            Text(
                text = event.name,
                style = MaterialTheme.typography.titleMedium
            )

            Text(
                text = event.organization,
                style = MaterialTheme.typography.labelLarge
            )

            Text(text = "Date: ${event.date}")
            Text(text = "Time: ${event.time}")
            Text(text = "Location: ${event.location}")
        }
    }
}
