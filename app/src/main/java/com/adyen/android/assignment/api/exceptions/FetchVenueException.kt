package com.adyen.android.assignment.api.exceptions

class FetchVenueException : Exception() {
  override val message: String
    get() = "Failed to fetch venues"
}