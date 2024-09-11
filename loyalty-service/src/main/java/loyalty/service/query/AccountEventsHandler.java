package loyalty.service.query;

import loyalty.service.core.data.AccountEntity;
import loyalty.service.core.data.AccountRepository;
import loyalty.service.core.events.AccountCreatedEvent;
import loyalty.service.core.events.AccountUpdatedEvent;
import loyalty.service.core.exceptions.AccountNotFoundException;
import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.messaging.interceptors.ExceptionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@ProcessingGroup("account-group")
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
    }

    @EventHandler
    public void on(AccountUpdatedEvent event) {
        Optional<AccountEntity> accountEntityOptional = accountRepository.findByAccountId(event.getAccountId());

        if (accountEntityOptional.isPresent()) {
            AccountEntity accountEntity = accountEntityOptional.get();
            BeanUtils.copyProperties(event, accountEntity);
            accountRepository.save(accountEntity);
        } else {
            throw new AccountNotFoundException(event.getAccountId());
        }
    }
}
