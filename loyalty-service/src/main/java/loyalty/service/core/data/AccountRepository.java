package loyalty.service.core.data;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface AccountRepository extends MongoRepository<AccountEntity, String> {

    Optional<AccountEntity> findByAccountId(String accountId);
}
