package br.com.leandro.fichaleitura.ui.activity

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import br.com.leandro.fichaleitura.R
import br.com.leandro.fichaleitura.data.model.Book
import br.com.leandro.fichaleitura.data.model.Record
import br.com.leandro.fichaleitura.data.viewmodel.BookViewModel
import br.com.leandro.fichaleitura.data.viewmodel.CoroutineListener
import br.com.leandro.fichaleitura.data.viewmodel.RecordViewModel
import br.com.leandro.fichaleitura.databinding.ActivityRecordBinding
import br.com.leandro.fichaleitura.security.RandomGenerator
import br.com.leandro.fichaleitura.ui.dialog.CalendarDialog
import br.com.leandro.fichaleitura.ui.dialog.MessageDialog
import br.com.leandro.fichaleitura.utils.dateToText
import br.com.leandro.fichaleitura.utils.getSystemTime
import br.com.leandro.fichaleitura.utils.hourToText
import br.com.leandro.fichaleitura.utils.isEmptyText
import br.com.leandro.fichaleitura.utils.textToDate
import dagger.hilt.android.AndroidEntryPoint
import java.util.Date


@AndroidEntryPoint
class RecordActivity : AppCompatActivity(), CoroutineListener {


    private lateinit var binding: ActivityRecordBinding
    private val bookViewModel: BookViewModel by viewModels()
    private val recordViewModel: RecordViewModel by viewModels()
    private var book: Book? = null
    private var record: Record?= null
    private var mode: Int = REGISTRATION_MODE


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        binding = ActivityRecordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.edtRecordBook.setOnClickListener { onFindBookButtonClick(it) }
        binding.btnRecordFindBook.setOnClickListener { onFindBookButtonClick(it) }

        binding.edtRecordBeginDate.setOnClickListener {
            val beginDate = textToDate(binding.edtRecordBeginDate.text.toString().replace(" ", ""))
            CalendarDialog(this, beginDate).setOnConfirmButtonClickHandler { date: Date ->
                formatBeginDate(date)
            }.show()
        }

        binding.edtRecordEndDate.setOnClickListener {
            val endDate = if (binding.edtRecordEndDate.text.toString() == "") {
                Date(getSystemTime())
            } else {
                textToDate(binding.edtRecordEndDate.text.toString().replace(" ", ""))
            }
            CalendarDialog(this, endDate).setOnConfirmButtonClickHandler { date: Date ->
                formatEndDate(date)
            }.show()
        }

        if (intent.extras != null) {
            setUpdateMode(intent.extras!!.getString("id_record")!!)
        } else {
            setRegistrationMode()
        }

    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.register_menu, menu)
        return true
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.mniSave -> {
                onSaveButtonClick()
            }
            R.id.mniCancel -> {
                onCancelButtonClick()
            }
        }
        return true
    }


    private fun configureViews(enabled: Boolean, cancelButtonEnable: Boolean = true) {

        binding.edtRecordBeginDate.isEnabled = enabled
        binding.edtRecordNotes.isEnabled = enabled

        if (mode == UPDATE_MODE) {
            binding.edtRecordEndDate.isEnabled = enabled
            if (record!!.readingCompleted) {
                binding.edtRecordBook.isEnabled = false
                binding.btnRecordFindBook.isEnabled = false
            } else {
                binding.edtRecordBook.isEnabled = enabled
                binding.btnRecordFindBook.isEnabled = enabled
            }
        } else {
            binding.edtRecordEndDate.isEnabled = false
            binding.edtRecordBook.isEnabled = enabled
            binding.btnRecordFindBook.isEnabled = enabled
        }

        //binding.fabRecordSave.isEnabled = enabled
        //binding.fabRecordCancel.isEnabled = cancelButtonEnable

    }


    private fun setRegistrationMode() {
        supportActionBar?.title = "Cadastrar Leitura"
        val date = Date()
        formatBeginDate(date)
        configureViews(true)
    }


    private fun setUpdateMode(idRecord: String) {
        mode = UPDATE_MODE
        supportActionBar?.title = "Alterar Leitura"
        recordViewModel.getRecord(idRecord, this).observe(this) { record ->
            this.record = record
            bookViewModel.getBook(record!!.idBook, this).observe(this) { book ->
                this.book = book
                binding.edtRecordBook.setText(book!!.title)
                formatBeginDate(Date(record.beginDate))
                formatEndDate(if (record.endDate != null) Date(record.endDate!!) else null)
                binding.edtRecordNotes.setText(record.notes)
                configureViews(true)
            }
        }
    }


    private fun formatBeginDate(date: Date) {
        binding.edtRecordBeginDate.setText(dateToText(date) + " " + hourToText(date))
    }


    private fun formatEndDate(date: Date?) {
        if (date != null) {
            binding.edtRecordEndDate.setText(dateToText(date) + " " + hourToText(date))
        } else {
            binding.edtRecordEndDate.setText("")
        }
    }


    private val resultBook = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            val param = result.data?.getStringExtra("id_book")
            bookViewModel.getBook(param!!, this).observe(this) { book ->
                this.book = book
                binding.edtRecordBook.setText(book!!.title)
            }
        }
    }


    private fun onFindBookButtonClick(view: View) {
        val intent = Intent(this, BooksFilterActivity::class.java)
        resultBook.launch(intent)
    }


    private fun onSaveButtonClick() {

        var error = false
        val beginDate = textToDate(binding.edtRecordBeginDate.text.toString().replace(" ", ""))
        val endDate: Date? = if (!isEmptyText(binding.edtRecordEndDate.text.toString())) {
            textToDate(binding.edtRecordEndDate.text.toString().replace(" ", ""))
        } else {
            null
        }
        val notes = binding.edtRecordNotes.text.toString()

        if (book == null) {
            binding.edtRecordBook.error = "Selecionar o livro"
            error = true
        }

        if (!error) {

            configureViews(false, false)

            when (mode) {

                REGISTRATION_MODE -> {

                    record = Record(
                        RandomGenerator.getUUID(),
                        book!!.id,
                        beginDate.time,
                        endDate?.time,
                        notes
                    )

                    recordViewModel.insertRecord(record!!, this).observe(this) {
                        finish()
                    }

                }

                UPDATE_MODE -> {

                    record!!.idBook = book!!.id
                    record!!.beginDate = beginDate.time
                    record!!.endDate = endDate?.time
                    record!!.notes = notes
                    record!!.lastUpdateDate = getSystemTime()

                    if (endDate != null) {

                        configureViews(true)

                        val owner = this

                        with(AlertDialog.Builder(this)) {
                            setTitle("Atenção")
                            setMessage("Confirma que terminou de ler este livro?")
                            setPositiveButton("Sim") { _, _ ->
                                record!!.readingCompleted = true
                                recordViewModel.updateRecord(record!!, owner).observe(owner) {
                                    finish()
                                }
                            }
                            setNegativeButton("Não", null)
                            show()
                        }

                    } else {

                        recordViewModel.updateRecord(record!!, this).observe(this) {
                            finish()
                        }

                    }

                }

            }

        }

    }


    private fun onCancelButtonClick() {
        finish()
    }


    override fun onCoroutineException(ex: Throwable) {
        configureViews(true)
        MessageDialog.show(this, "Erro", ex.message)
    }


}