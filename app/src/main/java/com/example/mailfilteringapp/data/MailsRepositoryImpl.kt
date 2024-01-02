package com.example.mailfilteringapp.data

import com.example.mailfilteringapp.domain.MailsRepository
import com.example.mailfilteringapp.domain.model.Result
import com.google.api.services.gmail.model.Label
import com.google.api.services.gmail.model.Message

class MailsRepositoryImpl(private val gmailService: GmailService): MailsRepository {
    override suspend fun getMessages(labelId: String): Result<List<Message>> {
        return try {
            val messages = gmailService.getMessages("me", labelId)
            if (messages.isNotEmpty()) {
                Result.Success(messages)
            } else
            {
                Result.Error("Произошла ошибка")
            }
        } catch (ex: Exception) {
            Result.Error("Произошла ошибка")
        }
    }

    override suspend fun getLabels(user: String): Result<List<Label>> {
        return try {
            val labels = gmailService.getLabels(user)
            if (labels.isNotEmpty()) {
                Result.Success(labels)
            } else
            {
                Result.Error("Произошла ошибка")
            }
        } catch (ex: Exception) {
            Result.Error("Произошла ошибка")
        }
    }
}