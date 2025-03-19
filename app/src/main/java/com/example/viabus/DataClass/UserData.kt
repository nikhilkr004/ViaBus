package com.example.viabus.DataClass

import com.google.firebase.Timestamp

data class UserData(
    val name: String? = "null",
    val email: String? = "null",
    val userId: String? = "null",
    val date: Timestamp? = null
)
