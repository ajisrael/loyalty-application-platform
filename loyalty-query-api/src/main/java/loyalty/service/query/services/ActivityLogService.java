package loyalty.service.query.services;

import lombok.AllArgsConstructor;
import loyalty.service.core.events.account.*;
import loyalty.service.query.data.entities.ActivityLogEntryEntity;
import loyalty.service.query.data.enums.ActivityLogType;
import loyalty.service.query.data.enums.Actor;
import loyalty.service.query.data.repositories.ActivityLogRepository;
import loyalty.service.query.projections.ActivityLogEventsHandler;
import net.logstash.logback.marker.Markers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Optional;

import static loyalty.service.core.constants.DomainConstants.REQUEST_ID;

@Component
@AllArgsConstructor
public class ActivityLogService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ActivityLogEventsHandler.class);

    private final ActivityLogRepository activityLogRepository;

    public void saveActivityLogEntryFromEvent(AbstractAccountEvent event, Instant timestamp) {
        Optional<ActivityLogEntryEntity> activityLogEntryEntityOptional = activityLogRepository.findByRequestId(event.getRequestId());

        ActivityLogEntryEntity activityLogEntry;

        if (activityLogEntryEntityOptional.isPresent()) {
            activityLogEntry = activityLogEntryEntityOptional.get();
        } else {
            activityLogEntry = new ActivityLogEntryEntity();
            activityLogEntry.setRequestId(event.getRequestId());
            activityLogEntry.setTimestamp(timestamp);
            activityLogEntry.setActor(Actor.USER);
            activityLogEntry.setActivityLogType(ActivityLogType.ACCOUNT);
            activityLogEntry.setActivityLogId(event.getAccountId());
        }

        // TODO: show old and new state in message and save messages as a constant
        if (event instanceof AccountCreatedEvent) {
            activityLogEntry.getMessages().add("Account created");
        } else if (event instanceof AccountFirstNameChangedEvent) {
            activityLogEntry.getMessages().add("First Name changed on account");
        } else if (event instanceof AccountLastNameChangedEvent) {
            activityLogEntry.getMessages().add("Last Name changed on account");
        } else if (event instanceof AccountEmailChangedEvent) {
            activityLogEntry.getMessages().add("Email changed on account");
        } else {
            throw new IllegalArgumentException("Invalid event type");
        }

        activityLogRepository.save(activityLogEntry);
        LOGGER.info(Markers.append(REQUEST_ID, event.getRequestId()),
                "Activity log entry saved for {} on {} event",
                event.getAccountId(),
                event.getClass().getSimpleName());
    }

    public void deleteActivityLogEntries(AccountDeletedEvent event) {
        activityLogRepository.deleteAllByActivityLogId(event.getAccountId());
        LOGGER.info(Markers.append(REQUEST_ID, event.getRequestId()), "Activity log entries deleted for {} on {} event", event.getAccountId(), event.getClass().getSimpleName());
    }
}
