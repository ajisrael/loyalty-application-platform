package loyalty.service.query.queryModels;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class EnrichedLoyaltyBankForBusinessQueryModel {

    private String loyaltyBankId;
    private AccountQueryModel accountQueryModel;
    private int pending;
    private int earned;
    private int authorized;
    private int captured;
    private int available;
}
