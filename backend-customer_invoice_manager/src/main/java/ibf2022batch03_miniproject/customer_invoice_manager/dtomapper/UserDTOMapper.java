package ibf2022batch03_miniproject.customer_invoice_manager.dtomapper;

import org.springframework.beans.BeanUtils;

import ibf2022batch03_miniproject.customer_invoice_manager.dto.UserDTO;
import ibf2022batch03_miniproject.customer_invoice_manager.model.Role;
import ibf2022batch03_miniproject.customer_invoice_manager.model.User;

public class UserDTOMapper {
    public static UserDTO fromUser(User user) {
        UserDTO userDTO = new UserDTO();
        BeanUtils.copyProperties(user, userDTO);
        return userDTO;
    }

    public static UserDTO fromUser(User user, Role role) {
        UserDTO userDTO = new UserDTO();
        BeanUtils.copyProperties(user, userDTO);
        userDTO.setRoleName(role.getName());
        userDTO.setPermissions(role.getPermission());
        return userDTO;
    }

    public static User toUser(UserDTO userDTO) {
        User user = new User();
        BeanUtils.copyProperties(userDTO, user);
        return user;
    }
}
