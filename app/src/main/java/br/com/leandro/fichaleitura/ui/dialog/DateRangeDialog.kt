package br.com.leandro.fichaleitura.ui.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import br.com.leandro.fichaleitura.databinding.DialogLayoutDateRangeBinding
import br.com.leandro.fichaleitura.utils.dateToText
import br.com.leandro.fichaleitura.utils.textToDate
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.Date

class DateRangeDialog(context: Context, private val title: String, private val firstDate: Date,
private val lastDate: Date): Dialog(context) {


    private lateinit var binding: DialogLayoutDateRangeBinding
    private var onPrintButtonClickHandler: ((beginDate: Date, endDate: Date) -> Unit)? = null


    @OptIn(DelicateCoroutinesApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        binding = DialogLayoutDateRangeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.imbDateDialogPrintReport.setOnClickListener {
            if (onPrintButtonClickHandler != null) {
                val beginDate: Date = textToDate(binding.edtDateDialogBeginDate.text.toString() + "00:00:00")
                val endDate: Date = textToDate(binding.edtDateDialogEndDate.text.toString() + "23:59:59")
                if (beginDate.time <= endDate.time) {
                    onPrintButtonClickHandler!!.invoke(beginDate, endDate)
                    dismiss()
                } else {
                    binding.edtDateDialogEndDate.error = "Data final menor que data inicial."
                }
            }
        }

        binding.edtDateDialogTitle.text = title

        binding.imbDateDialogCancelReport.setOnClickListener {
            dismiss()
        }

        GlobalScope.launch {
            setDataRange()
        }

    }


    private fun setDataRange() {

        try {

            binding.edtDateDialogBeginDate.setText(dateToText(firstDate))
            binding.edtDateDialogEndDate.setText(dateToText(lastDate))

            binding.edtDateDialogBeginDate.setOnClickListener {
                val beginDate = textToDate(binding.edtDateDialogBeginDate.text.toString() + "00:00:00")
                CalendarDialog(this.context, beginDate).setOnConfirmButtonClickHandler { date: Date ->
                    binding.edtDateDialogBeginDate.setText(dateToText(date))
                }.show()
            }
            binding.edtDateDialogBeginDate.keyListener = null
            binding.edtDateDialogBeginDate.isFocusable = false

            binding.edtDateDialogEndDate.setOnClickListener {
                try {
                    val endDate = textToDate(binding.edtDateDialogEndDate.text.toString() + "23:59:59")
                    CalendarDialog(this.context, endDate).setOnConfirmButtonClickHandler { date: Date ->
                        binding.edtDateDialogEndDate.setText(dateToText(date))
                    }.show()
                } catch (ex: Exception) {
                    binding.edtDateDialogBeginDate.error = "Data inicial no formato dd/mm/aaaa"
                }
            }
            binding.edtDateDialogEndDate.keyListener = null
            binding.edtDateDialogEndDate.isFocusable = false

        } catch (ex: Exception) {
            MessageDialog.show(
                context,
                "Erro",
                ex.message
            )
        }

    }


    fun setOnPrintButtonClickHandler(onPrintButtonClickHandler: ((beginDate: Date, endDate: Date) -> Unit)?): DateRangeDialog {
        this.onPrintButtonClickHandler = onPrintButtonClickHandler
        return this
    }


}