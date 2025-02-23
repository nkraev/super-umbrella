package com.adyen.android.assignment.money

object RandomMoneyGenerator : MoneyGenerator {
  // this is in minor monetary units, 30000 is 300.00 EUR
  override fun generateRandomAmount(): Long {
    return (10000L..30000L).random()
  }
}