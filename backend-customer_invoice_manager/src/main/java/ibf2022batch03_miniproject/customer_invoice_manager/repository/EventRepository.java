package ibf2022batch03_miniproject.customer_invoice_manager.repository;

import java.util.Collection;

import ibf2022batch03_miniproject.customer_invoice_manager.enumeration.EventType;
import ibf2022batch03_miniproject.customer_invoice_manager.model.UserEvent;

public interface EventRepository {
    
    Collection<UserEvent> getEventsByUserId(Long userId);

    void addUserEvent(String email, EventType eventType, String device, String ipAddress);

    void addUserEvent(Long userId, EventType eventType, String device, String ipAddress);
}
