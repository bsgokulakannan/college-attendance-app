package com.example.attendance

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.attendance.ui.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    var currentScreen by remember { mutableStateOf("login") }
                    var userRole by remember { mutableStateOf("Student") }

                    val auth = FirebaseAuth.getInstance()
                    val firestore = FirebaseFirestore.getInstance()

                    when (currentScreen) {
                        "login" -> LoginScreen(
                            onLoginSuccess = { role ->
                                userRole = role
                                currentScreen = when (role) {
                                    "Staff" -> "staffPin"
                                    "Admin" -> "admin"
                                    else -> "student"
                                }
                            }
                        )
                        "student" -> StudentDashboard(
                            onLogout = {
                                auth.signOut()
                                currentScreen = "login"
                            }
                        )
                        "staffPin" -> StaffClassPinScreen(
                            firestore = firestore,
                            onPinVerified = { className ->
                                currentScreen = "student"
                            },
                            onLogout = {
                                auth.signOut()
                                currentScreen = "login"
                            }
                        )
                        "admin" -> AdminPanel(
                            firestore = firestore,
                            onLogout = {
                                auth.signOut()
                                currentScreen = "login"
                            }
                        )
                    }
                }
            }
        }
    }
}