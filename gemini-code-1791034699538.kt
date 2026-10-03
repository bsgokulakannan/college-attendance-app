package com.example.attendance.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.attendance.model.ClassAssignment
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun AdminPanel(
    firestore: FirebaseFirestore,
    onLogout: () -> Unit
) {
    var className by remember { mutableStateOf("") }
    var staffUid by remember { mutableStateOf("") }
    var pin by remember { mutableStateOf("") }
    var assignments by remember { mutableStateOf<List<ClassAssignment>>(emptyList()) }
    var statusMessage by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        firestore.collection("assignments").addSnapshotListener { snapshot, _ ->
            if (snapshot != null) {
                assignments = snapshot.toObjects(ClassAssignment::class.java)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Admin Panel", style = MaterialTheme.typography.titleLarge)
            TextButton(onClick = onLogout) {
                Text("Logout")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = className,
            onValueChange = { className = it },
            label = { Text("Class Name (e.g., Math 101)") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = staffUid,
            onValueChange = { staffUid = it },
            label = { Text("Staff UID") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = pin,
            onValueChange = { pin = it },
            label = { Text("Assign PIN") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = {
                if (className.isBlank() || staffUid.isBlank() || pin.isBlank()) {
                    statusMessage = "All fields are required"
                    return@Button
                }
                val newAssignment = ClassAssignment(
                    id = firestore.collection("assignments").document().id,
                    className = className,
                    staffUid = staffUid,
                    pin = pin
                )
                firestore.collection("assignments").document(newAssignment.id)
                    .set(newAssignment)
                    .addOnSuccessListener {
                        statusMessage = "Assignment added successfully!"
                        className = ""
                        staffUid = ""
                        pin = ""
                    }
                    .addOnFailureListener { e ->
                        statusMessage = "Error: ${e.localizedMessage}"
                    }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Save Assignment")
        }

        if (statusMessage.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = statusMessage, color = MaterialTheme.colorScheme.primary)
        }

        Spacer(modifier = Modifier.height(24.dp))
        Text(text = "Existing Assignments", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(assignments) { item ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(text = "Class: ${item.className}", style = MaterialTheme.typography.bodyLarge)
                        Text(text = "Staff UID: ${item.staffUid}", style = MaterialTheme.typography.bodyMedium)
                        Text(text = "PIN: ${item.pin}", style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }
    }
}