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

package io.gumil.kaskade

internal sealed class TestState : State {
    object State1 : TestState()
    object State2 : TestState()
    object State3 : TestState()
}

internal sealed class TestAction : Action {
    object Action1 : TestAction()
    object Action2 : TestAction()
    object Action3 : TestAction()
    object Action4 : TestAction()
}

internal sealed class TestResult : Effect {
    class Result1 : TestResult()

    class Result2 : TestResult()

    class Result3 : TestResult()
}