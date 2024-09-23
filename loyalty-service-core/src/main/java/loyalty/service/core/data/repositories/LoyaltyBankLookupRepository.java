package loyalty.service.core.data.repositories;

import loyalty.service.core.data.entities.LoyaltyBankLookupEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LoyaltyBankLookupRepository extends JpaRepository<LoyaltyBankLookupEntity, String> {

    LoyaltyBankLookupEntity findByLoyaltyBankId(String loyaltyBankId);
    LoyaltyBankLookupEntity findByAccountId(String accountId);
}
