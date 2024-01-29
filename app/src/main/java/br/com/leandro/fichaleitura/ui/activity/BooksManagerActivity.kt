package br.com.leandro.fichaleitura.ui.activity

import android.R.attr.numColumns
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import br.com.leandro.fichaleitura.R
import br.com.leandro.fichaleitura.data.model.Book
import br.com.leandro.fichaleitura.data.model.Order
import br.com.leandro.fichaleitura.data.viewmodel.BookViewModel
import br.com.leandro.fichaleitura.data.viewmodel.CoroutineListener
import br.com.leandro.fichaleitura.databinding.ActivityBooksManagerBinding
import br.com.leandro.fichaleitura.image.Photo
import br.com.leandro.fichaleitura.ui.dialog.MessageDialog
import br.com.leandro.fichaleitura.ui.recyclerview.adapter.SelectableAdapter
import br.com.leandro.fichaleitura.ui.recyclerview.decoration.RecyclerViewDecoration
import br.com.leandro.fichaleitura.utils.intToText
import br.com.leandro.fichaleitura.utils.upper
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class BooksManagerActivity : AppCompatActivity(), CoroutineListener {


    private lateinit var binding: ActivityBooksManagerBinding
    private val bookViewModel: BookViewModel by viewModels()
    private var booksList: List<Book>? = null


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        binding = ActivityBooksManagerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.edtBooksManagerFilter.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) { filter(s.toString()) }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        binding.imbBooksManagerAdd.setOnClickListener { onAddBookButtonClick(it) }
        binding.imbBooksManagerEdit.setOnClickListener { onAddEditButtonClick(it) }
        binding.imbBooksManagerDelete.setOnClickListener { onDeleteBookButtonClick(it) }
        binding.imbBooksManagerClean.setOnClickListener { onCleanButtonClick(it) }

        supportActionBar?.title = "Cadastro de Livros"

        binding.rcvBooksManagerBooksList.layoutManager = LinearLayoutManager(this)
        val customAdapter = CustomAdapter(this)
        customAdapter.setBackgroundColorGrid1(Color.WHITE)
        customAdapter.setBackgroundColorGrid2(Color.WHITE)
        customAdapter.setOnSelectItemHandler { _, _ ->
            val enable = customAdapter.selectedItems.isNotEmpty()
            binding.imbBooksManagerDelete.isEnabled = enable
            binding.imbBooksManagerEdit.isEnabled = enable
        }
        binding.rcvBooksManagerBooksList.adapter = customAdapter
        val decoration = RecyclerViewDecoration(0, 0, 0, 50, numColumns)
        binding.rcvBooksManagerBooksList.addItemDecoration(decoration)

    }


    override fun onResume() {
        super.onResume()
        listAllBooks()
    }


    private fun listAllBooks() {
        bookViewModel.getAllBooksLite(this, Order.ASC).observe(this) { booksList ->
            this.booksList = booksList
            val customAdapter = (binding.rcvBooksManagerBooksList.adapter as CustomAdapter)
            customAdapter.data = booksList
            binding.imbBooksManagerDelete.isEnabled = false
            binding.imbBooksManagerDelete.isEnabled = false
            binding.edtBooksManagerFilter.setText("")
        }
    }


    private fun filter(filter: String) {
        if (booksList != null) {
            val newList = booksList!!.filter { upper(it.title).contains(upper(filter)) }
            val customAdapter = (binding.rcvBooksManagerBooksList.adapter as CustomAdapter)
            customAdapter.data = newList
        }
    }


    private fun onCleanButtonClick(it: View?) {
        binding.edtBooksManagerFilter.setText("")
        filter("")
    }


    private fun onAddBookButtonClick(view: View) {
        startActivity(Intent(this, BookActivity::class.java))
    }


    private fun onAddEditButtonClick(view: View) {
        val customAdapter = (binding.rcvBooksManagerBooksList.adapter as CustomAdapter)
        val book: Book = customAdapter.selectedItems[0]
        val intent = Intent(this, BookActivity::class.java)
        intent.putExtra("id_book", book.id)
        startActivity(intent)
    }


    private fun onDeleteBookButtonClick(view: View) {

        val owner = this
        val customAdapter = (binding.rcvBooksManagerBooksList.adapter as CustomAdapter)
        val selectedBooks: List<Book> = customAdapter.selectedItems

        with (AlertDialog.Builder(this)) {
            setTitle("Atenção")
            setMessage("Confirma a exclusão do(s) livro(s) selecionado(s)?")
            setPositiveButton("Sim") { _, _ ->
                selectedBooks.forEach { city ->
                    bookViewModel.deleteBook(city, owner).observe(owner) {
                        listAllBooks()
                    }
                }
            }
            setNegativeButton("Não", null)
            show()
        }

    }


    override fun onCoroutineException(ex: Throwable) {
        MessageDialog.show(this, "Erro", ex.message)
    }


    private inner class CustomAdapter(val owner: BooksManagerActivity): SelectableAdapter<Book>(R.layout.recycler_layout_book) {

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