package com.example.mailfilteringapp.ui.messages

import android.content.res.ColorStateList
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.mailfilteringapp.R
import com.example.mailfilteringapp.databinding.ItemMessageBinding
import com.example.mailfilteringapp.ui.model.CustomLabel
import com.google.android.material.chip.Chip
import com.google.api.services.gmail.model.Message
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MessageAdapter(private val onClick: (String) -> Unit, private val labels: Array<CustomLabel>) :
    ListAdapter<Message, MessageAdapter.ElementsViewHolder>(
        NewsDiffCallback()
    ) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ElementsViewHolder {
        val binding =
            ItemMessageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ElementsViewHolder(binding, onClick)
    }

    override fun onBindViewHolder(holder: ElementsViewHolder, position: Int) {
        val message = getItem(position)
        holder.bind(message)
    }

    inner class ElementsViewHolder(
        private val binding: ItemMessageBinding,
        private val onClick: (String) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(message: Message) {
            binding.chipGroup.removeAllViews()
            val headers = message.payload.headers
            val subject = headers.find { it.name == "Subject" }?.value ?: "No Subject"
            val from = headers.find { it.name == "From" }?.value ?: "No Sender"
            val snippet = message.snippet
            binding.senderName.text = from.split(" ").first()
            val sdf = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
            val date = Date(message.internalDate)
            binding.messageTime.text = sdf.format(date)
            binding.messageText.text = snippet
            val msgLabels = getLabels(message)
            if (msgLabels.isNotEmpty()) {
                initChipGroup(msgLabels)
            }
        }

        private fun initChipGroup(labels: List<CustomLabel>) {
            binding.chipGroupView.isHorizontalScrollBarEnabled = false
            binding.chipGroup.removeAllViews()
            for (label in labels) {
                val chip = Chip(binding.chipGroup.context)
                chip.text = label.labelName
                chip.isClickable = true
                chip.isCheckable = true
                chip.chipIcon = binding.root.context.getDrawable(R.drawable.baseline_label_24)
                chip.chipIconTint = ColorStateList.valueOf(Color.parseColor(label.labelColor))
                binding.chipGroup.addView(chip)
            }
        }

        private fun getLabels(message: Message): List<CustomLabel> {
            val listLabels = mutableListOf<CustomLabel>()
            labels.forEach {
                if (message.labelIds.contains(it.labelId)) {
                    listLabels.add(it)
                }
            }
            return listLabels
        }
    }

    private class NewsDiffCallback :
        DiffUtil.ItemCallback<Message>() {
        override fun areItemsTheSame(
            oldItem: Message,
            newItem: Message
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: Message,
            newItem: Message
        ): Boolean {
            return oldItem == newItem
        }
    }
}