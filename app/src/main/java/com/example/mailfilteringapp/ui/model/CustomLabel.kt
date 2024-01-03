package com.example.mailfilteringapp.ui.model

import android.os.Parcelable
import com.google.api.services.gmail.model.Label
import kotlinx.parcelize.Parcelize
import java.io.Serializable

@Parcelize
data class CustomLabel(
    val labelId: String,
    val labelColor: String,
    val labelName: String,
): Serializable, Parcelable {
    override fun toString(): String {
        return labelName
    }
}


fun Label.toCustomLabel(): CustomLabel {
    return CustomLabel(
        labelId = this.id,
        labelColor = this.color.backgroundColor ?: "",
        labelName = this.name
    )
}