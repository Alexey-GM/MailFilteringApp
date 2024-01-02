package com.example.mailfilteringapp.ui.accountPicker

import android.accounts.AccountManager
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.mailfilteringapp.App
import com.example.mailfilteringapp.R
import com.example.mailfilteringapp.databinding.FragmentAccountPickerBinding
import com.google.android.gms.common.AccountPicker
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.services.gmail.GmailScopes

class AccountPickerFragment : Fragment() {
    private var _binding: FragmentAccountPickerBinding? = null
    private val binding get() = _binding!!
    private val REQUEST_CODE_EMAIL = 1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAccountPickerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btChooseAccount.setOnClickListener {
            val mCredential = GoogleAccountCredential.usingOAuth2(
                requireContext(), listOf(
                    GmailScopes.GMAIL_LABELS,
                    GmailScopes.GMAIL_READONLY,
                    GmailScopes.GMAIL_MODIFY
                )
            )
            val intent = mCredential.newChooseAccountIntent()
            startActivityForResult(intent, REQUEST_CODE_EMAIL)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_EMAIL && resultCode == Activity.RESULT_OK) {
            val accountName = data!!.getStringExtra(AccountManager.KEY_ACCOUNT_NAME)
            (activity?.application as App).mCredential.selectedAccountName = accountName
            findNavController().navigate(R.id.action_accountPickerFragment_to_labelsFragment)
        } else {
            Toast.makeText(context, "Произошла ошибка", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
