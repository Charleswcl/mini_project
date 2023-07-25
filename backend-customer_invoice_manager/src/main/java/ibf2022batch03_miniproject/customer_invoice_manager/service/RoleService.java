package ibf2022batch03_miniproject.customer_invoice_manager.service;

import java.util.Collection;

import ibf2022batch03_miniproject.customer_invoice_manager.model.Role;

public interface RoleService {
    
    Role getRoleByUserId(Long id);
    Collection<Role> getRoles();
}
