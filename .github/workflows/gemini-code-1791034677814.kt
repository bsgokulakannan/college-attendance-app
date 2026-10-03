package com.example.attendance.model

data class User(
    val uid: String = "",
    val email: String = "",
    val role: String = "Student", // "Student", "Staff", "Admin"
    val name: String = ""
)

data class ClassAssignment(
    val id: String = "",
    val className: String = "",
    val staffUid: String = "",
    val pin: String = ""
)

data class AttendanceRecord(
    val id: String = "",
    val studentUid: String = "",
    val className: String = "",
    val date: String = "",
    val present: Boolean = true
)