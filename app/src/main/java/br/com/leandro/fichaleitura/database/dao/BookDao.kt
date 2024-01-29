package br.com.leandro.fichaleitura.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import br.com.leandro.fichaleitura.database.entity.BookEntity

@Dao
interface BookDao {


    @Insert
    suspend fun insertBook(bookEntity: BookEntity)


    @Update
    suspend fun updateBook(bookEntity: BookEntity)


    @Delete
    suspend fun deleteBook(bookEntity: BookEntity)


    @Query("SELECT * FROM book WHERE id = :idBook")
    suspend fun getBookById(idBook: String): BookEntity?


    @Query("""
        SELECT
            id,
            title,
            subtitle,
            author,
            publisher,
            isbn,
            edition,
            volume,
            release_year,
            summary,
            file_path,
            registration_date,
            last_update_date
        FROM book 
        WHERE id = :idBook
    """)
    suspend fun getBookByIdLite(idBook: String): BookEntity?


    @Query("SELECT cover FROM book WHERE id = :idBook")
    suspend fun getBookCover(idBook: String): ByteArray?


    @Query("SELECT * FROM book ORDER BY title ASC")
    suspend fun getAllBooksAsc(): List<BookEntity>


    @Query("SELECT * FROM book ORDER BY title DESC")
    suspend fun getAllBooksDesc(): List<BookEntity>


    @Query("""
        SELECT
            id,
            title,
            subtitle,
            author,
            publisher,
            isbn,
            edition,
            volume,
            release_year,
            summary,
            file_path,
            registration_date,
            last_update_date
        FROM book ORDER BY title ASC
    """)
    suspend fun getAllBooksLiteAsc(): List<BookEntity>


    @Query("""
        SELECT
            id,
            title,
            subtitle,
            author,
            publisher,
            isbn,
            edition,
            volume,
            release_year,
            summary,
            file_path,
            registration_date,
            last_update_date
        FROM book ORDER BY title DESC
    """)
    suspend fun getAllBooksLiteDesc(): List<BookEntity>


}