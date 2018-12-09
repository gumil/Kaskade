package io.gumil.kaskade.sample.todo

import io.gumil.kaskade.sample.todo.data.TodoItem
import io.gumil.kaskade.sample.todo.data.TodoRepository
import io.mockk.every
import io.mockk.mockk
import org.junit.Test
import kotlin.test.assertEquals

class TodoKaskadeTest {

    private val repository = mockk<TodoRepository>(relaxUnitFun = true)

    private val todoKaskade = TodoKaskade(repository)

    @Test
    fun `Action Refresh`() {
        every { repository.getToDoItems() } returns emptyList()

        todoKaskade.process(TodoAction.Refresh)

        todoKaskade.state.subscribe { assertEquals(TodoState.OnLoaded(emptyList()), it) }
    }

    @Test
    fun `Action Delete`() {
        todoKaskade.process(TodoAction.Delete(1, TodoItem(1, "test", false)))

        todoKaskade.state.subscribe { assertEquals(TodoState.OnDeleted(1), it) }
    }

    @Test
    fun `Action Add`() {
        val todoItem = TodoItem(1, "test", false)
        todoKaskade.process(TodoAction.Add(todoItem))

        todoKaskade.state.subscribe { assertEquals(TodoState.OnAdded(todoItem), it) }
    }

    @Test
    fun `Action Update`() {
        val todoItem = TodoItem(1, "test", false)
        todoKaskade.process(TodoAction.Update(1, todoItem))

        todoKaskade.state.subscribe { assertEquals(TodoState.OnUpdated(1, todoItem), it) }
    }
}