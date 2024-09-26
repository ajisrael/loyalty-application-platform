package loyalty.service.core.events;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class LoyaltyBankCreatedEvent extends AbstractEvent {

    private String loyaltyBankId;
    private String accountId;
    private String businessName;
    private int pending;
    private int earned;
    private int authorized;
    private int captured;
}
