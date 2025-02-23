package com.adyen.android.assignment.api.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Icon(
    val prefix: String,
    val suffix: String
)
