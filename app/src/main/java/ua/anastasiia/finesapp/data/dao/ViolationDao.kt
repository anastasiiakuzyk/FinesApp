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
        Violation(1, "BLOCKING DRIVEWAY", 70.0),
        Violation(2, "PARKED IN CROSS WALK", 50.0),
        Violation(3, "VEHICLE PARKED IN TWO SPACES", 65.0),
        Violation(4, "BUS ZONE", 250.0),
        Violation(5, "NO PAID PARKING SESSION", 100.0),
        Violation(6, "DISABLED ZONE", 421.0),
        Violation(7, "VIOLATION OF POSTED SIGNS", 200.0)
    )
}