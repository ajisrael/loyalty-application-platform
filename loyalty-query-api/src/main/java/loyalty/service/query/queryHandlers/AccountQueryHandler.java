package loyalty.service.query.queryHandlers;

import lombok.AllArgsConstructor;
import loyalty.service.core.data.entities.AccountEntity;
import loyalty.service.core.data.repositories.AccountRepository;
import loyalty.service.core.exceptions.AccountNotFoundException;
import loyalty.service.core.utils.MarkerGenerator;
import loyalty.service.query.queries.FindAccountQuery;
import loyalty.service.query.queries.FindAllAccountsQuery;
import loyalty.service.query.querymodels.AccountQueryModel;
import net.logstash.logback.marker.Markers;
import org.axonframework.queryhandling.QueryHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static loyalty.service.core.constants.DomainConstants.REQUEST_ID;
import static loyalty.service.core.constants.LogMessages.ACCOUNT_NOT_FOUND_IN_DB;
import static loyalty.service.core.constants.LogMessages.PROCESSING_EVENT;

@Component
@AllArgsConstructor
public class AccountQueryHandler {

    public static final Logger LOGGER = LoggerFactory.getLogger(AccountQueryHandler.class);
    private final AccountRepository accountRepository;

    @QueryHandler
    public Page<AccountQueryModel> findAllAccounts(FindAllAccountsQuery query) {
        LOGGER.info(MarkerGenerator.generateMarker(query), PROCESSING_EVENT, query.getClass().getSimpleName());

        return accountRepository.findAll(query.getPageable())
                .map(this::convertAccountEntityToAccountQueryModel);
    }

    @QueryHandler
    public AccountQueryModel findAccount(FindAccountQuery query) {
        LOGGER.info(MarkerGenerator.generateMarker(query), PROCESSING_EVENT, query.getClass().getSimpleName());

        String accountId = query.getAccountId();

        Optional<AccountEntity> accountEntityOptional = accountRepository.findById(accountId);

        if (accountEntityOptional.isEmpty()) {
            LOGGER.info(Markers.append(REQUEST_ID, query.getRequestId()), ACCOUNT_NOT_FOUND_IN_DB, accountId);
            throw new AccountNotFoundException(accountId);
        }

        return convertAccountEntityToAccountQueryModel(accountEntityOptional.get());
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
