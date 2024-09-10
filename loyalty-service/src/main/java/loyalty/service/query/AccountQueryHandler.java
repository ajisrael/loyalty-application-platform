package loyalty.service.query;

import lombok.AllArgsConstructor;
import loyalty.service.core.data.AccountEntity;
import loyalty.service.core.data.AccountRepository;
import loyalty.service.query.queries.FindAccountQuery;
import loyalty.service.query.queryModels.AccountQueryModel;
import org.axonframework.queryhandling.QueryHandler;
import org.springframework.stereotype.Component;

import static loyalty.service.core.constants.ExceptionMessages.ACCOUNT_WITH_ID_DOES_NOT_EXIST;

@Component
@AllArgsConstructor
public class AccountQueryHandler {

    private final AccountRepository accountRepository;

    @QueryHandler
    public AccountQueryModel findAccount(FindAccountQuery query) {
        AccountEntity accountEntity = accountRepository.findById(query.getAccountId()).orElseThrow(
                () -> new IllegalStateException(String.format(ACCOUNT_WITH_ID_DOES_NOT_EXIST, query.getAccountId())));
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
