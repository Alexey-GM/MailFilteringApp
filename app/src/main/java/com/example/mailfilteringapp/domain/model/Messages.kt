package com.example.mailfilteringapp.domain.model

import com.google.api.services.gmail.model.Message

data class Messages (
    val messages: List<Message>,
    val nextPageToken: String
)