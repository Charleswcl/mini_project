package ibf2022batch03_miniproject.customer_invoice_manager.repository;

import java.util.Collection;

import ibf2022batch03_miniproject.customer_invoice_manager.model.Role;

public interface RoleRepository<T extends Role> {
    T create(T data);

    Collection<T> list();

    T get(Long id);

    T update(T data);

    Boolean delete(Long id);

    void addRoleToUser(Long userId, String roleName);

    Role getRoleByUserId(Long userId);

    Role getRoleByUserEmail(String email);

    void updateUserRole(Long userId, String roleName);
}
