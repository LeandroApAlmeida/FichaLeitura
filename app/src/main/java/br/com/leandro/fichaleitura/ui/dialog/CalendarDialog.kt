package br.com.leandro.fichaleitura.ui.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import br.com.leandro.fichaleitura.databinding.DialogLayoutCalendarBinding
import java.util.Calendar
import java.util.Date

class CalendarDialog(context: Context, val date: Date?): Dialog(context) {


    private lateinit var binding: DialogLayoutCalendarBinding
    private var onConfirmButtonClickHandler: ((date: Date) -> Unit)? = null


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        binding = DialogLayoutCalendarBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val calendar: Calendar = Calendar.getInstance()
        if (date != null) {
            calendar.time = date
        } else {
            calendar.time = Date()
        }
        binding.dpkCalendarDialogDate.updateDate(
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )

        binding.npkTimeDialogHours.maxValue = 23
        binding.npkTimeDialogHoursMinutes.maxValue = 59
        binding.npkTimeDialogHoursSeconds.maxValue = 59
        binding.npkTimeDialogHours.setFormatter {i->String.format("%02d", i)}
        binding.npkTimeDialogHoursMinutes.setFormatter {i->String.format("%02d", i)}
        binding.npkTimeDialogHoursSeconds.setFormatter {i->String.format("%02d", i)}

        calendar.time = date
        binding.npkTimeDialogHours.value = calendar.get(Calendar.HOUR_OF_DAY)
        binding.npkTimeDialogHoursMinutes.value = calendar.get(Calendar.MINUTE)
        binding.npkTimeDialogHoursSeconds.value = calendar.get(Calendar.SECOND)

        binding.imbCalendarDialogConfirm.setOnClickListener {
            if (onConfirmButtonClickHandler != null) {
                val calendar: Calendar = Calendar.getInstance()
                calendar.set(Calendar.YEAR, binding.dpkCalendarDialogDate.year)
                calendar.set(Calendar.MONTH, binding.dpkCalendarDialogDate.month)
                calendar.set(Calendar.DAY_OF_MONTH, binding.dpkCalendarDialogDate.dayOfMonth)
                calendar.set(Calendar.HOUR_OF_DAY, binding.npkTimeDialogHours.value)
                calendar.set(Calendar.MINUTE, binding.npkTimeDialogHoursMinutes.value)
                calendar.set(Calendar.SECOND, binding.npkTimeDialogHoursSeconds.value)
                onConfirmButtonClickHandler!!.invoke(Date(calendar.time.time))
                dismiss()
            }
        }

        binding.imbCalendarDialogCancel.setOnClickListener {
            dismiss()
        }

    }


    fun setOnConfirmButtonClickHandler(onConfirmButtonClickHandler: ((date: Date) -> Unit)?): CalendarDialog {
        this.onConfirmButtonClickHandler = onConfirmButtonClickHandler
        return this
    }


}