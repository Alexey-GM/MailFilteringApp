package com.example.mailfilteringapp.ui.messages

import android.R
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mailfilteringapp.App
import com.example.mailfilteringapp.databinding.FragmentMessagesBinding
import com.example.mailfilteringapp.ui.model.CustomLabel

class MessagesFragment : Fragment() {
    private var _binding: FragmentMessagesBinding? = null
    private val binding get() = _binding!!
    private val model: MessagesViewModel by viewModels { MessagesViewModelFactory((activity?.application as App).mailsRepository) }
    private lateinit var adapter: MessageAdapter
    private lateinit var label: String
    private lateinit var labels: Array<CustomLabel>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            val args = MessagesFragmentArgs.fromBundle(it)
            if (!args.labelId.isNullOrEmpty()){
                label = args.labelId!!
                model.getMessages(args.labelId!!)
            }
            labels = if (args.allLabels.isNotEmpty()){
                args.allLabels
            } else {
                emptyArray()
            }
        }
        adapter = MessageAdapter(
            onClick = {  },
            labels = labels
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMessagesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btNextPage.isEnabled = false
        observeViewModel()
        initRecyclerView()
        binding.btNextPage.setOnClickListener {
            loadNextPage()
        }
        ArrayAdapter(
            requireContext(),
            R.layout.simple_spinner_item,
            labels
        ).also { adapter ->
            adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
            binding.sLabels.adapter = adapter
        }
        filterMessages()
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun observeViewModel() {
        model.messages.observe(viewLifecycleOwner) { messages ->
            adapter.submitList(messages)
            binding.btNextPage.isEnabled = true
        }
        model.isError.observe(viewLifecycleOwner) { result ->
            result.getContentIfNotHandled()?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
            }
        }
        model.isUpdateError.observe(viewLifecycleOwner) { result ->
            result.getContentIfNotHandled()?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
            }
        }
        model.isUpdateSuccess.observe(viewLifecycleOwner) { result ->
            result.getContentIfNotHandled()?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun initRecyclerView() {
        binding.rvMessages.adapter = adapter
        binding.rvMessages.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun loadNextPage() {
        model.getMessages(label)
        binding.btNextPage.isEnabled = false
    }

    private fun filterMessages() {
        binding.btFilter.setOnClickListener {
            if (labels.isNotEmpty()) {
                val keywords = binding.etKeyword.text.toString()
                val senders = binding.etSenders.text.toString()
                val label = binding.sLabels.selectedItem as CustomLabel
                if (keywords.isNotBlank() || senders.isNotBlank()) {
                    model.filterMessages(keywords, senders, label.labelId)
                } else {
                    Toast.makeText(requireContext(), "Заполните хотя бы одно поле", Toast.LENGTH_LONG).show()
                }
            } else {
                Toast.makeText(requireContext(), "Отсутствуют метки для фильтрации! Создайте хотя бы одну!", Toast.LENGTH_LONG).show()
            }
        }
    }
}