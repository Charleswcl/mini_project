package ibf2022batch03_miniproject.customer_invoice_manager.service;

import ibf2022batch03_miniproject.customer_invoice_manager.enumeration.VerificationType;

public interface EmailService {

    void sendVerificationEmail(String firstName, String email, String verificationUrl, VerificationType verificationType);

}
