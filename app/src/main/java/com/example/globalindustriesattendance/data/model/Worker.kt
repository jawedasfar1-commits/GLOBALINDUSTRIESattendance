package com.example.globalindustriesattendance.data.model

import com.google.firebase.database.IgnoreExtraProperties
import com.google.firebase.database.PropertyName

@IgnoreExtraProperties
data class Worker(
    val id: String = "",
    val name: String = "",
    @get:PropertyName("desig") @set:PropertyName("desig") var desig: String = "",
    @get:PropertyName("rate") @set:PropertyName("rate") var rate: Double = 0.0,
    val baseAdvance: Double = 0.0
) {
    val designation: String get() = desig
    val dailyRate: Double get() = rate
}
