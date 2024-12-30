package loyalty.service.query.services;

import lombok.AllArgsConstructor;
import loyalty.service.core.events.AbstractBusinessEvent;
import loyalty.service.core.events.BusinessDeletedEvent;
import loyalty.service.core.events.BusinessEnrolledEvent;
import loyalty.service.core.events.BusinessNameChangedEvent;
import loyalty.service.core.events.loyalty.bank.AllPointsExpiredEvent;
import loyalty.service.core.events.loyalty.bank.LoyaltyBankCreatedEvent;
import loyalty.service.core.events.loyalty.bank.LoyaltyBankDeletedEvent;
import loyalty.service.core.events.account.*;
import loyalty.service.core.events.loyalty.bank.AbstractLoyaltyBankEvent;
import loyalty.service.core.events.loyalty.bank.transactions.*;
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
        Optional<ActivityLogEntryEntity> activityLogEntryEntityOptional =
                activityLogRepository.findByRequestIdAndActivityLogType(event.getRequestId(), ActivityLogType.ACCOUNT);

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

    public void saveActivityLogEntryFromEvent(AbstractLoyaltyBankEvent event, Instant timestamp) {
        Optional<ActivityLogEntryEntity> activityLogEntryEntityOptional =
                activityLogRepository.findByRequestIdAndActivityLogType(event.getRequestId(), ActivityLogType.LOYALTY_BANK);

        ActivityLogEntryEntity activityLogEntry;

        if (activityLogEntryEntityOptional.isPresent()) {
            activityLogEntry = activityLogEntryEntityOptional.get();
        } else {
            activityLogEntry = new ActivityLogEntryEntity();
            activityLogEntry.setRequestId(event.getRequestId());
            activityLogEntry.setTimestamp(timestamp);
            activityLogEntry.setActor(Actor.USER);
            activityLogEntry.setActivityLogType(ActivityLogType.LOYALTY_BANK);
            activityLogEntry.setActivityLogId(event.getLoyaltyBankId());
        }

        // TODO: show points in message and save messages as a constant
        if (event instanceof LoyaltyBankCreatedEvent) {
            activityLogEntry.getMessages().add("Loyalty Bank created");
        } else if (event instanceof LoyaltyBankDeletedEvent) {
            activityLogEntry.getMessages().add("Loyalty Bank deleted");
        } else if (event instanceof AllPointsExpiredEvent) {
            activityLogEntry.getMessages().add("All points expired for Loyalty Bank");
            activityLogEntry.setActor(Actor.SYSTEM);
        } else if (event instanceof AuthorizedTransactionCreatedEvent) {
            activityLogEntry.getMessages().add("Authorize transaction created");
        } else if (event instanceof AwardedTransactionCreatedEvent) {
            activityLogEntry.getMessages().add("Awarded transaction created");
        } else if (event instanceof CapturedTransactionCreatedEvent) {
            activityLogEntry.getMessages().add("Captured transaction created");
        } else if (event instanceof EarnedTransactionCreatedEvent) {
            activityLogEntry.getMessages().add("Earned transaction created");
        } else if (event instanceof ExpiredTransactionCreatedEvent expiredTransactionCreatedEvent) {
            activityLogEntry.getMessages().add(String.format(
                    "%d points expired from %s transaction",
                    expiredTransactionCreatedEvent.getPoints(),
                    expiredTransactionCreatedEvent.getTargetTransactionId()
            ));
        } else if (event instanceof PendingTransactionCreatedEvent) {
            activityLogEntry.getMessages().add("Pending transaction created");
        } else if (event instanceof VoidTransactionCreatedEvent) {
            activityLogEntry.getMessages().add("Void transaction created");
        } else {
            throw new IllegalArgumentException("Invalid event type");
        }

        activityLogRepository.save(activityLogEntry);
        LOGGER.info(Markers.append(REQUEST_ID, event.getRequestId()),
                "Activity log entry saved for {} on {} event",
                event.getLoyaltyBankId(),
                event.getClass().getSimpleName());
    }

    public void deleteActivityLogEntries(LoyaltyBankDeletedEvent event) {
        activityLogRepository.deleteAllByActivityLogId(event.getLoyaltyBankId());
        LOGGER.info(Markers.append(REQUEST_ID, event.getRequestId()), "Activity log entries deleted for {} on {} event", event.getAccountId(), event.getClass().getSimpleName());
    }

    public void saveActivityLogEntryFromEvent(AbstractBusinessEvent event, Instant timestamp) {
        Optional<ActivityLogEntryEntity> activityLogEntryEntityOptional =
                activityLogRepository.findByRequestIdAndActivityLogType(event.getRequestId(), ActivityLogType.BUSINESS);

        ActivityLogEntryEntity activityLogEntry;

        if (activityLogEntryEntityOptional.isPresent()) {
            activityLogEntry = activityLogEntryEntityOptional.get();
        } else {
            activityLogEntry = new ActivityLogEntryEntity();
            activityLogEntry.setRequestId(event.getRequestId());
            activityLogEntry.setTimestamp(timestamp);
            activityLogEntry.setActor(Actor.USER);
            activityLogEntry.setActivityLogType(ActivityLogType.ACCOUNT);
            activityLogEntry.setActivityLogId(event.getBusinessId());
        }

        // TODO: show old and new state in message and save messages as a constant
        if (event instanceof BusinessEnrolledEvent) {
            activityLogEntry.getMessages().add("Business enrolled");
        } else if (event instanceof BusinessNameChangedEvent) {
            activityLogEntry.getMessages().add("Business Name changed");
        } else {
            throw new IllegalArgumentException("Invalid event type");
        }

        activityLogRepository.save(activityLogEntry);
        LOGGER.info(Markers.append(REQUEST_ID, event.getRequestId()),
                "Activity log entry saved for {} on {} event",
                event.getBusinessId(),
                event.getClass().getSimpleName());
    }

    public void deleteActivityLogEntries(BusinessDeletedEvent event) {
        activityLogRepository.deleteAllByActivityLogId(event.getBusinessId());
        LOGGER.info(Markers.append(REQUEST_ID, event.getRequestId()), "Activity log entries deleted for {} on {} event", event.getBusinessId(), event.getClass().getSimpleName());
    }
}
