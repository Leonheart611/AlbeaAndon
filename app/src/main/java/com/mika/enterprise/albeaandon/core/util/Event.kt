package com.mika.enterprise.albeaandon.core.util

import androidx.lifecycle.Observer

class Event<out T>(private val content: T?) {

    private var hasBeenHandled = false

    fun getContentIfNotHandled(): T? {
        return if (hasBeenHandled) {
            null
        } else {
            hasBeenHandled = true
            content
        }
    }

    fun peekContent(): T? = content
}

class EventObserver<T>(private val onEventUnhandledContent: (T) -> Unit) : Observer<Event<T>> {
    override fun onChanged(value: Event<T>) {
        value.getContentIfNotHandled()?.let { event ->
            onEventUnhandledContent(event)
        }
    }
}