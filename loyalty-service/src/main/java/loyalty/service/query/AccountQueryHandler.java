package loyalty.service.query;

import lombok.AllArgsConstructor;
import loyalty.service.core.data.AccountEntity;
import loyalty.service.core.data.AccountRepository;
import loyalty.service.core.exceptions.AccountNotFoundException;
import loyalty.service.query.queries.FindAccountQuery;
import loyalty.service.query.queryModels.AccountQueryModel;
import org.axonframework.queryhandling.QueryHandler;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class AccountQueryHandler {

    private final AccountRepository accountRepository;

    @QueryHandler
    public AccountQueryModel findAccount(FindAccountQuery query) {
        AccountEntity accountEntity = accountRepository.findById(query.getAccountId()).orElseThrow(
                () -> new AccountNotFoundException(query.getAccountId()));
        return convertAccountEntityToAccountQueryModel(accountEntity);
    }

    private AccountQueryModel convertAccountEntityToAccountQueryModel(AccountEntity accountEntity) {
        return new AccountQueryModel(
                accountEntity.getAccountId(),
                accountEntity.getFirstName(),
                accountEntity.getLastName(),
                accountEntity.getEmail()
        );
    }
}
