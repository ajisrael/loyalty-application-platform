package loyalty.service.core.events.loyalty.bank.transactions;

import lombok.Getter;
import lombok.experimental.SuperBuilder;
import loyalty.service.core.events.loyalty.bank.AbstractLoyaltyBankEvent;

@Getter
@SuperBuilder
public abstract class AbstractTransactionEvent extends AbstractLoyaltyBankEvent {

    private int points;
}
