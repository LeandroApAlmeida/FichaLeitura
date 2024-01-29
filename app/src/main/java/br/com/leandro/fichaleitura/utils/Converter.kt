package br.com.leandro.fichaleitura.utils

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


fun dateToText(date: Date): String = SimpleDateFormat("dd/MM/yyyy").format(date)


fun hourToText(date: Date): String = SimpleDateFormat("HH:mm:ss").format(date)


fun longToDate(date: Long): Date = Date(date)


fun dateToLong(date: Date): Long = date.time


fun textToDate(dateStr: String): Date = SimpleDateFormat("dd/MM/yyyyHH:mm:ss").parse("$dateStr")


fun textToFloat(text: String): Float? = text.toFloatOrNull()


fun floatToText(value: Float): String = "%.2f".format(value).replace(",", ".")


fun intToText(value: Int): String = value.toString()


fun textToInt(text: String): Int? = text.toIntOrNull()


fun formatCurrency(value: Float): String = NumberFormat.getCurrencyInstance(Locale.getDefault()).format(value)


fun isEmptyText(string: String?): Boolean = string == null || (string.isEmpty() || string.isBlank())


fun upper(str: String): String = str.uppercase(Locale.ROOT)


fun lower(str: String): String = str.lowercase(Locale.ROOT)


fun formatFloat(value: Float): String {
    val decimalFormat = DecimalFormat("#,###.00", DecimalFormatSymbols(Locale.getDefault()))
    decimalFormat.minimumFractionDigits = 2
    decimalFormat.maximumFractionDigits = 2
    var formatted = decimalFormat.format(value)
    if (formatted.startsWith(",")) {
        formatted = "0$formatted"
    }
    return formatted
}

