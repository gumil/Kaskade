package io.gumil.kaskade.sample.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import io.gumil.kaskade.sample.R
import io.gumil.kaskade.sample.data.ListTodoRepository
import kotlinx.android.synthetic.main.fragment_list.*

internal class ListFragment : androidx.fragment.app.Fragment() {

    private val todoKaskade = TodoKaskade(ListTodoRepository()).apply {
        listenToUpdates { render(it) }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView.layoutManager = LinearLayoutManager(context)

        todoKaskade.process(TodoAction.Refresh)
    }

    private fun render(state: TodoState) {
        when (state) {
            is TodoState.OnLoaded -> recyclerView.adapter = TodoAdapter(state.list)
        }
    }
}