package ua.anastasiia.finesapp.data.entity

import androidx.room.Entity

@Entity(primaryKeys = ["fine_id", "violation_id"])
data class FineViolationCrossRef(
    val fine_id: Int,
    val violation_id: Int
)