package ua.anastasiia.finesapp.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Violation(
    @PrimaryKey
    val violation_id: Int = 0,
    val description: String,
    val price: Double
)