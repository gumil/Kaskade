package dev.gumil.kaskade.sample.todo.data

internal class ListTodoRepository : TodoRepository {

    private val list = mutableListOf<TodoItem>()

    init {
        (0..SIZE_LIST).map { createTodoItem(it) }.also { list.addAll(it) }
    }

    override fun getToDoItems(): List<TodoItem> = list

    override fun updateItem(item: TodoItem) {
        list[list.indexOfFirst { item.id == it.id } ] = item
    }

    override fun removeItem(item: TodoItem) {
        list.remove(item)
    }

    override fun addItem(item: TodoItem) {
        list.add(item)
    }

    private fun createTodoItem(index: Int): TodoItem = TodoItem(index, "item $index", index % 2 == 0)

    companion object {
        private const val SIZE_LIST = 5
    }
}
