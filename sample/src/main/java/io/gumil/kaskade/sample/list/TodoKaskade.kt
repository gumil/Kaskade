package io.gumil.kaskade.sample.list

import io.gumil.kaskade.Action
import io.gumil.kaskade.Kaskade
import io.gumil.kaskade.State
import io.gumil.kaskade.sample.data.TodoItem
import io.gumil.kaskade.sample.data.TodoRepository
import io.gumil.kaskade.stateFlow

internal class TodoKaskade(
        private val todoRepository: TodoRepository
) {

    private val kaskade = Kaskade.create<TodoState, TodoAction>(TodoState.OnLoaded(listOf())) {
        on<TodoAction.Refresh> {
            TodoState.OnLoaded(todoRepository.getToDoItems())
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
}

internal sealed class TodoAction : Action {
    object Refresh : TodoAction()
}