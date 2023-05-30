package ua.anastasiia.finesapp.data.dao

import android.database.sqlite.SQLiteConstraintException
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import ua.anastasiia.finesapp.data.FineWithCarAndViolations
import ua.anastasiia.finesapp.data.entity.Fine

@Dao
interface FineDao {
    @Insert
    suspend fun insert(fine: Fine): Long

    @Update
    suspend fun update(fine: Fine)

    suspend fun upsert(fine: Fine) {
        try {
            insert(fine)
        } catch (e: SQLiteConstraintException) {
            update(fine)
        }
    }

    @Delete
    suspend fun delete(fine: Fine)

    @Transaction
    @Query("SELECT * FROM Fine")
    fun getFinesWithCarAndViolationsStream(): Flow<List<FineWithCarAndViolations>>

    @Transaction
    @Query("SELECT * FROM Fine WHERE fine_id = :fineId")
    fun getFineWithCarInfoAndViolationsById(fineId: Int): Flow<FineWithCarAndViolations>

    @Query("SELECT * FROM Fine ORDER BY fine_id DESC LIMIT 1")
    suspend fun getLastInsertedFine(): Fine

}