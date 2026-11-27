package com.example.campusconnect

import android.app.DatePickerDialog
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.UUID

@Composable
fun CreateMeetingPollScreen(
    organization: RSOOrganization,
    onCreatePoll: (MeetingPoll) -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current

    var title by rememberSaveable {
        mutableStateOf("")
    }

    var startDate by rememberSaveable {
        mutableStateOf("")
    }

    var endDate by rememberSaveable {
        mutableStateOf("")
    }

    var startHour by rememberSaveable {
        mutableIntStateOf(15)
    }

    var endHour by rememberSaveable {
        mutableIntStateOf(20)
    }

    val dateFormatter = DateTimeFormatter.ofPattern(
        "MMM d, yyyy"
    )

    fun showDatePicker(
        currentDate: String,
        onSelected: (String) -> Unit
    ) {
        val initialDate = runCatching {
            LocalDate.parse(currentDate)
        }.getOrDefault(LocalDate.now())

        DatePickerDialog(
            context,
            { _, year, month, day ->
                onSelected(
                    LocalDate.of(
                        year,
                        month + 1,
                        day
                    ).toString()
                )
            },
            initialDate.year,
            initialDate.monthValue - 1,
            initialDate.dayOfMonth
        ).show()
    }

    fun displayDate(date: String): String {
        return if (date.isBlank()) {
            "Select Date"
        } else {
            LocalDate.parse(date).format(dateFormatter)
        }
    }

    fun displayHour(hour: Int): String {
        val hour12 = if (hour % 12 == 0) 12 else hour % 12
        val period = if (hour < 12) "AM" else "PM"
        return "$hour12:00 $period"
    }

    val start = runCatching {
        LocalDate.parse(startDate)
    }.getOrNull()

    val end = runCatching {
        LocalDate.parse(endDate)
    }.getOrNull()

    val validDates = start != null &&
            end != null &&
            !end.isBefore(start) &&
            ChronoUnit.DAYS.between(start, end) <= 6

    val validTimes = endHour > startHour

    val canCreate = title.isNotBlank() &&
            validDates && validTimes

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        TextButton(onClick = onBack) {
            Text("← Back to Meetings")
        }

        Text(
            "Create Meeting Poll",
            style = MaterialTheme.typography.headlineMedium
        )

        Text(
            "Organization: ${organization.name}",
            style = MaterialTheme.typography.titleMedium
        )

        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Meeting Name") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Text(
            "Choose Meeting Dates",
            style = MaterialTheme.typography.titleMedium
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedButton(
                onClick = {
                    showDatePicker(startDate) {
                        startDate = it
                    }
                },
                modifier = Modifier.weight(1f)
            ) {
                Text(displayDate(startDate))
            }

            OutlinedButton(
                onClick = {
                    showDatePicker(endDate) {
                        endDate = it
                    }
                },
                modifier = Modifier.weight(1f)
            ) {
                Text(displayDate(endDate))
            }
        }

        Text(
            "Select a date range of up to 7 days.",
            style = MaterialTheme.typography.bodySmall
        )

        Text(
            "Earliest Time: ${displayHour(startHour)}",
            style = MaterialTheme.typography.titleMedium
        )

        Slider(
            value = startHour.toFloat(),
            onValueChange = {
                startHour = it.toInt()
            },
            valueRange = 8f..21f,
            steps = 12
        )

        Text(
            "Latest Time: ${displayHour(endHour)}",
            style = MaterialTheme.typography.titleMedium
        )

        Slider(
            value = endHour.toFloat(),
            onValueChange = {
                endHour = it.toInt()
            },
            valueRange = 9f..22f,
            steps = 12
        )

        if (start != null && end != null && !validDates) {
            Text(
                "Please select a valid date range of 1 to 7 days.",
                color = MaterialTheme.colorScheme.error
            )
        }

        if (!validTimes) {
            Text(
                "The latest time must be after the earliest time.",
                color = MaterialTheme.colorScheme.error
            )
        }

        Button(
            onClick = {
                val poll = MeetingPoll(
                    id = UUID.randomUUID().toString(),
                    organizationId = organization.id,
                    title = title.trim(),
                    startDate = startDate,
                    endDate = endDate,
                    startHour = startHour,
                    endHour = endHour
                )

                onCreatePoll(poll)
            },
            enabled = canCreate,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Create Meeting Poll")
        }
    }
}
