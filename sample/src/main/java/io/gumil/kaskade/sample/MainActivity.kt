package io.gumil.kaskade.sample

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import io.gumil.kaskade.DeferredValue
import io.gumil.kaskade.Intent
import io.gumil.kaskade.Result
import io.gumil.kaskade.State
import io.gumil.kaskade.StateMachine
import kotlinx.android.synthetic.main.activity_main.toastButton

class MainActivity : AppCompatActivity() {

    private val stateMachine = StateMachine<ToastState, ToastIntent, ToastResult>(ToastState).apply {
        addIntentHandler(ToastIntent, DeferredValue(ToastResult))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        toastButton.setOnClickListener {
            stateMachine.processIntent(ToastIntent)
        }

        stateMachine.onStateChanged = {
            Toast.makeText(this, "Hello world", Toast.LENGTH_SHORT).show()
        }
    }

    object ToastIntent : Intent
    object ToastState : State
    object ToastResult : Result<ToastState> {
        override fun reduceToState(oldState: ToastState): ToastState {
            return ToastState
        }

    }
}
