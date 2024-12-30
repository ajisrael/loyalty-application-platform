package loyalty.service.core.events.loyalty.bank.transactions;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class ExpiredTransactionCreatedEvent extends AbstractTransactionEvent {
    private String targetTransactionId;
}