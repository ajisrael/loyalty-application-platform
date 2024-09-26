package loyalty.service.command.projections;

import lombok.AllArgsConstructor;
import loyalty.service.command.data.entities.AccountLookupEntity;
import loyalty.service.command.data.repositories.AccountLookupRepository;
import loyalty.service.core.events.AccountCreatedEvent;
import loyalty.service.core.events.AccountDeletedEvent;
import loyalty.service.core.events.AccountUpdatedEvent;
import loyalty.service.core.utils.MarkerGenerator;
import net.logstash.logback.marker.Markers;
import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import static loyalty.service.core.constants.DomainConstants.REQUEST_ID;
import static loyalty.service.core.constants.ExceptionMessages.ACCOUNT_WITH_ID_DOES_NOT_EXIST;
import static loyalty.service.core.constants.LogMessages.*;
import static loyalty.service.core.utils.Helper.throwExceptionIfEntityDoesNotExist;

@Component
@AllArgsConstructor
@ProcessingGroup("account-lookup-group")
public class AccountLookupEventsHandler {

    private AccountLookupRepository accountLookupRepository;

    private static final Logger LOGGER = LoggerFactory.getLogger(AccountLookupEventsHandler.class);

    @EventHandler
    public void on(AccountCreatedEvent event) {
        accountLookupRepository.save(
                new AccountLookupEntity(
                        event.getAccountId(),
                        event.getEmail()
                )
        );

        LOGGER.info(Markers.append(REQUEST_ID, event.getRequestId()), ACCOUNT_SAVED_IN_LOOKUP_DB, event.getAccountId());
    }

    @EventHandler
    public void on(AccountUpdatedEvent event) {
        AccountLookupEntity accountLookupEntity = accountLookupRepository.findByAccountId(event.getAccountId());
        throwExceptionIfEntityDoesNotExist(accountLookupEntity, String.format(ACCOUNT_WITH_ID_DOES_NOT_EXIST, event.getAccountId()));
        BeanUtils.copyProperties(event, accountLookupEntity);
        accountLookupRepository.save(accountLookupEntity);

        LOGGER.info(Markers.append(REQUEST_ID, event.getRequestId()), ACCOUNT_UPDATED_IN_LOOKUP_DB, event.getAccountId());
    }

    @EventHandler
    public void on(AccountDeletedEvent event) {
        AccountLookupEntity accountLookupEntity = accountLookupRepository.findByAccountId(event.getAccountId());
        throwExceptionIfEntityDoesNotExist(accountLookupEntity, String.format(ACCOUNT_WITH_ID_DOES_NOT_EXIST, event.getAccountId()));
        accountLookupRepository.delete(accountLookupEntity);

        LOGGER.info(Markers.append(REQUEST_ID, event.getRequestId()), ACCOUNT_DELETED_FROM_LOOKUP_DB, event.getAccountId());
    }
}
