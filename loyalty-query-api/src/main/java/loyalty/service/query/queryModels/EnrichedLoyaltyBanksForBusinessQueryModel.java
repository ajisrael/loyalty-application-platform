package loyalty.service.query.queryModels;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class EnrichedLoyaltyBanksForBusinessQueryModel {

    private String businessId;
    private String businessName;
    private List<EnrichedLoyaltyBankForBusinessQueryModel> loyaltyBankList;

    public void addLoyaltyBank(EnrichedLoyaltyBankForBusinessQueryModel loyaltyBank) {
        loyaltyBankList.add(loyaltyBank);
    }
}
