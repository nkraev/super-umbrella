package com.adyen.android.assignment.api.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ResponseWrapper(
    val results: List<Result>?,
)
