package loyalty.service.core.data.repositories;

import loyalty.service.core.data.entities.LoyaltyBankEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;
import java.util.List;

public interface LoyaltyBankRepository extends MongoRepository<LoyaltyBankEntity, String> {

    Optional<LoyaltyBankEntity> findByLoyaltyBankId(String loyaltyBankId);
    Optional<List<LoyaltyBankEntity>> findByAccountId(String accountId);
}