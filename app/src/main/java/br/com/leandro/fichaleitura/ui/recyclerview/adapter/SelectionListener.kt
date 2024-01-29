package br.com.leandro.fichaleitura.ui.recyclerview.adapter

interface SelectionListener<T: Selectable> {

    fun onSelectionChanged(adapter: SelectableAdapter<T>, allowSelection: Boolean)

    fun onSelectItem(adapter: SelectableAdapter<T>, position: Int, selected: Boolean)

}