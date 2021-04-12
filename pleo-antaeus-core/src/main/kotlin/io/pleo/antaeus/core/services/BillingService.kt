package io.pleo.antaeus.core.services

import io.pleo.antaeus.core.exceptions.CurrencyMismatchException
import io.pleo.antaeus.core.exceptions.CustomerNotFoundException
import io.pleo.antaeus.core.exceptions.InvoiceNotFoundException
import io.pleo.antaeus.core.exceptions.NetworkException
import io.pleo.antaeus.core.external.PaymentProvider
import io.pleo.antaeus.models.InvoiceStatus
import io.pleo.antaeus.models.Invoice
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

class BillingService(
    private val paymentProvider: PaymentProvider,
    private val invoiceService: InvoiceService

) {
    /*
     * Charges all the pending invoices and return the list of paid invoices
     */
    fun chargePendingInvoices(): List<Invoice> {
        val invoices = invoiceService.fetchInvoicesByStatus(InvoiceStatus.PENDING)
        val paidInvoices: List<Invoice> = mutableListOf()
        mutableListOf<Invoice>()
        for (invoice in invoices) {
            try {
                var paid = paymentProvider.charge(invoice)
                if (paid) {
                    invoiceService.setInvoice(invoice.id, InvoiceStatus.PAID)
                    logger.info {"Invoice " + invoice.id + " successfully paid!"}
                } else {
                    //TODO: Provide any other status as 'Failed' to avoid to process again, or provide another treat
                    logger.warn {"Invoice " + invoice.id + " cannot be paid because customer account balance " + invoice.customerId + "  did not allow the charge"}
                }
            } catch (e: CustomerNotFoundException) {
                logger.error(e) {"Invoice " + invoice.id + " cannot be paid because customer " + invoice.customerId + " doesn't found or not exist"}
            } catch (e: CurrencyMismatchException) {
                logger.error(e) {"Invoice " + invoice.id + " cannot be paid because currency doesn't match the one of the customer " + invoice.customerId}
            } catch (e: NetworkException) {
                logger.error(e) {"Invoice " + invoice.id + " cannot be paid because of a network issue"}
            } catch (e : InvoiceNotFoundException) {
                logger.error(e) {"Invoice " + invoice.id + " doesn't found or not exist"}
            }
        }
        return paidInvoices
    }

}
