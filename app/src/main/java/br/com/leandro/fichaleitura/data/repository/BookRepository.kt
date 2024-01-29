package br.com.leandro.fichaleitura.data.repository

import br.com.leandro.fichaleitura.data.model.Book
import br.com.leandro.fichaleitura.data.model.Order
import kotlinx.coroutines.Deferred

interface BookRepository {

    suspend fun insertBook(book: Book)

    suspend fun updateBook(book: Book)

    suspend fun deleteBook(book: Book)

    suspend fun getBook(idBook: String): Deferred<Book?>

    suspend fun getBookCover(idBook: String): Deferred<ByteArray?>

    suspend fun getAllBooks(order: Order): Deferred<List<Book>>

    suspend fun getAllBooksLite(order: Order): Deferred<List<Book>>

}