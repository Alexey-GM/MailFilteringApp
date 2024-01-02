package com.example.mailfilteringapp.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import com.example.mailfilteringapp.R
import com.example.mailfilteringapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        val navInflater = navController.navInflater
        val graph = navInflater.inflate(R.navigation.mails_navigation)
        navController.graph = graph
    }
}

//    private fun getMessages() {
//        CoroutineScope(Dispatchers.IO).launch {
//            val result = mailsRepository.getMessages("me")
//            if (result is Result.Success) {
//                result.data.forEach { message ->
//                    Log.i("MessageDetails", "Snippet: ${message.snippet}")
//                }
//            } else {
//                Log.e("Error", "Failed to fetch messages")
//            }
//        }
//    }
//
//    private fun getLabels() {
//        CoroutineScope(Dispatchers.IO).launch {
//            val result = mailsRepository.getLabels("me")
//            if (result is Result.Success) {
//                result.data.forEach { label ->
//                    Log.i("LabelDetails", "Label: ${label.toString()}")
//                }
//            } else {
//                Log.e("Error", "Failed to fetch labels")
//            }
//        }
//    }

