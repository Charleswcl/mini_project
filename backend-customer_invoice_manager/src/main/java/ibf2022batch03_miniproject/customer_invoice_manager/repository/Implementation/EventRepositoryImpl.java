package ibf2022batch03_miniproject.customer_invoice_manager.repository.Implementation;

import java.util.Collection;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import ibf2022batch03_miniproject.customer_invoice_manager.enumeration.EventType;
import ibf2022batch03_miniproject.customer_invoice_manager.model.UserEvent;
import ibf2022batch03_miniproject.customer_invoice_manager.repository.EventRepository;
import ibf2022batch03_miniproject.customer_invoice_manager.rowmapper.UserEventRowMapper;
import lombok.RequiredArgsConstructor;

import static ibf2022batch03_miniproject.customer_invoice_manager.query.EventQuery.*;
import static java.util.Map.of;

@Repository
@RequiredArgsConstructor
public class EventRepositoryImpl implements EventRepository {

    private final NamedParameterJdbcTemplate jdbc;

    @Override
    public Collection<UserEvent> getEventsByUserId(Long userId) {
        return jdbc.query(SELECT_EVENTS_BY_USER_ID_QUERY, of("id", userId), new UserEventRowMapper());
    }

    @Override
    public void addUserEvent(String email, EventType eventType, String device, String ipAddress) {
        jdbc.update(INSERT_EVENT_BY_USER_EMAIL_QUERY,
                of("email", email, "type", eventType.toString(), "device", device, "ipAddress", ipAddress));
    }

    @Override
    public void addUserEvent(Long userId, EventType eventType, String device, String ipAddress) {
    }
}
