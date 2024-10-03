package loyalty.service.query.queryModels;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class EnrichedLoyaltyBanksForAccountQueryModel {

    private String accountId;
    private String firstName;
    private String lastName;
    private String email;
    private List<EnrichedLoyaltyBankForAccountQueryModel> loyaltyBankList;

    public void addLoyaltyBank(EnrichedLoyaltyBankForAccountQueryModel loyaltyBank) {
        loyaltyBankList.add(loyaltyBank);
    }
}
