package ibf2022batch03_miniproject.customer_invoice_manager.service.Implementatoin;

import java.util.Collection;

import org.springframework.stereotype.Service;

import ibf2022batch03_miniproject.customer_invoice_manager.enumeration.EventType;
import ibf2022batch03_miniproject.customer_invoice_manager.model.UserEvent;
import ibf2022batch03_miniproject.customer_invoice_manager.repository.EventRepository;
import ibf2022batch03_miniproject.customer_invoice_manager.service.EventService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    @Override
    public Collection<UserEvent> getEventsByUserId(Long userId) {
        return eventRepository.getEventsByUserId(userId);
    }

    @Override
    public void addUserEvent(String email, EventType eventType, String device, String ipAddress) {
        eventRepository.addUserEvent(email, eventType, device, ipAddress);
    }

    @Override
    public void addUserEvent(Long userId, EventType eventType, String device, String ipAddress) {

    }
}

