package br.com.leandro.fichaleitura.ui.recyclerview.adapter

import android.graphics.drawable.GradientDrawable
import androidx.recyclerview.widget.RecyclerView
import br.com.leandro.fichaleitura.ui.activity.SELECTION_COLOR

/**
 * Adaptador para uma RecyclerView com seleção de itens. Permite tratar evento de onSelectItem nos
 * items da lista. O evento onSelectItem é disparado quando o item é selecionado ou desselecionado
 * na lista permitindo executar ações relacionadas.[<br><br>]
 * Os items que preenchem esta lista devem, obrigatóriamente, implementar a interface [Selectable]
 */
abstract class SelectableAdapter<T: Selectable>(resource: Int, var listener: SelectionListener<T>? = null):
Adapter<T>(resource) {


    /**Tratador de evento de seleção de itens na list view.*/
    private var onSelectItemHandler: ((position: Int, selected: Boolean)-> Unit)? = null
    /**Flag para controle de seleção de itens.*/
    private var activatedSelection = false
    private var selectionColor: Int = SELECTION_COLOR
    private var multiselectionMode: Boolean = true
    private var fixedRows: MutableList<Int> = mutableListOf()
    /**Lista de items selecionados na RecyclerView.*/
    val selectedItems: List<T> get() = dataList.filter { it.isSelected() }


    override fun onDataListChanged(changedDataListSize: Boolean) {
        super.onDataListChanged(changedDataListSize)
        activatedSelection = false
        for (position in 0 until dataList.size) {
            val view = recyclerView?.findViewHolderForAdapterPosition(position)?.itemView
            val background1 = view?.background
            val background2 = getDefaultBackground(position)
            if (background1 != background2) {
                view?.background = background2
                notifyItemChanged(position)
            }
        }
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val viewHolder = (holder as Adapter<T>.ViewHolder)
        viewHolder.bind(dataList[position], position, false)
        if (!fixedRows.contains(position)) {
            if (dataList[position].isSelected()) {
                viewHolder.view.background = getSelectedBackground()
            } else {
                viewHolder.view.background = getDefaultBackground(position)
            }
        } else {
            viewHolder.view.background = getDefaultBackground(position)
        }
        //Atribui tratador de evento de 0nClick ao item da RecyclerView.
        viewHolder.view.setOnClickListener {
            if (activatedSelection) {
                if (!fixedRows.contains(position)) {
                    if (!multiselectionMode) {
                        for (i in 0 until dataList.size) {
                            val item = dataList[i]
                            if (i != position) {
                                item.setSelected(false)
                                notifyItemChanged(i)
                            }
                        }
                    }
                    toggleSelection(position)
                }
            }
        }
        //Atribui tratador de evento de OnLongClick ao item da RecyclerView.
        viewHolder.view.setOnLongClickListener {
            if (!fixedRows.contains(position)) {
                activatedSelection = !activatedSelection
                if (activatedSelection) {
                    //Modo de seleção foi ativado.
                    toggleSelection(position)
                } else {
                    //Modo de seleção foi desativado.
                    for (i in 0 until dataList.size) {
                        val item = dataList[i]
                        item.setSelected(false)
                        notifyItemChanged(i)
                    }
                    if (onSelectItemHandler != null) {
                        onSelectItemHandler!!.invoke(position, false)
                    }
                }
                if (listener != null) {
                    listener!!.onSelectionChanged(this, activatedSelection)
                }
            }
            true
        }
    }


    /**
     * Alterna o estado de selecionado/desselecionado na RecyclerView.
     * @param position posição do clique no item da RecyclerView.
     */
    private fun toggleSelection(position: Int) {
        dataList[position].setSelected(!dataList[position].isSelected())
        if (onSelectItemHandler != null) {
            onSelectItemHandler!!.invoke(
                position,
                dataList[position].isSelected()
            )
        }
        if (listener != null) {
            listener!!.onSelectItem(
                this,
                position,
                dataList[position].isSelected()
            )
        }
        notifyItemChanged(position)
    }


    fun setActiveSelection(active: Boolean) {
        this.activatedSelection = active
        if (!this.activatedSelection) {
            for (i in 0 until  dataList.size) {
                val item = dataList[i]
                item.setSelected(false)
                notifyItemChanged(i)
            }
        }
    }


    /**
     * Obter o background padrão para uma linha selecionada de uma RecyclerView.
     */
    private fun getSelectedBackground(): GradientDrawable {
        return GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            cornerRadius = 0f
            setColor(selectionColor)
        }
    }


    fun setSelectionBackgroundColor(color: Int) {
        selectionColor = color
    }


    /**
     * Atribuir o tratador de evento de selecionar/desselecionar itens da RecyclerView.
     * @param onSelectItemHandler método para tratar o evento de selecionar item da RecyclerView.
     */
    fun setOnSelectItemHandler(onSelectItemHandler: (position: Int, selected: Boolean)-> Unit) {
        this.onSelectItemHandler = onSelectItemHandler
    }


    fun setMultiSelectionMode(value: Boolean) {
        this.multiselectionMode = value
    }


    fun addFixedRow(row: Int) {
        fixedRows.add(row)
    }


    fun removeFixedRow(row: Int) {
        for (i in 0 until fixedRows.size) {
            if (fixedRows[i] == row) {
                fixedRows.removeAt(i)
                break
            }
        }
    }


}