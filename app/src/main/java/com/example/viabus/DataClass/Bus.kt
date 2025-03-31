package com.example.viabus.DataClass

data class Bus(
    val busNumber: String?="null",
    val busRoute: String?="null",
    val busType: String?="null",
    val busStops: List<String> = emptyList()
)
