package io.gumil.kaskade.sample.todo.data

internal interface TodoRepository {

    fun getToDoItems(): List<TodoItem>

    fun updateItem(item: TodoItem)

    fun removeItem(item: TodoItem)

    fun addItem(item: TodoItem)
}
