package com.example.campusconnect

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun EventDetailsScreen(
    event: RSOEvent,
    isSaved: Boolean,
    onSaveClick: () -> Unit,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        TextButton(onClick = onBack) {
            Text("← Back to Events")
        }

        Text(
            text = event.name,
            style = MaterialTheme.typography.headlineMedium
        )

        Text(
            text = "Hosted by ${event.organization}",
            style = MaterialTheme.typography.titleMedium
        )

        HorizontalDivider()

        Text(
            text = "Event Information",
            style = MaterialTheme.typography.titleLarge
        )

        Text("Date: ${event.date}")
        Text("Time: ${event.time}")
        Text("Location: ${event.location}")
        Text("Category: ${event.category}")

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onSaveClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                if (isSaved) "✓ Saved Event"
                else "Save Event"
            )
        }

        OutlinedButton(
            onClick = onBack,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Return to Home")
        }
    }
}
