package br.com.leandro.fichaleitura.data.model

import br.com.leandro.fichaleitura.ui.recyclerview.adapter.Selectable
import br.com.leandro.fichaleitura.utils.getSystemTime

data class Record (

    var id: String,

    var idBook: String,

    var beginDate: Long,

    var endDate: Long?,

    var notes: String?,

    var registrationDate: Long = getSystemTime(),

    var lastUpdateDate: Long = getSystemTime(),

    var readingCompleted: Boolean = false,

    var isDeleted: Boolean = false

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
        if (other !is Record) return false
        return (other).id == this.id
    }

}