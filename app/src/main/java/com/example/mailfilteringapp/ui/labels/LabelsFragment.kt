package com.example.mailfilteringapp.ui.labels

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mailfilteringapp.App
import com.example.mailfilteringapp.databinding.FragmentLabelsBinding

class LabelsFragment : Fragment() {
    private var _binding: FragmentLabelsBinding? = null
    private val binding get() = _binding!!
    private val model: LabelsViewModel by viewModels { LabelsViewModelFactory((activity?.application as App).mailsRepository) }
    private val adapter = LabelsAdapter(
        onClick = { labelId -> navigateToMessages(labelId) }
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLabelsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeViewModel()
        initRecyclerView()
        applyItemDecoration()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun observeViewModel() {
        model.labels.observe(viewLifecycleOwner) { labels ->
            adapter.submitList(labels)
        }
        model.isError.observe(viewLifecycleOwner) { result ->
            result.getContentIfNotHandled()?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun initRecyclerView() {
        binding.rvLabels.adapter = adapter
        binding.rvLabels.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun applyItemDecoration() {
        val dividerItemDecoration = DividerItemDecoration(
            binding.rvLabels.context,
            LinearLayoutManager.VERTICAL
        )
        binding.rvLabels.addItemDecoration(dividerItemDecoration)
    }

    private fun navigateToMessages(labelId: String) {
        val action = LabelsFragmentDirections.actionLabelsFragmentToMessagesFragment(labelId)
        findNavController().navigate(action)
    }
}