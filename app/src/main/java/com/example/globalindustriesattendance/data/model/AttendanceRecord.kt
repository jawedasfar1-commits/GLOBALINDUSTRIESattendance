package com.example.globalindustriesattendance.data.model

import com.google.firebase.database.IgnoreExtraProperties
import com.google.firebase.database.PropertyName

@IgnoreExtraProperties
data class AttendanceRecord(
    val duty: Double = 0.0,
    val ot: Double = 0.0,
    @get:PropertyName("adv") @set:PropertyName("adv") var adv: Double = 0.0
) {
    val dailyAdvance: Double get() = adv
}
