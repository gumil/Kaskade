package io.gumil.kaskade.sample

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import io.gumil.kaskade.Deferred
import io.gumil.kaskade.MviIntent
import io.gumil.kaskade.MviResult
import io.gumil.kaskade.MviState
import io.gumil.kaskade.MviStateMachine
import kotlinx.android.synthetic.main.activity_main.toastButton

class MainActivity : AppCompatActivity() {

    private val stateMachine = MviStateMachine<ToastState, ToastIntent, ToastResult>(
            ToastState,
            {
                Deferred(ToastResult)
            },
            { _, _ ->
                ToastState
            }
    )

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

    object ToastIntent : MviIntent
    object ToastState : MviState
    object ToastResult : MviResult
}
