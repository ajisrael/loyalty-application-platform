package loyalty.service.query.queryhandlers;

import lombok.AllArgsConstructor;
import loyalty.service.core.data.entities.AccountEntity;
import loyalty.service.core.data.repositories.AccountRepository;
import loyalty.service.core.exceptions.AccountNotFoundException;
import loyalty.service.query.queries.FindAccountQuery;
import loyalty.service.query.queries.FindAllAccountsQuery;
import loyalty.service.query.querymodels.AccountQueryModel;
import org.axonframework.queryhandling.QueryHandler;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class AccountQueryHandler {

    private final AccountRepository accountRepository;

    @QueryHandler
    public Page<AccountQueryModel> findAllAccounts(FindAllAccountsQuery query) {
        return accountRepository.findAll(query.getPageable())
                .map(this::convertAccountEntityToAccountQueryModel);
    }

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