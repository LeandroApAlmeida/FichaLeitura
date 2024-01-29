package br.com.leandro.fichaleitura.ui.activity

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import br.com.leandro.fichaleitura.R
import br.com.leandro.fichaleitura.data.model.Book
import br.com.leandro.fichaleitura.data.model.Order
import br.com.leandro.fichaleitura.data.viewmodel.BookViewModel
import br.com.leandro.fichaleitura.data.viewmodel.CoroutineListener
import br.com.leandro.fichaleitura.databinding.ActivityBooksFilterBinding
import br.com.leandro.fichaleitura.image.Photo
import br.com.leandro.fichaleitura.ui.dialog.MessageDialog
import br.com.leandro.fichaleitura.ui.recyclerview.adapter.DefaultAdapter
import br.com.leandro.fichaleitura.ui.recyclerview.decoration.RecyclerViewDecoration
import br.com.leandro.fichaleitura.utils.intToText
import br.com.leandro.fichaleitura.utils.upper
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BooksFilterActivity : AppCompatActivity(), CoroutineListener {


    private lateinit var binding: ActivityBooksFilterBinding
    private val bookViewModel: BookViewModel by viewModels()
    private var booksList: List<Book>? = null


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        binding = ActivityBooksFilterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.edtBooksFilterTitle.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) { filter(s.toString()) }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        binding.imbBooksFilterClean.setOnClickListener { onCleanButtonClick(it) }

        binding.rcvBooksFilterResult.layoutManager = LinearLayoutManager(this)
        val customAdapter = CustomAdapter(this)
        customAdapter.setBackgroundColorGrid1(Color.WHITE)
        customAdapter.setBackgroundColorGrid2(Color.WHITE)
        customAdapter.setOnClickHandler { position ->
            val book: Book = customAdapter.data[position]
            val resultIntent = Intent()
            resultIntent.putExtra (
                "id_book",
                book.id
            )
            setResult(RESULT_OK, resultIntent)
            finish()
        }
        binding.rcvBooksFilterResult.adapter = customAdapter
        val decoration = RecyclerViewDecoration(0, 0, 0, 50, android.R.attr.numColumns)
        binding.rcvBooksFilterResult.addItemDecoration(decoration)

        supportActionBar?.title = "Buscar Livro"

    }


    override fun onResume() {
        super.onResume()
        listAllBooks()
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.book_filter_menu, menu)
        return true
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.mniBooksFilterAddBook -> {
                startActivity(Intent(this, BookActivity::class.java))
            }
        }
        return true
    }


    private fun listAllBooks() {
        bookViewModel.getAllBooksLite(this, Order.ASC).observe(this) { booksList ->
            this.booksList = booksList
            val customAdapter = (binding.rcvBooksFilterResult.adapter as CustomAdapter)
            customAdapter.data = booksList!!
            binding.edtBooksFilterTitle.setText("")
        }
    }


    private fun filter(filter: String) {
        if (booksList != null) {
            val newList = booksList!!.filter { upper(it.title).contains(upper(filter)) }
            val customAdapter = (binding.rcvBooksFilterResult.adapter as CustomAdapter)
            customAdapter.data = newList
        }
    }


    private fun onCleanButtonClick(it: View?) {
        binding.edtBooksFilterTitle.setText("")
        filter("")
    }


    override fun onCoroutineException(ex: Throwable) {
        MessageDialog.show(this, "Erro", ex.message)
    }


    private inner class CustomAdapter(val owner: BooksFilterActivity): DefaultAdapter<Book>(R.layout.recycler_layout_book) {

        override fun compareItemsContents(oldItem: Book, newItem: Book): Boolean {
            return (oldItem.lastUpdateDate == newItem.lastUpdateDate)
        }

        override fun onBinding(view: View, item: Book, position: Int) {
            val txvBookLayoutTitle: TextView = view.findViewById(R.id.txvBookLayoutTitle)
            val txvBookLayoutIsbn: TextView = view.findViewById(R.id.txvBookLayoutIsbn)
            val txvBookLayoutAuthor: TextView = view.findViewById(R.id.txvBookLayoutAuthor)
            val txvBookLayoutYear: TextView = view.findViewById(R.id.txvBookLayoutYear)
            val imvBookLayoutCover: ImageView = view.findViewById(R.id.imvBookLayoutCover)
            txvBookLayoutTitle.text = item.title
            txvBookLayoutAuthor.text = item.author
            txvBookLayoutYear.text = intToText(item.releaseYear)
            txvBookLayoutIsbn.text = item.isbn
            bookViewModel.getBookCover(item.id, owner).observe(owner) { bookCover ->
                if (bookCover != null) {
                    imvBookLayoutCover.setImageBitmap(Photo.toBitmap(bookCover))
                } else {
                    imvBookLayoutCover.setImageResource(R.drawable.image_cover)
                }
            }
        }

    }


}