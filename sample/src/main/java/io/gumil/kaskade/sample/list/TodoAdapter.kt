package io.gumil.kaskade.sample.list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import io.gumil.kaskade.sample.R
import io.gumil.kaskade.sample.data.TodoItem
import kotlinx.android.synthetic.main.item_todo.view.*

internal class TodoAdapter(
        private val list: List<TodoItem>
) : RecyclerView.Adapter<TodoAdapter.ToDoViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ToDoViewHolder {
        return ToDoViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_todo, parent, false))
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: ToDoViewHolder, position: Int) {
        holder.bind(list[position])
    }

    class ToDoViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(item: TodoItem) {
            itemView.textDescription.text = item.description
            itemView.checkbox.isChecked = item.isDone
            itemView.buttonDelete.setOnClickListener {  }
        }
    }
}