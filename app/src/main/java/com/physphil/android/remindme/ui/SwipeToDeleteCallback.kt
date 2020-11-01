package com.physphil.android.remindme.ui

import android.graphics.Canvas
import android.graphics.drawable.ColorDrawable
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.physphil.android.remindme.R
import kotlin.reflect.KClass

class SwipeToDeleteCallback(
    private val unswipeableViewHolders: List<KClass<out RecyclerView.ViewHolder>> = emptyList(),
    private val callback: OnSwipeCallback
) : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean = true

    override fun getSwipeDirs(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int {
        return when (unswipeableViewHolders.contains(viewHolder::class)) {
            true -> 0
            else -> super.getSwipeDirs(recyclerView, viewHolder)
        }
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        callback(viewHolder.adapterPosition)
    }

    override fun onChildDraw(
        canvas: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        with(viewHolder.itemView) {
            val topPadding = context.resources.getDimensionPixelSize(R.dimen.global_single)
            val background = ColorDrawable(ContextCompat.getColor(context, R.color.colorCriticalRed))
            when {
                // swipe right
                dX > 0 -> background.setBounds(
                    left,
                    top + topPadding,
                    left + dX.toInt(),
                    bottom
                )
                // swipe left
                dX < 0 -> background.setBounds(
                    right + dX.toInt(),
                    top + topPadding,
                    right,
                    bottom
                )
                else -> background.setBounds(0, 0, 0, 0)
            }
            background.draw(canvas)
        }
        super.onChildDraw(canvas, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
    }
}

typealias OnSwipeCallback = (Int) -> Unit