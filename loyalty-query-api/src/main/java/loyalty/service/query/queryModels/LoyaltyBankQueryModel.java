package loyalty.service.query.querymodels;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LoyaltyBankQueryModel {

    private String loyaltyBankId;
    private String accountId;
    private int pending;
    private int earned;
    private int authorized;
    private int captured;
    private int available;
}
