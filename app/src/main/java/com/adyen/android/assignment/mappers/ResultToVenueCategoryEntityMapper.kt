package com.adyen.android.assignment.mappers

import com.adyen.android.assignment.api.model.Result
import com.adyen.android.assignment.db.VenueCategoryEntity

class ResultToVenueCategoryEntityMapper {
  fun map(result: Result): VenueCategoryEntity {
    val category = result.categories.firstOrNull()
    return VenueCategoryEntity(
      id = category?.id ?: "",
      icon = category?.let {
        it.icon.prefix + "bg_88" + it.icon.suffix
      } ?: "",
      type = category?.name ?: ""
    )
  }
}