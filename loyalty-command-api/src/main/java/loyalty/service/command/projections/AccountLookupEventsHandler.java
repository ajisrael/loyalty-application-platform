package loyalty.service.command.projections;

import loyalty.service.command.data.entities.AccountLookupEntity;
import loyalty.service.command.data.repositories.AccountLookupRepository;
import loyalty.service.core.exceptions.IllegalProjectionStateException;
import loyalty.service.core.events.AccountCreatedEvent;
import loyalty.service.core.events.AccountDeletedEvent;
import loyalty.service.core.events.AccountUpdatedEvent;
import loyalty.service.core.utils.MarkerGenerator;
import net.logstash.logback.marker.Markers;
import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.messaging.interceptors.ExceptionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.springframework.beans.BeanUtils;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.SmartValidator;
import org.springframework.validation.annotation.Validated;

import static loyalty.service.core.constants.DomainConstants.REQUEST_ID;
import static loyalty.service.core.constants.ExceptionMessages.ACCOUNT_WITH_ID_DOES_NOT_EXIST;
import static loyalty.service.core.constants.LogMessages.*;
import static loyalty.service.core.utils.Helper.throwExceptionIfEntityDoesNotExist;

@Component
@Validated
@ProcessingGroup("account-lookup-group")
@Order(1)
public class AccountLookupEventsHandler {

    private final AccountLookupRepository accountLookupRepository;
    private final SmartValidator validator;
    private Marker marker = null;

    private static final Logger LOGGER = LoggerFactory.getLogger(AccountLookupEventsHandler.class);

    public AccountLookupEventsHandler(AccountLookupRepository accountLookupRepository, SmartValidator validator) {
        this.accountLookupRepository = accountLookupRepository;
        this.validator = validator;
    }

    @ExceptionHandler(resultType = IllegalArgumentException.class)
    public void handle(IllegalArgumentException exception) {
        LOGGER.error(marker, exception.getLocalizedMessage());
    }

    @ExceptionHandler(resultType = IllegalProjectionStateException.class)
    public void handle(IllegalProjectionStateException exception) {
        LOGGER.error(marker, exception.getLocalizedMessage());
    }

    @EventHandler
    public void on(AccountCreatedEvent event) {
        marker = Markers.append(REQUEST_ID, event.getRequestId());

        AccountLookupEntity accountLookupEntity = new AccountLookupEntity(event.getAccountId(), event.getEmail());

        marker.add(MarkerGenerator.generateMarker(accountLookupEntity));

        validateEntity(accountLookupEntity);
        accountLookupRepository.save(accountLookupEntity);

        LOGGER.info(marker, ACCOUNT_SAVED_IN_LOOKUP_DB, event.getAccountId());
    }

    @EventHandler
    public void on(AccountUpdatedEvent event) {
        marker = Markers.append(REQUEST_ID, event.getRequestId());

        AccountLookupEntity accountLookupEntity = accountLookupRepository.findByAccountId(event.getAccountId());
        throwExceptionIfEntityDoesNotExist(accountLookupEntity, String.format(ACCOUNT_WITH_ID_DOES_NOT_EXIST, event.getAccountId()));

        BeanUtils.copyProperties(event, accountLookupEntity);

        marker.add(MarkerGenerator.generateMarker(accountLookupEntity));

        validateEntity(accountLookupEntity);
        accountLookupRepository.save(accountLookupEntity);

        LOGGER.info(marker, ACCOUNT_UPDATED_IN_LOOKUP_DB, event.getAccountId());
    }

    @EventHandler
    public void on(AccountDeletedEvent event) {
        marker = Markers.append(REQUEST_ID, event.getRequestId());

        AccountLookupEntity accountLookupEntity = accountLookupRepository.findByAccountId(event.getAccountId());
        throwExceptionIfEntityDoesNotExist(accountLookupEntity, String.format(ACCOUNT_WITH_ID_DOES_NOT_EXIST, event.getAccountId()));

        marker.add(MarkerGenerator.generateMarker(accountLookupEntity));

        accountLookupRepository.delete(accountLookupEntity);

        LOGGER.info(marker, ACCOUNT_DELETED_FROM_LOOKUP_DB, event.getAccountId());
    }

    private void validateEntity(AccountLookupEntity entity) {
        BindingResult bindingResult = new BeanPropertyBindingResult(entity, "accountLookupEntity");
        validator.validate(entity, bindingResult);

        if (bindingResult.hasErrors()) {
            throw new IllegalProjectionStateException(bindingResult.getFieldError().getDefaultMessage());
        }
    }
}
