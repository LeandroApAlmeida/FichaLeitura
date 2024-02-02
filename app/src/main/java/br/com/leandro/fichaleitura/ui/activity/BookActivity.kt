package br.com.leandro.fichaleitura.ui.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import br.com.leandro.fichaleitura.R
import br.com.leandro.fichaleitura.data.model.Book
import br.com.leandro.fichaleitura.data.viewmodel.BookViewModel
import br.com.leandro.fichaleitura.data.viewmodel.CoroutineListener
import br.com.leandro.fichaleitura.databinding.ActivityBookBinding
import br.com.leandro.fichaleitura.image.Photo
import br.com.leandro.fichaleitura.security.RandomGenerator
import br.com.leandro.fichaleitura.ui.dialog.MessageDialog
import br.com.leandro.fichaleitura.utils.getSystemTime
import br.com.leandro.fichaleitura.utils.intToText
import br.com.leandro.fichaleitura.utils.isEmptyText
import br.com.leandro.fichaleitura.utils.textToInt
import dagger.hilt.android.AndroidEntryPoint
import java.io.File

@AndroidEntryPoint
class BookActivity : AppCompatActivity(), CoroutineListener {

    private val GALLERY_REQUEST_CODE = 1234

    private lateinit var binding: ActivityBookBinding
    private val bookViewModel: BookViewModel by viewModels()
    private var book: Book? = null
    private var cover: ByteArray? = null
    private var mode: Int = REGISTRATION_MODE
    private var coverUri: Uri? = null


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        binding = ActivityBookBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.fabBookAddCover.setOnClickListener { onFindCoverImageButtonClick(it) }
        binding.fabBookEditCover.setOnClickListener { onFindCoverImageButtonClick(it) }
        binding.fabBookDeleteCover.setOnClickListener { onDeleteCoverImageButtonClick(it) }
        binding.edtBookFilePath.setOnClickListener { onFindFileButtonClick(it) }
        binding.btnBookFindFile.setOnClickListener { onFindFileButtonClick(it) }

        configureViews(true)

