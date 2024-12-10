package loyalty.service.query.populators;

import loyalty.service.core.events.AbstractEvent;
import loyalty.service.core.events.AccountCreatedEvent;
import loyalty.service.core.events.AccountUpdatedEvent;
import loyalty.service.query.data.entities.ActivityLogEntryEntity;
import loyalty.service.query.data.enums.ActivityLogType;
import loyalty.service.query.data.enums.Actor;

import java.time.Instant;

public class ActivityLogPopulator {
    public static ActivityLogEntryEntity createActivityLogEntryFromEvent(AbstractEvent event, Instant timestamp) {
        ActivityLogEntryEntity activityLogEntry = new ActivityLogEntryEntity();

        activityLogEntry.setRequestId(event.getRequestId());
        activityLogEntry.setTimestamp(timestamp);
        activityLogEntry.setActor(Actor.USER);

        switch (event) {
            case AccountCreatedEvent accountCreatedEvent -> {
                activityLogEntry.setActivityLogType(ActivityLogType.ACCOUNT);
                activityLogEntry.setActivityLogId(accountCreatedEvent.getAccountId());
                activityLogEntry.getMessages().add("Account created");
            }
            case AccountUpdatedEvent accountUpdatedEvent -> {
                activityLogEntry.setActivityLogType(ActivityLogType.ACCOUNT);
                activityLogEntry.setActivityLogId(accountUpdatedEvent.getAccountId());
                activityLogEntry.getMessages().add("Account updated");
            }
            default -> {
                throw new IllegalArgumentException("Invalid event type");
            }
        }

        return activityLogEntry;
    }
}
