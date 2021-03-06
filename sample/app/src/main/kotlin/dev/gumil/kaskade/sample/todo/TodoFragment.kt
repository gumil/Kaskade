package dev.gumil.kaskade.sample.todo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import dev.gumil.kaskade.sample.R
import dev.gumil.kaskade.sample.todo.data.ListTodoRepository
import kotlinx.android.synthetic.main.fragment_list.*

internal class TodoFragment : Fragment() {

    private val todoKaskade = TodoKaskade(ListTodoRepository())

    private val adapter = TodoAdapter(emptyList())

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_list, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = adapter

        todoKaskade.state.subscribe { render(it) }

        adapter.onItemAction.subscribe { todoKaskade.dispatch(it) }

        todoKaskade.dispatch(TodoAction.Refresh)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        todoKaskade.unsubscribe()
    }

    private fun render(state: TodoState) {
        when (state) {
            is TodoState.OnLoaded -> adapter.list = state.list
            is TodoState.OnDeleted -> adapter.removeItem(state.position)
            is TodoState.OnAdded -> adapter.addItem(state.item)
            is TodoState.OnUpdated -> recyclerView.post {
                adapter.updateItem(state.position, state.item)
            }
        }
    }
}
