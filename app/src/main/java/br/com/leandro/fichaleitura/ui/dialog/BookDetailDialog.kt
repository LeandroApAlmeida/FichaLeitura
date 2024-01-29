package br.com.leandro.fichaleitura.ui.dialog

import android.R
import android.app.Dialog
import android.content.Context
import android.graphics.Rect
import android.os.Bundle
import android.view.View
import android.view.Window
import androidx.core.content.ContextCompat.getSystemService
import br.com.leandro.fichaleitura.data.model.Book
import br.com.leandro.fichaleitura.databinding.DialogLayoutBookDetailBinding
import br.com.leandro.fichaleitura.utils.intToText


class BookDetailDialog(context: Context, val book: Book): Dialog(context) {


    private lateinit var binding: DialogLayoutBookDetailBinding


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        binding = DialogLayoutBookDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.txvBookDetailLayoutTitle.text = book.title
        binding.txvBookDetailLayoutSubtitle.text = book.subtitle
        binding.txvBookDetailLayoutAuthor.text = book.author
        binding.txvBookDetailLayoutPublisher.text = book.publisher
        binding.txvBookDetailLayoutIsbn.text = book.isbn
        binding.txvBookDetailLayoutEdition.text = intToText(book.edition)
        binding.txvBookDetailLayoutVolume.text = intToText(book.volume)
        binding.txvBookDetailLayoutYear.text = intToText(book.releaseYear)
        binding.txvBookDetailLayoutSummary.text = book.summary

    }


}