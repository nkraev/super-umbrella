package com.adyen.android.assignment

import com.adyen.android.assignment.money.Bill
import com.adyen.android.assignment.money.Change
import com.adyen.android.assignment.money.Coin
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Before
import org.junit.Test

class CashRegisterTest {

    private lateinit var cashRegister: CashRegister
    private lateinit var registry: Change

    @Before
    fun setUp() {
        // Initialize the registry with some initial money for testing
        registry = Change()
        registry.add(Bill.FIVE_HUNDRED_EURO, 5)
            .add(Bill.TWO_HUNDRED_EURO, 5)
            .add(Bill.ONE_HUNDRED_EURO, 5)
            .add(Bill.FIFTY_EURO, 5)
            .add(Bill.TWENTY_EURO, 10)
            .add(Bill.TEN_EURO, 10)
            .add(Bill.FIVE_EURO, 20)

        cashRegister = CashRegister(registry)
    }

    @Test
    fun `performTransaction with valid input returns correct change`() {
        val price = 70_00L // 70 Euro in minor value
        val amountPaid = Change().apply {
            add(Bill.TWENTY_EURO, 5) // 100 EUR paid
        }
        val expectedChange = Change().apply {
            add(Bill.TWENTY_EURO, 1)
            add(Bill.TEN_EURO, 1)
        }

        val change = cashRegister.performTransaction(price, amountPaid)

        assertEquals(expectedChange.total, change.total)
        // verify each element of change
        expectedChange.getElements().forEach { element ->
            assertEquals(expectedChange.getCount(element), change.getCount(element))
        }
    }

    @Test
    fun `performTransaction with exact amount paid returns no change`() {
        val price = 100_00L // 100 Euro in minor value
        val amountPaid = Change().apply {
            add(Bill.ONE_HUNDRED_EURO, 1)
        }
        val expectedChange = Change()

        val change = cashRegister.performTransaction(price, amountPaid)

        assertEquals(expectedChange.total, change.total)
        // verify each element of change
        expectedChange.getElements().forEach { element ->
            assertEquals(expectedChange.getCount(element), change.getCount(element))
        }
    }

    @Test
    fun `performTransaction with negative price throws TransactionException`() {
        val price = -100_00L // -100 Euro in minor value
        val amountPaid = Change().apply {
            add(Bill.ONE_HUNDRED_EURO, 2)
        }

        val exception = assertThrows(CashRegister.TransactionException::class.java) {
            cashRegister.performTransaction(price, amountPaid)
        }

        assertEquals("E000: Price cannot be negative", exception.message)
    }

    @Test
    fun `performTransaction with insufficient amount paid throws TransactionException`() {
        val price = 200_00L // 200 Euro in minor value
        val amountPaid = Change().apply {
            add(Bill.ONE_HUNDRED_EURO, 1)
        }

        val exception = assertThrows(CashRegister.TransactionException::class.java) {
            cashRegister.performTransaction(price, amountPaid)
        }

        assertEquals("E001: Amount paid is less than the price", exception.message)
    }

    @Test
    fun `performTransaction with insufficient change in register throws TransactionException`() {
        // Deplete the registry first
        registry.remove(Bill.FIVE_EURO, 20)

        val price = 135_00L // 130 Euro in minor value
        val amountPaid = Change().apply {
            add(Bill.ONE_HUNDRED_EURO, 2)
        }

        val exception = assertThrows(CashRegister.TransactionException::class.java) {
            cashRegister.performTransaction(price, amountPaid)
        }

        assertEquals("E002: Not enough change in the register", exception.message)
    }

    @Test
    fun `performTransaction handles multiple denominations correctly`() {
        registry
            .add(Coin.ONE_EURO, 3)
        val price = 173_00L // 173 Euro in minor value
        val amountPaid = Change().apply {
            add(Bill.ONE_HUNDRED_EURO, 2)
        }
        val expectedChange = Change().apply {
            add(Bill.TWENTY_EURO, 1)
            add(Bill.FIVE_EURO, 1)
            add(Coin.ONE_EURO, 2)
        }

        val change = cashRegister.performTransaction(price, amountPaid)

        assertEquals(expectedChange.total, change.total)
        expectedChange.getElements().forEach { element ->
            assertEquals(expectedChange.getCount(element), change.getCount(element))
        }
    }

    @Test
    fun `performTransaction updates registry correctly`() {
        val price = 70_00L // 70 Euro in minor value
        val amountPaid = Change().apply {
            add(Bill.ONE_HUNDRED_EURO, 1)
        }

        cashRegister.performTransaction(price, amountPaid)

        //verify that we added 1*100 to registry and removed 1*20 and 1*10
        assertEquals(6, registry.getCount(Bill.ONE_HUNDRED_EURO))
        assertEquals(9, registry.getCount(Bill.TWENTY_EURO))
        assertEquals(9, registry.getCount(Bill.TEN_EURO))
    }
}