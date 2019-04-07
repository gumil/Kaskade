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

package dev.gumil.kaskade

/**
 * Upper bound of all [Action].
 */
interface Action

/**
 * Upper bound of all [State].
 */
interface State

/**
 * Type to indicate that a [State]'s duty is to only show this state once.
 */
interface SingleEvent : State

/**
 * A pair of [Action] and [State].
 *
 * Receiver used in [Reducer] for ease of use in the DSL.
 */
data class ActionState<A : Action, S : State>(
    val action: A,
    val currentState: S
)
