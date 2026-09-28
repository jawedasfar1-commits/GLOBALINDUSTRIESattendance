package com.example.globalindustriesattendance.data.model

import com.google.firebase.database.IgnoreExtraProperties
import com.google.firebase.database.PropertyName

@IgnoreExtraProperties
data class MaterialItem(
    val item: String = "",
    val qty: String = "",
    val unit: String = "Bag"
)

@IgnoreExtraProperties
data class DprRecord(
    @get:PropertyName("work") @set:PropertyName("work") var work: String = "",
    val materials: List<MaterialItem> = emptyList()
) {
    val workText: String get() = work
}
