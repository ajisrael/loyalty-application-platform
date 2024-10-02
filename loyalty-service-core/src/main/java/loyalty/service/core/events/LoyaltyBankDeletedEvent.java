package loyalty.service.core.events;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class LoyaltyBankDeletedEvent extends AbstractEvent {

    private String loyaltyBankId;
    private String accountId;
    private String businessId;
}
