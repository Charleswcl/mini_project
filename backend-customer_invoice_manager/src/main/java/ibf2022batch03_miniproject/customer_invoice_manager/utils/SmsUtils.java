package ibf2022batch03_miniproject.customer_invoice_manager.utils;

import org.springframework.beans.factory.annotation.Value;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

public class SmsUtils {

    @Value ("${FROM_NUMBER}")
    private static String FROM_NUMBER;

    @Value ("${ACC_SID}")
    private static String ACC_SID;

    @Value ("${TOKEN_KEY}")
    private static String TOKEN_KEY;

    public static void sendSMS(String to, String messageBody) {
        Twilio.init(ACC_SID, TOKEN_KEY);
        Message message  = Message.creator(new PhoneNumber("+65" + to), new PhoneNumber(FROM_NUMBER), messageBody).create();
        System.out.println(message);
    }
    
}
