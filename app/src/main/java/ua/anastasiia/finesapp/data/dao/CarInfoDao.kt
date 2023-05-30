package ua.anastasiia.finesapp.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Update
import ua.anastasiia.finesapp.data.entity.CarInfo

@Dao
interface CarInfoDao {
    @Insert
    suspend fun insert(carInfo: CarInfo): Long

    @Update
    suspend fun update(carInfo: CarInfo)

    @Delete
    suspend fun delete(carInfo: CarInfo)

}