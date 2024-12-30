package loyalty.service.core.events;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class LoyaltyBankCreatedEvent extends AbstractLoyaltyBankEvent {

    private String accountId;
    private String businessId;
    private int pending;
    private int earned;
    private int authorized;
    private int captured;
}
