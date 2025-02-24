package com.adyen.android.assignment.api.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Result(
    val categories: List<Category>,
    val distance: Int,
    val geocodes: GeoCode?,
    val location: Location,
    val name: String,
    val timezone: String,
)