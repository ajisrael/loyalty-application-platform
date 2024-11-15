package loyalty.service.core.events;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class LoyaltyBankDeletionStartedEvent extends AbstractEvent {

    private String loyaltyBankId;
}
