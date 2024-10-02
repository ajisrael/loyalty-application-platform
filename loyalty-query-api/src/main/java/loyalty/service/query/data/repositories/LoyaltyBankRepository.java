package loyalty.service.query.data.repositories;

import loyalty.service.query.data.entities.LoyaltyBankEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;
import java.util.List;

public interface LoyaltyBankRepository extends MongoRepository<LoyaltyBankEntity, String> {

    Optional<LoyaltyBankEntity> findByLoyaltyBankId(String loyaltyBankId);
    Optional<List<LoyaltyBankEntity>> findByAccountId(String accountId);
    Optional<List<LoyaltyBankEntity>> findByBusinessId(String businessId);
}
