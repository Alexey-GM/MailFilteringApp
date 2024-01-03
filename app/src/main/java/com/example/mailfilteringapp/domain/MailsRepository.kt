package com.example.mailfilteringapp.domain

import com.example.mailfilteringapp.domain.model.Messages
import com.example.mailfilteringapp.domain.model.MessagesToFilter
import com.example.mailfilteringapp.domain.model.Result
import com.google.api.services.gmail.model.Label
import com.google.api.services.gmail.model.Message

interface MailsRepository {
    suspend fun getMessages(labelId: String, nextPage: String?): Result<Messages>
    suspend fun getLabels(user: String): Result<List<Label>>
    suspend fun createLabel(user: String, name: String, labelColor:String): Result<String>
    suspend fun addLabelsToMessages(user: String, messagesToFilter: List<MessagesToFilter>): Result<String>
}