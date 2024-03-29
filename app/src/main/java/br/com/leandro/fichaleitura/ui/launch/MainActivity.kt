package br.com.leandro.fichaleitura.ui.launch

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.StrictMode
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.webkit.MimeTypeMap
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import br.com.leandro.fichaleitura.R
import br.com.leandro.fichaleitura.android.FileUtils
import br.com.leandro.fichaleitura.android.FilesManager
import br.com.leandro.fichaleitura.data.model.Record
import br.com.leandro.fichaleitura.data.viewmodel.BookViewModel
import br.com.leandro.fichaleitura.data.viewmodel.CoroutineListener
import br.com.leandro.fichaleitura.data.viewmodel.RecordViewModel
import br.com.leandro.fichaleitura.databinding.ActivityMainBinding
import br.com.leandro.fichaleitura.image.Photo
import br.com.leandro.fichaleitura.report.ReportBuilder
import br.com.leandro.fichaleitura.ui.activity.BackupActivity
import br.com.leandro.fichaleitura.ui.activity.BooksManagerActivity
import br.com.leandro.fichaleitura.ui.activity.RecordActivity
import br.com.leandro.fichaleitura.ui.activity.RecordManagerActivity
import br.com.leandro.fichaleitura.ui.dialog.CalendarDialog
import br.com.leandro.fichaleitura.ui.dialog.DateRangeDialog
import br.com.leandro.fichaleitura.ui.dialog.FilterDialog
import br.com.leandro.fichaleitura.ui.dialog.MessageDialog
import br.com.leandro.fichaleitura.ui.recyclerview.adapter.DefaultAdapter
import br.com.leandro.fichaleitura.utils.dateToText
import br.com.leandro.fichaleitura.utils.getSystemTime
import br.com.leandro.fichaleitura.utils.isEmptyText
import br.com.leandro.fichaleitura.utils.textToDate
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.karumi.dexter.Dexter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.util.Date
import java.util.Locale


@AndroidEntryPoint
class MainActivity : AppCompatActivity(), CoroutineListener {


    private lateinit var binding: ActivityMainBinding
    private val bookViewModel: BookViewModel by viewModels()
    private val recordViewModel: RecordViewModel by viewModels()
    private var listIsReadingCompleted: Boolean = true
    private var listIsReadingNotCompleted: Boolean = true
    private var listIsReadingDeleted: Boolean = true


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.rcvMainReadingRecordList.layoutManager = LinearLayoutManager(this)
        val customAdapter = CustomAdapter(this)
        customAdapter.setBackgroundColorGrid1(Color.WHITE)
        customAdapter.setBackgroundColorGrid2(Color.WHITE)
        binding.rcvMainReadingRecordList.adapter = customAdapter

        StrictMode.setVmPolicy(StrictMode.VmPolicy.Builder().build())
        FilesManager.checkPermissions(this)
        FilesManager.clearCache()
        Locale.setDefault(Locale("pt", "BR"))

