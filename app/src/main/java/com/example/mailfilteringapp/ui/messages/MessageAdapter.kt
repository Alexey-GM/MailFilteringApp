package com.example.mailfilteringapp.ui.messages

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.mailfilteringapp.databinding.ItemMessageBinding
import com.google.api.services.gmail.model.Message
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MessageAdapter(private val onClick: (String) -> Unit) :
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
            val headers = message.payload.headers
            val subject = headers.find { it.name == "Subject" }?.value ?: "No Subject"
            val from = headers.find { it.name == "From" }?.value ?: "No Sender"
            val snippet = message.snippet
            binding.senderName.text = from.split(" ").first()
            val sdf = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
            val date = Date(message.internalDate)
            binding.messageTime.text = sdf.format(date)
            binding.messageText.text = snippet
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

