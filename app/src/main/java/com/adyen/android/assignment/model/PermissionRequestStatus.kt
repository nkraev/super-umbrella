package com.adyen.android.assignment.model

enum class PermissionRequestStatus(val value: String) {
  INITIAL("initial"),
  GRANTED("granted"),
  DENIED("denied"),
  SHOW_RATIONALE("show_rationale");

  companion object {
    fun fromValue(value: String): PermissionRequestStatus {
      return entries.firstOrNull { it.value == value } ?: INITIAL
    }
  }
}