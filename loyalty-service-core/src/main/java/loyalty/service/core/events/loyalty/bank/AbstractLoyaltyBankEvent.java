package loyalty.service.core.events.loyalty.bank;

import lombok.Getter;
import lombok.experimental.SuperBuilder;
import loyalty.service.core.events.AbstractEvent;

@Getter
@SuperBuilder
public abstract class AbstractLoyaltyBankEvent extends AbstractEvent {

    private String loyaltyBankId;
}
