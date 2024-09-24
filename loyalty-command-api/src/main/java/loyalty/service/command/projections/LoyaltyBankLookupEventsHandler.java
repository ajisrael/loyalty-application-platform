package loyalty.service.command.projections;

import lombok.AllArgsConstructor;
import loyalty.service.command.data.entities.LoyaltyBankLookupEntity;
import loyalty.service.command.data.repositories.LoyaltyBankLookupRepository;
import loyalty.service.core.events.LoyaltyBankCreatedEvent;
import loyalty.service.core.events.LoyaltyBankDeletedEvent;
import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;

import static loyalty.service.core.constants.ExceptionMessages.LOYALTY_BANK_WITH_ID_DOES_NOT_EXIST;
import static loyalty.service.core.utils.Helper.throwExceptionIfEntityDoesNotExist;

@Component
@AllArgsConstructor
@ProcessingGroup("loyalty-bank-lookup-group")
public class LoyaltyBankLookupEventsHandler {

    private LoyaltyBankLookupRepository loyaltyBankLookupRepository;

    @EventHandler
    public void on(LoyaltyBankCreatedEvent event) {
        loyaltyBankLookupRepository.save(
                new LoyaltyBankLookupEntity(
                        event.getLoyaltyBankId(),
                        event.getAccountId(),
                        event.getBusinessName()
                )
        );
    }

    @EventHandler
    public void on(LoyaltyBankDeletedEvent event) {
        LoyaltyBankLookupEntity loyaltyBankLookupEntity = loyaltyBankLookupRepository.findByLoyaltyBankId(event.getLoyaltyBankId());
        throwExceptionIfEntityDoesNotExist(loyaltyBankLookupEntity, String.format(LOYALTY_BANK_WITH_ID_DOES_NOT_EXIST, event.getLoyaltyBankId()));
        loyaltyBankLookupRepository.delete(loyaltyBankLookupEntity);
    }
}
