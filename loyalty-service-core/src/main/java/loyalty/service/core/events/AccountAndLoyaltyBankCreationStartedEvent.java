package loyalty.service.core.events;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class AccountAndLoyaltyBankCreationStartedEvent extends AbstractEvent {

    private String accountId;
    private String firstName;
    private String lastName;
    private String email;
    private String loyaltyBankId;
    private String businessId;
}
