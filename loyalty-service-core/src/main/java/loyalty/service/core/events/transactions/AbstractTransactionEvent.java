package loyalty.service.core.events.transactions;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public abstract class AbstractTransactionEvent {

    private String loyaltyBankId;
    private int points;
}
