package com.example.test24simpleapp2.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.test24simpleapp2.databinding.ItemViewBinding
import com.example.test24simpleapp2.model.ModelClass

class RecyclerAdapter(
    private val itemList: List<ModelClass>,
    private val onItemClicked: (Int) -> Unit
) : RecyclerView.Adapter<RecyclerAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemViewBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return itemList.count()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.titleTextView.text = itemList[position].title
        holder.binding.titleTextView.setOnClickListener {
            val item = itemList[position].id
            onItemClicked(item)
        }
    }
}