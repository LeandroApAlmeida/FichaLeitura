package br.com.leandro.fichaleitura.ui.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import br.com.leandro.fichaleitura.databinding.DialogLayoutFilterBinding

class FilterDialog(context: Context, private val readingNotCompleted: Boolean,
private val readingCompleted: Boolean): Dialog(context) {


    private lateinit var binding: DialogLayoutFilterBinding
    private var onConfirmButtonClickHandler: ((readingNotCompleted: Boolean, readingCompleted: Boolean) -> Unit)? = null


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        binding = DialogLayoutFilterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.swcFilterLayoutReadingNotCompleted.isChecked = readingNotCompleted
        binding.swcFilterLayoutReadingCompleted.isChecked = readingCompleted

        binding.btnFilterLayoutOk.setOnClickListener {
            onConfirmButtonClickHandler!!.invoke(
                binding.swcFilterLayoutReadingNotCompleted.isChecked,
                binding.swcFilterLayoutReadingCompleted.isChecked
            )
            dismiss()
        }

    }


    fun setOnConfirmButtonClickHandler(onConfirmButtonClickHandler: ((readingNotCompleted: Boolean,
    readingCompleted: Boolean) -> Unit)?): FilterDialog {
        this.onConfirmButtonClickHandler = onConfirmButtonClickHandler
        return this
    }


}