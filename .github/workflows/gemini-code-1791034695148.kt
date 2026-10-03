package com.example.attendance.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun StaffClassPinScreen(
    firestore: FirebaseFirestore,
    onPinVerified: (String) -> Unit,
    onLogout: () -> Unit
) {
    var className by remember { mutableStateOf("") }
    var enteredPin by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentWidth(Alignment.End)
        ) {
            TextButton(onClick = onLogout) {
                Text("Logout")
            }
        }

        Text(text = "Staff Class Access", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = className,
            onValueChange = { className = it },
            label = { Text("Class / Subject Name") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = enteredPin,
            onValueChange = { enteredPin = it },
            label = { Text("Class PIN / Password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(24.dp))

        if (errorMessage.isNotEmpty()) {
            Text(text = errorMessage, color = MaterialTheme.colorScheme.error)
            Spacer(modifier = Modifier.height(8.dp))
        }

        Button(
            onClick = {
                if (className.isBlank() || enteredPin.isBlank()) {
                    errorMessage = "Please enter both class name and PIN"
                    return@Button
                }
                isLoading = true
                firestore.collection("assignments")
                    .whereEqualTo("className", className)
                    .get()
                    .addOnSuccessListener { documents ->
                        isLoading = false
                        var matched = false
                        for (doc in documents) {
                            val pin = doc.getString("pin")
                            if (pin == enteredPin) {
                                matched = true
                                break
                            }
                        }
                        if (matched) {
                            onPinVerified(className)
                        } else {
                            errorMessage = "Invalid PIN for this class"
                        }
                    }
                    .addOnFailureListener { e ->
                        isLoading = false
                        errorMessage = e.localizedMessage ?: "Verification failed"
                    }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading
        ) {
            Text(if (isLoading) "Verifying..." else "Verify & Continue")
        }
    }
}