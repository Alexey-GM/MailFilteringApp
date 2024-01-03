package com.example.mailfilteringapp.domain.model

data class MessagesToFilter(
    val labelId: String,
    val messageId: List<String>,
)
