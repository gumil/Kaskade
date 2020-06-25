package dev.gumil.kaskade.coroutines

import dev.gumil.kaskade.Action
import dev.gumil.kaskade.SingleEvent
import dev.gumil.kaskade.State

sealed class TestState : State {
    object State1 : TestState()
    object State2 : TestState()
    object SingleStateEvent : TestState(), SingleEvent
}

sealed class TestAction : Action {
    object Action1 : TestAction()
    object Action2 : TestAction()
    object Action3 : TestAction()
}
