package br.com.leandro.fichaleitura.report

import br.com.leandro.fichaleitura.android.FilesManager
import com.itextpdf.layout.border.Border
import com.itextpdf.layout.element.Cell
import com.itextpdf.layout.element.Paragraph
import com.itextpdf.layout.element.Text
import com.itextpdf.layout.property.TextAlignment
import java.io.File

abstract class PdfReport(fileName: String) {


    val file = getPath(fileName)


    abstract suspend fun print(params: List<Any>)


    private fun getPath(fileName: String): File? {
        val directory = FilesManager.getAppDirectory()
        return if (directory != null) {
            val outputFile = File(directory.absolutePath, "/${fileName}")
            File(outputFile.absolutePath)
        } else {
            null
        }
    }


    protected fun getCell(paragraph: Paragraph, alignment: TextAlignment?): Cell? {
        return Cell().add(paragraph).setPadding(0f).setTextAlignment(alignment).setBorder(Border.NO_BORDER)
    }


    protected fun getBoldText(text: String, size: Float = 12f, alignment: TextAlignment = TextAlignment.LEFT): Paragraph {
        return Paragraph(Text(text).setBold().setFontSize(size)).setTextAlignment(alignment)
    }


    protected fun getPlainText(text: String, size: Float = 10f, alignment: TextAlignment = TextAlignment.LEFT): Paragraph {
        return Paragraph(Text(text).setBold().setFontSize(size)).setTextAlignment(alignment)
    }


    protected fun getItalicText(text: String, size: Float = 12f, alignment: TextAlignment = TextAlignment.LEFT): Paragraph {
        return Paragraph(Text(text).setItalic().setFontSize(size)).setTextAlignment(alignment)
    }


}