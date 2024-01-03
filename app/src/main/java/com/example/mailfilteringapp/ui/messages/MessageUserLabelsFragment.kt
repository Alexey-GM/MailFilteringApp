package com.example.mailfilteringapp.ui.messages

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
import com.example.mailfilteringapp.databinding.FragmentMessageUserLabelsBinding
import com.example.mailfilteringapp.ui.model.CustomLabel

class MessageUserLabelsFragment : Fragment() {
    private var _binding: FragmentMessageUserLabelsBinding? = null
    private val binding get() = _binding!!
    private val model: MessageUserLabelsViewModel by viewModels { MessageUserLabelsViewModelFactory((activity?.application as App).mailsRepository) }
    private lateinit var adapter: MessageAdapter
    private lateinit var label: String
    private lateinit var labels: Array<CustomLabel>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            val args = MessageUserLabelsFragmentArgs.fromBundle(it)
            if (!args.labelId.isNullOrEmpty()){
                label = args.labelId!!
                model.getMessages(args.labelId!!)
            }
            if (args.allLabels.isNotEmpty()){
                labels = args.allLabels
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
        _binding = FragmentMessageUserLabelsBinding.inflate(inflater, container, false)
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
    }

    private fun initRecyclerView() {
        binding.rvMessages.adapter = adapter
        binding.rvMessages.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun loadNextPage() {
        model.getMessages(label)
        binding.btNextPage.isEnabled = false
    }
}