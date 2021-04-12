package io.pleo.antaeus.core.services

import io.mockk.every
import io.mockk.mockk
import io.pleo.antaeus.core.exceptions.InvoiceNotFoundException
import io.pleo.antaeus.data.AntaeusDal
import io.pleo.antaeus.models.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.math.BigDecimal

class InvoiceServiceTest {


    private val dal = mockk<AntaeusDal> {

        var invoices = mutableListOf<Invoice>(
                Invoice(id = 1, customerId = 1, amount = Money(BigDecimal.TEN, Currency.EUR), status = InvoiceStatus.PENDING),
                Invoice(id = 2, customerId = 1, amount = Money(BigDecimal.TEN, Currency.EUR), status = InvoiceStatus.PAID),
                Invoice(id = 3, customerId = 2, amount = Money(BigDecimal.TEN, Currency.EUR), status = InvoiceStatus.PENDING),
                Invoice(id = 4, customerId = 2, amount = Money(BigDecimal.TEN, Currency.EUR), status = InvoiceStatus.PENDING),
                Invoice(id = 5, customerId = 2, amount = Money(BigDecimal.TEN, Currency.EUR), status = InvoiceStatus.PENDING)
        )

        every { fetchInvoices() } returns invoices

        every { fetchInvoice(any()) } answers {
            val id = firstArg<Int>()
            val list = invoices.filter { invoice ->  invoice.id == id}
            if (list.isEmpty()){
                throw InvoiceNotFoundException(404)
            }
            list.first();
        }

        every { fetchInvoicesByStatus(any()) } answers {
            val inv = firstArg<InvoiceStatus>()
            invoices.filter { invoice ->  invoice.status == inv}
        }

        every { updateInvoiceStatus(any(), any()) } answers {
            val list = invoices.filter { invoice ->  invoice.id == firstArg<Int>() }
            if (list.isEmpty()){
                throw InvoiceNotFoundException(404)
            }
            val invo = list.first();
            val ninv = invo.copy(status = InvoiceStatus.PAID)
            invoices.remove(invo)
            invoices.add(ninv)
        }

    }

    private val invoiceService = InvoiceService(dal = dal)

    @Test
    fun `will throw if invoice is not found`() {
        assertThrows<InvoiceNotFoundException> {
            invoiceService.fetch(404)
        }
    }

    @Test
    fun `setInvoice throw if invoice not found`() {
        assertThrows<InvoiceNotFoundException> {
            invoiceService.setInvoice(404, InvoiceStatus.PAID)
        }
    }

    @Test
    fun `retrieve only pending invoices`() {
        val subject = invoiceService.fetchInvoicesByStatus(InvoiceStatus.PENDING)
        val expectedSubject = Invoice(id = 1, customerId = 1, amount = Money(BigDecimal.TEN, Currency.EUR), status = InvoiceStatus.PENDING)

        assertEquals(4, subject.size)
        assertEquals(expectedSubject, subject.first())
    }

    @Test
    fun `setInvoice update the status to PAID`() {
        val invoiceO = invoiceService.fetch(1)
        assertEquals(InvoiceStatus.PENDING, invoiceO.status)

        invoiceService.setInvoice(1, InvoiceStatus.PAID)

        val invoiceT = invoiceService.fetch(1)
        assertEquals(InvoiceStatus.PAID, invoiceT.status)

    }
}
