package br.com.leandro.fichaleitura.data.hilt

import br.com.leandro.fichaleitura.database.AppDatabase
import br.com.leandro.fichaleitura.database.dao.BookDao
import br.com.leandro.fichaleitura.database.dao.RecordDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object Provides {

    @Provides
    @Singleton
    fun provideAppDatabase(): AppDatabase {
        return AppDatabase.instance
    }

    @Singleton
    @Provides
    fun provideBook(appDatabase: AppDatabase): BookDao {
        return appDatabase.getBookDao()
    }

    @Singleton
    @Provides
    fun provideRecord(appDatabase: AppDatabase): RecordDao {
        return appDatabase.getReadingRecordDao()
    }

}