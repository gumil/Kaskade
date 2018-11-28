package io.gumil.kaskade.sample.list

import io.gumil.kaskade.*
import io.gumil.kaskade.sample.data.TodoItem
import io.gumil.kaskade.sample.data.TodoRepository

internal class TodoKaskade(
        private val todoRepository: TodoRepository
) {

    private val kaskade = Kaskade.create<TodoState, TodoAction>(TodoState.OnLoaded(listOf())) {
        on<TodoAction.Refresh> {
            TodoState.OnLoaded(todoRepository.getToDoItems())
        }

        on<TodoAction.Delete> {
            todoRepository.removeItem(action.todoItem)
            TodoState.OnDeleted(action.position)
        }

        on<TodoAction.Add> {
            todoRepository.addItem(action.todoItem)
            TodoState.OnAdded(action.todoItem)
        }

        on<TodoAction.Update> {
            todoRepository.updateItem(action.todoItem)
            TodoState.OnUpdated(action.position, action.todoItem)
        }
    }

    val state = kaskade.stateFlow()

    fun process(action: TodoAction) {
        kaskade.process(action)
    }

    fun unsubscribe() {
        state.unsubscribe()
        kaskade.unsubscribe()
    }
}

internal sealed class TodoState : State {
    class OnLoaded(val list: List<TodoItem>) : TodoState()
    class OnDeleted(val position: Int) : TodoState()
    class OnAdded(val item: TodoItem) : TodoState()
    class OnUpdated(val position: Int, val item: TodoItem) : TodoState()
}

internal sealed class TodoAction : Action {
    object Refresh : TodoAction()
    class Delete(val position: Int, val todoItem: TodoItem) : TodoAction()
    class Add(val todoItem: TodoItem) : TodoAction()
    class Update(val position: Int, val todoItem: TodoItem) : TodoAction()
}