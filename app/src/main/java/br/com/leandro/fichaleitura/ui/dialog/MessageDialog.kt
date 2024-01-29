package br.com.leandro.fichaleitura.ui.dialog

import android.content.Context
import androidx.appcompat.app.AlertDialog

object MessageDialog {


    fun show(context: Context, title: String, message: String?) {
        with(AlertDialog.Builder(context)) {
            setTitle(title)
            setMessage(message)
            setPositiveButton("OK", null)
            show()
        }
    }


}