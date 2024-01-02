package com.example.mailfilteringapp

import android.app.Application
import com.example.mailfilteringapp.data.GmailService
import com.example.mailfilteringapp.data.MailsRepositoryImpl
import com.example.mailfilteringapp.domain.MailsRepository
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.util.ExponentialBackOff
import com.google.api.services.gmail.GmailScopes

class App : Application() {
    lateinit var mCredential: GoogleAccountCredential
    lateinit var gmailService: GmailService
    lateinit var mailsRepository: MailsRepository
    private val REQUEST_CODE_EMAIL = 1
    private val SCOPE = setOf(
        GmailScopes.GMAIL_LABELS,
        GmailScopes.GMAIL_READONLY,
        GmailScopes.GMAIL_MODIFY
    )

    companion object {
        lateinit var instance: App
            private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        mCredential = GoogleAccountCredential.usingOAuth2(
            applicationContext, SCOPE
        ).setBackOff(ExponentialBackOff())
        gmailService = GmailService(mCredential)
        mailsRepository = MailsRepositoryImpl(gmailService)
    }
}
