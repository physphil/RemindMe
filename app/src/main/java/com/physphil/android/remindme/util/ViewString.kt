package com.physphil.android.remindme.util

import androidx.annotation.StringRes
import com.physphil.android.remindme.util.ViewString.String

/**
 * Represents a String in a View that can either be a [String] or an [Int] resource.
 */
sealed class ViewString {
    data class String(val value: kotlin.String) : ViewString()
    data class Integer(@StringRes val resId: Int) : ViewString()
}