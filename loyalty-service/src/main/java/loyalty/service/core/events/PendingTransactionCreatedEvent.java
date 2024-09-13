package loyalty.service.core.events;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PendingTransactionCreatedEvent {

    private String loyaltyBankId;
    private int points;
}
