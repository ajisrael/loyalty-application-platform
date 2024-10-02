package loyalty.service.query.queryHandlers;

import lombok.AllArgsConstructor;
import loyalty.service.core.data.entities.AccountEntity;
import loyalty.service.core.data.repositories.AccountRepository;
import loyalty.service.core.exceptions.AccountNotFoundException;
import loyalty.service.core.utils.MarkerGenerator;
import loyalty.service.query.queries.FindAccountQuery;
import loyalty.service.query.queries.FindAllAccountsQuery;
import loyalty.service.query.querymodels.AccountQueryModel;
import org.axonframework.queryhandling.QueryHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class AccountQueryHandler {

    public static final Logger LOGGER = LoggerFactory.getLogger(AccountQueryHandler.class);
    private final AccountRepository accountRepository;

    @QueryHandler
    public Page<AccountQueryModel> findAllAccounts(FindAllAccountsQuery query) {
        LOGGER.info(MarkerGenerator.generateMarker(query), "Processing {}", query.getClass().getSimpleName());

        return accountRepository.findAll(query.getPageable())
                .map(this::convertAccountEntityToAccountQueryModel);
    }

    @QueryHandler
    public AccountQueryModel findAccount(FindAccountQuery query) {
        LOGGER.info(MarkerGenerator.generateMarker(query), "Processing {}", query.getClass().getSimpleName());

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
