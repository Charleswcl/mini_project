package ibf2022batch03_miniproject.customer_invoice_manager.event;

import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

import ibf2022batch03_miniproject.customer_invoice_manager.enumeration.EventType;

@Getter
@Setter
    public class NewUserEvent extends ApplicationEvent{
        private EventType type;
        private String email;

        public NewUserEvent(String email, EventType type) {
            super(email);
            this.type = type;
            this.email = email;
        }
    }
