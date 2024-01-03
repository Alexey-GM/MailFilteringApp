package com.example.mailfilteringapp.ui.labels

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mailfilteringapp.App
import com.example.mailfilteringapp.R
import com.example.mailfilteringapp.databinding.FragmentLabelsBinding
import com.example.mailfilteringapp.ui.model.toCustomLabel
import kotlin.random.Random

class LabelsFragment : Fragment() {
    private var _binding: FragmentLabelsBinding? = null
    private val binding get() = _binding!!
    private val model: LabelsViewModel by viewModels { LabelsViewModelFactory((activity?.application as App).mailsRepository) }
    private val adapter = LabelsAdapter(
        onClick = { labelId, isUserType -> navigateToMessages(labelId, isUserType) }
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
        binding.fab.setOnClickListener {
            openCreateLabelDialog()
        }
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
        model.isCreateLabelSuccess.observe(viewLifecycleOwner) { result ->
            result.getContentIfNotHandled()?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
                model.getLabels()
            }
        }
        model.isCreateLabelError.observe(viewLifecycleOwner) { result ->
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

    private fun navigateToMessages(labelId: String, isUserType: Boolean) {
        if (!isUserType) {
            val allUserLabels = model.labels.value?.filter { it.type == "user" }
            if (allUserLabels!!.isNotEmpty()) {
                val labels = allUserLabels.map { it.toCustomLabel() }.toTypedArray()
                val action = LabelsFragmentDirections.actionLabelsFragmentToMessagesFragment(labelId, labels)
                findNavController().navigate(action)
            } else {
                val action = LabelsFragmentDirections.actionLabelsFragmentToMessagesFragment(labelId, emptyArray())
                findNavController().navigate(action)
            }
        } else {
            val allUserLabels = model.labels.value?.filter { it.type == "user" }
            if (allUserLabels!!.isNotEmpty()) {
                val labels = allUserLabels.map { it.toCustomLabel() }.toTypedArray()
                val action = LabelsFragmentDirections.actionLabelsFragmentToMessageUserLabelsFragment(labelId, labels)
                findNavController().navigate(action)
            } else {
                val action = LabelsFragmentDirections.actionLabelsFragmentToMessageUserLabelsFragment(labelId, emptyArray())
                findNavController().navigate(action)
            }
        }
    }

    private fun getRandomColor(): String {
        val colors = requireContext().resources.getStringArray(R.array.colors_array)
        val randomIndex = Random.nextInt(colors.size)
        return colors[randomIndex]
    }
    private fun openCreateLabelDialog() {
        findNavController().navigate(R.id.action_labelsFragment_to_createLabelDialogFragment)
        setLabelNameResult()
    }

    private fun setLabelNameResult() {
        setFragmentResultListener(CreateLabelDialogFragment.REQUEST_KEY) { key, bundle ->
            val name = bundle.getString(CreateLabelDialogFragment.BUNDLE_KEY)
            model.createLabel(name!!, getRandomColor())
        }
    }
}