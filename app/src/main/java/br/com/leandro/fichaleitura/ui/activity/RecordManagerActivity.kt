package br.com.leandro.fichaleitura.ui.activity

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import br.com.leandro.fichaleitura.R
import br.com.leandro.fichaleitura.data.model.Record
import br.com.leandro.fichaleitura.data.viewmodel.BookViewModel
import br.com.leandro.fichaleitura.data.viewmodel.CoroutineListener
import br.com.leandro.fichaleitura.data.viewmodel.RecordViewModel
import br.com.leandro.fichaleitura.databinding.ActivityRecordManagerBinding
import br.com.leandro.fichaleitura.image.Photo
import br.com.leandro.fichaleitura.ui.dialog.MessageDialog
import br.com.leandro.fichaleitura.ui.recyclerview.adapter.DefaultAdapter
import br.com.leandro.fichaleitura.utils.dateToText
import br.com.leandro.fichaleitura.utils.hourToText
import dagger.hilt.android.AndroidEntryPoint
import java.util.Date

@AndroidEntryPoint
class RecordManagerActivity : AppCompatActivity(), CoroutineListener {


    private lateinit var binding: ActivityRecordManagerBinding
    private val bookViewModel: BookViewModel by viewModels()
    private val recordViewModel: RecordViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        binding = ActivityRecordManagerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.rcvRecordsManagerList.layoutManager = LinearLayoutManager(this)
        val customAdapter = CustomAdapter(this)
        customAdapter.setBackgroundColorGrid1(Color.WHITE)
        customAdapter.setBackgroundColorGrid2(Color.WHITE)
        binding.rcvRecordsManagerList.adapter = customAdapter

        supportActionBar?.title = "Histórico de Leitura"

    }


    override fun onResume() {
        super.onResume()
        listAllRecords()
    }


    private fun listAllRecords() {
        recordViewModel.getAllReadingCompleted(this).observe(this) { recordsList ->
            val customAdapter = (binding.rcvRecordsManagerList.adapter as CustomAdapter)
            customAdapter.data = recordsList
        }
    }


    override fun onCoroutineException(ex: Throwable) {
        MessageDialog.show(this, "Erro", ex.message)
    }


    private inner class CustomAdapter(val owner: RecordManagerActivity): DefaultAdapter<Record>(R.layout.recycler_layout_record_manager) {

        override fun compareItemsContents(oldItem: Record, newItem: Record): Boolean {
            return (oldItem.lastUpdateDate == newItem.lastUpdateDate)
        }

        override fun onBinding(view: View, item: Record, position: Int) {

            val imvRecordsManagerCover: ImageView = view.findViewById(R.id.imvRecordsManagerCover)
            val txvRecordsManagerTitle: TextView = view.findViewById(R.id.txvRecordsManagerTitle)
            val txvRecordsManagerBeginDate: TextView = view.findViewById(R.id.txvRecordsManagerBeginDate)
            val txvRecordsManagerEndDate: TextView = view.findViewById(R.id.txvRecordsManagerEndDate)
            val btnRecordsManagerAlter: ImageButton = view.findViewById(R.id.btnRecordsManagerAlter)
            val btnRecordsManagerRestore: ImageButton = view.findViewById(R.id.btnRecordsManagerRestore)

            val beginDate = Date(item.beginDate)
            txvRecordsManagerBeginDate.text = "${dateToText(beginDate)}  ${hourToText(beginDate)}"

            if (!item.isDeleted) {
                btnRecordsManagerAlter.isEnabled = true
                val endDate = Date(item.endDate!!)
                txvRecordsManagerEndDate.text = "${dateToText(endDate)}  ${hourToText(endDate)}"
            } else {
                btnRecordsManagerAlter.isEnabled = false
                txvRecordsManagerEndDate.text = "CANCELADO"
            }

            btnRecordsManagerAlter.setOnClickListener {
                val intent = Intent(owner, RecordActivity::class.java)
                intent.putExtra("id_record", item.id)
                startActivity(intent)
            }

            btnRecordsManagerRestore.setOnClickListener {
                with(AlertDialog.Builder(owner)) {
                    setTitle("Atenção")
                    setMessage("Confirma a reativação da leitura?")
                    setPositiveButton("Sim") { _, _ ->
                        if (!item.isDeleted) {
                            recordViewModel.restoreReading(item, owner).observe(owner) {
                                listAllRecords()
                            }
                        } else {
                            recordViewModel.undeleteRecord(item, owner).observe(owner) {
                                listAllRecords()
                            }
                        }
                    }
                    setNegativeButton("Não", null)
                    show()
                }
            }

            bookViewModel.getBook(item.idBook, owner).observe(owner) { book ->
                if (book?.cover != null) {
                    imvRecordsManagerCover.setImageBitmap(Photo.toBitmap(book.cover!!))
                } else {
                    imvRecordsManagerCover.setImageResource(R.drawable.image_cover)
                }
                txvRecordsManagerTitle.text = book!!.title
            }

        }

    }


}