package ibf2022batch03_miniproject.customer_invoice_manager.service.Implementatoin;

import java.util.Collection;
import org.springframework.stereotype.Service;

import ibf2022batch03_miniproject.customer_invoice_manager.model.Role;
import ibf2022batch03_miniproject.customer_invoice_manager.repository.RoleRepository;
import ibf2022batch03_miniproject.customer_invoice_manager.service.RoleService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService{

    private final RoleRepository<Role> roleRepository;

    @Override
    public Role getRoleByUserId(Long id) {
        return roleRepository.getRoleByUserId(id);
    }

    @Override
    public Collection<Role> getRoles() {
        return roleRepository.list();
    }
    
}
