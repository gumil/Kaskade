# Kaskade
[![](https://jitpack.io/v/gumil/Kaskade.svg)](https://jitpack.io/#gumil/Kaskade)

State Machine for Kotlin and Android.

This is inspired by **MVI** or **Model View Intent** where an intent is passed to the **StateMachine** and it outputs a **State**. In the case of this library, **Intent** is changed to **Action** to avoid confusion with Android's Intent. It is purely written in Kotlin and can be used to non Android projects.

## Concept
![State Machine](https://raw.githubusercontent.com/gumil/Kaskade/master/art/StateMachine.png)
* **StateMachine** - Immutable and has a unidirectional data flow. It inputs Actions and outputs States. Objects passed are never changed rather a new object is created to pass new data. The flow is simply `Action -> Result -> State`.
* **State** - Model that represents the ouput
* **Action** - Model that represents the input
* **Result** - Reduces action and state to a new state. Paired with an action to determine new state.

# Installation

Add the JitPack repository to your build file
```
allprojects {
  repositories {
    ...
    maven { url 'https://jitpack.io' }
  }
}
```
Add the dependency
```
dependencies {
  implementation 'com.github.gumil.kaskade:kaskade:0.1.1'
}
```

# Usage
Create the `Action`, `Result`, `State` objects
```Kotlin
object TestAction : Action
object TestState : State
object TestResult : Result<TestState> {
  override fun reduceToState(oldState: TestState): TestState {
    // For a real use case, there should be some logic in determining the new state
    // For our example we just return TestState
    return TestState
  }
}
```

Create the `StateMachine` applying `TestState` as initial state
```Kotlin
val stateMachine = StateMachine<TestState, TestAction, TestResult>(TestState)
```

Adding handler to `Action`
```Kotlin
stateMachine.addActionHandler(TestAction::class) {
    DeferredValue(TestResult)
}
```

Observing states
```Kotlin
stateMachine.onStateChanged = {
  //Do something with new state
  render(it)
}
```

Executing actions
```Kotlin
stateMachine.processAction(ToastAction)
```

## RxJava2
Add the dependency
```
dependencies {
  implementation 'com.github.gumil.kaskade:kaskade-rx:0.1.1'
}
```

For transforming `Observables` to `Deferred`
```Kotlin
Observable.just("hello world").toDeferred()
```

Observing state as `Observable`
```Kotlin
stateMachine.stateObservable()
```

## LiveData
Add the dependency
```
dependencies {
  implementation 'com.github.gumil.kaskade:kaskade-livedata:0.1.1'
}
```

For transforming `LiveData` to `Deferred`
```Kotlin
val liveData = MutableLiveData<String>()
liveData.toDeferred()
```

Observing state as `LiveData`
```Kotlin
stateMachine.stateLiveData()
```
