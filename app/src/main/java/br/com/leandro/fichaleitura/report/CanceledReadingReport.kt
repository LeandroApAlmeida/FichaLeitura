package br.com.leandro.fichaleitura.report

import br.com.leandro.fichaleitura.database.AppDatabase
import br.com.leandro.fichaleitura.utils.dateToText
import br.com.leandro.fichaleitura.utils.hourToText
import br.com.leandro.fichaleitura.utils.intToText
import com.itextpdf.kernel.color.Color
import com.itextpdf.kernel.geom.PageSize
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.layout.Document
import com.itextpdf.layout.element.Cell
import com.itextpdf.layout.element.Paragraph
import com.itextpdf.layout.element.Table
import com.itextpdf.layout.property.TextAlignment
import com.itextpdf.layout.property.UnitValue
import java.util.Date

class CanceledReadingReport(fileName: String): PdfReport(fileName) {


    private val recordDao = AppDatabase.instance.getRecordDao()
    private val bookDao = AppDatabase.instance.getBookDao()


    override suspend fun print(params: List<Any>) {

        val beginDate = params[0] as Date
        val endDate = params[1] as Date

        val pdfWriter = PdfWriter(file!!.absolutePath)
        val pdf = PdfDocument(pdfWriter)
        val document = Document(pdf, PageSize.A4.rotate())
        document.setMargins(20f, 20f, 20f, 20f)
        document.pdfDocument.addNewPage()

        val current = Date()
        val headerTable = Table(2)
        headerTable.addCell(getCell(getBoldText("LEITURAS CANCELADAS"), TextAlignment.LEFT))
        headerTable.addCell(getCell(getBoldText(
            "IMPRESSO: ${dateToText(current)}, ${hourToText(current)}",
            alignment = TextAlignment.RIGHT
        ), TextAlignment.RIGHT))
        document.add(headerTable)
        document.add(Paragraph("\n").setFontSize(1f))

        document.add(Paragraph("\n").setFontSize(8f))
        val headerTable2 = Table(2)
        headerTable2.addCell(getCell(getItalicText(
            "* PERÍODO: DE ${dateToText(beginDate)} À ${dateToText(endDate)}",
            10f,
            TextAlignment.LEFT
        ), TextAlignment.LEFT))
        headerTable2.addCell(getCell(getBoldText(""), TextAlignment.RIGHT))
        document.add(headerTable2)
        document.add(Paragraph("\n").setFontSize(8f))

        val columnsSize = floatArrayOf(1.6f, 1.0f, 1.0f, 0.45f, 0.15f, 0.15f, 0.15f, 0.30f, 0.30f)
        val textSize = 8f
        val columnColor = Color.LIGHT_GRAY
        val dataTable = Table(UnitValue.createPercentArray(columnsSize)).useAllAvailableWidth()
        dataTable.addCell(Cell().add(getBoldText("LIVRO", textSize)).setBackgroundColor(columnColor))
        dataTable.addCell(Cell().add(getBoldText("AUTOR", textSize)).setBackgroundColor(columnColor))
        dataTable.addCell(Cell().add(getBoldText("EDITORA", textSize)).setBackgroundColor(columnColor))
        dataTable.addCell(Cell().add(getBoldText("ISBN", textSize)).setBackgroundColor(columnColor))
        dataTable.addCell(Cell().add(getBoldText("ED.", textSize)).setBackgroundColor(columnColor))
        dataTable.addCell(Cell().add(getBoldText("VOL.", textSize)).setBackgroundColor(columnColor))
        dataTable.addCell(Cell().add(getBoldText("ANO", textSize)).setBackgroundColor(columnColor))
        dataTable.addCell(Cell().add(getBoldText("INÍCIO", textSize)).setBackgroundColor(columnColor))
        dataTable.addCell(Cell().add(getBoldText("FINAL", textSize)).setBackgroundColor(columnColor))

        val recordsList = recordDao.getAllCanceledReadingAsc(beginDate.time, endDate.time)
        var totalAmount = 0
        for (record in recordsList) {
            totalAmount++
            val date1 = Date(record.beginDate)
            val date2 = Date(record.lastUpdateDate)
            val book = bookDao.getBookByIdLite(record.idBook)
            dataTable.addCell(Cell().add(getPlainText(book!!.title, textSize)))
            dataTable.addCell(Cell().add(getPlainText(book.author, textSize)))
            dataTable.addCell(Cell().add(getPlainText(book.publisher, textSize)))
            dataTable.addCell(Cell().add(getPlainText(book.isbn, textSize)))
            dataTable.addCell(Cell().add(getPlainText(intToText(book.edition), textSize)))
            dataTable.addCell(Cell().add(getPlainText(intToText(book.volume), textSize)))
            dataTable.addCell(Cell().add(getPlainText(intToText(book.releaseYear), textSize)))
            dataTable.addCell(Cell().add(getPlainText(dateToText(date1), textSize)))
            dataTable.addCell(Cell().add(getPlainText(dateToText(date2), textSize)))
        }

        document.add(dataTable)

        if (recordsList.isNotEmpty()) {
            document.add(Paragraph("\n").setFontSize(1f))
            val resumeTable = Table(UnitValue.createPercentArray(floatArrayOf(9.0f, 0.562f))).useAllAvailableWidth()
            resumeTable.addCell(Cell().add(getBoldText("TOTAL DE LEITURAS CANCELADAS NO PERÍODO:", textSize)))
            resumeTable.addCell(Cell().add(getBoldText(intToText(totalAmount), textSize, TextAlignment.RIGHT)))
            document.add(resumeTable)
        }

        document.close()

    }


}