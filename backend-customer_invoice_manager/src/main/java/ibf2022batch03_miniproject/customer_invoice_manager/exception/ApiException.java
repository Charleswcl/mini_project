package ibf2022batch03_miniproject.customer_invoice_manager.exception;

public class ApiException extends RuntimeException {
    public ApiException(String message) {
        super(message);
    }
}
