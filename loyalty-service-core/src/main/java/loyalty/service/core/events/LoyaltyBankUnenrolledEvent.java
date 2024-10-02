package loyalty.service.core.events;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class LoyaltyBankUnenrolledEvent extends AbstractEvent {

    private String loyaltyBankId;
}
