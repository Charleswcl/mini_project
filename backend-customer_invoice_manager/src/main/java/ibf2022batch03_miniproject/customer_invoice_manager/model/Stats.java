package ibf2022batch03_miniproject.customer_invoice_manager.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Setter
@Getter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Stats {
    private int totalCustomers;
    private int totalInvoices;
    private double totalBilled;
}
