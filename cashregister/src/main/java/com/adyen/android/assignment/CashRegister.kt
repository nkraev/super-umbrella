package com.adyen.android.assignment

import com.adyen.android.assignment.money.Change

/**
 * The CashRegister class holds the logic for performing transactions.
 *
 * @param registry The change that the CashRegister is holding.
 */
class CashRegister(private val registry: Change) {
    /**
     * Performs a transaction for a product/products with a certain price and a given amount.
     *
     * @param price The price of the product(s).
     * @param amountPaid The amount paid by the shopper.
     *
     * @return The change for the transaction.
     *
     * @throws TransactionException If the transaction cannot be performed.
     */
    fun performTransaction(price: Long, amountPaid: Change): Change {
        // assuming the price is in minor value
        if (price < 0) {
            throw TransactionException("E000: Price cannot be negative")
        }

        if (amountPaid.total < price) {
            throw TransactionException("E001: Amount paid is less than the price")
        }

        // starting with adding amount paid to registry
        // e.g. if user pays with 5*20 for 70 price, we can immediately return 20
        addClientChangeToRegistry(amountPaid)

        return calculateChange(price, amountPaid)
    }

    // Time complexity of this method is O(n) where n is the number of elements in the registry
    // Memory complexity is O(1) - no additional memory is allocated
    // (That's actually even better than a DP solution, which would require O(n) memory)
    private fun calculateChange(price: Long, amountPaid: Change): Change {
        val elements = registry.getElements().reversed() // from the biggest to the lowest
        val change = Change() // this is the change that will be returned

        var i = 0
        var remainingChange = amountPaid.total - price
        while (i < elements.size) {
            val element = elements[i]
            // can be further optimized by checking if the element is bigger than the remaining change
            // but this will affect the readability
            val available = registry.getCount(element)
            // element=20, i need to return 100, i have 3*20
            // max is 3
            val needed = remainingChange / element.minorValue
            val maxGiven: Int = when {
                needed > Int.MAX_VALUE -> available // we won't have that much anyways
                needed < available -> needed.toInt()
                else -> available
            }
            if (maxGiven > 0) {
                change.add(element, maxGiven)
                registry.remove(element, maxGiven)
                remainingChange -= element.minorValue * maxGiven
            }
            i++
        }

        if (remainingChange > 0) {
            throw TransactionException("E002: Not enough change in the register")
        }

        return change
    }

    private fun addClientChangeToRegistry(amountPaid: Change) {
        amountPaid.getElements().forEach { element ->
            registry.add(element, amountPaid.getCount(element))
        }
    }

    class TransactionException(message: String, cause: Throwable? = null) : Exception(message, cause)
}
