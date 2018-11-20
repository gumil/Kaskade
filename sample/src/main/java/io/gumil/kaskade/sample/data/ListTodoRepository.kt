package io.gumil.kaskade.sample.data

internal class ListTodoRepository : TodoRepository {

    private val list = mutableListOf<TodoItem>()

    override fun getToDoItems(): List<TodoItem> {
        return (0..20).map { createTodoItem(it) }
    }

    override fun updateItem(item: TodoItem) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun removeItem(item: TodoItem) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun addItem(item: TodoItem) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun createTodoItem(index: Int) : TodoItem = TodoItem("item $index", index % 2 == 0)
}