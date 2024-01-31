package br.com.leandro.fichaleitura.report

import android.app.Activity
import br.com.leandro.fichaleitura.android.FilesManager
import br.com.leandro.fichaleitura.utils.dateToText
import br.com.leandro.fichaleitura.utils.hourToText
import java.util.Date

class ReportBuilder(var activity: Activity) {


    companion object {
        const val READING_RECORD_REPORT = 1
        const val CANCELED_READING_REPORT = 2
    }


    suspend fun printReport(reportId: Int, vararg params: Any) {
        
        FilesManager.checkPermissions(activity)

        var report: PdfReport? = null
        val date = Date()
        val dateStr = dateToText(date).replace("/", "")
        val hourStr = hourToText(date).replace(":", "")

        when (reportId) {
            READING_RECORD_REPORT -> report = ReadingRecordReport("FichaLeitura${dateStr}${hourStr}.pdf")
            CANCELED_READING_REPORT -> report = CanceledReadingReport("FichaLeitura${dateStr}${hourStr}.pdf")
        }

        if (report != null) {
            report.print(params.asList())
            report.file!!
            FilesManager.openFile(
                report.file!!,
                "pdf/*"
            )
        }

    }


}