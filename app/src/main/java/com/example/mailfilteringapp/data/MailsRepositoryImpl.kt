package com.example.mailfilteringapp.data

import android.util.Log
import com.example.mailfilteringapp.domain.MailsRepository
import com.example.mailfilteringapp.domain.model.Messages
import com.example.mailfilteringapp.domain.model.MessagesToFilter
import com.example.mailfilteringapp.domain.model.Result
import com.google.api.services.gmail.model.Label
import com.google.api.services.gmail.model.Message

class MailsRepositoryImpl(private val gmailService: GmailService): MailsRepository {
    override suspend fun getMessages(labelId: String, nextPage: String?): Result<Messages> {
        return try {
            val messages = gmailService.getMessages("me", labelId, nextPage)
            if (messages.messages.isNotEmpty()) {
                Result.Success(messages)
            } else
            {
                Result.Error("Прогружаемая страница пустая")
            }
        } catch (ex: Exception) {
            Result.Error("Произошла ошибка: ${ex.message.toString()}")
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

    override suspend fun createLabel(user: String, name: String, labelColor:String): Result<String> {
        return try {
            val label = gmailService.createLabel(user, name, labelColor)
            if (label.isNotEmpty()) {
                Result.Success("Метка создана успешно!")
            } else
            {
                Result.Error("Произошла ошибка")
            }
        } catch (ex: Exception) {
            Log.i("error label", ex.message.toString())
            Result.Error("Произошла ошибка ${ex.message.toString()}")
        }
    }

    override suspend fun addLabelsToMessages(
        user: String,
        messagesToFilter: List<MessagesToFilter>
    ): Result<String> {
        return try {
            val result = gmailService.addLabelsToMessages(user, messagesToFilter)
            if (result) {
                Result.Success("Сообщения отфильтрованы!")
            } else {
                Result.Error("Метки не были добавлены")
            }
        } catch (ex: Exception) {
            Log.i("error label", ex.message.toString())
            Result.Error("Произошла ошибка ${ex.message.toString()}")
        }
    }
}