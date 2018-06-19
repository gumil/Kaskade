/*
 * Copyright 2018 Miguel Panelo
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.gumil.kaskade.sample

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import io.gumil.kaskade.HolderValue
import io.gumil.kaskade.Action
import io.gumil.kaskade.Effect
import io.gumil.kaskade.State
import io.gumil.kaskade.StateMachine
import kotlinx.android.synthetic.main.activity_main.toastButton

class MainActivity : AppCompatActivity() {

    private val stateMachine = StateMachine<ToastState, ToastAction, ToastResult>(ToastState).apply {
        addActionHandler(ToastAction::class) {
            HolderValue(ToastResult)
        }
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
    object ToastResult : Effect
}
