package ua.anastasiia.finesapp.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    foreignKeys = [ForeignKey(
        entity = CarInfo::class,
        parentColumns = ["id"],
        childColumns = ["car_id"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class Fine(
    @PrimaryKey
    val fine_id: Int,
    val location: String,
    val date: String,
    val imageUri: String,
    val valid: Boolean,
    val car_id: Int
)