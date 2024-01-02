package com.example.mailfilteringapp.data

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.gmail.Gmail
import com.google.api.services.gmail.GmailScopes
import com.google.api.services.gmail.model.Label
import com.google.api.services.gmail.model.ListMessagesResponse
import com.google.api.services.gmail.model.Message

class GmailService(private val mCredential: GoogleAccountCredential) {
    private val service: Gmail

    init {
        val transport = com.google.api.client.http.javanet.NetHttpTransport()
        val jsonFactory = GsonFactory.getDefaultInstance()
        service = Gmail.Builder(transport, jsonFactory, mCredential)
            .setApplicationName("com.example.mailfilteringapp")
            .build()
    }

    fun getMessages(user: String, labelId: String): List<Message> {
        val listResponse: ListMessagesResponse = service.users().messages().list(user)
            .setLabelIds(listOf(labelId))
            .setMaxResults(10L)
            .execute()
        val messageIds: List<String> = listResponse.messages.map { it.id }
        val fullMessages: MutableList<Message> = mutableListOf()

        for (messageId in messageIds) {
            val fullMessage = service.users().messages().get(user, messageId).execute()
            fullMessages.add(fullMessage)
        }

        return fullMessages
    }

    fun getLabels(user: String): List<Label> {
        val listResponse = service.users().labels().list(user).execute()
        return listResponse.labels
    }
}
