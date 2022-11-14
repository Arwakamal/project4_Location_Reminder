package com.udacity.project4.base

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView

abstract class BaseRecyclerViewAdapter<T>(private val callback: ((item: T) -> Unit)? = null) :
    RecyclerView.Adapter<DataBindingViewHolder<T>>() {

    private var Items: MutableList<T> = mutableListOf()

    /**
     * Returns the _items data
     */

    private val items: List<T>?
        get() = this.Items
    override fun getItemCount() = Items.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataBindingViewHolder<T> {
        val layoutInflater = LayoutInflater.from(parent.context)

        val binding = DataBindingUtil
            .inflate<ViewDataBinding>(layoutInflater, getLayoutRes(viewType), parent, false)

        binding.lifecycleOwner = getLifecycleOwner()

        return DataBindingViewHolder(binding)
    }
    fun getItem(position: Int) = Items[position]

    override fun onBindViewHolder(holder: DataBindingViewHolder<T>, position: Int) {
        val item = getItem(position)
        holder.bind(item)
        holder.itemView.setOnClickListener {
            callback?.invoke(item)
        }
    }
    /**
     * Adds data to the actual Dataset
     *
     * @param items to be merged
     */
    fun addData(items: List<T>) {
        Items.addAll(items)
        notifyDataSetChanged()
    }
    /**
     * Clears the _items data
     */
    fun clear() {
        Items.clear()
        notifyDataSetChanged()
    }

    @LayoutRes
    abstract fun getLayoutRes(viewType: Int): Int

    open fun getLifecycleOwner(): LifecycleOwner? {
        return null
    }
}

