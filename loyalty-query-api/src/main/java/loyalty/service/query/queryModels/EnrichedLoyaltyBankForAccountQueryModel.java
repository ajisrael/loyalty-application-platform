package loyalty.service.query.queryModels;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class EnrichedLoyaltyBankForAccountQueryModel {

    private String loyaltyBankId;
    private BusinessQueryModel businessQueryModel;
    private int pending;
    private int earned;
    private int authorized;
    private int captured;
    private int available;
}
