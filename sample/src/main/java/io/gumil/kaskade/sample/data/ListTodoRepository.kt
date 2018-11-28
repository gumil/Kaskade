package io.gumil.kaskade.sample.data

internal class ListTodoRepository : TodoRepository {

    private val list = mutableListOf<TodoItem>()

    override fun getToDoItems(): List<TodoItem> {
        return (0..5).map { createTodoItem(it) }
    }

    override fun updateItem(item: TodoItem) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun removeItem(item: TodoItem) {
        list.remove(item)
    }

    override fun addItem(item: TodoItem) {
        list.add(item)
    }

    private fun createTodoItem(index: Int) : TodoItem = TodoItem(index, "item $index", index % 2 == 0)
}