        Dexter.withContext(this).withPermission(Manifest.permission.MANAGE_DOCUMENTS)
        Dexter.withContext(this).withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
        Dexter.withContext(this).withPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)

    }


    override fun onResume() {
        super.onResume()
        listAllRecords()
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.main_menu, menu)
        return true
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {

            R.id.mniSearchBook -> {
                FilterDialog(this, listIsReadingNotCompleted, listIsReadingCompleted, listIsReadingDeleted)
                .setOnConfirmButtonClickHandler { isReadingNotCompleted, isReadingCompleted, isReadingDeleted ->
                    this.listIsReadingNotCompleted = isReadingNotCompleted
                    this.listIsReadingCompleted = isReadingCompleted
                    this.listIsReadingDeleted = isReadingDeleted
                    listAllRecords()
                }.show()
            }

            R.id.mniBooksManager -> {
                startActivity(Intent(this, BooksManagerActivity::class.java))
            }

            R.id.mniAddRecord -> {
                startActivity(Intent(this, RecordActivity::class.java))
            }

            R.id.mniRecordsManager -> {
                startActivity(Intent(this, RecordManagerActivity::class.java))
            }

            R.id.mniReadingRecordReport -> {
                printReport("FICHA DE LEITURA", ReportBuilder.READING_RECORD_REPORT)
            }

            R.id.mniReadingCanceledReport -> {
                printReport("LEITURAS CANCELADAS", ReportBuilder.CANCELED_READING_REPORT)
            }

            R.id.mniBackup -> {
                startActivity(Intent(this, BackupActivity::class.java))
            }

            R.id.mniRestoreView -> {
                listIsReadingCompleted = true
                listIsReadingNotCompleted = true
                listIsReadingDeleted = true
                listAllRecords()
            }

            R.id.mniAbout -> {
                MessageDialog.show(
                    this,
                    "SOBRE",
                    "\nDESENVOLVEDOR:" +
                    "\nLeandro A. Almeida" +
                    "\n\nVERSÃO:" +
                    "\n1.0.0"
                )
            }

        }

        return true

    }


    private fun listAllRecords() {
        val customAdapter = (binding.rcvMainReadingRecordList.adapter as CustomAdapter)
        recordViewModel.getAllRecords(listIsReadingCompleted, listIsReadingNotCompleted,
        listIsReadingDeleted, this).observe(this) { recordsList ->
            customAdapter.data = recordsList
        }
    }


    private fun printReport(title: String, reportId: Int) {
        val owner = this
        val beginDate: Date = textToDate("01/01/190000:00:00")
        val endDate: Date = textToDate("31/12/999923:59:59")
        DateRangeDialog(this, title, beginDate, endDate).setOnPrintButtonClickHandler { beginDate, endDate ->
            lifecycleScope.launch(Dispatchers.IO) {
                try {
                    ReportBuilder(owner).printReport(
                        reportId,
                        beginDate,
                        endDate
                    )
                } catch (ex: Exception) {
                    MessageDialog.show(
                        owner,
                        "Erro",
                        ex.message
                    )
                }
            }
        }.show()
    }


    override fun onCoroutineException(ex: Throwable) {
        MessageDialog.show(this, "Erro", ex.message)
    }


    private inner class CustomAdapter(val owner: MainActivity): DefaultAdapter<Record>(R.layout.recycler_layout_record) {

        override fun compareItemsContents(oldItem: Record, newItem: Record): Boolean {
            return false
        }

        @SuppressLint("WrongConstant", "ResourceType")
        override fun onBinding(view: View, item: Record, position: Int) {

            val imvRecordLayoutCover: ImageView = view.findViewById(R.id.imvRecordLayoutCover)
            val txvRecordLayoutTitle: TextView = view.findViewById(R.id.txvRecordLayoutTitle)
            val txvRecordLayoutBeginDate: TextView = view.findViewById(R.id.txvRecordLayoutBeginDate)
            val txvRecordLayoutEndDate: TextView = view.findViewById(R.id.txvRecordLayoutEndDate)
            val fabRecordLayoutLink: FloatingActionButton = view.findViewById(R.id.fabRecordLayoutLink)
            val fabRecordLayoutEdit: FloatingActionButton = view.findViewById(R.id.fabRecordLayoutEdit)
            val fabRecordLayoutDelete: FloatingActionButton = view.findViewById(R.id.fabRecordLayoutDelete)
            val fabRecordLayoutTerminate: FloatingActionButton = view.findViewById(R.id.fabRecordLayoutTerminate)

            if (!item.isDeleted && !item.readingCompleted) {

                fabRecordLayoutLink.isVisible = true
                fabRecordLayoutEdit.isVisible = true
                fabRecordLayoutDelete.isVisible = true
                fabRecordLayoutTerminate.isVisible = true

                fabRecordLayoutTerminate.setOnClickListener {
                    with(AlertDialog.Builder(owner)) {
                        setTitle("Atenção")
                        setMessage("Confirma a conclusão da leitura deste livro?")
                        setPositiveButton("Sim") { _, _ ->
                            val endDate = Date(getSystemTime())
                            CalendarDialog(owner, endDate).setOnConfirmButtonClickHandler { date: Date ->
                                recordViewModel.terminateReading(item, date.time, owner).observe(owner) {
                                    listAllRecords()
                                }
                            }.show()
                        }
                        setNegativeButton("Não", null)
                        show()
                    }
                }

                fabRecordLayoutEdit.setOnClickListener {
                    val intent = Intent(owner, RecordActivity::class.java)
                    intent.putExtra("id_record", item.id)
                    startActivity(intent)
                }

                fabRecordLayoutDelete.setOnClickListener {
                    with(AlertDialog.Builder(owner)) {
                        setTitle("Atenção")
                        setMessage("Confirma o cancelamento da leitura?")
                        setPositiveButton("Sim") { _, _ ->
                            recordViewModel.deleteRecord(item, owner).observe(owner) {
                                listAllRecords()
                            }
                        }
                        setNegativeButton("Não", null)
                        show()
                    }
                }

            } else {
                fabRecordLayoutLink.isVisible = false
                fabRecordLayoutEdit.isVisible = false
                fabRecordLayoutDelete.isVisible = false
                fabRecordLayoutTerminate.isVisible = false
            }

            bookViewModel.getBook(item.idBook, owner).observe(owner) { book ->

                if (book?.cover != null) {
                    imvRecordLayoutCover.setImageBitmap(Photo.toBitmap(book.cover!!))
                } else {
                    imvRecordLayoutCover.setImageResource(R.drawable.image_cover)
                }

                if (!isEmptyText(book!!.subtitle)) {
                    txvRecordLayoutTitle.text = "${book.title} - ${book.subtitle}"
                } else {
                    txvRecordLayoutTitle.text = book.title
                }

                val beginDate = Date(item.beginDate)
                txvRecordLayoutBeginDate.text = dateToText(beginDate)

                txvRecordLayoutEndDate.setTextColor(Color.parseColor(getString(R.color.record_text_color)))

                if (!item.isDeleted) {
                    if (item.readingCompleted) {
                        val endDate: Date? = if (item.endDate != null) Date(item.endDate!!) else null
                        txvRecordLayoutEndDate.text = dateToText(endDate!!)
                    } else {
                        if (!isEmptyText(book.filePath)) {
                            fabRecordLayoutLink.setOnClickListener {
                                if (!isEmptyText(book.filePath)) {
                                    try {
                                        if (!isEmptyText(book.filePath)) {
                                            val path = book.filePath!!.replace("document", "storage", false)
                                            .replace("primary", "emulated/0/").replace(":", "/")
                                            val file = File(path)
                                            var type: String? = null
                                            var extension: String? = null
                                            val i: Int = path.lastIndexOf('.')
                                            if (i > 0) extension = path.substring(i + 1)
                                            if (extension != null) type = MimeTypeMap.getSingleton()
                                            .getMimeTypeFromExtension(extension)
                                            val intent = Intent(Intent.ACTION_VIEW)
                                            intent.setDataAndType(Uri.fromFile(file), type)
                                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                            startActivity(
                                                Intent.createChooser(intent, "Abrir com")
                                            )
                                        }
                                    } catch (ex: Exception) {
                                        MessageDialog.show(owner, "Erro", ex.message)
                                    }
                                }
                            }
                        }
                        txvRecordLayoutEndDate.setTextColor(Color.RED)
                        txvRecordLayoutEndDate.text = "NÃO CONCLUÍDA"
                    }
                } else {
                    txvRecordLayoutEndDate.text = "CANCELADA"
                }

            }

        }

    }


}