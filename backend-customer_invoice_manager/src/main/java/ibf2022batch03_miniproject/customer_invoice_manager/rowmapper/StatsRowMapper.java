package ibf2022batch03_miniproject.customer_invoice_manager.rowmapper;

import org.springframework.jdbc.core.RowMapper;
import ibf2022batch03_miniproject.customer_invoice_manager.model.Stats;

import java.sql.ResultSet;
import java.sql.SQLException;

public class StatsRowMapper implements RowMapper<Stats> {
    @Override
    public Stats mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        return Stats.builder()
                .totalCustomers(resultSet.getInt("total_customers"))
                .totalInvoices(resultSet.getInt("total_invoices"))
                .totalBilled(resultSet.getDouble("total_billed"))
                .build();
    }
}
