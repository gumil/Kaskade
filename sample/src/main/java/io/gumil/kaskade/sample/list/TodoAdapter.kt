package io.gumil.kaskade.sample.list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import io.gumil.kaskade.Flow
import io.gumil.kaskade.sample.R
import io.gumil.kaskade.sample.data.TodoItem
import kotlinx.android.synthetic.main.item_todo.view.*

internal class TodoAdapter(
        list: List<TodoItem>
) : RecyclerView.Adapter<TodoAdapter.TodoViewHolder>() {

    private var _list = list.toMutableList()

    var list: List<TodoItem>
        get() = _list
        set(value) {
            _list = value.toMutableList()
            notifyDataSetChanged()
        }

    val onItemAction = Flow<TodoAction>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoViewHolder {
        return TodoViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_todo, parent, false))
    }

    override fun getItemCount(): Int = _list.size

    override fun onBindViewHolder(holder: TodoViewHolder, position: Int) {
        holder.bind(_list[position])
    }

    fun removeItem(position: Int) {
        _list.removeAt(position)
        notifyItemRemoved(position)
    }

    inner class TodoViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(item: TodoItem) {
            itemView.textDescription.text = item.description
            itemView.checkbox.isChecked = item.isDone
            itemView.buttonDelete.setOnClickListener {
                onItemAction.sendValue(TodoAction.Delete(layoutPosition, item))
            }
        }
    }
}