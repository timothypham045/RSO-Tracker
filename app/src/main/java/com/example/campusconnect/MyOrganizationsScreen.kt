package com.example.campusconnect

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import androidx.compose.runtime.mutableStateListOf

// Information about a meeting poll
data class MeetingPoll(
    val id: String,
    val organizationId: String,
    val title: String,
    val startDate: String = "",
    val endDate: String = "",
    val startHour: Int = 16,
    val endHour: Int = 20
)


// Information about an organization
data class RSOOrganization(
    val id: String,
    val name: String,
    val meetings: List<MeetingPoll>
)

// Sample organizations and meetings
val sampleOrganizations = listOf(
    RSOOrganization(
        id = "sia",
        name = "Sigma Iota Alpha",
        meetings = listOf(
            MeetingPoll(
                "sia_chapter",
                "sia",
                "Chapter Meeting"
            ),
            MeetingPoll(
                "sia_board",
                "sia",
                "Executive Board Meeting"
            )
        )
    ),

    RSOOrganization(
        id = "alma",
        name = "ALMA",
        meetings = listOf(
            MeetingPoll(
                "alma_board",
                "alma",
                "General Board Meeting"
            ),
            MeetingPoll(
                "alma_latido",
                "alma",
                "Latido Planning Meeting"
            )
        )
    ),

    RSOOrganization(
        id = "shpe",
        name = "Society of Hispanic Professional Engineers",
        meetings = listOf(
            MeetingPoll(
                "shpe_general",
                "shpe",
                "General Meeting"
            ),
            MeetingPoll(
                "shpe_project",
                "shpe",
                "Project Team Meeting"
            )
        )
    )
)

@Composable
fun MyOrganizationsScreen(
    onBack: () -> Unit,
    onPollSelected: (MeetingPoll) -> Unit
) {

    var selectedOrganizationId by rememberSaveable {
        mutableStateOf<String?>(null)
    }

    val context = LocalContext.current

    val createdPolls = remember(context) {
        mutableStateListOf<MeetingPoll>().apply {
            addAll(MeetingPollStorage.load(context))
        }
    }

    var showCreatePoll by rememberSaveable {
        mutableStateOf(false)
    }


    val selectedOrganization = sampleOrganizations
        .find { it.id == selectedOrganizationId }


    if (showCreatePoll && selectedOrganization != null) {
        CreateMeetingPollScreen(
            organization = selectedOrganization,

            onCreatePoll = { newPoll ->
                createdPolls.add(newPoll)

                MeetingPollStorage.save(
                    context,
                    createdPolls.toList()
                )

                showCreatePoll = false
            },

            onBack = {
                showCreatePoll = false
            }
        )
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        // Back button
        TextButton(
            onClick = {
                if (selectedOrganizationId != null) {
                    selectedOrganizationId = null
                } else {
                    onBack()
                }
            }
        ) {
            Text(
                if (selectedOrganizationId == null)
                    "← Back to Home"
                else
                    "← My Organizations"
            )
        }

        if (selectedOrganization == null) {

            Text(
                "My Organizations",
                style = MaterialTheme.typography.headlineMedium
            )

            Text(
                "Select an organization to view its meetings."
            )

            // Display organizations
            sampleOrganizations.forEach { organization ->

                ElevatedCard(
                    onClick = {
                        selectedOrganizationId = organization.id
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement =
                            Arrangement.spacedBy(6.dp)
                    ) {

                        Text(
                            organization.name,
                            style = MaterialTheme.typography.titleMedium
                        )

                        Text(
                            "${organization.meetings.size} meeting polls"
                        )
                    }
                }
            }

        } else {

            Text(
                selectedOrganization.name,
                style = MaterialTheme.typography.headlineMedium
            )

            Text(
                "Select a meeting to submit availability."
            )

            Button(
                onClick = { showCreatePoll = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("+ Create Meeting Poll")
            }

            // Display meetings for selected organization
            val organizationMeetings =
                selectedOrganization.meetings +
                        createdPolls.filter {
                            it.organizationId == selectedOrganization.id
                        }

            organizationMeetings.forEach { meeting ->


            ElevatedCard(
                    onClick = {
                        onPollSelected(meeting)
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {

                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement =
                            Arrangement.spacedBy(6.dp)
                    ) {

                        Text(
                            meeting.title,
                            style = MaterialTheme.typography.titleMedium
                        )

                        Text("View Meeting Availability")
                    }
                }
            }
        }
    }
}
