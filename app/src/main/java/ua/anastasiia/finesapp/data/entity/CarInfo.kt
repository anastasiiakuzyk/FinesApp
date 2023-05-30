package ua.anastasiia.finesapp.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class CarInfo(
    @PrimaryKey
    val id: Int,
    val plate: String,
    val make: String,
    val model: String,
    val color: String
)