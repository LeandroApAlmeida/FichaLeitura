package br.com.leandro.fichaleitura.ui.recyclerview.adapter

/**
 * Define um objeto que é selecionável numa RecyclerView.
 */
interface Selectable {


    /**
     * Define se o objeto está ou não selecionado.
     * @param selected true, o objeto foi selecionado, false, não foi selecionado.
     */
    fun setSelected(selected: Boolean)


    /**
     * Retorna o status de selecionado de um objeto.
     * @return true, o objeto está selecionado, false, o objeto não está selecionado.
     */
    fun isSelected(): Boolean


}