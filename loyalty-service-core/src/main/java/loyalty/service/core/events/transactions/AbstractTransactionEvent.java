package loyalty.service.core.events.transactions;

import lombok.Getter;
import lombok.experimental.SuperBuilder;
import loyalty.service.core.events.AbstractLoyaltyBankEvent;

@Getter
@SuperBuilder
public abstract class AbstractTransactionEvent extends AbstractLoyaltyBankEvent {

    private int points;
}
