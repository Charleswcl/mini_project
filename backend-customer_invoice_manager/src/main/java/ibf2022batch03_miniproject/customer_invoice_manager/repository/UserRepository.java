package ibf2022batch03_miniproject.customer_invoice_manager.repository;

import java.util.Collection;

import org.springframework.web.multipart.MultipartFile;

import ibf2022batch03_miniproject.customer_invoice_manager.dto.UserDTO;
import ibf2022batch03_miniproject.customer_invoice_manager.form.UpdateForm;
import ibf2022batch03_miniproject.customer_invoice_manager.model.User;

public interface UserRepository<T extends User> {

    T create(T data);
    Collection<T> list(int page, int pageSize);
    T get(Long id);
    T update(T data);
    Boolean delete(Long id);

    User getUserByEmail(String email);
    void sendVerificationCode(UserDTO user);
    User verifyCode(String email, String code);
    void resetPassword(String email);
    T verifyPasswordKey(String key);
    void renewPassword(String key, String password, String confirmPassword);
    void renewPassword(Long userId, String password, String confirmPassword);
    T verifyAccountKey(String key);
    T updateUserDetails(UpdateForm user);
    void updatePassword(Long id, String currentPassword, String newPassword, String confirmNewPassword);
    void updateAccountSettings(Long userId, Boolean enabled, Boolean notLocked);
    User toggleMfa(String email);
    void updateImage(UserDTO user, MultipartFile image);
}
