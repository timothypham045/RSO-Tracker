
package com.example.campusconnect

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.json.JSONArray
import org.json.JSONObject

@Composable
fun MeetingSchedulerScreen(
    poll: MeetingPoll,
    onBack: () -> Unit
) {
    val context = LocalContext.current


    val preferences = remember(context, poll.id) {
        context.getSharedPreferences(
            "meeting_poll_${poll.id}",
            Context.MODE_PRIVATE
        )
    }

    val days = listOf(
        "Monday", "Tuesday", "Wednesday",
        "Thursday", "Friday"
    )

    val times = listOf(
        "4:00 PM", "5:00 PM", "6:00 PM",
        "7:00 PM", "8:00 PM"
    )

    // Load saved member schedules
    val memberSchedules = remember(context, poll.id) {
        mutableStateMapOf<String, Set<String>>().apply {
            val savedData = preferences.getString(
                "schedules", "{}"
            ) ?: "{}"

            val json = try {
                JSONObject(savedData)
            } catch (e: Exception) {
                JSONObject()
            }

            val names = json.keys()

            while (names.hasNext()) {
                val name = names.next()
                val slots = json.optJSONArray(name)
                    ?: JSONArray()

                val availability = mutableSetOf<String>()

                for (i in 0 until slots.length()) {
                    availability.add(slots.getString(i))
                }

                put(name, availability)
            }
        }
    }

    var memberName by remember(poll.id) {
        mutableStateOf("")
    }

    val selectedSlots = remember(poll.id) {
        mutableStateListOf<String>()
    }

    var showResults by remember(poll.id) {
        mutableStateOf(false)
    }

    var saved by remember(poll.id) {
        mutableStateOf(false)
    }

    fun selectMember(name: String) {
        memberName = name
        selectedSlots.clear()
        selectedSlots.addAll(
            memberSchedules[name].orEmpty()
        )
        saved = false
    }

    val totalMembers = memberSchedules.size

    val slotCounts = days.flatMap { day ->
        times.map { time ->
            val slot = "$day-$time"
            slot to memberSchedules.values.count {
                slot in it
            }
        }
    }

    val highestCount = slotCounts.maxOfOrNull {
        it.second
    } ?: 0

    val bestSlots = slotCounts.filter {
        it.second == highestCount && highestCount > 0
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        TextButton(onClick = onBack) {
            Text("← Back to Home")
        }

        Text(
            "Group Meeting Scheduler",
            style = MaterialTheme.typography.headlineMedium
        )

        val organizationName = sampleOrganizations
            .find { it.id == poll.organizationId }
            ?.name ?: "Organization"

        Text(
            text = organizationName,
            style = MaterialTheme.typography.titleMedium
        )

        Text(
            text = poll.title,
            style = MaterialTheme.typography.titleLarge
        )


        Text(
            "$totalMembers " +
                    if (totalMembers == 1) {
                        "member submitted availability"
                    } else {
                        "members submitted availability"
                    }
        )

        TabRow(
            selectedTabIndex = if (showResults) 1 else 0
        ) {
            Tab(
                selected = !showResults,
                onClick = { showResults = false },
                text = { Text("My Availability") }
            )

            Tab(
                selected = showResults,
                onClick = { showResults = true },
                text = { Text("Group Results") }
            )
        }

        if (!showResults) {

            OutlinedTextField(
                value = memberName,
                onValueChange = { newName ->
                    memberName = newName
                    selectedSlots.clear()

                    val existing = memberSchedules.keys
                        .firstOrNull {
                            it.equals(
                                newName.trim(),
                                ignoreCase = true
                            )
                        }

                    if (existing != null) {
                        selectedSlots.addAll(
                            memberSchedules[existing].orEmpty()
                        )
                    }

                    saved = false
                },
                label = { Text("Your Name") },
                placeholder = {
                    Text("Enter your name")
                },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            if (memberSchedules.isNotEmpty()) {
                Text(
                    "Previous Members (tap to edit):",
                    style = MaterialTheme.typography.labelLarge
                )

                Row(
                    modifier = Modifier.horizontalScroll(
                        rememberScrollState()
                    ),
                    horizontalArrangement =
                        Arrangement.spacedBy(8.dp)
                ) {
                    memberSchedules.keys.sorted().forEach { name ->
                        AssistChip(
                            onClick = { selectMember(name) },
                            label = { Text(name) }
                        )
                    }
                }
            }

            Text("Select the times you are available.")
        } else {
            Text(
                "Darker green means more members are available."
            )
        }

        // Availability grid
        Column(
            modifier = Modifier.horizontalScroll(
                rememberScrollState()
            ),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {

            Row(
                horizontalArrangement =
                    Arrangement.spacedBy(6.dp)
            ) {
                Spacer(modifier = Modifier.width(76.dp))

                days.forEach { day ->
                    Text(
                        day.take(3),
                        modifier = Modifier.width(55.dp),
                        textAlign = TextAlign.Center
                    )
                }
            }

            times.forEach { time ->

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement =
                        Arrangement.spacedBy(6.dp)
                ) {

                    Text(
                        time,
                        modifier = Modifier.width(76.dp),
                        style = MaterialTheme.typography.labelSmall
                    )

                    days.forEach { day ->

                        val slot = "$day-$time"
                        val selected = slot in selectedSlots

                        val count = memberSchedules.values.count {
                            slot in it
                        }

                        val boxColor = if (showResults) {
                            when {
                                count == 0 ->
                                    MaterialTheme.colorScheme.surfaceVariant
                                count == totalMembers ->
                                    Color(0xFF267747)
                                count * 2 >= totalMembers ->
                                    Color(0xFF8BC6A2)
                                else ->
                                    Color(0xFFD9EEDD)
                            }
                        } else {
                            if (selected) {
                                Color(0xFF267747)
                            } else {
                                MaterialTheme.colorScheme.surfaceVariant
                            }
                        }

                        val lightText = if (showResults) {
                            count > 0 && count == totalMembers
                        } else {
                            selected
                        }

                        Box(
                            modifier = Modifier
                                .width(55.dp)
                                .height(45.dp)
                                .background(
                                    boxColor,
                                    MaterialTheme.shapes.small
                                )
                                .then(
                                    if (!showResults) {
                                        Modifier.clickable {
                                            if (selected) {
                                                selectedSlots.remove(slot)
                                            } else {
                                                selectedSlots.add(slot)
                                            }
                                            saved = false
                                        }
                                    } else {
                                        Modifier
                                    }
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (showResults) {
                                    "$count/$totalMembers"
                                } else if (selected) {
                                    "✓"
                                } else {
                                    ""
                                },
                                color = if (lightText) {
                                    Color.White
                                } else {
                                    MaterialTheme.colorScheme.onSurface
                                }
                            )
                        }
                    }
                }
            }
        }

        if (!showResults) {

            Text(
                "${selectedSlots.size} time slots selected",
                style = MaterialTheme.typography.titleMedium
            )

            Button(
                onClick = {
                    val typedName = memberName.trim()

                    val existingName = memberSchedules.keys
                        .firstOrNull {
                            it.equals(
                                typedName,
                                ignoreCase = true
                            )
                        }

                    val name = existingName ?: typedName

                    memberSchedules[name] = selectedSlots.toSet()

                    val json = JSONObject()

                    memberSchedules.forEach { (person, slots) ->
                        json.put(
                            person,
                            JSONArray(slots.toList())
                        )
                    }

                    preferences.edit()
                        .putString("schedules", json.toString())
                        .apply()

                    saved = true
                },
                enabled = memberName.isNotBlank(),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Save My Availability")
            }

            if (saved) {
                Text(
                    "Availability saved successfully!",
                    color = MaterialTheme.colorScheme.primary
                )
            }

        } else {

            HorizontalDivider()

            Text(
                "Best Meeting Times",
                style = MaterialTheme.typography.titleLarge
            )

            if (totalMembers == 0) {
                Text("No members have submitted availability yet.")
            } else if (highestCount == 0) {
                Text("No available times have been submitted.")
            } else {
                Text(
                    "$highestCount out of $totalMembers members " +
                            "are available at the best times."
                )

                bestSlots.forEach { (slot, _) ->
                    Text(
                        "✓ ${slot.replace("-", " at ")}"
                    )
                }
            }
        }
    }
}
