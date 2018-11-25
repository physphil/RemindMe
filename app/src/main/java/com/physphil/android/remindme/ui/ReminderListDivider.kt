package com.physphil.android.remindme.ui

import android.graphics.Canvas
import android.graphics.drawable.Drawable
import androidx.recyclerview.widget.RecyclerView

/**
 * Copyright (c) 2018 Phil Shadlyn
 */
class ReminderListDivider(private val divider: Drawable) : RecyclerView.ItemDecoration() {

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        val dividerLeft = parent.paddingLeft
        val dividerRight = parent.width - parent.paddingRight

        // Ignore the last entry when drawing the divider
        val limit = parent.childCount - 2
        for (i in 0..limit) {
            val child = parent.getChildAt(i)
            val position = parent.getChildAdapterPosition(child)

            // Also, don't draw the divider for the entry in the first position
            if (position > 0) {
                val params: RecyclerView.LayoutParams = child.layoutParams as RecyclerView.LayoutParams
                val dividerTop = child.bottom + params.bottomMargin
                val dividerBottom = dividerTop + divider.intrinsicHeight

                divider.setBounds(dividerLeft, dividerTop, dividerRight, dividerBottom)
                divider.draw(c)
            }
        }
    }
}