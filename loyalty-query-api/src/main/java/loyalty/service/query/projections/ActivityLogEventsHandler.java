package loyalty.service.query.projections;

import loyalty.service.core.events.business.AbstractBusinessEvent;
import loyalty.service.core.events.business.BusinessDeletedEvent;
import loyalty.service.core.events.loyalty.bank.AbstractLoyaltyBankEvent;
import loyalty.service.core.events.loyalty.bank.LoyaltyBankDeletedEvent;
import loyalty.service.core.events.account.AbstractAccountEvent;
import loyalty.service.core.events.account.AccountDeletedEvent;
import loyalty.service.query.services.ActivityLogService;
import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.eventhandling.Timestamp;
import org.axonframework.messaging.interceptors.ExceptionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.Instant;

import static loyalty.service.core.constants.DomainConstants.ACTIVITY_LOG_GROUP;

@Component
@ProcessingGroup(ACTIVITY_LOG_GROUP)
public class ActivityLogEventsHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(ActivityLogEventsHandler.class);

    private final ActivityLogService activityLogService;

    public ActivityLogEventsHandler(ActivityLogService activityLogService) {
        this.activityLogService = activityLogService;
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
    public void on(AbstractAccountEvent event, @Timestamp Instant timestamp) {
        activityLogService.saveActivityLogEntryFromEvent(event, timestamp);
    }

    @EventHandler
    public void on(AccountDeletedEvent event) {
        activityLogService.deleteActivityLogEntries(event);
    }

    @EventHandler
    public void on(AbstractLoyaltyBankEvent event, @Timestamp Instant timestamp) {
        activityLogService.saveActivityLogEntryFromEvent(event, timestamp);
    }

    @EventHandler
    public void on(LoyaltyBankDeletedEvent event) {
        activityLogService.deleteActivityLogEntries(event);
    }

    @EventHandler
    public void on(AbstractBusinessEvent event, @Timestamp Instant timestamp) {
        activityLogService.saveActivityLogEntryFromEvent(event, timestamp);
    }

    @EventHandler
    public void on(BusinessDeletedEvent event) {
        activityLogService.deleteActivityLogEntries(event);
    }
}