        if (intent.extras != null) {
            setUpdateMode(intent.extras!!.getString("id_book")!!)
        } else {
            setRegistrationMode()
        }

    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.register_menu, menu)
        return true
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.mniSave -> {
                onSaveButtonClick()
            }
            R.id.mniCancel -> {
                onCancelButtonClick()
            }
        }
        return true
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            GALLERY_REQUEST_CODE -> {
                if (resultCode == Activity.RESULT_OK) {
                    data?.data?.let { uri ->
                        try {
                            val bitmap = Photo.toBitmap(this, uri)
                            if (bitmap != null) {
                                this.cover = Photo.toStream(bitmap)
                                binding.imvBookCover.setImageBitmap(bitmap)
                                configureCoverButtons()
                            }
                        } catch (ex: Exception) {
                            MessageDialog.show(
                                this,
                                "Erro",
                                ex.message
                            )
                        }
                    }
                }
            }
        }
    }


    private fun configureViews(enabled: Boolean, cancelButtonEnable: Boolean = true) {

        binding.imvBookCover.isEnabled = enabled
        binding.fabBookAddCover.isEnabled = enabled
        binding.fabBookEditCover.isEnabled = enabled
        binding.fabBookDeleteCover.isEnabled = enabled
        binding.edtBookTitle.isEnabled = enabled
        binding.edtBookSubtitle.isEnabled = enabled
        binding.edtBookAuthor.isEnabled = enabled
        binding.edtBookPublisher.isEnabled = enabled
        binding.edtBookIsbn.isEnabled = enabled
        binding.edtBookEdition.isEnabled = enabled
        binding.edtBookVolume.isEnabled = enabled
        binding.edtBookReleaseYear.isEnabled = enabled

        //binding.fbBookSave.isEnabled = enabled
        //binding.fbBookCancel.isEnabled = cancelButtonEnable

    }


    private fun configureCoverButtons() {
        if (cover == null) {
            binding.fabBookAddCover.isEnabled = true
            binding.fabBookEditCover.isEnabled = false
            binding.fabBookDeleteCover.isEnabled = false
        } else {
            binding.fabBookAddCover.isEnabled = false
            binding.fabBookEditCover.isEnabled = true
            binding.fabBookDeleteCover.isEnabled = true
        }
    }


    private fun setRegistrationMode() {
        mode = REGISTRATION_MODE
        supportActionBar?.title = "Cadastrar Livro"
        binding.imvBookCover.setImageResource(R.drawable.image_cover)
        configureCoverButtons()
    }


    private fun setUpdateMode(idBook: String) {
        mode = UPDATE_MODE
        supportActionBar?.title = "Alterar Livro"
        bookViewModel.getBook(idBook, this).observe(this) { book ->
            this.book = book
            this.cover = book?.cover
            if (book?.cover != null) {
                binding.imvBookCover.setImageBitmap(Photo.toBitmap(book.cover!!))
            } else {
                binding.imvBookCover.setImageResource(R.drawable.image_cover)
            }
            if (book?.filePath != null) {
                binding.edtBookFilePath.setText(book!!.filePath)
            }
            binding.edtBookTitle.setText(book!!.title)
            binding.edtBookSubtitle.setText(book.subtitle)
            binding.edtBookAuthor.setText(book.author)
            binding.edtBookPublisher.setText(book.publisher)
            binding.edtBookIsbn.setText(book.isbn)
            binding.edtBookEdition.setText(intToText(book.edition))
            binding.edtBookVolume.setText(intToText(book.volume))
            binding.edtBookReleaseYear.setText(intToText(book.releaseYear))
            binding.edtBookSummary.setText(book.summary)
            configureCoverButtons()
        }
    }


    private fun onFindCoverImageButtonClick(view: View) {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        val mimeTypes = arrayOf("image/jpeg", "image/png", "image/jpg")
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
        intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        startActivityForResult(intent, GALLERY_REQUEST_CODE)
    }


    private fun onDeleteCoverImageButtonClick(view: View) {
        with (AlertDialog.Builder(this)) {
            setTitle("Atenção")
            setMessage("Excluir a foto da capa do livro?")
            setPositiveButton("Sim") { _, _ ->
                cover = null
                binding.imvBookCover.setImageResource(R.drawable.image_cover)
                configureCoverButtons()
            }
            setNegativeButton("Não", null)
            show()
        }
    }


    @SuppressLint("WrongConstant")
    private var resultFile = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            data?.data?.also { uri ->
                coverUri = uri
                val file = File(uri.path)
                binding.edtBookFilePath.setText(file.absolutePath)
            }
        }
    }


    private fun onFindFileButtonClick(view: View) {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "*/*"
        intent.putExtra(
            DocumentsContract.EXTRA_INITIAL_URI,
            MediaStore.Downloads.EXTERNAL_CONTENT_URI
        )
        resultFile.launch(intent)
    }


    private fun onSaveButtonClick() {

        var error = false

        val title = binding.edtBookTitle.text.toString()
        val subtitle = binding.edtBookSubtitle.text.toString()
        val author = binding.edtBookAuthor.text.toString()
        val publisher = binding.edtBookPublisher.text.toString()
        val isbn = binding.edtBookIsbn.text.toString()
        val edition = binding.edtBookEdition.text.toString()
        val volume = binding.edtBookVolume.text.toString()
        val releaseYear = binding.edtBookReleaseYear.text.toString()
        val summary = binding.edtBookSummary.text.toString()

        if (isEmptyText(title)) {
            error = true
            binding.edtBookTitle.error = "Título do livro"
        }

        if (isEmptyText(author)) {
            error = true
            binding.edtBookAuthor.error = "Autor do livro"
        }

        if (isEmptyText(publisher)) {
            error = true
            binding.edtBookPublisher.error = "Editora do livro"
        }

        if (isEmptyText(isbn)) {
            error = true
            binding.edtBookIsbn.error = "ISBN da publicação"
        }

        if (isEmptyText(edition)) {
            error = true
            binding.edtBookEdition.error = "Edição do livro"
        }

        if (isEmptyText(volume)) {
            error = true
            binding.edtBookVolume.error = "Volume do livro"
        }

        if (isEmptyText(releaseYear)) {
            error = true
            binding.edtBookReleaseYear.error = "Ano de lançamento do livro"
        }

        if (!error) {

            configureViews(false, false)

            when (mode) {

                REGISTRATION_MODE -> {

                    book = Book(
                        RandomGenerator.getUUID(),
                        title,
                        subtitle,
                        author,
                        publisher,
                        isbn,
                        textToInt(edition)!!,
                        textToInt(volume)!!,
                        textToInt(releaseYear)!!,
                        cover,
                        summary,
                        coverUri?.path
                    )

                    bookViewModel.insertBook(book!!, this).observe(this) {
                        finish()
                    }

                }

                UPDATE_MODE -> {

                    book!!.title = title
                    book!!.subtitle = subtitle
                    book!!.author = author
                    book!!.publisher = publisher
                    book!!.isbn = isbn
                    book!!.edition = textToInt(edition)!!
                    book!!.volume = textToInt(volume)!!
                    book!!.releaseYear = textToInt(releaseYear)!!
                    book!!.cover = cover
                    book!!.summary = summary
                    book!!.filePath = coverUri?.path
                    book!!.lastUpdateDate = getSystemTime()

                    bookViewModel.updateBook(book!!, this).observe(this) {
                        finish()
                    }

                }

            }

        }

    }


    private fun onCancelButtonClick() {
        finish()
    }


    override fun onCoroutineException(ex: Throwable) {
        configureViews(true)
        configureCoverButtons()
        MessageDialog.show(this, "Erro", ex.message)
    }


}