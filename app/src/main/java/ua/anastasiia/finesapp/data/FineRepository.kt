package ua.anastasiia.finesapp.data

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow
import ua.anastasiia.finesapp.data.dao.CarInfoDao
import ua.anastasiia.finesapp.data.dao.FineDao
import ua.anastasiia.finesapp.data.dao.FineViolationCrossRefDao
import ua.anastasiia.finesapp.data.entity.CarInfo
import ua.anastasiia.finesapp.data.entity.Fine
import ua.anastasiia.finesapp.data.entity.FineViolationCrossRef
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FineRepository @Inject constructor(
    private val carInfoDao: CarInfoDao,
    private val fineDao: FineDao,
    private val fineViolationCrossRefDao: FineViolationCrossRefDao
) {

    @Transaction
    suspend fun insertFineWithCarAndViolations(
        fineState: FineWithCarAndViolations
    ) {
        var fineIdSAVE by mutableStateOf(1)

        // from the second launch
        val lastInsertedFine = fineDao.getLastInsertedFine()
        Log.d("lastInsertedFine", lastInsertedFine.toString())
        val lastInsertedFineId = lastInsertedFine.fine_id
        fineIdSAVE = lastInsertedFineId + 1

        val newCarInfo =
            CarInfo(
                fineIdSAVE,
                fineState.carInfo.plate,
                fineState.carInfo.make,
                fineState.carInfo.model,
                fineState.carInfo.color
            )
        Log.d("newCarInfo", newCarInfo.toString())
        carInfoDao.insert(newCarInfo)

        val newFine = Fine(
            fineIdSAVE,
            fineState.fine.location,
            fineState.fine.date,
            fineState.fine.imageUri,
            fineIdSAVE
        )
        Log.d("newFine", newFine.toString())
        fineDao.insert(newFine).toInt()

        for (violation in fineState.violations) {
            fineViolationCrossRefDao.insert(
                FineViolationCrossRef(
                    fineIdSAVE,
                    violation.violation_id
                )
            )
        }
    }

    fun getFinesWithCarAndViolations(): Flow<List<FineWithCarAndViolations>> {
        return fineDao.getFinesWithCarAndViolationsStream()
    }

    fun getFineWithCarAndViolationsStream(fineId: Int): Flow<FineWithCarAndViolations> {
        return fineDao.getFineWithCarInfoAndViolationsById(fineId)
    }

    suspend fun deleteFine(fine: Fine) {
        fineDao.delete(fine)
    }

    @Transaction
    suspend fun updateFullFine(fineState: FineWithCarAndViolations) {
        carInfoDao.update(fineState.carInfo)
        fineDao.upsert(fineState.fine)

        val oldCrossRefs = fineViolationCrossRefDao.getAllByFineId(fineState.fine.fine_id)
        for (crossRef in oldCrossRefs) {
            fineViolationCrossRefDao.delete(crossRef)
        }
        for (violation in fineState.violations) {
            fineViolationCrossRefDao.insert(
                FineViolationCrossRef(
                    fineState.fine.fine_id,
                    violation.violation_id
                )
            )
        }
    }
}