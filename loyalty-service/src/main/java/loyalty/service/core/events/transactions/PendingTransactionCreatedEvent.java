package loyalty.service.core.events.transactions;

import lombok.Getter;
import lombok.experimental.SuperBuilder;
import loyalty.service.core.events.transactions.AbstractTransactionEvent;

@Getter
@SuperBuilder
public class PendingTransactionCreatedEvent extends AbstractTransactionEvent {
}