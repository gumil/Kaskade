package io.gumil.kaskade.sample

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import io.gumil.kaskade.DeferredValue
import io.gumil.kaskade.Action
import io.gumil.kaskade.Result
import io.gumil.kaskade.State
import io.gumil.kaskade.StateMachine
import kotlinx.android.synthetic.main.activity_main.toastButton

class MainActivity : AppCompatActivity() {

    private val stateMachine = StateMachine<ToastState, ToastAction, ToastResult>(ToastState).apply {
        addIntentHandler(ToastAction, DeferredValue(ToastResult))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        toastButton.setOnClickListener {
            stateMachine.processAction(ToastAction)
        }

        stateMachine.onStateChanged = {
            Toast.makeText(this, "Hello world", Toast.LENGTH_SHORT).show()
        }
    }

    object ToastAction : Action
    object ToastState : State
    object ToastResult : Result<ToastState> {
        override fun reduceToState(oldState: ToastState): ToastState {
            return ToastState
        }

    }
}
