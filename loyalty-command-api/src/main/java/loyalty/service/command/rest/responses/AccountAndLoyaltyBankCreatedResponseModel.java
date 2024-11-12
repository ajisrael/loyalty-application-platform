package loyalty.service.command.rest.responses;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AccountAndLoyaltyBankCreatedResponseModel {

    private String accountId;
    private String loyaltyBankId;
}
