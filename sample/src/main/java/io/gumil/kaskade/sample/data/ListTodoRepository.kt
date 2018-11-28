package io.gumil.kaskade.sample.data

internal class ListTodoRepository : TodoRepository {

    private val list = mutableListOf<TodoItem>()

    override fun getToDoItems(): List<TodoItem> {
        return (0..5).map { createTodoItem(it) }.also { list.addAll(it) }
    }

    override fun updateItem(item: TodoItem) {
        list[list.indexOfFirst { item.id == it.id }] = item
    }

    override fun removeItem(item: TodoItem) {
        list.remove(item)
    }

    override fun addItem(item: TodoItem) {
        list.add(item)
    }

    private fun createTodoItem(index: Int) : TodoItem = TodoItem(index, "item $index", index % 2 == 0)
}