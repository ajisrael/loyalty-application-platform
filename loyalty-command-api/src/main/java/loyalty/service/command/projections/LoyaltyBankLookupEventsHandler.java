package loyalty.service.command.projections;

import lombok.AllArgsConstructor;
import loyalty.service.command.data.entities.LoyaltyBankLookupEntity;
import loyalty.service.command.data.repositories.LoyaltyBankLookupRepository;
import loyalty.service.core.events.LoyaltyBankCreatedEvent;
import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.stereotype.Component;

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
                        event.getAccountId()
                )
        );
    }
}
