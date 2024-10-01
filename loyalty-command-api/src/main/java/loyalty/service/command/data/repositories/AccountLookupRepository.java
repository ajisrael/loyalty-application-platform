package loyalty.service.command.data.repositories;

import loyalty.service.command.data.entities.AccountLookupEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountLookupRepository extends JpaRepository<AccountLookupEntity, String> {

    AccountLookupEntity findByAccountId(String accountId);
    AccountLookupEntity findByEmail(String email);
}
