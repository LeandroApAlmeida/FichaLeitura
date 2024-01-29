package br.com.leandro.fichaleitura.data.datasource

import br.com.leandro.fichaleitura.data.model.Book
import br.com.leandro.fichaleitura.data.model.Order
import br.com.leandro.fichaleitura.data.repository.BookRepository
import br.com.leandro.fichaleitura.database.dao.BookDao
import br.com.leandro.fichaleitura.database.entity.BookEntity
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import javax.inject.Inject

class BookDbDatasource @Inject constructor(

    private val bookDao: BookDao

): BookRepository {


    override suspend fun insertBook(book: Book): Unit = withContext(IO) {
        val bookEntity = BookEntity (
            book.id,
            book.title,
            book.subtitle,
            book.author,
            book.publisher,
            book.isbn,
            book.edition,
            book.volume,
            book.releaseYear,
            book.cover,
            book.summary,
            book.filePath,
            book.registrationDate,
            book.lastUpdateDate
        )
        bookDao.insertBook(bookEntity)
    }


    override suspend fun updateBook(book: Book): Unit = withContext(IO) {
        val bookEntity = bookDao.getBookById(book.id)
        if (bookEntity != null) {
            bookEntity.title = book.title
            bookEntity.subtitle = book.subtitle
            bookEntity.author = book.author
            bookEntity.publisher = book.publisher
            bookEntity.isbn = book.isbn
            bookEntity.edition = book.edition
            bookEntity.volume = book.volume
            bookEntity.releaseYear = book.releaseYear
            bookEntity.cover = book.cover
            bookEntity.summary = book.summary
            bookEntity.filePath = book.filePath
            bookEntity.lastUpdateDate = book.lastUpdateDate
            bookDao.updateBook(bookEntity)
        }
    }


    override suspend fun deleteBook(book: Book): Unit = withContext(IO) {
        val bookEntity = bookDao.getBookById(book.id)
        if (bookEntity != null) {
            bookDao.deleteBook(bookEntity)
        }
    }


    override suspend fun getBook(idBook: String) = withContext(IO) {
        async {
            bookDao.getBookById(idBook)?.toBook()
        }
    }


    override suspend fun getBookCover(idBook: String) = withContext(IO) {
        async {
            bookDao.getBookCover(idBook)
        }
    }


    override suspend fun getAllBooks(order: Order) = withContext(IO) {
        async {
            val bookEntityList: List<BookEntity> = when (order) {
                Order.ASC -> bookDao.getAllBooksAsc()
                Order.DESC -> bookDao.getAllBooksDesc()
                Order.DEFAULT -> bookDao.getAllBooksAsc()
            }
            val bookList: MutableList<Book> = mutableListOf()
            bookEntityList.forEach { bookEntity ->
                bookList.add(bookEntity.toBook())
            }
            bookList
        }
    }


    override suspend fun getAllBooksLite(order: Order)= withContext(IO) {
        async {
            val bookEntityList: List<BookEntity> = when (order) {
                Order.ASC -> bookDao.getAllBooksLiteAsc()
                Order.DESC -> bookDao.getAllBooksLiteDesc()
                Order.DEFAULT -> bookDao.getAllBooksLiteAsc()
            }
            val bookList: MutableList<Book> = mutableListOf()
            bookEntityList.forEach { bookEntity ->
                bookList.add(bookEntity.toBook())
            }
            bookList
        }
    }


}