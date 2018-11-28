package io.gumil.kaskade.sample.list

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import io.gumil.kaskade.Flow
import io.gumil.kaskade.sample.R
import io.gumil.kaskade.sample.data.TodoItem
import kotlinx.android.synthetic.main.item_footer.view.*
import kotlinx.android.synthetic.main.item_todo.view.*

internal class TodoAdapter(
        list: List<TodoItem>
) : RecyclerView.Adapter<TodoAdapter.BindableViewHolder>() {

    private var _list = list.toMutableList()

    var list: List<TodoItem>
        get() = _list
        set(value) {
            _list = value.toMutableList()
            notifyDataSetChanged()
        }

    val onItemAction = Flow<TodoAction>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BindableViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        if (viewType == TYPE_FOOTER) {
            return FooterViewHolder(inflater.inflate(R.layout.item_footer, parent, false))
        }
        return TodoViewHolder(inflater.inflate(R.layout.item_todo, parent, false))
    }

    override fun getItemCount(): Int = _list.size + 1

    override fun onBindViewHolder(holder: BindableViewHolder, position: Int) {
        val todoItem = when (holder) {
            is FooterViewHolder -> null
            is TodoViewHolder -> _list[position]
            else -> throw UnsupportedOperationException()
        }

        holder.bind(todoItem)
    }

    override fun getItemViewType(position: Int): Int {
        if (position == _list.size) {
            return TYPE_FOOTER
        }
        return super.getItemViewType(position)
    }

    fun removeItem(position: Int) {
        _list.removeAt(position)
        notifyItemRemoved(position)
    }

    fun addItem(todoItem: TodoItem) {
        _list.add(todoItem)
        notifyItemInserted(itemCount + 1)
    }

    fun updateItem(position: Int, todoItem: TodoItem) {
        _list[position] = todoItem
        notifyItemChanged(position)
    }

    inner class TodoViewHolder(view: View) : BindableViewHolder(view) {
        init {
            itemView.setOnClickListener { itemView.checkbox.performClick() }
        }

        override fun bind(item: TodoItem?) {
            itemView.textDescription.text = item?.description
            itemView.checkbox.isChecked = item?.isDone ?: false

            if (item?.isDone == true) {
                itemView.textDescription.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
            } else {
                itemView.textDescription.paintFlags = 0
            }

            itemView.buttonDelete.setOnClickListener {
                item?.let { item ->
                    onItemAction.sendValue(TodoAction.Delete(layoutPosition, item))
                }
            }

            itemView.checkbox.setOnCheckedChangeListener { _, isChecked ->
                item?.let {
                    onItemAction.sendValue(TodoAction.Update(layoutPosition, item.copy(isDone = isChecked)))
                }
            }
        }
    }

    inner class FooterViewHolder(view: View) : BindableViewHolder(view) {

        init {
            itemView.inputAddItem.setOnEditorActionListener { _, _, _ ->
                itemView.buttonAdd.performClick()
            }
        }

        override fun bind(item: TodoItem?) {
            itemView.buttonAdd.setOnClickListener {
                val description = itemView.inputAddItem.text.toString()
                onItemAction.sendValue(TodoAction.Add(TodoItem(layoutPosition, description, false)))
                itemView.inputAddItem.setText("")
            }
        }
    }

    abstract class BindableViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        abstract fun bind(item: TodoItem?)
    }

    companion object {
        private const val TYPE_FOOTER = 1001
    }
}