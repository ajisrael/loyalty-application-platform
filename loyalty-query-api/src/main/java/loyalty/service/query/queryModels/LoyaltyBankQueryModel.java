package loyalty.service.query.queryModels;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LoyaltyBankQueryModel {

    private String loyaltyBankId;
    private String accountId;
    private String businessId;
    private int pending;
    private int earned;
    private int authorized;
    private int captured;
    private int available;
}
