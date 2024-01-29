package br.com.leandro.fichaleitura.data.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.leandro.fichaleitura.data.model.Book
import br.com.leandro.fichaleitura.data.model.Order
import br.com.leandro.fichaleitura.data.repository.BookRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookViewModel @Inject constructor(

    private val bookRepository: BookRepository

): ViewModel() {


    fun insertBook(book: Book, listener: CoroutineListener): LiveData<Boolean> {
        val result = MutableLiveData<Boolean>()
        val handler = CoroutineExceptionHandler { _, ex -> listener.onCoroutineException(ex) }
        viewModelScope.launch(handler) {
            bookRepository.insertBook(book)
            result.postValue(true)
        }
        return result
    }


    fun updateBook(book: Book, listener: CoroutineListener): LiveData<Boolean> {
        val result = MutableLiveData<Boolean>()
        val handler = CoroutineExceptionHandler { _, ex -> listener.onCoroutineException(ex) }
        viewModelScope.launch(handler) {
            bookRepository.updateBook(book)
            result.postValue(true)
        }
        return result
    }


    fun deleteBook(book: Book, listener: CoroutineListener): LiveData<Boolean> {
        val result = MutableLiveData<Boolean>()
        val handler = CoroutineExceptionHandler { _, ex -> listener.onCoroutineException(ex) }
        viewModelScope.launch(handler) {
            bookRepository.deleteBook(book)
            result.postValue(true)
        }
        return result
    }


    fun getBook(idBook: String, listener: CoroutineListener): LiveData<Book?> {
        val result = MutableLiveData<Book?>()
        val handler = CoroutineExceptionHandler { _, ex -> listener.onCoroutineException(ex) }
        viewModelScope.launch(handler) {
            result.postValue(bookRepository.getBook(idBook).await())
        }
        return result
    }


    fun getBookCover(idBook: String, listener: CoroutineListener): LiveData<ByteArray?> {
        val result = MutableLiveData<ByteArray?>()
        val handler = CoroutineExceptionHandler { _, ex -> listener.onCoroutineException(ex) }
        viewModelScope.launch(handler) {
            result.postValue(bookRepository.getBookCover(idBook).await())
        }
        return result
    }


    fun getAllBooks(listener: CoroutineListener, order: Order): LiveData<List<Book>> {
        val result = MutableLiveData<List<Book>>()
        val handler = CoroutineExceptionHandler { _, ex -> listener.onCoroutineException(ex) }
        viewModelScope.launch(handler) {
            result.postValue(bookRepository.getAllBooks(order).await())
        }
        return result
    }


    fun getAllBooksLite(listener: CoroutineListener, order: Order): LiveData<List<Book>> {
        val result = MutableLiveData<List<Book>>()
        val handler = CoroutineExceptionHandler { _, ex -> listener.onCoroutineException(ex) }
        viewModelScope.launch(handler) {
            result.postValue(bookRepository.getAllBooksLite(order).await())
        }
        return result
    }


}