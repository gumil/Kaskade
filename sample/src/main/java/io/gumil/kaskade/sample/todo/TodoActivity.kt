package io.gumil.kaskade.sample.todo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import io.gumil.kaskade.sample.R

internal class TodoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_todo)

        supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, ListFragment())
                .commit()
    }
}