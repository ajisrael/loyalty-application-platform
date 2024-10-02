package loyalty.service.command.data.repositories;

import loyalty.service.command.data.entities.LoyaltyBankLookupEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LoyaltyBankLookupRepository extends JpaRepository<LoyaltyBankLookupEntity, String> {

    LoyaltyBankLookupEntity findByLoyaltyBankId(String loyaltyBankId);
    List<LoyaltyBankLookupEntity> findByAccountId(String accountId);
    List<LoyaltyBankLookupEntity> findByBusinessId(String businessId);
    LoyaltyBankLookupEntity findByBusinessIdAndAccountId(String businessId, String accountId);
}
