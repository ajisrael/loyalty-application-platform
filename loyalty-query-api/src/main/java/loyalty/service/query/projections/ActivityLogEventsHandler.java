package loyalty.service.query.projections;

import loyalty.service.core.events.AbstractEvent;
import loyalty.service.core.events.AccountCreatedEvent;
import loyalty.service.core.events.AccountDeletedEvent;
import loyalty.service.core.events.AccountUpdatedEvent;
import loyalty.service.query.data.entities.ActivityLogEntryEntity;
import loyalty.service.query.data.enums.ActivityLogType;
import loyalty.service.query.data.enums.Actor;
import loyalty.service.query.data.repositories.ActivityLogRepository;
import net.logstash.logback.marker.Markers;
import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.eventhandling.Timestamp;
import org.axonframework.messaging.interceptors.ExceptionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;

import static loyalty.service.core.constants.DomainConstants.ACTIVITY_LOG_GROUP;
import static loyalty.service.core.constants.DomainConstants.REQUEST_ID;
import static loyalty.service.query.populators.ActivityLogPopulator.createActivityLogEntryFromEvent;

@Component
@ProcessingGroup(ACTIVITY_LOG_GROUP)
public class ActivityLogEventsHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(ActivityLogEventsHandler.class);

    private final ActivityLogRepository activityLogRepository;

    public ActivityLogEventsHandler(ActivityLogRepository activityLogRepository) {
        this.activityLogRepository = activityLogRepository;
    }

    @ExceptionHandler(resultType = Exception.class)
    public void handle(Exception exception) throws Exception {
        throw exception;
    }

    @ExceptionHandler(resultType = IllegalArgumentException.class)
    public void handle(IllegalArgumentException exception) {
        LOGGER.error(exception.getLocalizedMessage());
    }

    @EventHandler
    public void on(AccountCreatedEvent event, @Timestamp Instant timestamp) {
        activityLogRepository.save(createActivityLogEntryFromEvent(event, timestamp));

        LOGGER.info(Markers.append(REQUEST_ID, event.getRequestId()), "Activity log entry saved for {} on {} event", event.getAccountId(), event.getClass().getSimpleName());
    }

    @EventHandler
    public void on(AccountUpdatedEvent event, @Timestamp Instant timestamp) {
        activityLogRepository.save(createActivityLogEntryFromEvent(event, timestamp));

        LOGGER.info(Markers.append(REQUEST_ID, event.getRequestId()), "Activity log entry saved for {} on {} event", event.getAccountId(), event.getClass().getSimpleName());
    }

    @EventHandler
    public void on(AccountDeletedEvent event, @Timestamp Instant timestamp) {
        activityLogRepository.deleteAllByActivityLogId(event.getAccountId());

        LOGGER.info(Markers.append(REQUEST_ID, event.getRequestId()), "Activity log entries deleted for {} on {} event", event.getAccountId(), event.getClass().getSimpleName());
    }
}
