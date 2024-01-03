package com.example.mailfilteringapp.data

import android.util.Log
import com.example.mailfilteringapp.R
import com.example.mailfilteringapp.domain.model.Messages
import com.example.mailfilteringapp.domain.model.MessagesToFilter
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.gmail.Gmail
import com.google.api.services.gmail.GmailScopes
import com.google.api.services.gmail.model.Label
import com.google.api.services.gmail.model.LabelColor
import com.google.api.services.gmail.model.ListMessagesResponse
import com.google.api.services.gmail.model.Message
import com.google.api.services.gmail.model.ModifyMessageRequest
import kotlin.random.Random

class GmailService(private val mCredential: GoogleAccountCredential) {
    private val service: Gmail

    init {
        val transport = com.google.api.client.http.javanet.NetHttpTransport()
        val jsonFactory = GsonFactory.getDefaultInstance()
        service = Gmail.Builder(transport, jsonFactory, mCredential)
            .setApplicationName("com.example.mailfilteringapp")
            .build()
    }

    fun getMessages(user: String, labelId: String, nextPage: String?): Messages {
        val listResponse: ListMessagesResponse = service.users().messages().list(user)
            .setLabelIds(listOf(labelId))
            .setPageToken(nextPage)
            .setMaxResults(10L)
            .execute()
        val messageIds: List<String> = listResponse.messages.map { it.id }
        val fullMessages: MutableList<Message> = mutableListOf()
        Log.i("listResponse", listResponse.toPrettyString())
        for (messageId in messageIds) {
            val fullMessage = service.users().messages().get(user, messageId).execute()
            fullMessages.add(fullMessage)
        }

        val nextPageTkn = listResponse.nextPageToken ?: ""
        return Messages(fullMessages, nextPageTkn)
    }

    fun getLabels(user: String): List<Label> {
        val listResponse = service.users().labels().list(user).execute()
        return listResponse.labels
    }

    fun createLabel(user: String, labelName: String, labelColor:String): Label {
        val newLabelColor = LabelColor()
        newLabelColor.backgroundColor = labelColor
        newLabelColor.textColor = "#000000"
        val newLabel = Label()
            .setName(labelName)
            .setLabelListVisibility("labelShow")
            .setMessageListVisibility("show")
            .setColor(newLabelColor)

        return service.users().labels().create(user, newLabel).execute()
    }
    fun addLabelsToMessages(user: String, messagesToFilter: List<MessagesToFilter>): Boolean {
        return try {
            for (messageToFilter in messagesToFilter) {
                val modifyMessageRequest = ModifyMessageRequest()
                modifyMessageRequest.addLabelIds = listOf(messageToFilter.labelId)
                for (messageId in messageToFilter.messageId) {
                    service.users().messages().modify(user, messageId, modifyMessageRequest).execute()
                }
            }
            true // Возвращает true, если все метки были успешно добавлены
        } catch (e: Exception) {
            false // Возвращает false, если произошла ошибка
        }
    }
}
