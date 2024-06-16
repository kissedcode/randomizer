package dev.kissed.common.util

fun Boolean.toFloat(): Float {
    return if (this) +1f else -1f
}