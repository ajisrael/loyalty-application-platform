package loyalty.service.command.projections;

import lombok.AllArgsConstructor;
import loyalty.service.core.data.entities.AccountLookupEntity;
import loyalty.service.core.data.entities.LoyaltyBankLookupEntity;
import loyalty.service.core.data.repositories.AccountLookupRepository;
import loyalty.service.core.data.repositories.LoyaltyBankLookupRepository;
import loyalty.service.core.events.AccountCreatedEvent;
import loyalty.service.core.events.AccountDeletedEvent;
import loyalty.service.core.events.AccountUpdatedEvent;
import loyalty.service.core.events.LoyaltyBankCreatedEvent;
import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@ProcessingGroup("loyalty-bank-group")
public class LoyaltyBankLookupEventsHandler {

    private LoyaltyBankLookupRepository loyaltyBankLookupRepository;

    @EventHandler
    public void on(LoyaltyBankCreatedEvent event) {
        loyaltyBankLookupRepository.save(
                new LoyaltyBankLookupEntity(
                        event.getLoyaltyBankId(),
                        event.getAccountId()
                )
        );
    }
}
