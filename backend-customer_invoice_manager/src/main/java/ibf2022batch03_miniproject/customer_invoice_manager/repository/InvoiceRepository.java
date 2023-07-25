package ibf2022batch03_miniproject.customer_invoice_manager.repository;

import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import ibf2022batch03_miniproject.customer_invoice_manager.model.Invoice;

public interface InvoiceRepository extends PagingAndSortingRepository<Invoice, Long>, ListCrudRepository<Invoice, Long> {

}
