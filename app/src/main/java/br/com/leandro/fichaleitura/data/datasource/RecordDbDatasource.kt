package br.com.leandro.fichaleitura.data.datasource

import br.com.leandro.fichaleitura.data.model.Record
import br.com.leandro.fichaleitura.data.repository.RecordRepository
import br.com.leandro.fichaleitura.database.dao.RecordDao
import br.com.leandro.fichaleitura.database.entity.RecordEntity
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import javax.inject.Inject

class RecordDbDatasource @Inject constructor(

    private val recordDao: RecordDao

): RecordRepository {


    override suspend fun insertRecord(record: Record): Unit = withContext(IO) {
        val isReading: Boolean = recordDao.isReadingTheBook(record.idBook) > 0
        if (!isReading) {
            val recordEntity = RecordEntity(
                record.id,
                record.idBook,
                record.beginDate,
                record.endDate,
                record.notes,
                record.registrationDate,
                record.lastUpdateDate,
                false,
                false
            )
            recordDao.insertRecord(recordEntity)
        } else {
            throw Exception("O livro já está registrado.")
        }
    }


    override suspend fun updateRecord(record: Record): Unit = withContext(IO) {
        val recordEntity = recordDao.getRecordById(record.id)
        if (recordEntity != null) {
            recordEntity.idBook = record.idBook
            recordEntity.beginDate = record.beginDate
            recordEntity.endDate = record.endDate
            recordEntity.notes = record.notes
            recordEntity.readingCompleted = record.readingCompleted
            recordEntity.lastUpdateDate = record.lastUpdateDate
            recordDao.updateRecord(recordEntity)
        }
    }


    override suspend fun deleteRecord(idRecord: String): Unit = withContext(IO) {
        val recordEntity = recordDao.getRecordById(idRecord)
        if (recordEntity != null) {
            recordDao.deleteRecord(recordEntity.id)
        }
    }


    override suspend fun undeleteRecord(idRecord: String): Unit = withContext(IO) {
        val recordEntity = recordDao.getRecordById(idRecord)
        if (recordEntity != null) {
            recordDao.undeleteRecord(recordEntity.id)
        }
    }


    override suspend fun terminateReading(idRecord: String, endDate: Long): Unit = withContext(IO) {
        val recordEntity = recordDao.getRecordById(idRecord)
        if (recordEntity != null) {
            recordDao.terminateReading(recordEntity.id, endDate)
        }
    }


    override suspend fun getRecord(idRecord: String) = withContext(IO) {
        async {
            recordDao.getRecordById(idRecord)?.toReadingRecord()
        }
    }


    override suspend fun getAllRecords() = withContext(IO) {
        async {
            val recordEntityList = recordDao.getAllRecords()
            val recordList: MutableList<Record> = mutableListOf()
            recordEntityList.forEach { recordEntity ->
                recordList.add(recordEntity.toReadingRecord())
            }
            recordList
        }
    }


    override suspend fun getAllRecords(beginDate: Long, endDate: Long) = withContext(IO) {
        async {
            val recordEntityList = recordDao.getAllRecords(beginDate, endDate)
            val recordList: MutableList<Record> = mutableListOf()
            recordEntityList.forEach { recordEntity ->
                recordList.add(recordEntity.toReadingRecord())
            }
            recordList
        }
    }


    override suspend fun getAllReadingCompleted() = withContext(IO) {
        async {
            val recordEntityList = recordDao.getAllReadingCompleted()
            val recordList: MutableList<Record> = mutableListOf()
            recordEntityList.forEach { recordEntity ->
                recordList.add(recordEntity.toReadingRecord())
            }
            recordList
        }
    }


    override suspend fun getAllReadingNotCompleted() = withContext(IO) {
        async {
            val recordEntityList = recordDao.getAllReadingNotCompleted()
            val recordList: MutableList<Record> = mutableListOf()
            recordEntityList.forEach { recordEntity ->
                recordList.add(recordEntity.toReadingRecord())
            }
            recordList
        }
    }


}