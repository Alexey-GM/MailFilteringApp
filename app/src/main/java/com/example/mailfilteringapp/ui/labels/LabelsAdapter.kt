package com.example.mailfilteringapp.ui.labels

import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.mailfilteringapp.databinding.ItemLabelsListBinding
import com.google.api.services.gmail.model.Label

class LabelsAdapter(private val onClick: (String, Boolean) -> Unit) :
    ListAdapter<Label, LabelsAdapter.ElementsViewHolder>(
        NewsDiffCallback()
    ) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ElementsViewHolder {
        val binding =
            ItemLabelsListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ElementsViewHolder(binding, onClick)
    }

    override fun onBindViewHolder(holder: ElementsViewHolder, position: Int) {
        val label = getItem(position)
        holder.bind(label)
    }

    inner class ElementsViewHolder(
        private val binding: ItemLabelsListBinding,
        private val onClick: (String, Boolean) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(label: Label) {
            binding.tvName.text = label.name
            if (label.type == "user" && label.color != null) {
                binding.ivLabelColor.setColorFilter(Color.parseColor(label.color.backgroundColor))
            } else {
                binding.ivLabelColor.isVisible = false
            }
            binding.root.setOnClickListener {
                if (label.type == "user") {
                    onClick(label.id, true)
                } else {
                    onClick(label.id, false)
                }
            }
        }
    }

    private class NewsDiffCallback :
        DiffUtil.ItemCallback<Label>() {
        override fun areItemsTheSame(
            oldItem: Label,
            newItem: Label
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: Label,
            newItem: Label
        ): Boolean {
            return oldItem == newItem
        }
    }
}