package ua.anastasiia.finesapp.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ua.anastasiia.finesapp.data.FineDatabase
import ua.anastasiia.finesapp.data.dao.CarInfoDao
import ua.anastasiia.finesapp.data.dao.FineDao
import ua.anastasiia.finesapp.data.dao.FineViolationCrossRefDao
import ua.anastasiia.finesapp.data.dao.ViolationDao
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class DatabaseModule {

    @Provides
    fun provideFineDao(appDatabase: FineDatabase): FineDao {
        return appDatabase.fineDao()
    }

    @Provides
    fun provideCarInfoDao(appDatabase: FineDatabase): CarInfoDao {
        return appDatabase.carInfoDao()
    }

    @Provides
    fun provideViolationDao(appDatabase: FineDatabase): ViolationDao {
        return appDatabase.violationDao()
    }

    @Provides
    fun provideFineViolationCrossRefDao(appDatabase: FineDatabase): FineViolationCrossRefDao {
        return appDatabase.fineViolationCrossRefDao()
    }

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext appContext: Context): FineDatabase {
        return FineDatabase.getDatabase(appContext)
    }
}