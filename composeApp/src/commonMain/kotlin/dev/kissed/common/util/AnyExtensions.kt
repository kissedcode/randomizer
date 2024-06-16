package dev.kissed.common.util

inline fun <T : Any?> grabIf(condition: Boolean, block: () -> T): T? {
    return if (condition) block() else null
}