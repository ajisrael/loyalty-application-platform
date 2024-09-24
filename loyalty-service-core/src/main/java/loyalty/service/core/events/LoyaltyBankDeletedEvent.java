package loyalty.service.core.events;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LoyaltyBankDeletedEvent {

    private String loyaltyBankId;
    private String accountId;
}
