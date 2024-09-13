package loyalty.service.query.projections;

import loyalty.service.core.data.entities.LoyaltyBankEntity;
import loyalty.service.core.data.repositories.LoyaltyBankRepository;
import loyalty.service.core.events.LoyaltyBankCreatedEvent;
import loyalty.service.core.events.PendingTransactionCreatedEvent;
import loyalty.service.core.exceptions.LoyaltyBankNotFoundException;
import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.messaging.interceptors.ExceptionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@ProcessingGroup("loyalty-bank-group")
public class LoyaltyBankEventsHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoyaltyBankEventsHandler.class);

    private final LoyaltyBankRepository loyaltyBankRepository;

    public LoyaltyBankEventsHandler(LoyaltyBankRepository loyaltyBankRepository) {
        this.loyaltyBankRepository = loyaltyBankRepository;
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
    public void on(LoyaltyBankCreatedEvent event) {
        LoyaltyBankEntity accountEntity = new LoyaltyBankEntity();
        BeanUtils.copyProperties(event, accountEntity);
        loyaltyBankRepository.save(accountEntity);
    }

    @EventHandler
    public void on(PendingTransactionCreatedEvent event) {
        Optional<LoyaltyBankEntity> loyaltyBankEntityOptional = loyaltyBankRepository.findByLoyaltyBankId(event.getLoyaltyBankId());

        if (loyaltyBankEntityOptional.isPresent()) {
            LoyaltyBankEntity loyaltyBankEntity = loyaltyBankEntityOptional.get();
            loyaltyBankEntity.setPending(loyaltyBankEntity.getPending() + event.getPoints());
            loyaltyBankRepository.save(loyaltyBankEntity);
        } else {
            throw new LoyaltyBankNotFoundException(event.getLoyaltyBankId());
        }
    }
}
