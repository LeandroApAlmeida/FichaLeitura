package br.com.leandro.fichaleitura.data.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.leandro.fichaleitura.data.model.Record
import br.com.leandro.fichaleitura.data.repository.RecordRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecordViewModel @Inject constructor(

    private val recordRepository: RecordRepository

): ViewModel() {


    fun insertRecord(record: Record, listener: CoroutineListener): LiveData<Boolean> {
        val result = MutableLiveData<Boolean>()
        val handler = CoroutineExceptionHandler { _, ex -> listener.onCoroutineException(ex) }
        viewModelScope.launch(handler) {
            recordRepository.insertRecord(record)
            result.postValue(true)
        }
        return result
    }


    fun updateRecord(record: Record, listener: CoroutineListener): LiveData<Boolean> {
        val result = MutableLiveData<Boolean>()
        val handler = CoroutineExceptionHandler { _, ex -> listener.onCoroutineException(ex) }
        viewModelScope.launch(handler) {
            recordRepository.updateRecord(record)
            result.postValue(true)
        }
        return result
    }


    fun deleteRecord(record: Record, listener: CoroutineListener): LiveData<Boolean> {
        val result = MutableLiveData<Boolean>()
        val handler = CoroutineExceptionHandler { _, ex -> listener.onCoroutineException(ex) }
        viewModelScope.launch(handler) {
            recordRepository.deleteRecord(record.id)
            result.postValue(true)
        }
        return result
    }


    fun undeleteRecord(record: Record, listener: CoroutineListener): LiveData<Boolean> {
        val result = MutableLiveData<Boolean>()
        val handler = CoroutineExceptionHandler { _, ex -> listener.onCoroutineException(ex) }
        viewModelScope.launch(handler) {
            recordRepository.undeleteRecord(record.id)
            result.postValue(true)
        }
        return result
    }


    fun terminateReading(record: Record, endDate: Long, listener: CoroutineListener): LiveData<Boolean> {
        val result = MutableLiveData<Boolean>()
        val handler = CoroutineExceptionHandler { _, ex -> listener.onCoroutineException(ex) }
        viewModelScope.launch(handler) {
            recordRepository.terminateReading(record.id, endDate)
            result.postValue(true)
        }
        return result
    }


    fun getRecord(idRecord: String, listener: CoroutineListener): LiveData<Record?> {
        val result = MutableLiveData<Record?>()
        val handler = CoroutineExceptionHandler { _, ex -> listener.onCoroutineException(ex) }
        viewModelScope.launch(handler) {
            result.postValue(recordRepository.getRecord(idRecord).await())
        }
        return result
    }


    fun getAllRecords(listener: CoroutineListener): LiveData<List<Record>> {
        val result = MutableLiveData<List<Record>>()
        val handler = CoroutineExceptionHandler { _, ex -> listener.onCoroutineException(ex) }
        viewModelScope.launch(handler) {
            result.postValue(recordRepository.getAllRecords().await())
        }
        return result
    }


    fun getAllRecords(beginDate: Long, endDate: Long, listener: CoroutineListener): LiveData<List<Record>> {
        val result = MutableLiveData<List<Record>>()
        val handler = CoroutineExceptionHandler { _, ex -> listener.onCoroutineException(ex) }
        viewModelScope.launch(handler) {
            result.postValue(recordRepository.getAllRecords(beginDate, endDate).await())
        }
        return result
    }


    fun getAllReadingCompleted(listener: CoroutineListener): LiveData<List<Record>> {
        val result = MutableLiveData<List<Record>>()
        val handler = CoroutineExceptionHandler { _, ex -> listener.onCoroutineException(ex) }
        viewModelScope.launch(handler) {
            result.postValue(recordRepository.getAllReadingCompleted().await())
        }
        return result
    }


    fun getAllReadingNotCompleted(listener: CoroutineListener): LiveData<List<Record>> {
        val result = MutableLiveData<List<Record>>()
        val handler = CoroutineExceptionHandler { _, ex -> listener.onCoroutineException(ex) }
        viewModelScope.launch(handler) {
            result.postValue(recordRepository.getAllReadingNotCompleted().await())
        }
        return result
    }


}