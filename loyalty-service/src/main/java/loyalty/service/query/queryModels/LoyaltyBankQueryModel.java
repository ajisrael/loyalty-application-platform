package loyalty.service.query.queryModels;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LoyaltyBankQueryModel {

    private String loyaltyBankId;
    private String accountId;
    private int pending;
    private int earned;
    private int reserved;
    private int redeemed;
    private int available;
}
