package br.com.leandro.fichaleitura.data.hilt

import br.com.leandro.fichaleitura.data.datasource.BookDbDatasource
import br.com.leandro.fichaleitura.data.datasource.RecordDbDatasource
import br.com.leandro.fichaleitura.data.repository.BookRepository
import br.com.leandro.fichaleitura.data.repository.RecordRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
interface Binds {

    @Binds
    fun bindBookRepository(bookDbDatasource: BookDbDatasource): BookRepository

    @Binds
    fun bindRecordRepository(recordDbDatasource: RecordDbDatasource): RecordRepository

}