package br.com.leandro.fichaleitura.ui.recyclerview.decoration

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration


class RecyclerViewDecoration (

    private val marginTop: Int,
    private val marginRight: Int,
    private val marginLeft: Int,
    private val marginBottom: Int,
    private val columns: Int

): ItemDecoration() {


    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val position = parent.getChildLayoutPosition(view)
        outRect.right = marginRight
        outRect.bottom = marginBottom
        if (position < columns) { outRect.top = marginTop }
        if (position % columns == 0) { outRect.left = marginLeft }
    }


}