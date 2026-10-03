package com.example.attendance.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun StudentDashboard(onLogout: () -> Unit) {
    var totalClasses by remember { mutableStateOf(40) }
    var attendedClasses by remember { mutableStateOf(32) }

    val percentage = if (totalClasses > 0) (attendedClasses.toFloat() / totalClasses) * 100f else 0f
    
    val targetPercentage = 75f
    val classesCanSkip = if (percentage >= targetPercentage) {
        ((attendedClasses - (targetPercentage / 100f * totalClasses)) / (1f - (targetPercentage / 100f))).toInt()
    } else {
        0
    }
    val classesNeeded = if (percentage < targetPercentage) {
        (((targetPercentage / 100f * totalClasses) - attendedClasses) / (1f - (targetPercentage / 100f))).toInt().coerceAtLeast(0)
    } else {
        0
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Student Dashboard", style = MaterialTheme.typography.headlineSmall)
            TextButton(onClick = onLogout) {
                Text("Logout")
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = "Overall Attendance", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "%.1f%%".format(percentage), style = MaterialTheme.typography.displayMedium)
                Spacer(modifier = Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = { percentage / 100f },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text("Total Classes: $totalClasses")
                Text("Classes Attended: $attendedClasses")
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = "75% Target Calculator", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))
                if (percentage >= targetPercentage) {
                    Text(text = "You are safe! You can skip up to $classesCanSkip classes and maintain 75%.")
                } else {
                    Text(text = "Warning! You need to attend the next $classesNeeded classes consecutively to reach 75%.")
                }
            }
        }
    }
}