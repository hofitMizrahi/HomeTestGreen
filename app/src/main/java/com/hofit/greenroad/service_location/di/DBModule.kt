package com.hofit.greenroad.service_location.di

import android.content.Context
import androidx.room.Room
import com.hofit.greenroad.service_location.db.AppDatabase
import com.hofit.greenroad.service_location.db.TriggerLogsDao
import com.hofit.greenroad.service_location.repository.IRepositoryController
import com.hofit.greenroad.service_location.repository.RepositoryImp
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object DBModule {

    @Provides
    fun provideDao(appDatabase : AppDatabase): TriggerLogsDao {
        return appDatabase.triggerLogsDao()
    }

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext appContext: Context): AppDatabase {
        return Room.databaseBuilder(
            appContext,
            AppDatabase::class.java,
            "my-database"
        ).build()
    }

    @Singleton
    @Provides
    fun provideRepository(dao : TriggerLogsDao): IRepositoryController {
        return RepositoryImp(dao)
    }
}