package ua.anastasiia.finesapp.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Update
import ua.anastasiia.finesapp.data.entity.Violation

@Dao
interface ViolationDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(violation: Violation): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(vararg violations: Violation)

    @Update
    suspend fun update(violation: Violation)

    @Delete
    suspend fun delete(violation: Violation)

}


object Violations {
    val violations = listOf(
        Violation(1, "VIOLATION OF LICENSE PLATE USE", 1190.0),
        Violation(2, "VIOLATION OF SIGNS", 340.0),

        Violation(3, "PARKED IN TWO LANES", 680.0),
        Violation(4, "PARKED IN FORBIDDEN AREAS", 680.0),
        Violation(5, "OBSTRUCTS TRAFFIC, PEDESTRIANS", 680.0),
        Violation(6, "PARKED ON PUBLIC TRANSPORT LANE", 680.0),
        Violation(7, "PARKED ON BIKE LANE", 680.0),
        Violation(8, "OBSTRUCTS MUNICIPAL TRANSPORT MOVEMENT", 680.0),
        Violation(9, "VIOLATES PARKING SCHEME", 680.0),

        Violation(10, "PARKED IN DISABLED ZONE", 1700.0)
    )
}