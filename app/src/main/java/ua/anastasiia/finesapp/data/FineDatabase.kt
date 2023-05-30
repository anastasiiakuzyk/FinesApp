package ua.anastasiia.finesapp.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ua.anastasiia.finesapp.data.dao.CarInfoDao
import ua.anastasiia.finesapp.data.dao.FineDao
import ua.anastasiia.finesapp.data.dao.FineViolationCrossRefDao
import ua.anastasiia.finesapp.data.dao.ViolationDao
import ua.anastasiia.finesapp.data.dao.Violations.violations
import ua.anastasiia.finesapp.data.entity.CarInfo
import ua.anastasiia.finesapp.data.entity.Fine
import ua.anastasiia.finesapp.data.entity.Violation
import ua.anastasiia.finesapp.data.entity.FineViolationCrossRef

@Database(
    entities = [CarInfo::class, Fine::class, Violation::class, FineViolationCrossRef::class],
    version = 1,
    exportSchema = false
)
abstract class FineDatabase : RoomDatabase() {
    abstract fun carInfoDao(): CarInfoDao
    abstract fun fineDao(): FineDao
    abstract fun violationDao(): ViolationDao
    abstract fun fineViolationCrossRefDao(): FineViolationCrossRefDao

    class DatabaseCallback(
        private val scope: CoroutineScope
    ) : Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch {
                    populateDatabase(database.violationDao())
                }
            }
        }

        private suspend fun populateDatabase(violationDao: ViolationDao) {
            violations.forEach { violation -> violationDao.insert(violation) }
        }
    }


    companion object {
        @Volatile
        private var INSTANCE: FineDatabase? = null

        fun getDatabase(context: Context): FineDatabase {
            return INSTANCE ?: synchronized(this) {
                val scope = CoroutineScope(Dispatchers.IO)
                Room.databaseBuilder(
                    context.applicationContext,
                    FineDatabase::class.java,
                    "fine_database"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(DatabaseCallback(scope))
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}