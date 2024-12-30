package loyalty.service.core.events.loyalty.bank;

import lombok.Getter;
import lombok.experimental.SuperBuilder;
import loyalty.service.core.events.loyalty.bank.AbstractLoyaltyBankEvent;

@Getter
@SuperBuilder
public class LoyaltyBankCreatedEvent extends AbstractLoyaltyBankEvent {

    private String accountId;
    private String businessId;
    private int pending;
    private int earned;
    private int authorized;
    private int captured;
}
