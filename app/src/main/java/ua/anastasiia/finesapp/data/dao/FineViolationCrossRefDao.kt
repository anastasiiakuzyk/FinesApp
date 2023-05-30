package ua.anastasiia.finesapp.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import ua.anastasiia.finesapp.data.entity.FineViolationCrossRef

@Dao
interface FineViolationCrossRefDao {
    @Insert
    suspend fun insert(crossRef: FineViolationCrossRef): Long

    @Update
    suspend fun update(crossRef: FineViolationCrossRef)

    @Query("DELETE FROM FineViolationCrossRef WHERE fine_id = :fineId")
    suspend fun deleteByFineId(fineId: Int)


    @Query("SELECT * FROM FineViolationCrossRef WHERE fine_id = :fineId")
    suspend fun getAllByFineId(fineId: Int): List<FineViolationCrossRef>

    @Delete
    suspend fun delete(crossRef: FineViolationCrossRef)
}