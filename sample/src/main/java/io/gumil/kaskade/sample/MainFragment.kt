package io.gumil.kaskade.sample

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import io.gumil.kaskade.sample.network.DogFragment
import io.gumil.kaskade.sample.todo.TodoActivity
import kotlinx.android.synthetic.main.fragment_main.*

internal class MainFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        buttonStartTodo.setOnClickListener { startActivity(Intent(context, TodoActivity::class.java)) }

        buttonStartNetwork.setOnClickListener {
            (activity as? MainActivity)?.goTo(DogFragment())
        }
    }
}