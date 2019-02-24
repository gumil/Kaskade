package io.gumil.kaskade.sample.todo

import io.gumil.kaskade.sample.todo.data.TodoItem
import io.gumil.kaskade.sample.todo.data.TodoRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Before
import org.junit.Test

class TodoKaskadeTest {

    private val repository = mockk<TodoRepository>(relaxUnitFun = true)

    private val subscriber = mockk<(TodoState) -> Unit>(relaxed = true)

    private val todoKaskade = TodoKaskade(repository)

    @Before
    fun setUp() {
        every { subscriber.invoke(any()) } returns Unit
    }

    @Test
    fun `Action Refresh`() {
        every { repository.getToDoItems() } returns emptyList()

        todoKaskade.state.subscribe(subscriber)

        todoKaskade.process(TodoAction.Refresh)
        verify { subscriber.invoke(TodoState.OnLoaded(emptyList())) }
    }

    @Test
    fun `Action Delete`() {
        todoKaskade.state.subscribe(subscriber)

        todoKaskade.process(TodoAction.Delete(1, TodoItem(1, "test", false)))

        verify { subscriber.invoke(TodoState.OnDeleted(1)) }
    }

    @Test
    fun `Action Add`() {
        val todoItem = TodoItem(1, "test", false)

        todoKaskade.state.subscribe(subscriber)

        todoKaskade.process(TodoAction.Add(todoItem))

        verify { subscriber.invoke(TodoState.OnAdded(todoItem)) }
    }

    @Test
    fun `Action Update`() {
        val todoItem = TodoItem(1, "test", false)

        todoKaskade.state.subscribe(subscriber)

        todoKaskade.process(TodoAction.Update(1, todoItem))

        verify { subscriber.invoke(TodoState.OnUpdated(1, todoItem)) }
    }
}
