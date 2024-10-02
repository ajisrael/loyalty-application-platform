package loyalty.service.query.data.repositories;

import loyalty.service.query.data.entities.AccountEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface AccountRepository extends MongoRepository<AccountEntity, String> {

    Optional<AccountEntity> findByAccountId(String accountId);
}
