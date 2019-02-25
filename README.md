# Kaskade
[![Build Status](https://travis-ci.org/gumil/Kaskade.svg?branch=master)](https://travis-ci.org/gumil/Kaskade)
[![](https://jitpack.io/v/gumil/Kaskade.svg)](https://jitpack.io/#gumil/Kaskade)
[![Android Arsenal]( https://img.shields.io/badge/Android%20Arsenal-Kaskade-green.svg?style=flat )]( https://android-arsenal.com/details/1/7421 )
[![codebeat badge](https://codebeat.co/badges/72f8b972-b305-4575-a234-eda25469541b)](https://codebeat.co/projects/github-com-gumil-kaskade-master)

State Container for Kotlin and Android.

The name comes from cascade, a waterfall, which reflects the objective of the library to make flows easier with unidirectional data flow.

Inspired by **MVI** or **Model View Intent**.

![Kaskade](art/kaskade.svg)

### Kaskade is:
* **Lightweight** - does not have external dependencies
* **Modular** - add modules as you need
* **Extendable** - create user defined `Reducers`
* **Unidirectional** - data flows in one direction
* **Predictable** - control on `state` changes and `action` triggers
* **DSL** - easy to setup DSL to get started

# Usage
Create the `Action` and `State` objects.

_Note: objects are only used here for simplicity in real projects data classes are more appropriate_

```Kotlin
internal sealed class TestState : State {
    object State1 : TestState()
    object State2 : TestState()
    object State3 : TestState()
}

internal sealed class TestAction : Action {
    object Action1 : TestAction()
    object Action2 : TestAction()
    object Action3 : TestAction()
}
```

Create `Kaskade` with `TestState.State1` as initial state
```Kotlin
val kaskade = Kaskade.create<TestAction, TestState>(TestState.State1) {
    on<TestAction.Action1> {
        TestState.State1
    }

    on<TestAction.Action2> {
        TestState.State2
    }

    on<TestAction.Action3> {
        TestState.State3
    }
}
```

Adding actions to `Action` with parameter [ActionState](kaskade/src/main/kotlin/io/gumil/kaskade/models.kt)
```Kotlin
on<TestAction.Action1> { actionState ->
    // do any side effects when returning a new state
    TestState.State1
}
```

Observing states
```Kotlin
kaskade.onStateChanged = {
    // Do something with new state
    render(it)
}
```

Observing states with [Flow](kaskade/src/main/kotlin/io/gumil/kaskade/flow/Flow.kt)
```Kotlin
kaskade.stateFlow.subscribe {
    // Do something with new state
    render(it)
}
```

Executing actions
```Kotlin
kaskade.process(TestAction.Action1)
```

# Documentation
Check out the [wiki](https://github.com/gumil/Kaskade/wiki) for documentation.

Some of the topics covered are:
* **[Coroutines](https://github.com/gumil/Kaskade/wiki/Coroutines)**
* **[RxJava](https://github.com/gumil/Kaskade/wiki/RxJava)**
* **[LiveData](https://github.com/gumil/Kaskade/wiki/LiveData)**
* **[Handling Process Death](https://github.com/gumil/Kaskade/wiki/Android)**

Also check out [sample-android](https://github.com/gumil/Kaskade/tree/master/sample-android) for Android use cases and [sample-kotlin](https://github.com/gumil/Kaskade/tree/master/sample-kotlin) for kotlin only project

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
  // core module
  implementation 'com.github.gumil.kaskade:kaskade:0.2.2'
  // coroutines module
  implementation 'com.github.gumil.kaskade:kaskade-coroutines:0.2.2'
  // rx module
  implementation 'com.github.gumil.kaskade:kaskade-rx:0.2.2'
  // livedata module
  implementation 'com.github.gumil.kaskade:kaskade-livedata:0.2.2'
}
```