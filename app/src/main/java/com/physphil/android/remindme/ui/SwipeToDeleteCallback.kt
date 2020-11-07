package com.physphil.android.remindme.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.ColorDrawable
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.physphil.android.remindme.R

class SwipeToDeleteCallback(
    context: Context,
    private val callback: OnSwipeCallback
) : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {

    private val topPadding = context.resources.getDimensionPixelSize(R.dimen.global_single)
    private val background = ColorDrawable(ContextCompat.getColor(context, R.color.colorCriticalRed))
    private val iconMargin = context.resources.getDimensionPixelSize(R.dimen.global_margin_xlarge)
    private val deleteIcon = ContextCompat.getDrawable(context, R.drawable.ic_delete_32dp)
        ?: throw IllegalStateException("Resource for delete icon could not be found.")

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean = true

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
        val itemView = viewHolder.itemView
        when {
            // swipe right
            dX > 0 -> {
                itemView.setIconBounds(SwipeDirection.Right)
                itemView.setBackgroundBounds(dX.toInt(), SwipeDirection.Right)
            }
            // swipe left
            dX < 0 -> {
                itemView.setIconBounds(SwipeDirection.Left)
                itemView.setBackgroundBounds(dX.toInt(), SwipeDirection.Left)
            }
            else -> background.setBounds(0, 0, 0, 0)
        }
        background.draw(canvas)
        deleteIcon.draw(canvas)

        super.onChildDraw(canvas, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
    }

    private enum class SwipeDirection {
        Left,
        Right
    }

    private fun View.setIconBounds(swipeDirection: SwipeDirection) {
        val iconTop = top + (height - deleteIcon.intrinsicHeight) / 2
        val iconBottom = iconTop + deleteIcon.intrinsicHeight

        when (swipeDirection) {
            SwipeDirection.Left -> {
                val iconLeft = right - iconMargin - deleteIcon.intrinsicWidth
                val iconRight = right - iconMargin
                deleteIcon.setBounds(iconLeft, iconTop, iconRight, iconBottom)
            }
            SwipeDirection.Right -> {
                val iconLeft = left + iconMargin + deleteIcon.intrinsicWidth
                val iconRight = left + iconMargin
                deleteIcon.setBounds(iconRight, iconTop, iconLeft, iconBottom)
            }
        }
    }

    private fun View.setBackgroundBounds(
        dx: Int,
        swipeDirection: SwipeDirection
    ) {
        when (swipeDirection) {
            SwipeDirection.Left -> this@SwipeToDeleteCallback.background.setBounds(
                right + dx,
                top + topPadding,
                right,
                bottom
            )
            SwipeDirection.Right -> this@SwipeToDeleteCallback.background.setBounds(
                left,
                top + topPadding,
                left + dx,
                bottom
            )
        }
    }
}

typealias OnSwipeCallback = (Int) -> Unit