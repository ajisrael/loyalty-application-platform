package loyalty.service.core.data;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountLookupRepository extends JpaRepository<AccountLookupEntity, String> {

    AccountLookupEntity findByAccountId(String accountId);

    AccountLookupEntity findByAccountIdOrEmail(String accountId, String email);
    AccountLookupEntity findByEmail(String email);
}
