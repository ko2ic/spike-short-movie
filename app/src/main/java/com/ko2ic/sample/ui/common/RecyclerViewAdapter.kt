package com.newspicks.ui.views.recyclerview.common

import android.view.LayoutInflater
import android.view.LayoutInflater.from
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableList
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.ko2ic.sample.ui.common.CollectionItemViewModel
import com.ko2ic.sample.ui.common.ItemViewTypeProvider

open class RecyclerViewAdapter<T>(
    private val list: ObservableArrayList<T>,
    private val viewTypeProvider: ItemViewTypeProvider,
    private val onPostBindViewListener: ((T, ViewGroup) -> Unit)? = null
) : RecyclerView.Adapter<RecyclerViewAdapter.ItemViewHolder>()
    where T : CollectionItemViewModel {

    init {
        list.addOnListChangedCallback(object :
            ObservableList.OnListChangedCallback<ObservableList<T>>() {
            override fun onChanged(viewModels: ObservableList<T>) {
                notifyDataSetChanged()
            }

            override fun onItemRangeChanged(
                viewModels: ObservableList<T>,
                positionStart: Int,
                itemCount: Int
            ) {
                notifyItemRangeChanged(positionStart, itemCount)
            }

            override fun onItemRangeInserted(
                viewModels: ObservableList<T>,
                positionStart: Int,
                itemCount: Int
            ) {
                notifyItemRangeInserted(positionStart, itemCount)
            }

            override fun onItemRangeMoved(
                viewModels: ObservableList<T>,
                fromPosition: Int,
                toPosition: Int,
                itemCount: Int
            ) {
                notifyItemMoved(fromPosition, toPosition)
            }

            override fun onItemRangeRemoved(
                viewModels: ObservableList<T>,
                positionStart: Int,
                itemCount: Int
            ) {
                notifyItemRangeRemoved(positionStart, itemCount)
            }
        })
    }

    private var inflater: LayoutInflater? = null

    fun getItem(position: Int) = list.elementAtOrNull(position)



    override fun getItemCount() = list.count()
    override fun getItemViewType(position: Int): Int {
        val item = list[position]
        return viewTypeProvider.getLayoutRes(item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val inflater = this.inflater ?: from(parent.context)
        val binding = DataBindingUtil.inflate<ViewDataBinding>(inflater, viewType, parent, false)
        return ItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = list[position]
        holder.binding.setVariable(viewTypeProvider.getBindingVariableId(item), item)
        holder.binding.executePendingBindings()
        val onPostBindViewListener = this.onPostBindViewListener
        if (onPostBindViewListener != null) {
            onPostBindViewListener(item, holder.itemView as ViewGroup)
        }
    }

    class ItemViewHolder(val binding: ViewDataBinding) : RecyclerView.ViewHolder(binding.root) {
    }
}


