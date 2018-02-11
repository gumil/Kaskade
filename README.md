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
  implementation 'com.github.gumil.Kaskade:kaskade:0.1.0'
}
```

# Usage

## RxJava2
Add the dependency
```
dependencies {
  implementation 'com.github.gumil.Kaskade:kaskade-rx:0.1.0'
}
```
## LiveData
Add the dependency
```
dependencies {
  implementation 'com.github.gumil.Kaskade:kaskade-livedata:0.1.0'
}
```