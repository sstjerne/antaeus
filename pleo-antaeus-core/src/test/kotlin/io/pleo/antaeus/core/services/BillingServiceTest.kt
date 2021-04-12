package io.pleo.antaeus.core.services

import io.mockk.every
import io.mockk.mockk
import io.pleo.antaeus.core.exceptions.NetworkException
import io.pleo.antaeus.core.external.PaymentProvider
import io.pleo.antaeus.data.AntaeusDal
import io.pleo.antaeus.models.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class BillingServiceTest {


    private val dal = mockk<AntaeusDal> {

        var invoices =  mutableListOf<Invoice>(
                Invoice(id = 6, customerId = 3, amount = Money(BigDecimal.TEN, Currency.EUR), status = InvoiceStatus.PENDING),
                Invoice(id = 7, customerId = 3, amount = Money(BigDecimal.TEN, Currency.EUR), status = InvoiceStatus.PAID),
                Invoice(id = 8, customerId = 4, amount = Money(BigDecimal.TEN, Currency.EUR), status = InvoiceStatus.PENDING),
                Invoice(id = 9, customerId = 4, amount = Money(BigDecimal.TEN, Currency.EUR), status = InvoiceStatus.PENDING),
                Invoice(id = 10, customerId = 5, amount = Money(BigDecimal.TEN, Currency.EUR), status = InvoiceStatus.PENDING)

        )
        every { fetchInvoices() } returns invoices

        every { fetchInvoice(any()) } answers {
            val id = firstArg<Int>()
            invoices.filter { invoice ->  invoice.id == id}.first()
        }

        every { updateInvoiceStatus(any(), any()) } answers {
            var invo = invoices.filter { invoice ->  invoice.id == firstArg<Int>() }.first()
            val ninv = invo.copy(status = InvoiceStatus.PAID)
            invoices.remove(invo)
            invoices.add(ninv)
        }

        every { fetchInvoicesByStatus(any()) } answers {
            val inv = firstArg<InvoiceStatus>()
            invoices.filter { invoice ->  invoice.status == inv}
        }
    }

    private val paymentProvider = mockk<PaymentProvider> ()

    private val billingService = BillingService(invoiceService = InvoiceService(dal = dal), paymentProvider = paymentProvider)

    private val invoiceService = InvoiceService(dal = dal)


    @Test
    fun `network exception`() {
        every { paymentProvider.charge(any()) } throws NetworkException()

        billingService.chargePendingInvoices()

        val invoicesPaid = invoiceService.fetchInvoicesByStatus(InvoiceStatus.PAID)
        assertEquals(1, invoicesPaid.size)
        val invoicesPending = invoiceService.fetchInvoicesByStatus(InvoiceStatus.PENDING)
        assertEquals(4, invoicesPending.size)
    }

    @Test
    fun `successfully charge`() {
        every { paymentProvider.charge(any()) } returns true

        billingService.chargePendingInvoices()

        val invoicesPaid = invoiceService.fetchInvoicesByStatus(InvoiceStatus.PAID)
        assertEquals(5, invoicesPaid.size)
        val invoicesPending = invoiceService.fetchInvoicesByStatus(InvoiceStatus.PENDING)
        assertEquals(0, invoicesPending.size)

    }



}