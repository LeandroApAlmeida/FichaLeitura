package br.com.leandro.fichaleitura.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import br.com.leandro.fichaleitura.database.entity.RecordEntity

@Dao
interface RecordDao {


    @Insert
    suspend fun insertRecord(recordEntity: RecordEntity)


    @Update
    suspend fun updateRecord(recordEntity: RecordEntity)


    @Query("UPDATE record SET is_deleted = 1, last_update_date = :date WHERE id = :idRecord")
    suspend fun deleteRecord(idRecord: String, date: Long)


    @Query("UPDATE record SET is_deleted = 0 WHERE id = :idRecord")
    suspend fun undeleteRecord(idRecord: String)


    @Query("UPDATE record SET reading_completed = 1, end_date = :endDate WHERE id = :idRecord")
    suspend fun terminateReading(idRecord: String, endDate: Long)


    @Query("UPDATE record SET reading_completed = 0, end_date = null WHERE id = :idRecord")
    suspend fun restoreReading(idRecord: String)


    @Query("UPDATE record SET reading_completed = 1 WHERE id = :idRecord")
    suspend fun readingTerminated(idRecord: String)


    @Query("""
        SELECT
            count(*)
        FROM record
        WHERE (id_book = :idBook) AND (reading_completed = 0) AND (is_deleted = 0)
    """)
    suspend fun isReadingTheBook(idBook: String): Int


    @Query("SELECT * FROM record WHERE id = :idRecord")
    suspend fun getRecordById(idRecord: String): RecordEntity?


    @Query("""
        SELECT
            *
        FROM record
        ORDER BY begin_date DESC
    """)
    suspend fun getAllRecordsDesc(): List<RecordEntity>


    @Query("""
        SELECT
            *
        FROM record
        WHERE (((reading_completed = 1) = :isReadingCompleted) AND reading_completed = 1 AND is_deleted = 0) OR 
        (((reading_completed = 0) = :isReadingNotCompleted) AND reading_completed = 0 AND is_deleted = 0) OR 
        ((is_deleted = 1) = :isReadingDeleted) AND is_deleted = 1
        ORDER BY begin_date DESC
    """)
    suspend fun getAllRecordsDesc(isReadingCompleted: Boolean, isReadingNotCompleted: Boolean, isReadingDeleted: Boolean): List<RecordEntity>


    @Query("""
        SELECT
            *
        FROM record
        WHERE begin_date BETWEEN :beginDate AND :endDate
        ORDER BY begin_date DESC
    """)
    suspend fun getAllRecordsDesc(beginDate: Long, endDate: Long): List<RecordEntity>


    @Query("""
        SELECT
            *
        FROM record
        WHERE begin_date BETWEEN :beginDate AND :endDate
        ORDER BY begin_date ASC
    """)
    suspend fun getAllRecordsAsc(beginDate: Long, endDate: Long): List<RecordEntity>


    @Query("""
        SELECT
            *
        FROM record
        WHERE (begin_date BETWEEN :beginDate AND :endDate) AND is_deleted = 1
        ORDER BY begin_date ASC
    """)
    suspend fun getAllCanceledReadingAsc(beginDate: Long, endDate: Long): List<RecordEntity>


    @Query("""
        SELECT
            *
        FROM record
        WHERE id_book = :idBook
        ORDER BY begin_date DESC
    """)
    suspend fun getAllRecordsDesc(idBook: String): List<RecordEntity>


    @Query("""
        SELECT
            *
        FROM record
        WHERE (reading_completed = 1) OR (is_deleted = 1)
        ORDER BY begin_date DESC
    """)
    suspend fun getAllReadingCompleted(): List<RecordEntity>


    @Query("""
        SELECT
            *
        FROM record
        WHERE (reading_completed = 0) AND (is_deleted = 0)
        ORDER BY begin_date DESC
    """)
    suspend fun getAllReadingNotCompleted(): List<RecordEntity>


}