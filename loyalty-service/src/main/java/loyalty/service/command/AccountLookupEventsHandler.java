package loyalty.service.command;

import lombok.AllArgsConstructor;
import loyalty.service.core.data.AccountLookupEntity;
import loyalty.service.core.data.AccountLookupRepository;
import loyalty.service.core.events.AccountCreatedEvent;
import loyalty.service.core.events.AccountUpdatedEvent;
import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import static loyalty.service.core.constants.ExceptionMessages.ACCOUNT_WITH_ID_DOES_NOT_EXIST;
import static loyalty.service.core.utils.Helper.throwExceptionIfEntityDoesNotExist;

@Component
@AllArgsConstructor
@ProcessingGroup("account-group")
public class AccountLookupEventsHandler {

    private AccountLookupRepository accountLookupRepository;

    @EventHandler
    public void on(AccountCreatedEvent event) {
        accountLookupRepository.save(
                new AccountLookupEntity(
                        event.getAccountId(),
                        event.getFirstName(),
                        event.getLastName(),
                        event.getEmail()
                )
        );
    }

    @EventHandler
    public void on(AccountUpdatedEvent event) {
        AccountLookupEntity accountLookupEntity = accountLookupRepository.findByAccountId(event.getAccountId());
        throwExceptionIfEntityDoesNotExist(accountLookupEntity, String.format(ACCOUNT_WITH_ID_DOES_NOT_EXIST, event.getAccountId()));
        BeanUtils.copyProperties(event, accountLookupEntity);
        accountLookupRepository.save(accountLookupEntity);
    }

    // TODO: implement remove account command
//    @EventHandler
//    public void on(AccountRemovedEvent event) {
//        AccountLookupEntity accountLookupEntity = accountLookupRepository.findByAccountId(event.getAccountId());
//        throwExceptionIfEntityDoesNotExist(accountLookupEntity, String.format(ACCOUNT_WITH_ID_DOES_NOT_EXIST, event.getAccountId()));
//        accountLookupRepository.delete(accountLookupEntity);
//    }
}
