package com.example.viabus.DataClass

import com.google.firebase.Timestamp

data class Ticket(
    val paymentID: String? = null,
    val busNumber: String? = null,
    val busRoutes: String? = null,
    val fromStop: String? = null,
    val toStop: String? = null,
    val ticketCount: Int? = null,
    val totalPrice: Int? = null,
    val ticketTime:Long=0L,
    val timestamp: Timestamp? = null,
    val ticketId: String? = null,
    val  status:String?=null
)
