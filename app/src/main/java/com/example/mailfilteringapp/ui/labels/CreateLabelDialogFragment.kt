package com.example.mailfilteringapp.ui.labels

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import com.example.mailfilteringapp.R

class CreateLabelDialogFragment : DialogFragment() {

    companion object {
        const val REQUEST_KEY = "key"
        const val BUNDLE_KEY = "bundleKey"
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_create_label_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<Button>(R.id.create_button).setOnClickListener {
            val name = view.findViewById<EditText>(R.id.label_name).text.toString()
            if (name.isNotBlank()) {
                setFragmentResult(
                    REQUEST_KEY,
                    bundleOf(BUNDLE_KEY to name)
                )
                dismiss()
            }
        }
        view.findViewById<Button>(R.id.cancel_button).setOnClickListener {
            dismiss()
        }
    }
}