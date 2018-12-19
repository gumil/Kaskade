package io.gumil.kaskade.sample.todo.data

internal data class TodoItem(
    val id: Int,
    val description: String,
    val isDone: Boolean
)