package br.com.leandro.fichaleitura.data.repository

import br.com.leandro.fichaleitura.data.model.Record
import kotlinx.coroutines.Deferred

interface RecordRepository {

    suspend fun insertRecord(record: Record)

    suspend fun updateRecord(record: Record)

    suspend fun deleteRecord(idRecord: String)

    suspend fun undeleteRecord(idRecord: String)

    suspend fun terminateReading(idRecord: String, endDate: Long)

    suspend fun restoreReading(idRecord: String)

    suspend fun getRecord(idRecord: String): Deferred<Record?>

    suspend fun getAllRecords(): Deferred<List<Record>>

    suspend fun getAllRecords(isReadingCompleted: Boolean, isReadingNotCompleted: Boolean,
    isReadingDeleted: Boolean): Deferred<List<Record>>

    suspend fun getAllRecords(beginDate: Long, endDate: Long): Deferred<List<Record>>

    suspend fun getAllReadingCompleted(): Deferred<List<Record>>

    suspend fun getAllReadingNotCompleted(): Deferred<List<Record>>

}