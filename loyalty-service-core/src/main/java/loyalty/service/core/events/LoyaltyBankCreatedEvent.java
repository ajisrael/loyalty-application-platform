package loyalty.service.core.events;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LoyaltyBankCreatedEvent {

    private String loyaltyBankId;
    private String accountId;
    private String businessName;
    private int pending;
    private int earned;
    private int authorized;
    private int captured;
}
