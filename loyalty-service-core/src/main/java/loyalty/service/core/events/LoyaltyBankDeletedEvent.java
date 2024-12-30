package loyalty.service.core.events;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class LoyaltyBankDeletedEvent extends AbstractLoyaltyBankEvent {

    private String accountId;
    private String businessId;
}
