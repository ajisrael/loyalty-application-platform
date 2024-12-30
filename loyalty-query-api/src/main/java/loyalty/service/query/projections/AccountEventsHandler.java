package loyalty.service.query.projections;

import loyalty.service.core.events.*;
import loyalty.service.query.data.entities.AccountEntity;
import loyalty.service.query.data.repositories.AccountRepository;
import loyalty.service.core.exceptions.AccountNotFoundException;
import net.logstash.logback.marker.Markers;
import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.messaging.interceptors.ExceptionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static loyalty.service.core.constants.DomainConstants.ACCOUNT_GROUP;
import static loyalty.service.core.constants.DomainConstants.REQUEST_ID;
import static loyalty.service.core.constants.LogMessages.*;

@Component
@ProcessingGroup(ACCOUNT_GROUP)
public class AccountEventsHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(AccountEventsHandler.class);

    private final AccountRepository accountRepository;

    public AccountEventsHandler(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
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
    public void on(AccountCreatedEvent event) {
        AccountEntity accountEntity = new AccountEntity();
        BeanUtils.copyProperties(event, accountEntity);
        accountRepository.save(accountEntity);

        LOGGER.info(Markers.append(REQUEST_ID, event.getRequestId()), ACCOUNT_SAVED_IN_DB, event.getAccountId());
    }

    @EventHandler
    public void on(AccountFirstNameChangedEvent event) {
        Optional<AccountEntity> accountEntityOptional = accountRepository.findByAccountId(event.getAccountId());

        if (accountEntityOptional.isPresent()) {
            AccountEntity accountEntity = accountEntityOptional.get();
            accountEntity.setFirstName(event.getNewFirstName());
            accountRepository.save(accountEntity);

            LOGGER.info(Markers.append(REQUEST_ID, event.getRequestId()), ACCOUNT_UPDATED_IN_DB, event.getAccountId(), event.getClass().getSimpleName());
        } else {
            LOGGER.error(Markers.append(REQUEST_ID, event.getRequestId()), ACCOUNT_NOT_FOUND_IN_DB, event.getAccountId());
            throw new AccountNotFoundException(event.getAccountId());
        }
    }

    @EventHandler
    public void on(AccountLastNameChangedEvent event) {
        Optional<AccountEntity> accountEntityOptional = accountRepository.findByAccountId(event.getAccountId());

        if (accountEntityOptional.isPresent()) {
            AccountEntity accountEntity = accountEntityOptional.get();
            accountEntity.setLastName(event.getNewLastName());
            accountRepository.save(accountEntity);

            LOGGER.info(Markers.append(REQUEST_ID, event.getRequestId()), ACCOUNT_UPDATED_IN_DB, event.getAccountId(), event.getClass().getSimpleName());
        } else {
            LOGGER.error(Markers.append(REQUEST_ID, event.getRequestId()), ACCOUNT_NOT_FOUND_IN_DB, event.getAccountId());
            throw new AccountNotFoundException(event.getAccountId());
        }
    }

    @EventHandler
    public void on(AccountEmailChangedEvent event) {
        Optional<AccountEntity> accountEntityOptional = accountRepository.findByAccountId(event.getAccountId());

        if (accountEntityOptional.isPresent()) {
            AccountEntity accountEntity = accountEntityOptional.get();
            accountEntity.setEmail(event.getNewEmail());
            accountRepository.save(accountEntity);

            LOGGER.info(Markers.append(REQUEST_ID, event.getRequestId()), ACCOUNT_UPDATED_IN_DB, event.getAccountId(), event.getClass().getSimpleName());
        } else {
            LOGGER.error(Markers.append(REQUEST_ID, event.getRequestId()), ACCOUNT_NOT_FOUND_IN_DB, event.getAccountId());
            throw new AccountNotFoundException(event.getAccountId());
        }
    }

    @EventHandler
    public void on(AccountDeletedEvent event) {
        Optional<AccountEntity> accountEntityOptional = accountRepository.findByAccountId(event.getAccountId());

        if (accountEntityOptional.isPresent()) {
            accountRepository.delete(accountEntityOptional.get());
            LOGGER.info(Markers.append(REQUEST_ID, event.getRequestId()), ACCOUNT_DELETED_FROM_DB, event.getAccountId());
        } else {
            LOGGER.error(Markers.append(REQUEST_ID, event.getRequestId()), ACCOUNT_NOT_FOUND_IN_DB, event.getAccountId());
            throw new AccountNotFoundException(event.getAccountId());
        }
    }
}
