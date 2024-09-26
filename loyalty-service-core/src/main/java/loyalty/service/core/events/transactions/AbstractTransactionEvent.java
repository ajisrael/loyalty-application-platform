package loyalty.service.core.events.transactions;

import lombok.Getter;
import lombok.experimental.SuperBuilder;
import loyalty.service.core.events.AbstractEvent;

@Getter
@SuperBuilder
public abstract class AbstractTransactionEvent extends AbstractEvent {

    private String loyaltyBankId;
    private int points;
}
