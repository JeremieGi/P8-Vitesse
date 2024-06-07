package com.openclassrooms.p8vitesse.di

import android.content.Context
import com.openclassrooms.p8vitesse.data.dao.AppDataBase
import com.openclassrooms.p8vitesse.data.dao.CandidateDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Provides
    @Singleton
    fun provideCoroutineScope(): CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context, coroutineScope: CoroutineScope): AppDataBase {
        return AppDataBase.getDatabase(context, coroutineScope)
    }

    @Provides
    fun provideCandidateDao(appDatabase: AppDataBase): CandidateDao {
        return appDatabase.candidateDao()
    }


}