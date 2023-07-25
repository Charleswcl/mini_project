package ibf2022batch03_miniproject.customer_invoice_manager.service;

import org.springframework.data.domain.Page;

import ibf2022batch03_miniproject.customer_invoice_manager.model.Customer;
import ibf2022batch03_miniproject.customer_invoice_manager.model.Invoice;
import ibf2022batch03_miniproject.customer_invoice_manager.model.Stats;

public interface CustomerService {
    
    Customer createCustomer(Customer customer);
    Customer updateCustomer(Customer customer);
    Page<Customer> getCustomers(int page, int size);
    Iterable<Customer> getCustomers();
    Customer getCustomer(Long id);
    Page<Customer> searchCustomers(String name, int page, int size);

    Invoice createInvoice(Invoice invoice);
    Page<Invoice> getInvoices(int page, int size);
    void addInvoiceToCustomer(Long id, Invoice invoice);
    Invoice getInvoice(Long id);
    Stats getStats();
}
