package loyalty.service.core.data.repositories;

import loyalty.service.core.data.entities.AccountEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface AccountRepository extends MongoRepository<AccountEntity, String> {

    Optional<AccountEntity> findByAccountId(String accountId);
}
