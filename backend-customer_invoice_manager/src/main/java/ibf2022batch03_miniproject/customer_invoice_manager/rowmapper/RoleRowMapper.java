package ibf2022batch03_miniproject.customer_invoice_manager.rowmapper;

import org.springframework.jdbc.core.RowMapper;

import ibf2022batch03_miniproject.customer_invoice_manager.model.Role;

import java.sql.ResultSet;
import java.sql.SQLException;

public class RoleRowMapper implements RowMapper<Role> {
    @Override
    public Role mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        return Role.builder()
                .id(resultSet.getLong("id"))
                .name(resultSet.getString("name"))
                .permission(resultSet.getString("permission"))
                .build();
    }
}
