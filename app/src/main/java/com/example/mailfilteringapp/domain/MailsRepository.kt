package com.example.mailfilteringapp.domain

import com.example.mailfilteringapp.domain.model.Result
import com.google.api.services.gmail.model.Label
import com.google.api.services.gmail.model.Message

interface MailsRepository {
    suspend fun getMessages(labelId: String): Result<List<Message>>
    suspend fun getLabels(user: String): Result<List<Label>>
}