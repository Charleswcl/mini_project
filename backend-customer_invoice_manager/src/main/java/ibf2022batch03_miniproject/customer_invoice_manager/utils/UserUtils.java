package ibf2022batch03_miniproject.customer_invoice_manager.utils;

import org.springframework.security.core.Authentication;

import ibf2022batch03_miniproject.customer_invoice_manager.dto.UserDTO;
import ibf2022batch03_miniproject.customer_invoice_manager.model.UserPrincipal;

public class UserUtils {
    
    public static UserDTO getAuthenticatedUser(Authentication authentication) {
        return ((UserDTO) authentication.getPrincipal());
    }

    public static UserDTO getLoggedInUser(Authentication authentication) {
        return ((UserPrincipal) authentication.getPrincipal()).getUser();
    }
}
