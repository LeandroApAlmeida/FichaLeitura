package br.com.leandro.fichaleitura.data.model

import br.com.leandro.fichaleitura.ui.recyclerview.adapter.Selectable
import br.com.leandro.fichaleitura.utils.getSystemTime

data class Book(

    var id: String,

    var title: String,

    var subtitle: String?,

    var author: String,

    var publisher: String,

    var isbn: String,

    var edition: Int,

    var volume: Int,

    var releaseYear: Int,

    var cover: ByteArray? = null,

    var summary: String? = null,

    var filePath: String? = null,

    var registrationDate: Long = getSystemTime(),

    var lastUpdateDate: Long = getSystemTime()

): Selectable {

    private var selected: Boolean = false

    override fun setSelected(selected: Boolean) {
        this.selected = selected
    }

    override fun isSelected(): Boolean {
        return selected
    }

    override fun equals(other: Any?): Boolean {
        if (other == null) return false
        if (other !is Book) return false
        return (other).id == this.id
    }

}